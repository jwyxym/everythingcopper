param(
	[Parameter(Mandatory = $true)]
	[string]$MinecraftJar,
	[string]$ProjectRoot = ''
)

$ErrorActionPreference = 'Stop'
if ([string]::IsNullOrEmpty($ProjectRoot)) {
	$ProjectRoot = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
}
Add-Type -AssemblyName System.Drawing
Add-Type -AssemblyName System.IO.Compression.FileSystem

$ProjectRoot = [System.IO.Path]::GetFullPath($ProjectRoot)
$textureRoot = Join-Path $ProjectRoot 'src/main/resources/assets/everythingcopper/textures'
$normalPalette = @('421C11', '5F2C1C', '9C4E31', 'C15A36', 'D36F52', 'EA866B', 'FC9982', 'FDD4CB')
$exposedPalette = @('421C11', '5F2C1C', '7F7257', 'A87762', '988C69', 'CE8D83', 'E6B2A5', 'FDD4CB')
# Match the existing hopper's olive weathering and teal oxidation, including its restrained highlights.
$weatheredPalette = @('497164', '6A7147', '748D57', '6C975C', '64A077', '66A977', '7AB799', '7AB799')
$oxidizedPalette = @('3B6655', '396E59', '4C9484', '53A178', '4FAB90', '59B292', '6EC59F', '6EC59F')
$shadeBoundaries = @(24, 48, 76, 104, 136, 168, 208)
$metalShades = @{}
for ($index = 0; $index -lt $normalPalette.Count; $index++) {
	$metalShades[$normalPalette[$index]] = $index
}
$validation = [System.Collections.Generic.List[object]]::new()
$archive = [System.IO.Compression.ZipFile]::OpenRead((Resolve-Path -LiteralPath $MinecraftJar).Path)

function Read-OriginalTexture {
	param([string]$Resource)
	$entry = $archive.GetEntry("assets/minecraft/textures/$Resource.png")
	if ($null -eq $entry) {
		throw "Missing original texture: $Resource"
	}
	$stream = $entry.Open()
	try {
		$decoded = [System.Drawing.Bitmap]::new($stream)
		try {
			return [System.Drawing.Bitmap]::new($decoded)
		} finally {
			$decoded.Dispose()
		}
	} finally {
		$stream.Dispose()
	}
}

function Get-PaletteColor {
	param([System.Drawing.Color]$Pixel, [string[]]$Palette, [int]$Shade = -1)
	if ($Shade -lt 0) {
		$brightness = (299 * [int]$Pixel.R + 587 * [int]$Pixel.G + 114 * [int]$Pixel.B) / 1000
		$Shade = 0
		foreach ($boundary in $shadeBoundaries) {
			if ($brightness -ge $boundary) { $Shade++ }
		}
	}
	$rgb = [Convert]::ToInt32($Palette[$Shade], 16)
	return [System.Drawing.Color]::FromArgb($Pixel.A, ($rgb -shr 16) -band 255, ($rgb -shr 8) -band 255, $rgb -band 255)
}

function Save-TextureVariant {
	param(
		[System.Drawing.Bitmap]$Source,
		[System.Drawing.Bitmap]$Output,
		[bool[,]]$MetalMask,
		[string]$RelativePath
	)
	if ($Source.Width -ne $Output.Width -or $Source.Height -ne $Output.Height) {
		throw "Texture dimensions changed: $RelativePath"
	}
	$changed = 0
	for ($y = 0; $y -lt $Source.Height; $y++) {
		for ($x = 0; $x -lt $Source.Width; $x++) {
			$before = $Source.GetPixel($x, $y)
			$after = $Output.GetPixel($x, $y)
			if ($before.A -ne $after.A) { throw "Alpha changed: $RelativePath ($x,$y)" }
			if ($before.ToArgb() -ne $after.ToArgb()) {
				if (!$MetalMask[$x, $y]) { throw "Non-metal pixel changed: $RelativePath ($x,$y)" }
				$changed++
			}
		}
	}
	$target = Join-Path $textureRoot $RelativePath
	$null = New-Item -ItemType Directory -Path ([System.IO.Path]::GetDirectoryName($target)) -Force
	$Output.Save($target, [System.Drawing.Imaging.ImageFormat]::Png)
	$validation.Add([PSCustomObject]@{ Path = $RelativePath; Width = $Output.Width; Height = $Output.Height; ChangedPixels = $changed })
}

function Save-CheckedTexture {
	param([System.Drawing.Bitmap]$Source, [System.Drawing.Bitmap]$Output, [bool[,]]$MetalMask, [string]$RelativePath)
	Save-TextureVariant $Source $Output $MetalMask $RelativePath
	$bucket = $RelativePath.StartsWith('item/bucket/')
	$cart = $RelativePath -match '^item/[^/]*copper_minecart\.png$'
	$entity = $RelativePath -eq 'entity/copper_minecart/unaffected.png'
	if (!$bucket -and !$cart -and !$entity) { return }
	foreach ($age in @('exposed', 'weathered', 'oxidized')) {
		$palette = switch ($age) {
			'exposed' { $exposedPalette }
			'weathered' { $weatheredPalette }
			'oxidized' { $oxidizedPalette }
		}
		$variant = [System.Drawing.Bitmap]::new($Source)
		try {
			for ($y = 0; $y -lt $Source.Height; $y++) {
				for ($x = 0; $x -lt $Source.Width; $x++) {
					if (!$MetalMask[$x, $y]) { continue }
					$pixel = $Source.GetPixel($x, $y)
					$shade = if ($bucket -and $age -ne 'exposed') { $bucketMetalShades[$pixel.ToArgb()] } else { -1 }
					$variant.SetPixel($x, $y, (Get-PaletteColor $pixel $palette $shade))
				}
			}
			$directory = [IO.Path]::GetDirectoryName($RelativePath)
			$name = [IO.Path]::GetFileName($RelativePath)
			$target = if ($entity) { "entity/copper_minecart/$age.png" } else { (Join-Path $directory ($age + '_' + $name)).Replace('\', '/') }
			Save-TextureVariant $Source $variant $MetalMask $target
		} finally {
			$variant.Dispose()
		}
	}
}

try {
	# Mob buckets shift the container down one pixel and can shade the body differently.
	# Align the lower body before identifying metal, preserving the vanilla contents.
	$emptyBucket = Read-OriginalTexture 'item/bucket'
	try {
		$bucketMetalColors = @{}
		for ($y = 0; $y -lt $emptyBucket.Height; $y++) {
			for ($x = 0; $x -lt $emptyBucket.Width; $x++) {
				$pixel = $emptyBucket.GetPixel($x, $y)
				if ($pixel.A -gt 0 -and $pixel.R -eq $pixel.G -and $pixel.G -eq $pixel.B) {
					$bucketMetalColors[$pixel.ToArgb()] = $true
				}
			}
		}
		$bucketMetalShades = @{}
		$orderedMetalColors = @($bucketMetalColors.Keys | Sort-Object { $_ -band 255 })
		for ($index = 0; $index -lt $orderedMetalColors.Count; $index++) {
			$bucketMetalShades[$orderedMetalColors[$index]] = $index
		}
		foreach ($name in @('bucket', 'water_bucket', 'lava_bucket', 'milk_bucket', 'powder_snow_bucket', 'axolotl_bucket', 'tadpole_bucket', 'cod_bucket', 'salmon_bucket', 'pufferfish_bucket', 'tropical_fish_bucket', 'sulfur_cube_bucket')) {
			$source = Read-OriginalTexture "item/$name"
			$output = [System.Drawing.Bitmap]::new($source)
			$mask = [bool[,]]::new($source.Width, $source.Height)
			try {
				$bodyOffset = 0
				$bestMatches = -1
				foreach ($offset in @(0, 1)) {
					$matches = 0
					for ($y = 10; $y -lt $source.Height; $y++) {
						for ($x = 0; $x -lt $source.Width; $x++) {
							$pixel = $source.GetPixel($x, $y)
							if ($pixel.A -gt 0 -and $pixel.ToArgb() -eq $emptyBucket.GetPixel($x, ($y - $offset)).ToArgb()) { $matches++ }
						}
					}
					if ($matches -gt $bestMatches) { $bodyOffset = $offset; $bestMatches = $matches }
				}
				for ($y = 0; $y -lt $source.Height; $y++) {
					for ($x = 0; $x -lt $source.Width; $x++) {
						$pixel = $source.GetPixel($x, $y)
						if ($y -lt $bodyOffset) { continue }
						$reference = $emptyBucket.GetPixel($x, ($y - $bodyOffset))
						$sharedMetal = $pixel.ToArgb() -eq $reference.ToArgb()
						$shiftedMetal = $bodyOffset -gt 0 -and $y -ge 6 -and $reference.A -gt 0 -and $bucketMetalColors.ContainsKey($pixel.ToArgb())
						if ($pixel.A -gt 0 -and ($sharedMetal -or $shiftedMetal)) {
							$mask[$x, $y] = $true
							$output.SetPixel($x, $y, (Get-PaletteColor $pixel $normalPalette))
						}
					}
				}
				Save-CheckedTexture $source $output $mask "item/bucket/copper_$name.png"
				if ($name -in @('bucket', 'water_bucket', 'lava_bucket')) {
					Save-CheckedTexture $source $output $mask "item/copper_$name.png"
				}
			} finally {
				$output.Dispose()
				$source.Dispose()
			}
		}
	} finally {
		$emptyBucket.Dispose()
	}

	# Recolor the entire shared metal chassis, including the lower corners and wheels; preserve cargo.
	$emptyCart = Read-OriginalTexture 'item/minecart'
	try {
		foreach ($name in @('minecart', 'chest_minecart', 'furnace_minecart', 'hopper_minecart', 'tnt_minecart', 'command_block_minecart')) {
			$source = Read-OriginalTexture "item/$name"
			$output = [System.Drawing.Bitmap]::new($source)
			$mask = [bool[,]]::new($source.Width, $source.Height)
			try {
				for ($y = 0; $y -lt $source.Height; $y++) {
					for ($x = 0; $x -lt $source.Width; $x++) {
						$pixel = $source.GetPixel($x, $y)
						if ($pixel.A -gt 0 -and $pixel.ToArgb() -eq $emptyCart.GetPixel($x, $y).ToArgb()) {
							$mask[$x, $y] = $true
							$output.SetPixel($x, $y, (Get-PaletteColor $pixel $normalPalette))
						}
					}
				}
				$targetName = $name.Replace('minecart', 'copper_minecart')
				Save-CheckedTexture $source $output $mask "item/$targetName.png"
			} finally {
				$output.Dispose()
				$source.Dispose()
			}
		}
	} finally {
		$emptyCart.Dispose()
	}

	# Native spear metal colors are distinct from shaft and grip colors.
	# Both layouts share the same shade mapping and stage palettes; wood and silhouette stay intact.
	foreach ($inHand in @($false, $true)) {
		$suffix = if ($inHand) { '_in_hand' } else { '' }
		$source = Read-OriginalTexture "item/copper_spear$suffix"
		try {
			foreach ($age in @('unaffected', 'exposed', 'weathered', 'oxidized')) {
				$output = [System.Drawing.Bitmap]::new($source)
				$mask = [bool[,]]::new($source.Width, $source.Height)
				try {
					$palette = switch ($age) {
						'exposed' { $exposedPalette }
						'weathered' { $weatheredPalette }
						'oxidized' { $oxidizedPalette }
						default { $null }
					}
					for ($y = 0; $y -lt $source.Height; $y++) {
						for ($x = 0; $x -lt $source.Width; $x++) {
							$pixel = $source.GetPixel($x, $y)
							$key = '{0:X2}{1:X2}{2:X2}' -f $pixel.R, $pixel.G, $pixel.B
							if ($pixel.A -eq 0 -or !$metalShades.ContainsKey($key)) { continue }
							$mask[$x, $y] = $true
							if ($null -ne $palette) {
								$output.SetPixel($x, $y, (Get-PaletteColor $pixel $palette $metalShades[$key]))
							}
						}
					}
					Save-CheckedTexture $source $output $mask "item/${age}_copper_spear$suffix.png"
				} finally {
					$output.Dispose()
				}
			}
		} finally {
			$source.Dispose()
		}
	}

	# The entity uses the original 64x32 UV atlas, without resampling or moving islands.
	$source = Read-OriginalTexture 'entity/minecart/minecart'
	$output = [System.Drawing.Bitmap]::new($source)
	$mask = [bool[,]]::new($source.Width, $source.Height)
	try {
		for ($y = 0; $y -lt $source.Height; $y++) {
			for ($x = 0; $x -lt $source.Width; $x++) {
				$pixel = $source.GetPixel($x, $y)
				if ($pixel.A -gt 0) {
					$mask[$x, $y] = $true
					$output.SetPixel($x, $y, (Get-PaletteColor $pixel $normalPalette))
				}
			}
		}
		Save-CheckedTexture $source $output $mask 'entity/copper_minecart/unaffected.png'
	} finally {
		$output.Dispose()
		$source.Dispose()
	}
} finally {
	$archive.Dispose()
}

$report = [ordered]@{
	Generator = 'tools/Regenerate-CopperTextures.ps1'
	Method = 'Original Minecraft pixels and alpha; fixed metal palettes; no image generation or resizing.'
	CopperPalette = $normalPalette
	ExposedPalette = $exposedPalette
	ExposedReferences = @('item/exposed_copper_sword.png', 'item/exposed_copper_axe.png', 'item/exposed_copper_pickaxe.png')
	WeatheredPalette = $weatheredPalette
	OxidizedPalette = $oxidizedPalette
	StageReferences = @('block/weathered_copper_hopper_outside.png', 'block/oxidized_copper_hopper_outside.png')
	Validation = 'Original dimensions and per-pixel alpha preserved; all non-metal pixels unchanged.'
	Assets = @($validation)
}
$reportPath = Join-Path $ProjectRoot 'docs/copper-texture-style.json'
$null = New-Item -ItemType Directory -Path (Split-Path -Parent $reportPath) -Force
$json = $report | ConvertTo-Json -Depth 6
[System.IO.File]::WriteAllText($reportPath, $json, [System.Text.UTF8Encoding]::new($false))
$validation | Format-Table -AutoSize

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
$patinaPalette = @('1C3D36', '28554A', '377363', '478F78', '5AAA8D', '74C5A5', 'A0E0C1', 'D3F5DE')
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

function Save-CheckedTexture {
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

try {
	# Contents are precisely the vanilla differences from the empty bucket.
	# Every shared container pixel uses the same copper color in every variant.
	$emptyBucket = Read-OriginalTexture 'item/bucket'
	try {
		foreach ($name in @('bucket', 'water_bucket', 'lava_bucket', 'milk_bucket', 'powder_snow_bucket')) {
			$source = Read-OriginalTexture "item/$name"
			$output = [System.Drawing.Bitmap]::new($source)
			$mask = [bool[,]]::new($source.Width, $source.Height)
			try {
				for ($y = 0; $y -lt $source.Height; $y++) {
					for ($x = 0; $x -lt $source.Width; $x++) {
						$pixel = $source.GetPixel($x, $y)
						if ($pixel.A -gt 0 -and $pixel.ToArgb() -eq $emptyBucket.GetPixel($x, $y).ToArgb()) {
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

	# Preserve vanilla cargo, shadows and wheel pixels; recolor shared body pixels only.
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
						$wheel = $y -ge 13 -or ($x -ge 12 -and $y -ge 11)
						if (!$wheel -and $pixel.A -gt 0 -and $pixel.ToArgb() -eq $emptyCart.GetPixel($x, $y).ToArgb()) {
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
	# Both layouts share one mirrored head pattern, so oxidation cannot alter the silhouette.
	foreach ($inHand in @($false, $true)) {
		$suffix = if ($inHand) { '_in_hand' } else { '' }
		$source = Read-OriginalTexture "item/copper_spear$suffix"
		try {
			foreach ($age in @('unaffected', 'exposed', 'weathered', 'oxidized')) {
				$output = [System.Drawing.Bitmap]::new($source)
				$mask = [bool[,]]::new($source.Width, $source.Height)
				try {
					$coverage = switch ($age) { 'exposed' { 32 } 'weathered' { 72 } 'oxidized' { 100 } default { 0 } }
					for ($y = 0; $y -lt $source.Height; $y++) {
						for ($x = 0; $x -lt $source.Width; $x++) {
							$pixel = $source.GetPixel($x, $y)
							$key = '{0:X2}{1:X2}{2:X2}' -f $pixel.R, $pixel.G, $pixel.B
							if ($pixel.A -eq 0 -or !$metalShades.ContainsKey($key)) { continue }
							$mask[$x, $y] = $true
							$canonicalX = if ($inHand -and $y -le 8) { 15 - $x } else { $x }
							$cellX = [int][Math]::Floor($canonicalX / 2)
							$cellY = [int][Math]::Floor($y / 2)
							$pattern = ($cellX * 37 + $cellY * 53 + $cellX * $cellY * 11 + 17) % 100
							if ($pattern -lt $coverage) {
								$output.SetPixel($x, $y, (Get-PaletteColor $pixel $patinaPalette $metalShades[$key]))
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
	PatinaPalette = $patinaPalette
	Validation = 'Original dimensions and per-pixel alpha preserved; all non-metal pixels unchanged.'
	Assets = @($validation)
}
$reportPath = Join-Path $ProjectRoot 'docs/copper-texture-style.json'
$null = New-Item -ItemType Directory -Path (Split-Path -Parent $reportPath) -Force
$json = $report | ConvertTo-Json -Depth 6
[System.IO.File]::WriteAllText($reportPath, $json, [System.Text.UTF8Encoding]::new($false))
$validation | Format-Table -AutoSize

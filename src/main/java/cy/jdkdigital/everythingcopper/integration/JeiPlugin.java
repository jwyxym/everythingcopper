package cy.jdkdigital.everythingcopper.integration;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.crafting.recipe.ScrapeRecipe;
import cy.jdkdigital.everythingcopper.crafting.recipe.WaxOnRecipe;
import cy.jdkdigital.everythingcopper.crafting.recipe.WeatheringRecipe;
import cy.jdkdigital.everythingcopper.init.ModBlocks;
import cy.jdkdigital.everythingcopper.init.ModTags;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
	private static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(EverythingCopper.MODID, "jei");
	public static final IRecipeType<WeatheringRecipe> WEATHERING_RECIPE_TYPE = IRecipeType.create(EverythingCopper.MODID, "weathering", WeatheringRecipe.class);
	public static final IRecipeType<CopperTreatmentRecipe> COPPER_TREATMENT_RECIPE_TYPE = IRecipeType.create(EverythingCopper.MODID, "copper_treatment", CopperTreatmentRecipe.class);

	@Override
	public Identifier getPluginUid() {
		return PLUGIN_ID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		for (Item item : CopperItemVariants.equipment()) {
			// Normalize absent components to their default values; damage and custom names do not change a recipe.
			registration.registerSubtypeInterpreter(item, (stack, context) -> ICopperItem.getAge(stack) + ":" + ICopperItem.isWaxed(stack));
		}
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new WeatheringRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
			new CopperTreatmentRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(WEATHERING_RECIPE_TYPE, ModBlocks.WEATHERING_STATION.get());
		registration.addCraftingStation(COPPER_TREATMENT_RECIPE_TYPE, Blocks.CRAFTING_TABLE);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		List<WeatheringRecipe> weathering = new ArrayList<>();
		BuiltInRegistries.BLOCK.forEach(block -> {
			if (block instanceof WeatheringCopper copper && block.asItem() != Items.AIR) {
				copper.getNext(block.defaultBlockState()).ifPresent(next -> {
					if (next.getBlock().asItem() != Items.AIR) {
						Identifier key = BuiltInRegistries.BLOCK.getKey(block);
						weathering.add(new WeatheringRecipe(displayId("weathering/block", key, ""), new ItemStack(block), new ItemStack(next.getBlock())));
					}
				});
			}
		});
		for (Item item : CopperItemVariants.equipment()) {
			ItemStack input = item.getDefaultInstance();
			while (ICopperItem.canAge(input)) {
				ItemStack output = input.copy();
				ICopperItem.setAge(output, WeatheringUtils.nextState(ICopperItem.getAge(input)));
				Identifier id = displayId("weathering/item", BuiltInRegistries.ITEM.getKey(item), "/" + ICopperItem.getAge(input));
				weathering.add(new WeatheringRecipe(id, input, output));
				input = output.copy();
			}
		}
		registration.addRecipes(WEATHERING_RECIPE_TYPE, weathering);
		registerTreatmentRecipes(registration);
	}

	private static void registerTreatmentRecipes(IRecipeRegistration registration) {
		List<CopperTreatmentRecipe> recipes = new ArrayList<>();
		Ingredient wax = Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(ModTags.WAXING_ITEMS));
		for (Item item : CopperItemVariants.equipment()) {
			Ingredient ingredient = Ingredient.of(item);
			WaxOnRecipe waxOn = new WaxOnRecipe(ingredient, wax);
			ScrapeRecipe scrape = new ScrapeRecipe(ingredient);
			Identifier key = BuiltInRegistries.ITEM.getKey(item);
			for (WeatheringCopper.WeatherState age : WeatheringCopper.WeatherState.values()) {
				String state = age.getSerializedName();
				ItemStack input = CopperItemVariants.create(item, age, false);
				if (!wax.isEmpty()) {
					ItemStack waxItem = wax.items().findFirst().orElseThrow().value().getDefaultInstance();
					ItemStack output = waxOn.assemble(CraftingInput.of(2, 1, List.of(input, waxItem)));
					recipes.add(new CopperTreatmentRecipe(displayId("wax_on", key, "/" + state), input, output, CopperTreatmentRecipe.Operation.WAX_ON));
				}
				if (age != WeatheringCopper.WeatherState.UNAFFECTED) {
					ItemStack output = scrape.assemble(CraftingInput.of(1, 1, List.of(input)));
					recipes.add(new CopperTreatmentRecipe(displayId("scrape", key, "/" + state), input, output, CopperTreatmentRecipe.Operation.SCRAPE));
				}
				ItemStack waxed = CopperItemVariants.create(item, age, true);
				ItemStack output = scrape.assemble(CraftingInput.of(1, 1, List.of(waxed)));
				recipes.add(new CopperTreatmentRecipe(displayId("wax_off", key, "/" + state), waxed, output, CopperTreatmentRecipe.Operation.WAX_OFF));
			}
		}
		// Keep component-sensitive conversions out of JEI's generic crafting recipe simulation.
		registration.addRecipes(COPPER_TREATMENT_RECIPE_TYPE, recipes);
	}

	private static Identifier displayId(String operation, Identifier item, String suffix) {
		return Identifier.fromNamespaceAndPath(EverythingCopper.MODID, "/" + operation + "/" + item.getNamespace() + "/" + item.getPath() + suffix);
	}
}

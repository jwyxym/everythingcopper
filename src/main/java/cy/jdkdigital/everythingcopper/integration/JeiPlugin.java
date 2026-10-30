package cy.jdkdigital.everythingcopper.integration;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.crafting.recipe.ScrapeRecipe;
import cy.jdkdigital.everythingcopper.crafting.recipe.WaxOnRecipe;
import cy.jdkdigital.everythingcopper.crafting.recipe.WeatheringRecipe;
import cy.jdkdigital.everythingcopper.init.ModTags;
import cy.jdkdigital.everythingcopper.init.ModItems;
import net.minecraft.network.chat.Component;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.vanilla.IJeiShapedRecipeBuilder;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.ArrayList;
import java.util.List;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
	private static final Identifier PLUGIN_ID = Identifier.fromNamespaceAndPath(EverythingCopper.MODID, "jei");
	public static final IRecipeType<WeatheringRecipe> WEATHERING_RECIPE_TYPE = IRecipeType.create(EverythingCopper.MODID, "weathering", WeatheringRecipe.class);

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
		registration.addRecipeCategories(new WeatheringRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addIngredientInfo(ModItems.COPPER_WATER_BUCKET.get(), Component.translatable("everythingcopper.bucket.water"));
		registration.addIngredientInfo(ModItems.COPPER_LAVA_BUCKET.get(), Component.translatable("everythingcopper.bucket.lava"));
		registration.addIngredientInfo(ModItems.COPPER_POWDER_SNOW_BUCKET.get(), Component.translatable("everythingcopper.bucket.powder_snow"));
		registration.addIngredientInfo(ModItems.COPPER_MILK_BUCKET.get(), Component.translatable("everythingcopper.bucket.milk"));
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
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
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
					recipes.add(treatmentRecipe(registration, displayId("wax_on", key, "/" + state), input, output, true));
				}
				if (age != WeatheringCopper.WeatherState.UNAFFECTED) {
					ItemStack output = scrape.assemble(CraftingInput.of(1, 1, List.of(input)));
					recipes.add(treatmentRecipe(registration, displayId("scrape", key, "/" + state), input, output, false));
				}
				ItemStack waxed = CopperItemVariants.create(item, age, true);
				ItemStack output = scrape.assemble(CraftingInput.of(1, 1, List.of(waxed)));
				recipes.add(treatmentRecipe(registration, displayId("wax_off", key, "/" + state), waxed, output, false));
			}
		}
		registration.addRecipes(RecipeTypes.CRAFTING, recipes);
	}

	private static RecipeHolder<CraftingRecipe> treatmentRecipe(IRecipeRegistration registration, Identifier id, ItemStack input, ItemStack output, boolean waxing) {
		// Explicit stack displays preserve oxidation and wax components in JEI's crafting category.
		IJeiShapedRecipeBuilder builder = registration.getVanillaRecipeFactory()
			.createShapedRecipeBuilder(CraftingBookCategory.MISC, new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(output)))
			.define('E', Ingredient.of(input.getItem()), new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(input)));
		if (waxing) {
			Ingredient wax = Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(ModTags.WAXING_ITEMS));
			builder.define('W', wax, new SlotDisplay.TagSlotDisplay(ModTags.WAXING_ITEMS)).pattern("EW");
		} else {
			builder.pattern("E");
		}
		return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, id), builder.build());
	}

	private static Identifier displayId(String operation, Identifier item, String suffix) {
		return Identifier.fromNamespaceAndPath(EverythingCopper.MODID, "/" + operation + "/" + item.getNamespace() + "/" + item.getPath() + suffix);
	}
}

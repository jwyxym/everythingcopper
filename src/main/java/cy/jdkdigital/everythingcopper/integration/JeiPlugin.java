package cy.jdkdigital.everythingcopper.integration;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.crafting.recipe.ScrapeRecipe;
import cy.jdkdigital.everythingcopper.crafting.recipe.WaxOnRecipe;
import cy.jdkdigital.everythingcopper.crafting.recipe.WeatheringRecipe;
import cy.jdkdigital.everythingcopper.init.ModBlocks;
import cy.jdkdigital.everythingcopper.init.ModTags;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.vanilla.IJeiShapedRecipeBuilder;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
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
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;

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
		for (Item item : copperItems()) {
			// Normalize absent components to their default values; damage and custom names do not change a recipe.
			registration.registerSubtypeInterpreter(item, (stack, context) -> ICopperItem.getAge(stack) + ":" + ICopperItem.isWaxed(stack));
		}
	}

	@Override
	public void registerExtraIngredients(IExtraIngredientRegistration registration) {
		List<ItemStack> variants = new ArrayList<>();
		for (Item item : copperItems()) {
			for (WeatheringCopper.WeatherState age : WeatheringCopper.WeatherState.values()) {
				ItemStack stack = agedStack(item, age.getSerializedName());
				if (age != WeatheringCopper.WeatherState.UNAFFECTED) {
					variants.add(stack.copy());
				}
				ICopperItem.setWaxed(stack, true);
				variants.add(stack);
			}
		}
		registration.addExtraItemStacks(variants);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new WeatheringRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(WEATHERING_RECIPE_TYPE, ModBlocks.WEATHERING_STATION.get());
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
		for (Item item : copperItems()) {
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
		registerCraftingRecipes(registration);
	}

	private static void registerCraftingRecipes(IRecipeRegistration registration) {
		List<RecipeHolder<CraftingRecipe>> recipes = new ArrayList<>();
		Ingredient wax = Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(ModTags.WAXING_ITEMS));
		for (Item item : copperItems()) {
			Ingredient ingredient = Ingredient.of(item);
			WaxOnRecipe waxOn = new WaxOnRecipe(ingredient, wax);
			ScrapeRecipe scrape = new ScrapeRecipe(ingredient);
			Identifier key = BuiltInRegistries.ITEM.getKey(item);
			for (WeatheringCopper.WeatherState age : WeatheringCopper.WeatherState.values()) {
				String state = age.getSerializedName();
				ItemStack input = agedStack(item, state);
				if (!wax.isEmpty()) {
					// Assemble with the real custom recipe so displayed results retain the same components.
					ItemStack waxItem = wax.items().findFirst().orElseThrow().value().getDefaultInstance();
					ItemStack output = waxOn.assemble(CraftingInput.of(2, 1, List.of(input, waxItem)));
					recipes.add(craftingDisplay(registration, displayId("wax_on", key, "/" + state), input, output, wax));
				}
				if (age != WeatheringCopper.WeatherState.UNAFFECTED) {
					ItemStack output = scrape.assemble(CraftingInput.of(1, 1, List.of(input)));
					recipes.add(craftingDisplay(registration, displayId("scrape", key, "/" + state), input, output, null));
				}
				ItemStack waxed = input.copy();
				ICopperItem.setWaxed(waxed, true);
				ItemStack output = scrape.assemble(CraftingInput.of(1, 1, List.of(waxed)));
				recipes.add(craftingDisplay(registration, displayId("wax_off", key, "/" + state), waxed, output, null));
			}
		}
		// These holders are JEI displays only; the game's recipes remain shapeless custom recipes.
		registration.addRecipes(RecipeTypes.CRAFTING, recipes);
	}

	private static RecipeHolder<CraftingRecipe> craftingDisplay(IRecipeRegistration registration, Identifier id, ItemStack input, ItemStack output, @Nullable Ingredient wax) {
		IJeiShapedRecipeBuilder builder = registration.getVanillaRecipeFactory()
			.createShapedRecipeBuilder(CraftingBookCategory.MISC, stackDisplay(output))
			.define('i', Ingredient.of(input.getItem()), stackDisplay(input))
			.pattern(wax == null ? "i" : "iw");
		if (wax != null) {
			builder.define('w', wax, new SlotDisplay.TagSlotDisplay(ModTags.WAXING_ITEMS));
		}
		return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, id), builder.build());
	}

	private static SlotDisplay stackDisplay(ItemStack stack) {
		return new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack));
	}

	private static List<Item> copperItems() {
		return BuiltInRegistries.ITEM.stream().filter(item -> ICopperItem.isCopperItem(item.getDefaultInstance())).toList();
	}

	private static ItemStack agedStack(Item item, String age) {
		ItemStack stack = item.getDefaultInstance();
		ICopperItem.setAge(stack, age);
		return stack;
	}

	private static Identifier displayId(String operation, Identifier item, String suffix) {
		return Identifier.fromNamespaceAndPath(EverythingCopper.MODID, "/" + operation + "/" + item.getNamespace() + "/" + item.getPath() + suffix);
	}
}

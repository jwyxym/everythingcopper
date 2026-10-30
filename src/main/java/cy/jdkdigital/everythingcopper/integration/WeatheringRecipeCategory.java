package cy.jdkdigital.everythingcopper.integration;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.crafting.recipe.WeatheringRecipe;
import cy.jdkdigital.everythingcopper.init.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluids;

public class WeatheringRecipeCategory implements IRecipeCategory<WeatheringRecipe> {
	private static final int WIDTH = 126;
	private static final int HEIGHT = 70;
	private final IDrawable background;
	private final IDrawable icon;

	public WeatheringRecipeCategory(IGuiHelper guiHelper) {
		Identifier texture = Identifier.fromNamespaceAndPath(EverythingCopper.MODID, "textures/gui/jei/weathering_recipe.png");
		background = guiHelper.drawableBuilder(texture, 0, 0, WIDTH, HEIGHT).setTextureSize(WIDTH, HEIGHT).build();
		icon = guiHelper.createDrawableItemLike(ModBlocks.WEATHERING_STATION.get());
	}

	@Override
	public Component getTitle() {
		return Component.translatable(EverythingCopper.MODID + ".recipe.weathering");
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public IRecipeType<WeatheringRecipe> getRecipeType() {
		return JeiPlugin.WEATHERING_RECIPE_TYPE;
	}

	@Override
	public Identifier getIdentifier(WeatheringRecipe recipe) {
		return recipe.id();
	}

	@Override
	public void draw(WeatheringRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		background.draw(graphics);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, WeatheringRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 33, 27).add(recipe.input()).setSlotName("input");
		builder.addSlot(RecipeIngredientRole.OUTPUT, 75, 27).add(recipe.output()).setSlotName("output");
		builder.addSlot(RecipeIngredientRole.INPUT, 0, 27)
			.add(Fluids.WATER, 100)
			.setFluidRenderer(100, false, 16, 16)
			.setSlotName("water");
	}
}

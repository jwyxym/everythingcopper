package cy.jdkdigital.everythingcopper.integration;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.crafting.recipe.WeatheringRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
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
import net.minecraft.world.item.Items;

public class WeatheringRecipeCategory implements IRecipeCategory<WeatheringRecipe> {
	private static final int WIDTH = 126;
	private static final int HEIGHT = 48;
	private final IDrawable arrow;
	private final IDrawable icon;

	public WeatheringRecipeCategory(IGuiHelper guiHelper) {
		arrow = guiHelper.getRecipeArrow();
		icon = guiHelper.createDrawableItemLike(Items.COPPER_BLOCK.weathering().unaffected());
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
		arrow.draw(graphics, 54, 15);
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, WeatheringRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 20, 16).add(recipe.input()).setStandardSlotBackground().setSlotName("input");
		builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 16).add(recipe.output()).setOutputSlotBackground().setSlotName("output");
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, WeatheringRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
		if (mouseX >= 54 && mouseX < 78 && mouseY >= 15 && mouseY < 32) {
			tooltip.add(Component.translatable(EverythingCopper.MODID + ".recipe.natural_weathering"));
		}
	}
}

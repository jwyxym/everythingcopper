package cy.jdkdigital.everythingcopper.integration;

import cy.jdkdigital.everythingcopper.init.ModTags;
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
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class CopperTreatmentRecipeCategory implements IRecipeCategory<CopperTreatmentRecipe> {
	private final IDrawable icon;
	private final IDrawable arrow;

	public CopperTreatmentRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableItemLike(Items.HONEYCOMB);
		arrow = guiHelper.getRecipeArrow();
	}

	@Override
	public IRecipeType<CopperTreatmentRecipe> getRecipeType() {
		return JeiPlugin.COPPER_TREATMENT_RECIPE_TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("everythingcopper.recipe.treatment");
	}

	@Override
	public int getWidth() {
		return 126;
	}

	@Override
	public int getHeight() {
		return 48;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public Identifier getIdentifier(CopperTreatmentRecipe recipe) {
		return recipe.id();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CopperTreatmentRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 29, 16).add(recipe.input()).setStandardSlotBackground().setSlotName("input");
		builder.addSlot(RecipeIngredientRole.OUTPUT, 99, 16).add(recipe.output()).setOutputSlotBackground().setSlotName("output");
		if (recipe.operation() == CopperTreatmentRecipe.Operation.WAX_ON) {
			builder.addSlot(RecipeIngredientRole.INPUT, 5, 16).add(new SlotDisplay.TagSlotDisplay(ModTags.WAXING_ITEMS))
				.setStandardSlotBackground().setSlotName("wax");
		}
	}

	@Override
	public void draw(CopperTreatmentRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
		arrow.draw(graphics, 62, 15);
	}

	@Override
	public void getTooltip(ITooltipBuilder tooltip, CopperTreatmentRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
		if (mouseX >= 62 && mouseX < 86 && mouseY >= 15 && mouseY < 32) {
			tooltip.add(Component.translatable("everythingcopper.recipe." + recipe.operation().path()));
		}
	}
}

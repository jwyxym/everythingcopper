package cy.jdkdigital.everythingcopper.init;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.effect.CopperPoisoning;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, EverythingCopper.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> COPPER_POISONING = EFFECTS.register("copper_poisoning", () -> new CopperPoisoning(MobEffectCategory.HARMFUL, 12741452));

	public static void modifyComponents(ModifyDefaultComponentsEvent event) {
		event.modify(Items.COPPER_NUGGET, (components, lookup, item) -> components
			.set(DataComponents.FOOD, new FoodProperties.Builder().nutrition(0).saturationModifier(0).alwaysEdible().build())
			.set(DataComponents.CONSUMABLE, Consumable.builder()
				.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(COPPER_POISONING, 1, 1), 1.0F)).build()));
	}

}

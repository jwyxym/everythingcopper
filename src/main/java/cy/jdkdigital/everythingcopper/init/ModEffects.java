package cy.jdkdigital.everythingcopper.init;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.effect.CopperPoisoning;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, EverythingCopper.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> COPPER_POISONING = EFFECTS.register("copper_poisoning", () -> new CopperPoisoning(MobEffectCategory.HARMFUL, 12741452));

}

package cy.jdkdigital.everythingcopper;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.everythingcopper.common.condition.LazyCondition;
import cy.jdkdigital.everythingcopper.event.EventHandler;
import cy.jdkdigital.everythingcopper.init.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("everythingcopper")
public class EverythingCopper
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "everythingcopper";

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MODID);
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<LazyCondition>> LAZY_CONDITION = CONDITION_CODECS.register("lazy", () -> LazyCondition.CODEC);

    public EverythingCopper(IEventBus modEventBus, ModContainer modContainer) {
        // TODO
        // cauldrons
        // bucket
        // dispenser behavior for minecart placement
        // fix minecart items
        // iron/copper bar intersecting borders
        // less noise on rails
        // outline waxed copper blocks when holding wax
        // kiln for smelting non-blast and non-smoker items
        // lightning rod extension
        // depth meter

        ModEffects.EFFECTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModLootModifiers.LOOT_SERIALIZERS.register(modEventBus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);
		ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
		CONDITION_CODECS.register(modEventBus);
		ModEntities.ENTITIES.addAlias(net.minecraft.resources.Identifier.fromNamespaceAndPath(MODID, "copper_golem"), net.minecraft.resources.Identifier.withDefaultNamespace("copper_golem"));
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
		ModCreativeTabs.TABS.register(modEventBus);

        modEventBus.addListener(EventHandler::buildContents);
    }

}

package cy.jdkdigital.everythingcopper.datagen;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.datagen.recipes.CopperRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = EverythingCopper.MODID)
public class DataProvider
{
    private DataProvider() {
    }

    @SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();

		gen.addProvider(true, new CopperRecipeProvider.Runner(gen.getPackOutput(), event.getLookupProvider()));
		gen.addProvider(true, new DataMapProvider(output, provider));
    }
}

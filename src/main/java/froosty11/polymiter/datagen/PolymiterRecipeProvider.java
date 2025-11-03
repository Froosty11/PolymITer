
        package froosty11.polymiter.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import froosty11.polymiter.item.ModItems;

public class PolymiterRecipeProvider extends FabricRecipeProvider {
    public PolymiterRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                // shaped: use separate .input calls for each key
                createShaped(RecipeCategory.MISC, ModItems.LASERVIOLETT_OVVE, 1)
                        .pattern("XYX")
                        .pattern("X X")
                        .pattern("X X")
                        .input('X', Items.PURPLE_WOOL)
                        .input('Y', ModItems.patch)
                        .criterion(hasItem(ModItems.LASERVIOLETT_OVVE), conditionsFromItem(ModItems.LASERVIOLETT_OVVE))
                        .criterion(hasItem(Items.PURPLE_WOOL), conditionsFromItem(Items.PURPLE_WOOL))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, ModItems.KISELBLA_OVVE, 1)
                        .pattern("XYX")
                        .pattern("X X")
                        .pattern("X X")
                        .input('X', Items.LIGHT_BLUE_WOOL)
                        .input('Y', ModItems.patch)
                        .criterion(hasItem(ModItems.KISELBLA_OVVE), conditionsFromItem(ModItems.KISELBLA_OVVE))
                        .criterion(hasItem(Items.LIGHT_BLUE_WOOL), conditionsFromItem(Items.LIGHT_BLUE_WOOL))
                        .offerTo(exporter);

                createShaped(RecipeCategory.MISC, ModItems.OCHRAROD_OVVE, 1)
                        .pattern("XYX")
                        .pattern("X X")
                        .pattern("X X")
                        .input('X', Items.RED_WOOL)
                        .input('Y', ModItems.patch)
                        .criterion(hasItem(ModItems.OCHRAROD_OVVE), conditionsFromItem(ModItems.OCHRAROD_OVVE))
                        .criterion(hasItem(Items.RED_WOOL), conditionsFromItem(Items.RED_WOOL))
                        .offerTo(exporter);

                // Shapeless recipe: apple + any potion (e.g. water bottle) -> nyckeln_drink
                createShapeless(RecipeCategory.MISC, ModItems.NYCKELN_DRINK, 1)
                        .input(Items.APPLE)
                        .input(Items.POTION)
                        .criterion(hasItem(Items.APPLE), conditionsFromItem(Items.APPLE))
                        .criterion(hasItem(Items.POTION), conditionsFromItem(Items.POTION))
                        .offerTo(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "PolymiterRecipeProvider";
    }
}

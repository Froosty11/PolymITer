package froosty11.polymiter.util;

import froosty11.polymiter.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public class PolymiterLoottableModifiers {
    public static void modifyLoottables() {
        LootTableEvents.MODIFY.register((registryKey, builder, lootTableSource, wrapperLookup) -> {
            boolean isTarget = registryKey.equals(LootTables.SIMPLE_DUNGEON_CHEST)
                    || registryKey.equals(LootTables.DESERT_PYRAMID_CHEST)
                    || registryKey.equals(LootTables.JUNGLE_TEMPLE_CHEST);

            if (isTarget) {
                // Banner pattern (rare)
                LootPool.Builder bannerPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.25f))
                        .with(ItemEntry.builder(ModItems.PIRKKO_BANNER_PATTERN));
                builder.pool(bannerPool);

                // Patches: more common and can drop in pairs (uses the generic 'patch' item)
                LootPool.Builder patchPool = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(2))
                        .conditionally(RandomChanceLootCondition.builder(0.5f))
                        .with(ItemEntry.builder(ModItems.patch));
                builder.pool(patchPool);
            }
        });
    }
}

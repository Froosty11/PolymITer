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
            if (registryKey.equals(LootTables.SIMPLE_DUNGEON_CHEST)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.25f)) // 12% chance
                        .with(ItemEntry.builder(ModItems.PIRKKO_BANNER_PATTERN));

                builder.pool(poolBuilder);
            }
        });
    }

}

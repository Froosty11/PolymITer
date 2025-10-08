package froosty11.polymiter.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import froosty11.polymiter.Polymiter;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class ModItems {
    public static final Item TEST_ITEM = register("test_item", Item::new, new Item.Settings());

    public static final TagKey<BannerPattern> PIRKKO_PATTERN_TAG = TagKey.of(RegistryKeys.BANNER_PATTERN, Identifier.of(Polymiter.MOD_ID, "pirkko"));
    public static final Item PIRKKO_BANNER_PATTERN = register("pirkko_banner_pattern", SimplePolymerItem::new, new SimplePolymerItem.Settings().maxCount(1).rarity(Rarity.RARE));

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Polymiter.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void initialize() {
    }
}

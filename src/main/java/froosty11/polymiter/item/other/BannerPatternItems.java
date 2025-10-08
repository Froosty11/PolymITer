package froosty11.polymiter.item.other;

import froosty11.polymiter.Polymiter;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.component.DataComponentTypes;;

public class BannerPatternItems {
    public static Item PIRKKO_BANNER_PATTERN;

    public static void registerAll() {
        Polymiter.LOGGER.info("Registering banner pattern items for " + Polymiter.MOD_ID);

        PIRKKO_BANNER_PATTERN = Registry.register(
                Registries.ITEM,
                Identifier.of(Polymiter.MOD_ID, "pirkko_banner_pattern"),
                new Item(
                        new Item.Settings()
                                .maxCount(1)
                                .rarity(Rarity.RARE)
                                .component(DataComponentTypes.PROVIDES_BANNER_PATTERNS, PolymiterBannerPatternTags.PIRKKO_PATTERN_ITEM)
                )
        );
    }
}

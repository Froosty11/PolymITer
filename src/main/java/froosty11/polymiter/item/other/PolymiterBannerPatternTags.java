package froosty11.polymiter.item.other;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class PolymiterBannerPatternTags {

    public static final TagKey<BannerPattern> PIRKKO_PATTERN_ITEM = of("pattern_item/pirkko");

    private PolymiterBannerPatternTags() {}

    private static TagKey<BannerPattern> of(String id) {
        // Use Identifier.of(namespace, path) for modded tags
        return TagKey.of(RegistryKeys.BANNER_PATTERN, Identifier.of("polymiter", id));
    }
}
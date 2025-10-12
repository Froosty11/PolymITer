package froosty11.polymiter.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import froosty11.polymiter.Polymiter;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.item.Item;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModItems {

    public static final List<Item> ITEMS = new ArrayList<>();

    // The tag that points to your banner_pattern tag file:
    // data/polymiter/tags/banner_pattern/pattern_item/pirkko.json
    public static final TagKey<BannerPattern> PIRKKO_PATTERN_TAG =
            TagKey.of(RegistryKeys.BANNER_PATTERN, Identifier.of(Polymiter.MOD_ID, "pattern_item/pirkko"));

    public static final TagKey<Item> REPAIRS_OVVE_ARMOR = TagKey.of(RegistryKeys.ITEM, Identifier.of(Polymiter.MOD_ID, "repairs_ovve_armor"));

    // Register your custom banner pattern item
    public static final Item PIRKKO_BANNER_PATTERN = register(
            "pirkko_banner_pattern",
            SimplePolymerItem::new,
            new SimplePolymerItem.Settings()
                    .maxCount(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponentTypes.PROVIDES_BANNER_PATTERNS, PIRKKO_PATTERN_TAG)
    );

    public static final Item SPIKEN_DRINK = register(
            "spiken_drink",
            DrinkItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.UNCOMMON)
            );
    public static final Item slaggan_drink = register(
            "slaggan_drink",
            DrinkItem::new,
            new Item.Settings()
                    .maxCount(1)
                    .rarity(Rarity.UNCOMMON)
    );
    public static final Item nyckeln_drink = register("nyckeln_drink",
            SimplePolymerItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(DataComponentTypes.CONSUMABLE,
                    new ConsumableComponent(32 / 20f, UseAction.DRINK, SoundEvents.ENTITY_GENERIC_DRINK, false, List.of())));


    public static final Item KISELBLA_OVVE = registerItem("kiselbla_ovve",
            (s) -> new PolymerArmorItem(ModArmorMaterial.KISELBLA_OVVE, EquipmentType.LEGGINGS, s.fireproof()));

    public static final Item patch = register(
            "patch",
            SimplePolymerItem::new,
            new SimplePolymerItem.Settings().maxCount(64).rarity(Rarity.RARE)
    );

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Polymiter.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }
    public static Item registerItem(String name, Function<Item.Settings, Item> function) {
        var id = Identifier.of(Polymiter.MOD_ID, name);
        var item = function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
        ITEMS.add(item);
        Registry.register(Registries.ITEM, id, item);
        return item;
    }



    public static void initialize() {
    }
}
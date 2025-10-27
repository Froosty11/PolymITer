// java
// File: src/main/java/froosty11/polymiter/item/ModItems.java
package froosty11.polymiter.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.core.api.other.PolymerPotion;
import eu.pb4.polymer.core.api.other.SimplePolymerPotion;
import froosty11.polymiter.Polymiter;
import froosty11.polymiter.statuseffects.ModStatusEffects;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ModItems {

    public static final List<Item> ITEMS = new ArrayList<>();


    //TAGS
    // data/polymiter/tags/banner_pattern/pattern_item/pirkko.json
    public static final TagKey<BannerPattern> PIRKKO_PATTERN_TAG = TagKey.of(RegistryKeys.BANNER_PATTERN, Identifier.of(Polymiter.MOD_ID, "pattern_item/pirkko"));
    public static final TagKey<Item> REPAIRS_OVVE_ARMOR = TagKey.of(RegistryKeys.ITEM, Identifier.of(Polymiter.MOD_ID, "repairs_ovve_armor"));

    //ITEMGROUPS
    public static final RegistryKey<ItemGroup> POLYMITER_IG_KEY =
            RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(Polymiter.MOD_ID, "polymiter"));

    // Build a server-side Polymer item group
    public static final ItemGroup POLYMITER_IG = PolymerItemGroupUtils.builder()
            .icon(() -> new ItemStack(ModItems.KISELBLA_OVVE))
            .displayName(Text.translatable("main_group.polymiter"))
            .build();
//banner patterns
    public static final Item PIRKKO_BANNER_PATTERN = registerItem("pirkko_banner_pattern",
            SimplePolymerItem::new, new SimplePolymerItem.Settings().maxCount(1)
                    .rarity(Rarity.RARE).component(DataComponentTypes.PROVIDES_BANNER_PATTERNS, PIRKKO_PATTERN_TAG));
    //Consumable Items
    public static final Item SPIKEN_DRINK = registerItem("spiken_drink", DrinkItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item SLAGGAN_DRINK = registerItem("slaggan_drink",
            DrinkItem::new, new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item NYCKELN_DRINK = registerItem("nyckeln_drink", SimplePolymerItem::new,
            new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON).component(DataComponentTypes.CONSUMABLE,
                    new ConsumableComponent(32 / 20f, UseAction.DRINK, SoundEvents.ENTITY_GENERIC_DRINK, false, List.of())));

    public static final SimplePolymerPotion ALCOHOL_POTION = Registry.register(Registries.POTION, Identifier.of(Polymiter.MOD_ID, "alcohol_potion"),
            new SimplePolymerPotion("alcohol", new StatusEffectInstance(ModStatusEffects.INEBRIATION, 6000, 0)));

    //Material Items
    public static final Item patch = registerItem("patch", SimplePolymerItem::new,
            new SimplePolymerItem.Settings().maxCount(64).rarity(Rarity.RARE));
    /*
    Armor Items
     */
    public static final Item KISELBLA_OVVE = registerItem("kiselbla_ovve",
            (s) -> new PolymerArmorItem(ModArmorMaterial.KISELBLA_OVVE, EquipmentType.LEGGINGS, s.fireproof().rarity(Rarity.EPIC).maxCount(1)), null);
    public static final Item OCHRAROD_OVVE = registerItem("red_ovve",
            (s) -> new PolymerArmorItem(ModArmorMaterial.RED_OVVE, EquipmentType.LEGGINGS, s.fireproof().rarity(Rarity.EPIC).maxCount(1)), null);
    public static final Item LASERVIOLETT_OVVE = registerItem("violet_ovve",
            (s) -> new PolymerArmorItem(ModArmorMaterial.VIOLET_OVVE, EquipmentType.LEGGINGS, s.fireproof().rarity(Rarity.EPIC).maxCount(1)), null);

    /**
     * Adds to the Custom Itemgroup "POLYMITER" automatically if group is null.
     * Registers an item with the given name and item factory. If settings is null a new Item.Settings() is created.
     * @param name name of item. Will be namespaced to the mod id automatically.
     * @param function Usually SimplePolymerItem::new or your custom item class constructor
     * @param settings Item settings (nullable)
     * @return the registered item
     */
    public static Item registerItem(String name, Function<Item.Settings, Item> function, Item.Settings settings, @Nullable ItemGroup group)
    {
        var id = Identifier.of(Polymiter.MOD_ID, name);
        Item.Settings s = (settings == null) ? new Item.Settings() : settings;
        Item item = function.apply(s.registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
        ITEMS.add(item);
        Registry.register(Registries.ITEM, id, item);

        if (group == null) {
            ItemGroupEvents.modifyEntriesEvent(POLYMITER_IG_KEY).register((entries) -> entries.add(item));
        }

        return item;
    }

    public static Item registerItem(String name, Function<Item.Settings, Item> function, Item.Settings settings) {
        return registerItem(name, function, settings, null);
    }

    public static void initialize() {
        // Register the server-side Polymer item group so Polymer-compatible clients and the /polymer creative command can see it
        PolymerItemGroupUtils.registerPolymerItemGroup(Identifier.of(Polymiter.MOD_ID, "polymiter"), POLYMITER_IG);

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Potions.WATER,
                    // Ingredient
                    Items.POTATO,
                    // Output potion.
                    Registries.POTION.getEntry(ALCOHOL_POTION)
            );
        });
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerItemRecipe(
                    // Input potion.
                    Registries.POTION.getEntry(ALCOHOL_POTION),
                    // Ingredient
                    Items.SUSPICIOUS_STEW,
                    // Output potion.
                    ModItems.SLAGGAN_DRINK.
            );
        });
    }
}

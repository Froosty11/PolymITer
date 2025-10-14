package froosty11.polymiter.item;

import froosty11.polymiter.Polymiter;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.EnumMap;

public interface ModArmorMaterial {
    ArmorMaterial KISELBLA_OVVE = new ArmorMaterial(69, Util.make(new EnumMap<>(EquipmentType.class), (map) ->  {
        map.put(EquipmentType.LEGGINGS, 3);
    }), 12, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,3.0f, 0.25f, TagKey.of(RegistryKeys.ITEM, Identifier.of("polymiter", "repairs_ovve_armor")),
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Polymiter.MOD_ID, "kiselbla_ovve")));


    ArmorMaterial VIOLET_OVVE = new ArmorMaterial(69, Util.make(new EnumMap<>(EquipmentType.class), (map) ->  {
        map.put(EquipmentType.LEGGINGS, 3);
    }), 12, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,3.0f, 0.25f, TagKey.of(RegistryKeys.ITEM, Identifier.of("polymiter", "repairs_ovve_armor")),
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Polymiter.MOD_ID, "violet_ovve")));

    ArmorMaterial RED_OVVE = new ArmorMaterial(69, Util.make(new EnumMap<>(EquipmentType.class), (map) ->  {
        map.put(EquipmentType.LEGGINGS, 3);
    }), 12, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,3.0f, 0.25f, TagKey.of(RegistryKeys.ITEM, Identifier.of("polymiter", "repairs_ovve_armor")),
            RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of(Polymiter.MOD_ID, "red_ovve")));

    static void register() {}
}

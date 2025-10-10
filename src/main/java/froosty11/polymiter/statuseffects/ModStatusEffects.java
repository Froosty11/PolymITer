package froosty11.polymiter.statuseffects;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModStatusEffects{
    public static final RegistryEntry<StatusEffect> INEBRIATION
            = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("polymiter", "inebriation"), new InebriationStatusEffect());

    public static void initialize() {

    }
}

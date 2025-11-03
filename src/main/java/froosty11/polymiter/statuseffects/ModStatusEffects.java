package froosty11.polymiter.statuseffects;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModStatusEffects{
    public static final RegistryEntry<StatusEffect> INEBRIATION_SLAGGAN
            = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("polymiter", "inebriation_slaggan"), new InebriationStatusEffect(true));

    public static final RegistryEntry<StatusEffect> INEBRIATION_SPIKEN
            = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of("polymiter", "inebriation_spiken"), new InebriationStatusEffect(false));

    public static void initialize() {

    }
}

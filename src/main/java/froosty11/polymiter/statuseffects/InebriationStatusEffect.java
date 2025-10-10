package froosty11.polymiter.statuseffects;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import froosty11.polymiter.Polymiter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import xyz.nucleoid.packettweaker.PacketContext;

public class InebriationStatusEffect extends StatusEffect implements PolymerStatusEffect {
    private boolean isSläggan = false;
    private int duration;
    private boolean isSlowness;
    protected InebriationStatusEffect(boolean isSläggan) {
        super(StatusEffectCategory.BENEFICIAL, isSläggan ? 0xFF69B4 : 0x8A2BE2); // Pink for Släggan, BlueViolet for regular drunk
    }

    protected InebriationStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFF69B4); // Pink for Släggan, BlueViolet for regular drunk
    }
    @Override
    public void onApplied(LivingEntity entity, int amplifier){
        isSlowness = entity.getRandom().nextDouble() < ( isSläggan ? 0.75 : 0.5 ); // 75% chance for Släggan, 50% for regular drunk
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        this.duration = duration;
        return true; // Apply effect every tick
    }

    @Override
    public StatusEffect getPolymerReplacement(StatusEffect potion, PacketContext context) {
        return StatusEffects.LUCK.value();
    }
    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        //stacking effect system. each level of amplify increases the amount of effects you get.
        // at amplification 1 you get slowness 1 OR speed 1 depending on a coinflip (isSlowness)
        // at amplification 2 you get the slowness/speed but also nausea
        // at amplification 3 you get slowness/speed, nausea and weakness/strength based on the other coinflip !isSlowness
        // at amplification 4 you instantly die to new death msg.
        switch(amplifier) {
            case 0:
                if (isSlowness) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, true, false, false));
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 0, true, false, false));
                }
                break;
            case 1:
                if (isSlowness) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, true, false, false));
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 0, true, false, false));
                }
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, duration, 0, true, false, false));
                break;
            case 2:
                if (isSlowness) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 1, true, false, false));
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 1, true, false, false));
                }
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, duration, 1, true, false, false));
                if (!isSlowness) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, duration, 0, true, false, false));
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, duration, 0, true, false, false));
                }
                break;
            case 3:
                entity.setHealth(0.0F);
                break;
        }
        return true;
    }
}

package froosty11.polymiter.statuseffects;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import froosty11.polymiter.Polymiter;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.particle.ParticleTypes;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Map;
import java.util.WeakHashMap;

public class InebriationStatusEffect extends StatusEffect implements PolymerStatusEffect {
    // per-entity runtime data (isSlaggan and isSlowness decisions)
    private static final Map<LivingEntity, EffectData> ACTIVE = new WeakHashMap<>();

    private static class EffectData {
        final boolean isSlaggan; // chosen by the status effect variant (constructor)
        final boolean isSlowness; // coinflip per-application
        int duration;

        EffectData(boolean isSlaggan, boolean isSlowness, int duration) {
            this.isSlaggan = isSlaggan;
            this.isSlowness = isSlowness;
            this.duration = duration;
        }
    }

    private final boolean isSlagganVariant;

    protected InebriationStatusEffect(boolean isSlaggan) {
        super(StatusEffectCategory.BENEFICIAL, isSlaggan ? 0x84c195 : 0x9e1515); // Pink for Slaggan, BlueViolet for Spiken
        this.isSlagganVariant = isSlaggan;
    }

    protected InebriationStatusEffect() {
        this(true);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier){
        // decide whether this application yields slowness or speed for this entity
        boolean isSlowness = entity.getRandom().nextDouble() < (this.isSlagganVariant ? 0.75 : 0.5);
        ACTIVE.put(entity, new EffectData(this.isSlagganVariant, isSlowness, 0));
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // update stored duration for the entity if present
        // called by the status system — we don't have the entity here, so we'll update duration inside applyUpdateEffect
        return true; // apply every tick
    }

    @Override
    public StatusEffect getPolymerReplacement(StatusEffect potion, PacketContext context) {
        // Keep showing the client as Luck so vanilla clients render a known effect; polymer will ensure the client sees Luck.
        return StatusEffects.LUCK.value();
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer)
    {
        super.onRemoved(attributeContainer);
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        EffectData data = ACTIVE.get(entity);
        if (data == null) {
            // fallback: default behavior
            boolean fallbackSlowness = entity.getRandom().nextDouble() < 0.5;
            data = new EffectData(false, fallbackSlowness, 0);
            ACTIVE.put(entity, data);
        }

        // try to use the current instance duration from the entity's StatusEffectInstance
        StatusEffectInstance inst = null;
        if (this.isSlagganVariant) {
            inst = entity.getStatusEffect(ModStatusEffects.INEBRIATION_SLAGGAN);
        } else {
            inst = entity.getStatusEffect(ModStatusEffects.INEBRIATION_SPIKEN);
        }

        int duration = (inst != null) ? inst.getDuration() : data.duration;
        data.duration = duration;

        // Apply sub-effects based on amplifier and stored flags
        switch(amplifier) {
            case 0 -> {
                if (data.isSlowness) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, true, false, false));
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 0, true, false, false));
                }
            }
            case 1 -> {
                if (data.isSlowness) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, true, false, false));
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 0, true, false, false));
                }
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, duration, 0, true, false, false));
            }
            case 2 -> {
                if (data.isSlowness) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 1, true, false, false));
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 1, true, false, false));
                }
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, duration, 1, true, false, false));
                if (!data.isSlowness) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, duration, 0, true, false, false));
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, duration, 0, true, false, false));
                }
            }
            case 3 -> {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 0, true, false, false));
                if (entity.getEntityWorld() instanceof ServerWorld serverWorld) {
                    if (entity instanceof ServerPlayerEntity srvPlayer) {
                        srvPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("You wake up with a splitting headache...").getWithStyle(Style.EMPTY.withBold(true).withColor(0xFF0F0F)).getFirst()));
                    }
                    while(true) {
                        double d = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 500.0D;
                        double e = net.minecraft.util.math.MathHelper.clamp(entity.getY() + (entity.getRandom().nextDouble() - 0.5D) * 500.0D, serverWorld.getBottomY(), (serverWorld.getBottomY() + serverWorld.getLogicalHeight() - 1));
                        double f = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 500.0D;
                        if (entity.hasVehicle()) {
                            entity.stopRiding();
                        }

                        net.minecraft.util.math.Vec3d vec3d = entity.getEntityPos();
                        if (entity.teleport(d, e, f, false)) {
                            serverWorld.emitGameEvent(net.minecraft.world.event.GameEvent.TELEPORT, vec3d, net.minecraft.world.event.GameEvent.Emitter.of(entity));
                            serverWorld.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_PARROT_FLY, net.minecraft.sound.SoundCategory.PLAYERS);
                            entity.onLanding();
                            break;
                        }
                    }
                    entity.clearStatusEffects();
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, duration, 0, true, false, false));
                }
            }
        }
        return true;

    }
}

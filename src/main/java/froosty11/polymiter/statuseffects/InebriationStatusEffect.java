package froosty11.polymiter.statuseffects;

import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import froosty11.polymiter.Polymiter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.Collection;

public class InebriationStatusEffect extends StatusEffect implements PolymerStatusEffect {
    private boolean isSlaggan = false;
    private int duration;
    private boolean isSlowness;

    protected InebriationStatusEffect(boolean isSlaggan) {
        super(StatusEffectCategory.BENEFICIAL, isSlaggan ? 0xFF69B4 : 0x8A2BE2); // Pink for Slaggan, BlueViolet for regular drunk
        this.isSlaggan = isSlaggan;
    }

    protected InebriationStatusEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFF69B4);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier){
        isSlowness = entity.getRandom().nextDouble() < ( isSlaggan ? 0.75 : 0.5 ); // 75% chance for Slaggan, 50% for regular drunk
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
    public void onRemoved(AttributeContainer attributeContainer)
    {
        super.onRemoved(attributeContainer);
    }

    @Override
    public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {
        //Polymiter.DEBUG.info("Inebriation effect tick for entity: {} with amplifier: {} and duration: {}", entity.getName().getString(), amplifier, duration);
        // stacking effect system. each level of amplify increases the amount of effects you get.
        // at amplification 1 you get slowness 1 OR speed 1 depending on a coinflip (isSlowness)
        // at amplification 2 you get the slowness/speed but also nausea
        // at amplification 3 you get slowness/speed, nausea and weakness/strength based on the other coinflip !isSlowness
        // at amplification 4 you instantly die to new death msg.
        switch(amplifier) {
            case 0 -> {
                if (isSlowness) {
                    if(entity.isPlayer() && entity instanceof LivingEntity) {
                        ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, true, false, false));
                    }
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 0, true, false, false));
                }
            }
            case 1 -> {
                if (isSlowness) {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, 0, true, false, false));
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, duration, 0, true, false, false));
                }
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, duration, 0, true, false, false));
            }
            case 2 -> {
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
            }
            case 3 -> {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 0, true, false, false));
                if (entity.getEntityWorld() instanceof ServerWorld serverWorld) {
                    ServerPlayerEntity srvPlayer = (ServerPlayerEntity) entity;
                    srvPlayer.networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("You wake up with a splitting headache...").getWithStyle(Style.EMPTY.withBold(true).withColor(0xFF0F0F)).getFirst()));
                    while(true) {
                        double d = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 500.0D;
                        double e = net.minecraft.util.math.MathHelper.clamp(entity.getY() + (entity.getRandom().nextDouble() - 0.5D) * 500.0D, (double) serverWorld.getBottomY(), (double) (serverWorld.getBottomY() + serverWorld.getLogicalHeight() - 1));
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

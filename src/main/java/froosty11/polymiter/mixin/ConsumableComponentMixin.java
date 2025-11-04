package froosty11.polymiter.mixin;

import froosty11.polymiter.item.ModItems;
import froosty11.polymiter.statuseffects.ModStatusEffects;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.WeakHashMap;

@Mixin(ConsumableComponent.class)
public class ConsumableComponentMixin {
    static {
        System.out.println("[DEBUG] ConsumableComponentMixin CLASS LOADED!");
    }

    // track amplifier (or -1 if none) that existed before consumption
    @Unique
    private static final Map<LivingEntity, Integer> prevInebAmplifier = new WeakHashMap<>();
    // track whether the previous inebriation effect was Slaggan (true) or Spiken (false)
    @Unique
    private static final Map<LivingEntity, Boolean> prevInebWasSlaggan = new WeakHashMap<>();
    // remember which potion (if any) was consumed so TAIL can rely on it even if the stack is mutated
    @Unique
    private static final Map<LivingEntity, RegistryEntry<Potion>> consumedPotion = new WeakHashMap<>();

    @Inject(method = "finishConsumption", at = @At("HEAD"))
    private void beforeFinishConsumption(World world, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        var currentSlag = user.getStatusEffect(ModStatusEffects.INEBRIATION_SLAGGAN);
        var currentSpik = user.getStatusEffect(ModStatusEffects.INEBRIATION_SPIKEN);

        if (currentSlag != null) {
            prevInebAmplifier.put(user, currentSlag.getAmplifier());
            prevInebWasSlaggan.put(user, true);
        } else if (currentSpik != null) {
            prevInebAmplifier.put(user, currentSpik.getAmplifier());
            prevInebWasSlaggan.put(user, false);
        } else {
            prevInebAmplifier.put(user, -1);
            prevInebWasSlaggan.remove(user);
        }

        // Capture potion info while the original stack is still intact
        if (stack.getItem() instanceof PotionItem) {
            PotionContentsComponent potionContents = stack.get(DataComponentTypes.POTION_CONTENTS);
            if (potionContents != null) {
                var entry = potionContents.potion().orElse(null);
                if (entry != null) {
                    consumedPotion.put(user, entry);
                }
            }
        }
    }

    @Inject(method = "finishConsumption", at = @At("TAIL"))
    private void applyStackingInebriation(World world, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        //System.out.println("[DEBUG] finishConsumption called for item: " + stack.getItem().getClass().getSimpleName());

        Integer prevAmpObj = prevInebAmplifier.remove(user);
        prevInebWasSlaggan.remove(user);
        if (prevAmpObj == null) {
            System.out.println("[DEBUG] No previous amplifier recorded, skipping stacking logic");
            // also clean up any captured potion entry
            consumedPotion.remove(user);
            return;
        }
        int prevAmp = prevAmpObj;

        // Use the potion info captured at HEAD instead of relying on the possibly-mutated stack
        RegistryEntry<Potion> potionEntry = consumedPotion.remove(user);
        if (potionEntry == null) {
            //System.out.println("[DEBUG] Not a PotionItem (or no potion info captured), skipping");
            return;
        }

        Potion potion = potionEntry.value();
        //System.out.println("[DEBUG] Potion: " + potion);

        if (potion == ModItems.SPIKEN_POTION || potion == ModItems.SLAGGAN_POTION || potion == ModItems.ALCOHOL_POTION) {
            if (prevAmp >= 0) {
                int newAmp = Math.min(prevAmp + 1, 4);
                // prefer current duration if potion applied one; otherwise fallback to default
                var currentSlag = user.getStatusEffect(ModStatusEffects.INEBRIATION_SLAGGAN);
                var currentSpik = user.getStatusEffect(ModStatusEffects.INEBRIATION_SPIKEN);
                int remainingDuration = 4000;
                if (currentSlag != null) remainingDuration = currentSlag.getDuration();
                else if (currentSpik != null) remainingDuration = currentSpik.getDuration();

                // Choose which status effect variant to apply based on the potion just consumed
                if (potion == ModItems.SLAGGAN_POTION) {
                    user.addStatusEffect(new StatusEffectInstance(ModStatusEffects.INEBRIATION_SLAGGAN, remainingDuration, newAmp));
                } else {
                    // ALCOHOL and SPIKEN map to the Spiken variant
                    user.addStatusEffect(new StatusEffectInstance(ModStatusEffects.INEBRIATION_SPIKEN, remainingDuration, newAmp));
                }

                //System.out.println("[DEBUG] Prev amp: " + prevAmp + ", New amp: " + newAmp + ", Duration: " + remainingDuration);
            } else {
                //System.out.println("[DEBUG] No existing inebriation before consumption — not stacking");
            }
        }
    }
}

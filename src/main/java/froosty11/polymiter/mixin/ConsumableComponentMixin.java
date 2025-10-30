// java
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
    private static final Map<LivingEntity, Integer> prevInebAmplifier = new WeakHashMap<>();

    @Inject(method = "finishConsumption", at = @At("HEAD"))
    private void beforeFinishConsumption(World world, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        var current = user.getStatusEffect(ModStatusEffects.INEBRIATION);
        prevInebAmplifier.put(user, current == null ? -1 : current.getAmplifier());
    }

    @Inject(method = "finishConsumption", at = @At("TAIL"))
    private void applyStackingInebriation(World world, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        System.out.println("[DEBUG] finishConsumption called for item: " + stack.getItem().getClass().getSimpleName());

        Integer prevAmpObj = prevInebAmplifier.remove(user);
        if (prevAmpObj == null) {
            // fail-safe: no stored state — do nothing
            System.out.println("[DEBUG] No previous amplifier recorded, skipping stacking logic");
            return;
        }
        int prevAmp = prevAmpObj;

        if (!(stack.getItem() instanceof PotionItem)) {
            System.out.println("[DEBUG] Not a PotionItem, skipping");
            return;
        }

        PotionContentsComponent potionContents = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (potionContents == null) {
            System.out.println("[DEBUG] No potion contents found");
            return;
        }

        RegistryEntry<Potion> potionEntry = potionContents.potion().orElse(null);
        if (potionEntry == null) {
            System.out.println("[DEBUG] No potion entry found");
            return;
        }

        Potion potion = potionEntry.value();
        System.out.println("[DEBUG] Potion: " + potion);

        if (potion == ModItems.SPIKEN_POTION || potion == ModItems.SLAGGAN_POTION || potion == ModItems.ALCOHOL_POTION) {
            // Only stack if there was an existing inebriation before drinking
            if (prevAmp >= 0) {
                int newAmp = Math.min(prevAmp + 1, 4);
                // prefer current duration if potion applied one; otherwise fallback to default
                var current = user.getStatusEffect(ModStatusEffects.INEBRIATION);
                int remainingDuration = (current != null) ? current.getDuration() : 4000;
                System.out.println("[DEBUG] Prev amp: " + prevAmp + ", New amp: " + newAmp + ", Duration: " + remainingDuration);
                user.addStatusEffect(new StatusEffectInstance(ModStatusEffects.INEBRIATION, remainingDuration, newAmp));
            } else {
                System.out.println("[DEBUG] No existing inebriation before consumption — not stacking");
            }
        }
    }
}

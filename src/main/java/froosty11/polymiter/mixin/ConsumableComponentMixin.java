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

@Mixin(ConsumableComponent.class)
public class ConsumableComponentMixin {
    static {
        System.out.println("[DEBUG] ConsumableComponentMixin CLASS LOADED!");
    }

    @Inject(method = "finishConsumption", at = @At("TAIL"))
    private void applyStackingInebriation(World world, LivingEntity user, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        System.out.println("[DEBUG] finishConsumption called for item: " + stack.getItem().getClass().getSimpleName());

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
        System.out.println("[DEBUG] Is Spiken? " + (potion == ModItems.SPIKEN_POTION));

        if (potion == ModItems.SPIKEN_POTION || potion == ModItems.SLAGGAN_POTION || potion == ModItems.ALCOHOL_POTION) {
            StatusEffectInstance current = user.getStatusEffect(ModStatusEffects.INEBRIATION);
            System.out.println("[DEBUG] Current inebriation effect: " + current);

            if (current != null) {
                int oldAmp = current.getAmplifier();
                int newAmp = Math.min(oldAmp + 1, 4);
                int remainingDuration = current.getDuration();
                System.out.println("[DEBUG] Old amp: " + oldAmp + ", New amp: " + newAmp);
                user.addStatusEffect(new StatusEffectInstance(ModStatusEffects.INEBRIATION, remainingDuration, newAmp));
            } else {
                System.out.println("[DEBUG] No existing inebriation effect");
            }
        }
    }
}

package froosty11.polymiter.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import froosty11.polymiter.statuseffects.ModStatusEffects;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class DrinkItem extends Item implements PolymerItem {
    public DrinkItem(Settings settings) {
        super(settings.maxCount(1).component(DataComponentTypes.CONSUMABLE, new ConsumableComponent(32 / 20f, UseAction.DRINK, SoundEvents.ENTITY_GENERIC_DRINK, false, List.of())));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {

        return 48;
    }
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        super.finishUsing(stack, world, user);
        if (user instanceof ServerPlayerEntity serverPlayer) {
            Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (user instanceof PlayerEntity) {
            stack.decrement(1);
        }

        StatusEffectInstance current = user.getStatusEffect(ModStatusEffects.INEBRIATION);
        if (current != null) {
            int newAmp = Math.min(current.getAmplifier() + 1, 4);
            user.addStatusEffect(new StatusEffectInstance(ModStatusEffects.INEBRIATION, 20 * 60, newAmp));
        } else {
            user.addStatusEffect(new StatusEffectInstance(ModStatusEffects.INEBRIATION, 20 * 60, 0));
        }
        return stack.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : stack;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.TRIAL_KEY;
    }
}
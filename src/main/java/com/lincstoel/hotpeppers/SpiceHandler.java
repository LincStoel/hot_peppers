package com.lincstoel.hotpeppers;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = HotPeppers.MOD_ID)
public class SpiceHandler {

    private static final int BASE_REGEN_INTERVAL_TICKS = 80;
    private static final float REGEN_EXHAUSTION_COST = 6.0F;

    // Covers plain peppers and spicy-modified food alike: both carry their heat as doses of the same effect.
    @SubscribeEvent
    static void onFinishEating(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getItem();
        int doses = (stack.is(HotPeppers.HOT_PEPPER) ? 1 : 0) + Spice.getSpiceLevel(stack);
        if (doses > 0) {
            Spice.apply(player, doses);
        }
    }

    // Spicy regen burns straight through hunger the way vanilla's saturation-fueled regen burns
    // through saturation: it doesn't stop at the "sated" threshold vanilla natural regen normally
    // requires, only when the hunger bar itself is empty (or the effect runs out).
    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        MobEffectInstance spicy = player.getEffect(HotPeppers.SPICY);
        if (spicy == null || !player.isHurt()) {
            return;
        }
        if (!player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            return;
        }

        FoodData foodData = player.getFoodData();
        if (foodData.getFoodLevel() <= 0) {
            return;
        }

        int level = spicy.getAmplifier() + 1;
        int interval = BASE_REGEN_INTERVAL_TICKS / level;
        if (player.tickCount % interval == 0) {
            player.heal(1.0F);
            foodData.addExhaustion(REGEN_EXHAUSTION_COST);
        }
    }
}

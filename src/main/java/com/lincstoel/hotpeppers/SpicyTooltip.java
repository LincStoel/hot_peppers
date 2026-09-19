package com.lincstoel.hotpeppers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = HotPeppers.MOD_ID)
public class SpicyTooltip {

    @SubscribeEvent
    static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.is(HotPeppers.HOT_PEPPER)) {
            event.getToolTip().add(Component.translatable("item.hot_peppers.hot_pepper.tooltip.spicy")
                    .withStyle(ChatFormatting.GOLD)
                    .append(Component.translatable("item.hot_peppers.hot_pepper.tooltip.warning")
                            .withStyle(ChatFormatting.WHITE)));
        }

        int level = Spice.getSpiceLevel(stack);
        if (level > 0) {
            event.getToolTip().add(Component.translatable("item.hot_peppers.spicy_tooltip." + level)
                    .withStyle(ChatFormatting.RED));
        }
    }
}

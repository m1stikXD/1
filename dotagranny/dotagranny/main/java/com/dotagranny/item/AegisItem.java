package com.dotagranny.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Эгида бессмертия. Сама по себе ничего не делает —
 * пока она лежит в инвентаре, смерть игрока отменяется (см. ForgeEvents).
 */
public class AegisItem extends Item {

    public AegisItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.dotagranny.aegis"));
    }
}

package com.dotagranny.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** ПКМ: мгновенный рывок (блинк) вперёд, куда смотрит игрок. */
public class BlinkDaggerItem extends Item {
    private static final double RANGE = 14.0D;
    private static final int COOLDOWN_TICKS = 160; // 8 секунд

    public BlinkDaggerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            Vec3 start = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 end = start.add(look.scale(RANGE));

            HitResult hit = level.clip(new ClipContext(start, end,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

            Vec3 eyeTarget = hit.getType() == HitResult.Type.MISS
                    ? end
                    : hit.getLocation().subtract(look.scale(0.7D));

            Vec3 feet = eyeTarget.subtract(0, player.getEyeHeight(), 0);
            Vec3 delta = feet.subtract(player.position());

            // Пробуем найти свободное место: на месте, на блок выше, на 2 блока выше
            Vec3 finalPos = null;
            for (int up = 0; up <= 2; up++) {
                AABB moved = player.getBoundingBox().move(delta.x, delta.y + up, delta.z);
                if (level.noCollision(player, moved)) {
                    finalPos = feet.add(0, up, 0);
                    break;
                }
            }

            if (finalPos == null) {
                level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL,
                        SoundSource.PLAYERS, 1.0F, 0.5F);
                player.displayClientMessage(Component.translatable("message.dotagranny.blink_blocked"), true);
                return InteractionResultHolder.fail(stack);
            }

            ServerLevel serverLevel = (ServerLevel) level;
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    player.getX(), player.getY() + 1.0D, player.getZ(), 40, 0.3D, 0.6D, 0.3D, 0.2D);
            level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.PLAYERS, 1.0F, 1.2F);

            serverPlayer.connection.teleport(finalPos.x, finalPos.y, finalPos.z,
                    serverPlayer.getYRot(), serverPlayer.getXRot());
            player.fallDistance = 0.0F;

            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    finalPos.x, finalPos.y + 1.0D, finalPos.z, 40, 0.3D, 0.6D, 0.3D, 0.2D);
            level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                    SoundSource.PLAYERS, 1.0F, 1.0F);

            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.dotagranny.blink_dagger"));
    }
}

package com.dotagranny.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** ПКМ: бросает крюк вперёд и подтягивает первое существо на линии прицела к игроку. */
public class HookItem extends Item {
    private static final double RANGE = 24.0D;

    public HookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            Vec3 start = player.getEyePosition();
            Vec3 look = player.getLookAngle();
            Vec3 end = start.add(look.scale(RANGE));

            // Крюк не летит сквозь стены
            HitResult blockHit = level.clip(new ClipContext(start, end,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            double maxDistSqr = blockHit.getType() == HitResult.Type.MISS
                    ? RANGE * RANGE
                    : blockHit.getLocation().distanceToSqr(start);

            AABB searchBox = player.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.0D);
            EntityHitResult hit = ProjectileUtil.getEntityHitResult(level, player, start, end, searchBox,
                    e -> e instanceof LivingEntity && e.isPickable() && !e.isSpectator() && e != player);

            level.playSound(null, player.blockPosition(), SoundEvents.FISHING_BOBBER_THROW,
                    SoundSource.PLAYERS, 1.0F, 0.6F);

            if (hit != null && hit.getLocation().distanceToSqr(start) <= maxDistSqr) {
                Entity target = hit.getEntity();
                Vec3 pull = player.position().subtract(target.position());
                Vec3 motion = pull.normalize().scale(1.7D).add(0.0D, 0.35D, 0.0D);
                target.setDeltaMovement(motion);
                target.hurtMarked = true; // синхронизировать скорость с клиентом
                target.hurt(player.damageSources().playerAttack(player), 4.0F);
                level.playSound(null, target.blockPosition(), SoundEvents.FISHING_BOBBER_RETRIEVE,
                        SoundSource.PLAYERS, 1.0F, 0.8F);
                player.getCooldowns().addCooldown(this, 60);
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            } else {
                player.getCooldowns().addCooldown(this, 30);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.dotagranny.hook"));
    }
}

package com.dotagranny;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Игровые события: Эгида спасает от смерти. */
@Mod.EventBusSubscriber(modid = DotaGranny.MODID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.is(DotaGranny.AEGIS.get())) {
                continue;
            }

            stack.shrink(1);
            event.setCanceled(true);

            player.setHealth(player.getMaxHealth());
            player.getFoodData().setFoodLevel(20);
            player.removeAllEffects();
            player.clearFire();
            player.fallDistance = 0.0F;
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 4));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));

            ServerLevel level = player.serverLevel();
            level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1.0D, player.getZ(), 60, 0.4D, 0.8D, 0.4D, 0.4D);
            level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE,
                    SoundSource.PLAYERS, 1.0F, 1.0F);
            player.sendSystemMessage(Component.translatable("message.dotagranny.aegis_used"));
            return;
        }
    }
}

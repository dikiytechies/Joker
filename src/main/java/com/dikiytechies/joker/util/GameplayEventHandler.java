package com.dikiytechies.joker.util;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.capability.JokerUtilCap;
import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.dikiytechies.joker.init.AddonStatusEffects;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID)
public class GameplayEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        //if (!entity.level.isClientSide()) {
            if (entity.hasEffect(AddonStatusEffects.SLOTH.get())) {
                EffectInstance sloth = entity.getActiveEffectsMap().get(AddonStatusEffects.SLOTH.get());
                if (INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false)) {
                    if (!entity.getCapability(JokerUtilCapProvider.CAPABILITY).map(JokerUtilCap::isSwanSong).orElse(false)) entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setSwanSong(true));
                } else if (sloth.getDuration() > 60) {
                    entity.removeEffect(sloth.getEffect());
                    entity.addEffect(new EffectInstance(AddonStatusEffects.SLOTH.get(), 60, sloth.getAmplifier(), sloth.isAmbient(), sloth.isVisible(), sloth.showIcon()));
                    entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setSwanSong(true));
                }
                if (entity.getCapability(JokerUtilCapProvider.CAPABILITY).map(JokerUtilCap::isSwanSong).orElse(false)) {
                    entity.setHealth(0.000001f);
                    event.setCanceled(true);
                }
            }
        //}
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDamage(LivingDamageEvent event) {
        consumeOrGiveEnergyFromSociopathy(event);
        borrowHealth(event);
    }
    private static void consumeOrGiveEnergyFromSociopathy(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        Entity hurtingEntity = event.getSource().getEntity();
        boolean isStand = false;
        if (hurtingEntity != null) {
            if (hurtingEntity instanceof StandEntity) {
                hurtingEntity = ((StandEntity) hurtingEntity).getUser();
                isStand = true;
            }
            if (INonStandPower.getNonStandPowerOptional(entity).isPresent()) {
                if (INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).get() && (hurtingEntity != entity && !isStand)) {
                    INonStandPower power = INonStandPower.getNonStandPowerOptional(entity).map(p -> p).get();
                    JokerData jokerData = power.getTypeSpecificData(JokerPowerInit.JOKER.get()).get();
                    if (jokerData.isSociopathyEnabled()) {
                        power.consumeEnergy(Math.min((event.getAmount() * 5), power.getEnergy()));
                    }
                }
                return;
            } else if (INonStandPower.getNonStandPowerOptional((LivingEntity) hurtingEntity).isPresent() && hurtingEntity instanceof LivingEntity) {
                if (INonStandPower.getNonStandPowerOptional((LivingEntity) hurtingEntity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).get()) {
                    INonStandPower power = INonStandPower.getNonStandPowerOptional((LivingEntity) hurtingEntity).map(p -> p).get();
                    JokerData jokerData = power.getTypeSpecificData(JokerPowerInit.JOKER.get()).get();
                    if (jokerData.isSociopathyEnabled()) {
                        power.addEnergy(event.getAmount() * 2.5f);
                    }
                }
            }
        }
    }
    private static void borrowHealth(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide())
            entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
            if (cap.isSwanSong()) cap.setBorrowedHealth(event.getAmount());
        });
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEffectExpiry(PotionEvent.PotionExpiryEvent event) {
        if (event.getPotionEffect().getEffect() == AddonStatusEffects.SLOTH.get() && !event.getEntityLiving().level.isClientSide()) {
            LivingEntity entity = event.getEntityLiving();
            entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                if (cap.isSwanSong()) {
                    cap.setSwanSong(false);
                    if ((entity instanceof PlayerEntity && !((PlayerEntity) entity).abilities.instabuild) &&
                    !(entity.isSpectator())) {
                        entity.hurt(DamageSource.MAGIC.bypassMagic().bypassArmor().bypassInvul(), cap.getBorrowedHealth());
                    }
                    cap.setBorrowedHealth(0.0f);
                }
            });
        }
    }
}

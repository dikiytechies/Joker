package com.dikiytechies.joker.util;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.capability.JokerUtilCap;
import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.dikiytechies.joker.init.AddonStatusEffects;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCapProvider;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.potion.BleedingEffect;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID)
public class GameplayEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        slothDeath(event);
        eraseEnvyDataOnDeath(event);
    }
    private static void slothDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.hasEffect(AddonStatusEffects.SLOTH.get())) {
            EffectInstance sloth = entity.getActiveEffectsMap().get(AddonStatusEffects.SLOTH.get());
            if (INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false)) {
                if (!entity.getCapability(JokerUtilCapProvider.CAPABILITY).map(JokerUtilCap::isSwanSong).orElse(false)) entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setSwanSong(true));
            } else if (sloth.getDuration() > 90) {
                entity.removeEffect(sloth.getEffect());
                entity.addEffect(new EffectInstance(AddonStatusEffects.SLOTH.get(), 90, sloth.getAmplifier(), sloth.isAmbient(), sloth.isVisible(), sloth.showIcon()));
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setSwanSong(true));
            }
            if (entity.getCapability(JokerUtilCapProvider.CAPABILITY).map(JokerUtilCap::isSwanSong).orElse(false)) {
                entity.setHealth(0.000001f);
                entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setBorrowedHealth(cap.getBorrowedHealth() + 0.00001f));
                event.setCanceled(true);
            }
        }
        //}
        if (!entity.level.isClientSide() && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity killer = (LivingEntity) event.getSource().getEntity();
            if (killer.hasEffect(AddonStatusEffects.SLOTH.get())) {
                if (entity instanceof PlayerEntity) {
                    killer.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setBorrowedHealth(0.0f));
                } else {
                    killer.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setBorrowedHealth(cap.getBorrowedHealth() / 3.0f));
                }
            }
        }
    }
    private static void eraseEnvyDataOnDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide() && !event.isCanceled()) {
            entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setStolenAttributesAmount(0.0, 0, true));
        }
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDamage(LivingDamageEvent event) {
        stealAttributesLivingEnvy(event);
        consumeOrGiveEnergyFromSociopathy(event);
        wrathDamage(event);
        delayDamage(event);
        borrowHealth(event);
        consumeGluttonyEnergy(event);
    }
    private static void consumeOrGiveEnergyFromSociopathy(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        boolean isStand = false;
        if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity hurtingEntity = (LivingEntity) event.getSource().getEntity();
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
            } else if (INonStandPower.getNonStandPowerOptional(hurtingEntity).isPresent()) {
                if (INonStandPower.getNonStandPowerOptional(hurtingEntity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).get()) {
                    INonStandPower power = INonStandPower.getNonStandPowerOptional(hurtingEntity).map(p -> p).get();
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
        if (!entity.level.isClientSide() && event.getAmount() >= entity.getHealth())
            entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
            if (cap.isSwanSong()) cap.setBorrowedHealth(event.getAmount() + cap.getBorrowedHealth());
        });
    }
    private static void delayDamage(LivingDamageEvent event) {
        LivingEntity targetEntity = event.getEntityLiving();
        if (!targetEntity.level.isClientSide() && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getSource().getEntity();
                if (entity != targetEntity && entity.hasEffect(AddonStatusEffects.SLOTH.get()) && entity.getEffect(AddonStatusEffects.SLOTH.get()).getAmplifier() == 0 && !INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false)) {
                    targetEntity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                        cap.addDelayedDamage(event.getAmount());
                        event.setCanceled(true);
                    });
                }
        }
    }
    private static void wrathDamage(LivingDamageEvent event) {
        LivingEntity targetEntity = event.getEntityLiving();
        if (!targetEntity.level.isClientSide() && event.getSource().getEntity() instanceof LivingEntity) {
            LivingEntity damagingEntity = (LivingEntity) event.getSource().getEntity();
            damagingEntity = event.getSource().getEntity() instanceof StandEntity? ((StandEntity) event.getSource().getEntity()).getUser(): damagingEntity;
            if (damagingEntity.hasEffect(AddonStatusEffects.WRATH.get()) && damagingEntity != targetEntity) {
                int maxBleeding = (int) Math.floor(targetEntity.getMaxHealth() / 4.0f) - 1;
                int currentBleeding = (int) Math.floor(targetEntity.getHealth() / 4.0f);
                int bleedingAmpl = maxBleeding - currentBleeding - 1;
                int wrathAmpl = damagingEntity.getEffect(AddonStatusEffects.WRATH.get()).getAmplifier();
                if (bleedingAmpl > -1) {
                    targetEntity.addEffect(new EffectInstance(ModStatusEffects.BLEEDING.get(), 150 * (wrathAmpl + 1), bleedingAmpl, false, false, false));
                }
                float additionalDamage = damagingEntity.getHealth() * 0.075f * (wrathAmpl + 1);
                event.setAmount(event.getAmount() + additionalDamage);
                if (wrathAmpl < 2 && !INonStandPower.getNonStandPowerOptional(damagingEntity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false)) {
                    if (((damagingEntity instanceof PlayerEntity) && !((PlayerEntity) damagingEntity).isCreative()) || (!(damagingEntity instanceof PlayerEntity) && !damagingEntity.isSpectator())) {
                        //damagingEntity.hurt(DamageSource.thorns(damagingEntity).bypassArmor().bypassMagic().bypassInvul(), additionalDamage / (wrathAmpl + 1));
                        damagingEntity.setHealth(damagingEntity.getHealth() - additionalDamage / (wrathAmpl + 1));
                        damagingEntity.hurtMarked = false;
                    }
                }
            }
        }
    }
    private static void tickDelayDamage(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide())
            entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
            if (cap.getDelayedDamage() > 0.0f && cap.getDamageDelay() == 0) {
                entity.hurt(DamageSource.WITHER.bypassMagic().bypassArmor().bypassInvul(), cap.getDelayedDamage());
                cap.setDelayedDamage(0.0f);
            } else if (cap.getDamageDelay() != 0) {
                cap.delayTick();
            }
        });
    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onEffectExpiry(PotionEvent.PotionExpiryEvent event) {
        if (event.getPotionEffect().getEffect() == AddonStatusEffects.SLOTH.get() && !event.getEntityLiving().level.isClientSide()) {
            LivingEntity entity = event.getEntityLiving();
            entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                if (cap.isSwanSong()) {
                    cap.setSwanSong(false);
                    if (!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).abilities.instabuild && !entity.isSpectator()) {
                        entity.hurt(DamageSource.WITHER.bypassMagic().bypassArmor().bypassInvul(), cap.getBorrowedHealth());
                    }
                    cap.setBorrowedHealth(0.0f);
                }
            });
        }
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void livingTick(LivingEvent.LivingUpdateEvent event) {
        envyTick(event);
        lustTick(event);
        tickDelayDamage(event);
    }
    private static void lustTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide()) {
            if (entity.hasEffect(AddonStatusEffects.LUST.get()) && entity.getEffect(AddonStatusEffects.LUST.get()).getAmplifier() < 1 && INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() != JokerPowerInit.JOKER.get()).orElse(false)) {
                if (entity.getCapability(PlayerUtilCapProvider.CAPABILITY).map(cap -> cap.getNoClientInputTimer() > 100).orElse(false) && entity.getHealth() / entity.getMaxHealth() >= 0.35f) {
                    if ((entity.hasEffect(Effects.POISON) && entity.getEffect(Effects.POISON).getDuration() < 25) || !entity.hasEffect(Effects.POISON))
                        entity.addEffect(new EffectInstance(Effects.POISON, 50, 0, false, false, false));
                }
            }
        }
    }
    private static void envyTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide()) entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(JokerUtilCap::envyTick);
    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void cancelGluttonyHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide()) {
            if (entity.hasEffect(AddonStatusEffects.GLUTTONY.get()) && entity.getEffect(AddonStatusEffects.GLUTTONY.get()).getAmplifier() < 1 && INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() != JokerPowerInit.JOKER.get()).orElse(false)) {
                if (entity instanceof PlayerEntity)
                    ((PlayerEntity) entity).getFoodData().setFoodLevel(Math.min(20, ((PlayerEntity) entity).getFoodData().getFoodLevel() + (int) Math.floor(event.getAmount() / 2)));
                event.setCanceled(true);
            }
        }
    }
    private static void consumeGluttonyEnergy(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide()) {
            if (entity.hasEffect(AddonStatusEffects.GLUTTONY.get()) && entity.getEffect(AddonStatusEffects.GLUTTONY.get()).getAmplifier() < 1 && INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() != JokerPowerInit.JOKER.get()).orElse(false)) {
                INonStandPower.getNonStandPowerOptional(entity).ifPresent(p -> p.consumeEnergy(Math.min(event.getAmount() * 20, p.getEnergy())));
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onPlayerAttack(AttackEntityEvent event) {
        stealAttributesPlayerEnvy(event);
    }
    private static void stealAttributesPlayerEnvy(AttackEntityEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!player.level.isClientSide()) {
            if (event.getTarget() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) event.getTarget();
                if (player.hasEffect(AddonStatusEffects.ENVY.get()) &&
                        (!(target instanceof PlayerEntity) || !((PlayerEntity) target).abilities.instabuild) && !target.isSpectator()) {
                    int amplifier = player.getEffect(AddonStatusEffects.ENVY.get()).getAmplifier();
                    if (player.getAttackStrengthScale(0f) == 1 && target.hurt(DamageSource.playerAttack(player), 0)) {
                        target.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(c -> player.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                            cap.addStolenAmount(0.10 * (amplifier + 1), (int) Math.ceil(300 * Math.sqrt(amplifier + 1)));
                            c.addStolenAmount(-0.10 * (amplifier + 1), (int) Math.ceil(300 * Math.sqrt(amplifier + 1)));
                        }));
                    } else if (player.getEffect(AddonStatusEffects.ENVY.get()).getAmplifier() < 1 && INonStandPower.getNonStandPowerOptional(player).map(p -> p.getType() != JokerPowerInit.JOKER.get()).orElse(false)) player.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                        cap.addStolenAmount(-0.10 * (amplifier + 1), 300);
                    });
                }
            }
        }
    }
    private static void stealAttributesLivingEnvy(LivingDamageEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (!(event.getSource().getEntity() instanceof PlayerEntity)) {
            if (event.getSource().getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) event.getSource().getEntity();
                if (entity.hasEffect(AddonStatusEffects.ENVY.get()) &&
                        (!(target instanceof PlayerEntity) || !((PlayerEntity) target).abilities.instabuild) && !target.isSpectator()) {
                    int amplifier = entity.getEffect(AddonStatusEffects.ENVY.get()).getAmplifier();
                        target.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(c -> entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                            cap.addStolenAmount(0.10 * (amplifier + 1), (int) Math.ceil(300 * Math.sqrt(amplifier + 1)));
                            c.addStolenAmount(-0.10 * (amplifier + 1), (int) Math.ceil(300 * Math.sqrt(amplifier + 1)));
                        }));
                }
            }
        }
    }
}

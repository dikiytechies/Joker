package com.dikiytechies.joker.util;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.capability.JokerUtilCap;
import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.dikiytechies.joker.init.AddonStatusEffects;
import com.dikiytechies.joker.init.Sounds;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.potion.GreedStatusEffect;
import com.dikiytechies.joker.potion.PrideStatusEffect;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCapProvider;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.potion.BleedingEffect;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID)
public class GameplayEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        slothDeath(event);
        eraseEnvyDataOnDeath(event);
    }
    //todo sloth death fix
    private static void slothDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.hasEffect(AddonStatusEffects.SLOTH.get())) {
            EffectInstance sloth = entity.getActiveEffectsMap().get(AddonStatusEffects.SLOTH.get());
            if (INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false)) {
                if (!entity.getCapability(JokerUtilCapProvider.CAPABILITY).map(JokerUtilCap::isSwanSong).orElse(false))
                    entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> cap.setSwanSong(true));
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
        prideMultiCast(event); //todo anti-recursive system
        prideStackDamage(event);
        stealAttributesLivingEnvy(event);
        consumeOrGiveEnergyFromSociopathy(event);
        wrathDamage(event);
        delayDamage(event);
        borrowHealth(event);
        consumeGreedHealth(event);
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
                    if (jokerData.isSociopathyEnabled() && !entity.hasEffect(AddonStatusEffects.GREED.get())) {
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
        if (event.getAmount() >= entity.getHealth() && !event.isCanceled())
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
                int wrathAmpl = damagingEntity.getEffect(AddonStatusEffects.WRATH.get()).getAmplifier();
                float additionalDamage = damagingEntity.getHealth() * 0.075f * (wrathAmpl + 1);
                event.setAmount(event.getAmount() + additionalDamage);
                if (wrathAmpl < 1 && !INonStandPower.getNonStandPowerOptional(damagingEntity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false)) {
                    if (((damagingEntity instanceof PlayerEntity) && !((PlayerEntity) damagingEntity).isCreative()) || (!(damagingEntity instanceof PlayerEntity) && !damagingEntity.isSpectator())) {
                        //damagingEntity.hurt(DamageSource.thorns(damagingEntity).bypassArmor().bypassMagic().bypassInvul(), additionalDamage / (wrathAmpl + 1));
                        damagingEntity.setHealth(damagingEntity.getHealth() - additionalDamage / (wrathAmpl + 1));
                        damagingEntity.hurtMarked = false;
                    }
                }
                int bleedingAmpl = (int) Math.floor((BleedingEffect.getMaxHealthWithoutBleeding(targetEntity) - targetEntity.getHealth() + event.getAmount()) / 4) - 1;
                if (bleedingAmpl > -1) {
                    targetEntity.addEffect(new EffectInstance(ModStatusEffects.BLEEDING.get(), 75 * (wrathAmpl + 1), bleedingAmpl, false, false, false));
                }
            }
        }
    }
    private static void prideStackDamage(LivingDamageEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (!target.level.isClientSide() && event.getSource().getEntity() instanceof LivingEntity && !event.isCanceled() && target != event.getSource().getEntity()) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            target.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(targetCap -> attacker.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                int amplifier = attacker.hasEffect(AddonStatusEffects.PRIDE.get())? attacker.getEffect(AddonStatusEffects.PRIDE.get()).getAmplifier(): 0;
                if (cap.getPrideAttackerStacks() > 0) {
                    float lifeSteal = target.getMaxHealth() * 0.15f * (amplifier + 1);
                    attacker.heal(lifeSteal);
                    event.setAmount(event.getAmount() + lifeSteal);
                    if (attacker instanceof PlayerEntity) ((PlayerEntity) attacker).magicCrit(target);
                    cap.addPrideAttackerStacks(-1);
                    if (cap.getPrideAttackerStacks() == 0) {
                        cap.setPrideAttackerTicks(0);
                        attacker.level.playSound(null, attacker.blockPosition(), Sounds.PRIDE_END.get(), attacker.getSoundSource(), 1.0f, 1.0f);
                    } else attacker.level.playSound(null, attacker.blockPosition(), Sounds.PRIDE_ATTACK.get(), attacker.getSoundSource(), 1.0f, 1.0f);
                } else if (attacker.hasEffect(AddonStatusEffects.PRIDE.get())) {
                    if (targetCap.getPrideTargetStacks() >= 2 - amplifier) {
                        attacker.level.playSound(null, attacker.blockPosition(), Sounds.PRIDE_PROC.get(), attacker.getSoundSource(), 1.0f, 1.0f);
                        targetCap.setPrideTargetStacks(0);
                        targetCap.setPrideTargetStacksTicks(0);
                        cap.setPrideAttackerStacks(3 + amplifier);
                    } else if (cap.getPrideAttackerStacks() == 0) {
                        targetCap.addPrideTargetStacks(1);
                    }
                }
            }));
        }
    }
    private static void prideMultiCast(LivingDamageEvent event) {
        LivingEntity target = event.getEntityLiving();
        if (!target.level.isClientSide() && event.getSource().getEntity() instanceof LivingEntity && target != event.getSource().getEntity()) {
            LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
            if (attacker.hasEffect(AddonStatusEffects.PRIDE.get())) {
                int amplifier = attacker.getEffect(AddonStatusEffects.PRIDE.get()).getAmplifier();
                attacker.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                    cap.setPrideMultiCast(PrideStatusEffect.applyMultiCast(amplifier));
                    cap.setMultiCastTarget(target);
                });
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
        slothExpiry(event);
        greedExpiry(event);
    }
    private static void slothExpiry(PotionEvent.PotionExpiryEvent event) {
        if (event.getPotionEffect().getEffect().getEffect() == AddonStatusEffects.SLOTH.get()) {
            LivingEntity entity = event.getEntityLiving();
            entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(cap -> {
                if (cap.isSwanSong()) {
                    entity.removeEffect(event.getPotionEffect().getEffect());
                    cap.setSwanSong(false);
                    if (!(entity instanceof PlayerEntity) || !((PlayerEntity) entity).abilities.instabuild && !entity.isSpectator()) {
                        entity.hurt(DamageSource.WITHER.bypassMagic().bypassArmor().bypassInvul(), cap.getBorrowedHealth());
                    }
                    cap.setBorrowedHealth(0.0f);
                }
            });
        }
    }
    private static void greedExpiry(PotionEvent.PotionExpiryEvent event) {
        if (event.getPotionEffect().getEffect() == AddonStatusEffects.SLOTH.get() && !event.getEntityLiving().level.isClientSide()) {
            LivingEntity entity = event.getEntityLiving();
            MCUtil.removeAttributeModifier(entity, Attributes.MAX_HEALTH, new AttributeModifier(GreedStatusEffect.HEALTH_ATTRIBUTE_MODIFIER_ID, "Greed max health", 0.0, AttributeModifier.Operation.ADDITION));
            MCUtil.removeAttributeModifier(entity, Attributes.ARMOR, new AttributeModifier(GreedStatusEffect.ARMOR_ATTRIBUTE_MODIFIER_ID, "Greed armor", 0.0, AttributeModifier.Operation.ADDITION));
            entity.setHealth(entity.getMaxHealth());
        }
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onEffectClear(PotionEvent.PotionRemoveEvent event) {
        greedClear(event);
    }
    private static void greedClear(PotionEvent.PotionRemoveEvent event) {
        if (event.getPotion().equals(AddonStatusEffects.GREED.get()) && !event.getEntityLiving().level.isClientSide()) {
            LivingEntity entity = event.getEntityLiving();
            MCUtil.removeAttributeModifier(entity, Attributes.MAX_HEALTH, new AttributeModifier(GreedStatusEffect.HEALTH_ATTRIBUTE_MODIFIER_ID, "Greed max health", 0.0, AttributeModifier.Operation.ADDITION));
            MCUtil.removeAttributeModifier(entity, Attributes.ARMOR, new AttributeModifier(GreedStatusEffect.ARMOR_ATTRIBUTE_MODIFIER_ID, "Greed armor", 0.0, AttributeModifier.Operation.ADDITION));
            entity.setHealth(entity.getMaxHealth());
        }
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void livingTick(LivingEvent.LivingUpdateEvent event) {
        envyTick(event);
        lustTick(event);
        prideTick(event);
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
    private static void prideTick(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        entity.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(JokerUtilCap::prideTick);
    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHeal(LivingHealEvent event) {
        consumeGreedArmor(event);
        cancelGluttonyHeal(event);
    }
    private static void cancelGluttonyHeal(LivingHealEvent event) {
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
        if (!entity.level.isClientSide() && !event.isCanceled()) {
            if (entity.hasEffect(AddonStatusEffects.GLUTTONY.get()) && entity.getEffect(AddonStatusEffects.GLUTTONY.get()).getAmplifier() < 1 && INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() != JokerPowerInit.JOKER.get()).orElse(false)) {
                INonStandPower.getNonStandPowerOptional(entity).ifPresent(p -> p.consumeEnergy(Math.min(event.getAmount() * 20, p.getEnergy())));
            }
        }
    }
    private static void consumeGreedArmor(LivingHealEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide() && !event.isCanceled()) {
            float healAmount = event.getAmount();
            if (entity.hasEffect(AddonStatusEffects.GREED.get())) {
                if (healAmount + entity.getHealth() >= entity.getMaxHealth()) {
                    double armorKeeper = entity.getEffect(AddonStatusEffects.GREED.get()).getAmplifier() > 0 || INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false)?
                            GreedStatusEffect.getMaxArmorWithoutGreed(entity): entity.getArmorValue() + (entity.getMaxHealth() - GreedStatusEffect.getMaxHealthWithoutGreed(entity));
                    double additionalHealth = entity.getMaxHealth() - GreedStatusEffect.getMaxHealthWithoutGreed(entity) +
                            Math.min(GreedStatusEffect.getMaxArmorWithoutGreed(entity) - (entity.getMaxHealth() - GreedStatusEffect.getMaxHealthWithoutGreed(entity)), event.getAmount());
                    MCUtil.applyAttributeModifier(entity, Attributes.MAX_HEALTH, new AttributeModifier(GreedStatusEffect.HEALTH_ATTRIBUTE_MODIFIER_ID, "Greed max health", additionalHealth, AttributeModifier.Operation.ADDITION));
                    if (!(entity.getEffect(AddonStatusEffects.GREED.get()).getAmplifier() > 0 || INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false))) {
                        MCUtil.applyAttributeModifier(entity, Attributes.ARMOR, new AttributeModifier(GreedStatusEffect.ARMOR_ATTRIBUTE_MODIFIER_ID, "Greed armor", -additionalHealth, AttributeModifier.Operation.ADDITION));
                    } else MCUtil.applyAttributeModifier(entity, Attributes.ARMOR, new AttributeModifier(GreedStatusEffect.ARMOR_ATTRIBUTE_MODIFIER_ID, "Greed armor", armorKeeper - additionalHealth, AttributeModifier.Operation.ADDITION));
                }
            }
        }
    }
    private static void consumeGreedHealth(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide() && !event.isCanceled()) {
            float damage = event.getAmount();
            if (entity.hasEffect(AddonStatusEffects.GREED.get())) {
                MCUtil.applyAttributeModifier(entity, Attributes.MAX_HEALTH, new AttributeModifier(GreedStatusEffect.HEALTH_ATTRIBUTE_MODIFIER_ID, "Greed max health", entity.getMaxHealth() - GreedStatusEffect.getMaxHealthWithoutGreed(entity) - damage, AttributeModifier.Operation.ADDITION));
                MCUtil.applyAttributeModifier(entity, Attributes.ARMOR, new AttributeModifier(GreedStatusEffect.ARMOR_ATTRIBUTE_MODIFIER_ID, "Greed armor", entity.getArmorValue() + damage, AttributeModifier.Operation.ADDITION));
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide() && entity.hasEffect(AddonStatusEffects.GREED.get())) {
            if (Arrays.stream(EquipmentSlotType.values()).anyMatch(equipmentSlotType -> equipmentSlotType.getType() == event.getSlot().getType())) {
                AtomicReference<Double> armorCounter = new AtomicReference<>(0.0);
                event.getFrom().getAttributeModifiers(event.getSlot()).get(Attributes.ARMOR).forEach(armor -> armorCounter.updateAndGet(v -> v + armor.getAmount()));
                double additionalHealth = Math.min(entity.getHealth() - GreedStatusEffect.getMaxHealthWithoutGreed(entity), GreedStatusEffect.getMaxArmorWithoutGreed(entity) - armorCounter.get());
                MCUtil.applyAttributeModifier(entity, Attributes.MAX_HEALTH, new AttributeModifier(GreedStatusEffect.HEALTH_ATTRIBUTE_MODIFIER_ID, "Greed max health", additionalHealth, AttributeModifier.Operation.ADDITION));
                if (!(entity.getEffect(AddonStatusEffects.GREED.get()).getAmplifier() > 0 || INonStandPower.getNonStandPowerOptional(entity).map(p -> p.getType() == JokerPowerInit.JOKER.get()).orElse(false))) {
                    MCUtil.applyAttributeModifier(entity, Attributes.ARMOR, new AttributeModifier(GreedStatusEffect.ARMOR_ATTRIBUTE_MODIFIER_ID, "Greed armor", -additionalHealth, AttributeModifier.Operation.ADDITION));
                } else MCUtil.applyAttributeModifier(entity, Attributes.ARMOR, new AttributeModifier(GreedStatusEffect.ARMOR_ATTRIBUTE_MODIFIER_ID, "Greed armor", GreedStatusEffect.getMaxArmorWithoutGreed(entity) - additionalHealth, AttributeModifier.Operation.ADDITION));
                entity.setHealth(entity.getMaxHealth());
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

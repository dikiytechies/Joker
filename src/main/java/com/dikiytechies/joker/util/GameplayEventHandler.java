package com.dikiytechies.joker.util;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerPowerType;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.NonStandPower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID)
public class GameplayEventHandler {
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDeath(LivingDeathEvent event) {

    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingHurtStart(LivingAttackEvent event) {

    }
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingHurt(LivingHurtEvent event) {
        System.out.println(event.getEntityLiving().getHealth() + " " + event.getAmount());
        consumeOrGiveEnergyFromSociopathy(event);
    }
    private static void consumeOrGiveEnergyFromSociopathy(LivingHurtEvent event) {
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
}

package com.dikiytechies.joker.util;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.init.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerPowerType;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID)
public class GameplayEventHandler {
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingDeath(LivingDeathEvent event) {

    }
}

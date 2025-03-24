package com.dikiytechies.joker.init.power;

import com.dikiytechies.joker.init.AddonStatusEffects;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import net.minecraftforge.eventbus.api.IEventBus;

public class AddonCustomRegistries {
    public static void initCustomRegistries(IEventBus modEventBus) {
        JokerPowerInit.loadRegistryObjects();
        JokerPowerInit.ACTIONS.register(modEventBus);
        JokerPowerInit.NON_STAND_POWER.register(modEventBus);
    }
    public static void initVanillaRegistries(IEventBus modEventBus) {
        AddonStatusEffects.EFFECTS.register(modEventBus);
    }
}

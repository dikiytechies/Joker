package com.dikiytechies.joker.init.power;

import com.dikiytechies.joker.init.JokerPowerInit;
import net.minecraftforge.eventbus.api.IEventBus;

public class AddonCustomRegistries {
    public static void initCustomRegistries(IEventBus modEventBus) {
        JokerPowerInit.loadRegistryObjects();
    }
}

package com.dikiytechies.joker.init.power;

import com.dikiytechies.joker.init.*;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class AddonCustomRegistries {
    public static void initCustomRegistries(IEventBus modEventBus) {
        JokerPowerInit.loadRegistryObjects();
        JokerPowerInit.ACTIONS.register(modEventBus);
        JokerPowerInit.NON_STAND_POWER.register(modEventBus);
    }
    public static void initVanillaRegistries(IEventBus modEventBus) {
        AddonStatusEffects.EFFECTS.register(modEventBus);
        AddonEntities.ENTITIES.register(modEventBus);
        AddonItems.ITEMS.register(modEventBus);
        AddonStructures.STRUCTURES.register(modEventBus);
        Sounds.SOUNDS.register(modEventBus);
    }
}

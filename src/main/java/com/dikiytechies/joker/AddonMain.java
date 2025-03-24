package com.dikiytechies.joker;

import com.dikiytechies.joker.capability.CapabilityHandler;
import com.dikiytechies.joker.init.power.AddonCustomRegistries;
import com.dikiytechies.joker.network.AddonPackets;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Your addon's main file

@Mod(AddonMain.MOD_ID)
public class AddonMain {
    // The mod's id. Used quite often, mostly when creating ResourceLocation (objects).
    // Its value should match the "modid" entry in the META-INF/mods.toml file
    public static final String MOD_ID = "joker";
    public static final Logger LOGGER = LogManager.getLogger();

    public AddonMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AddonCustomRegistries.initCustomRegistries(modEventBus);
        AddonCustomRegistries.initVanillaRegistries(modEventBus);

        modEventBus.addListener(this::preInit);
    }
    private void preInit(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AddonPackets.init();
            CapabilityHandler.commonSetupRegister();
        });
    }
}

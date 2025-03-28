package com.dikiytechies.joker.init;

import com.dikiytechies.joker.AddonMain;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Sounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(
            ForgeRegistries.SOUND_EVENTS, AddonMain.MOD_ID);

    public static final RegistryObject<SoundEvent> PRIDE_PROC = SOUNDS.register("pride_proc",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "pride_proc")));
    public static final RegistryObject<SoundEvent> PRIDE_ATTACK = SOUNDS.register("pride_attack",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "pride_attack")));
    public static final RegistryObject<SoundEvent> PRIDE_END = SOUNDS.register("pride_end",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "pride_end")));
    public static final RegistryObject<SoundEvent> PRIDE_MULTICAST_X2 = SOUNDS.register("pride_multicast_x2",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "pride_multicast_x2")));
    public static final RegistryObject<SoundEvent> PRIDE_MULTICAST_X3 = SOUNDS.register("pride_multicast_x3",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "pride_multicast_x3")));
    public static final RegistryObject<SoundEvent> PRIDE_MULTICAST_X4 = SOUNDS.register("pride_multicast_x4",
            () -> new SoundEvent(new ResourceLocation(AddonMain.MOD_ID, "pride_multicast_x4")));
}

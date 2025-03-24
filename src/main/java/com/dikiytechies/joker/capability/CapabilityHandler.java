package com.dikiytechies.joker.capability;

import com.dikiytechies.joker.AddonMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID)
public class CapabilityHandler {
    private static final ResourceLocation JOKER_UTIL_CAP = new ResourceLocation(AddonMain.MOD_ID, "joker_util");

    public static void commonSetupRegister() {
        CapabilityManager.INSTANCE.register(
                JokerUtilCap.class,
                new Capability.IStorage<JokerUtilCap>() {
                    @Override public INBT writeNBT(Capability<JokerUtilCap> capability, JokerUtilCap instance, Direction side) { return instance.serializeNBT(); }
                    @Override public void readNBT(Capability<JokerUtilCap> capability, JokerUtilCap instance, Direction side, INBT nbt) { instance.deserializeNBT((CompoundNBT) nbt); }
                },
                () -> new JokerUtilCap(null));
    }
    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            event.addCapability(JOKER_UTIL_CAP, new JokerUtilCapProvider(living));
        }
    }

    @SubscribeEvent
    public static void syncWithNewPlayer(PlayerEvent.StartTracking event) {
        syncAttachedData(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        syncAttachedData(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncAttachedData(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncAttachedData(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity original = event.getOriginal();
        PlayerEntity player = event.getPlayer();
        original.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent((oldCap) -> {
            player.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent((newCap) -> {
                newCap.onClone(oldCap);
            });
        });
    }

    private static void syncAttachedData(PlayerEntity player) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        player.getCapability(JokerUtilCapProvider.CAPABILITY).ifPresent(data -> {
            data.syncWithEntityOnly(serverPlayer);
            data.syncWithAnyPlayer(serverPlayer);
        });
    }
}

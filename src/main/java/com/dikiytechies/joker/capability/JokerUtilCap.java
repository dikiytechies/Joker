package com.dikiytechies.joker.capability;

import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.fromserver.TrSlothEffectPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class JokerUtilCap implements INBTSerializable<CompoundNBT> {
    private final LivingEntity livingEntity;
    private boolean swanSong;
    private float borrowedHealth;
    public JokerUtilCap(LivingEntity livingEntity) { this.livingEntity = livingEntity; }

    public void setSwanSong(boolean value) {
        this.swanSong = value;
        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;
            AddonPackets.sendToClient(new TrSlothEffectPacket(livingEntity.getId(), swanSong, borrowedHealth), player);
        }
    }
    public boolean isSwanSong() {
        return swanSong;
    }
    public void setBorrowedHealth(float value) {
        this.borrowedHealth = value;
        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;
            AddonPackets.sendToClient(new TrSlothEffectPacket(livingEntity.getId(), swanSong, borrowedHealth), player);
        }
    }
    public float getBorrowedHealth() { return borrowedHealth; }
    public void onClone(JokerUtilCap old) {
        this.swanSong = old.swanSong;
    }


    // Sync all the data that should be available to all players
    public void syncWithAnyPlayer(ServerPlayerEntity player) {
    }

    // Sync all the data that only this player needs to know
    public void syncWithEntityOnly(ServerPlayerEntity player) {
        AddonPackets.sendToClient(new TrSlothEffectPacket(livingEntity.getId(), swanSong, borrowedHealth), player);
    }
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("SwanSong", swanSong);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        swanSong = nbt.getBoolean("SwanSong");
    }
}

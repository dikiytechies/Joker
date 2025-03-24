package com.dikiytechies.joker.capability;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class JokerUtilCapProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(JokerUtilCap.class)
    public static Capability<JokerUtilCap> CAPABILITY = null;
    private LazyOptional<JokerUtilCap> instance;

    public JokerUtilCapProvider(LivingEntity entity) {
        this.instance = LazyOptional.of(() -> new JokerUtilCap(entity));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return CAPABILITY.orEmpty(cap, instance);
    }

    @Override
    public INBT serializeNBT() {
        return CAPABILITY.getStorage().writeNBT(CAPABILITY, instance.orElseThrow(
                () -> new IllegalArgumentException("Player capability LazyOptional is not attached.")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CAPABILITY.getStorage().readNBT(CAPABILITY, instance.orElseThrow(
                () -> new IllegalArgumentException("Player capability LazyOptional is not attached.")), null, nbt);
    }
}

package com.dikiytechies.joker.mixin;

import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.MainHamonSkillsManager;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = MainHamonSkillsManager.class, remap = false)
public interface MainHamonSkillsManagerAccessor {
    @Invoker("fromNbt")
    public void skillsFromNbt(CompoundNBT nbt);
    @Invoker("toNBT")
    public CompoundNBT skillsToNbt();
}

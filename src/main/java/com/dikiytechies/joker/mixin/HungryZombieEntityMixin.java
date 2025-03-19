package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.entity.mob.HungryZombieEntity;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(HungryZombieEntity.class)
public abstract class HungryZombieEntityMixin extends ZombieEntity {
    @Shadow @Nullable protected abstract UUID getOwnerUUID();

    public HungryZombieEntityMixin(World world) { super(world); }
    public HungryZombieEntityMixin(EntityType<? extends ZombieEntity> p_i48549_1_, World p_i48549_2_) { super(p_i48549_1_, p_i48549_2_); }


    @Inject(method = "getOwner", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/World;getPlayerByUUID(Ljava/util/UUID;)Lnet/minecraft/entity/player/PlayerEntity;"), cancellable = true, remap = false)
    private void fixJokerOwner(CallbackInfoReturnable<LivingEntity> cir) {
        UUID uuid = this.getOwnerUUID();
        if (uuid == null) cir.cancel();
        PlayerEntity owner = level.getPlayerByUUID(uuid);
        if (owner != null && INonStandPower.getNonStandPowerOptional(owner).map(
                power -> power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.VAMPIRISM.get()).orElse(false)).orElse(false)) {
            cir.setReturnValue(owner);
            cir.cancel();
        }
    }
}

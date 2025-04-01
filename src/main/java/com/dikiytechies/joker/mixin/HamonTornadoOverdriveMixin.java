package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.non_stand.HamonAction;
import com.github.standobyte.jojo.action.non_stand.HamonTornadoOverdrive;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonUtil;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.skill.BaseHamonSkill;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = HamonTornadoOverdrive.class, remap = false)
public abstract class HamonTornadoOverdriveMixin extends HamonAction {
    public HamonTornadoOverdriveMixin(HamonTornadoOverdriveMixin.AbstractBuilder<?> builder) { super(builder); }

    @Inject(method = "holdTick(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lcom/github/standobyte/jojo/power/impl/nonstand/INonStandPower;ILcom/github/standobyte/jojo/action/ActionTarget;Z)V", at = @At("HEAD"), cancellable = true, remap = false)
    protected void holdTick(World world, LivingEntity user, INonStandPower power, int ticksHeld, ActionTarget target, boolean requirementsFulfilled, CallbackInfo ci) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.HAMON.get()).orElse(false)) {
            if (requirementsFulfilled) {
                user.fallDistance = 0;
                Vector3d movement = user.getDeltaMovement();
                HamonData hamon = (HamonData) power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getPreviousData).get();
                if (!world.isClientSide()) {
                    AxisAlignedBB aabb = user.getBoundingBox().expandTowards(movement).inflate(1.0D);
                    float damage = 0.6F;
                    double gravity = user.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get());
                    if (movement.y < -gravity) {
                        damage *= (-movement.y / gravity) * 0.15F;
                    }
                    List<Entity> targets = user.level.getEntities(user, aabb);
                    boolean points = false;
                    for (Entity entity : targets) {
                        if (DamageUtil.dealHamonDamage(entity, damage, user, null)) {
                            points = true;
                        }
                    }
                    if (points) {
                        hamon.hamonPointsFromAction(BaseHamonSkill.HamonStat.STRENGTH, getHeldTickEnergyCost(power));
                    }
                }
                if (user.isShiftKeyDown()) {
                    user.setDeltaMovement(0, movement.y < 0 ? movement.y * 1.05 : 0, 0);
                }
                // FIXME ! (hamon 2) sfx
                HamonUtil.emitHamonSparkParticles(world, user instanceof PlayerEntity ? (PlayerEntity) user : null, user.position(),
                        hamon.getHamonDamageMultiplier() / HamonData.MAX_HAMON_STRENGTH_MULTIPLIER * 0.25F);
            }
            ci.cancel();
        }
    }
}

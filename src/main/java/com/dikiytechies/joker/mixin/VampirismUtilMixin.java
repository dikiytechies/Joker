package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.vampirism.VampirismUtil;
import com.github.standobyte.jojo.util.mc.reflection.CommonReflection;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(value = VampirismUtil.class, remap = false)
public abstract class VampirismUtilMixin {
    @Shadow
    public static float healCost(World world) {
        return 0;
    }
    @Overwrite
    private static void makeMobNeutralToVampirePlayers(MobEntity mob) {
        if (JojoModConfig.getCommonConfigInstance(false).vampiresAggroMobs.get()) return;
        Set<PrioritizedGoal> goals = CommonReflection.getGoalsSet(mob.targetSelector);
        for (PrioritizedGoal prGoal : goals) {
            Goal goal = prGoal.getGoal();
            if (goal instanceof NearestAttackableTargetGoal) {
                NearestAttackableTargetGoal<?> targetGoal = (NearestAttackableTargetGoal<?>) goal;
                Class<? extends LivingEntity> targetClass = CommonReflection.getTargetClass(targetGoal);

                if (targetClass == PlayerEntity.class) {
                    EntityPredicate selector = CommonReflection.getTargetConditions(targetGoal);
                    if (selector != null) {
                        Predicate<LivingEntity> oldPredicate = CommonReflection.getTargetSelector(selector);
                        Predicate<LivingEntity> undeadPredicate = target ->
                                target instanceof PlayerEntity && !(
//                                    JojoModUtil.isPlayerUndead((PlayerEntity) target) &&
                                        INonStandPower.getNonStandPowerOptional(target).map(
                                                power -> power.getTypeSpecificData(ModPowers.VAMPIRISM.get())
                                                        .map(vampirism -> vampirism.getCuringStage() < 3).orElse(false)).orElse(false))
                                        && !(INonStandPower.getNonStandPowerOptional(target).map(power ->power.getType() == ModPowers.ZOMBIE.get()).orElse(false))
                                        && !(INonStandPower.getNonStandPowerOptional(target).map(power -> power.getTypeSpecificData(ModPowers.PILLAR_MAN.get())
                                        .map(pillarman -> pillarman.isStoneFormEnabled()).orElse(false)).orElse(false) ||
                                        INonStandPower.getNonStandPowerOptional(target).map(power -> power.getTypeSpecificData(ModPowers.PILLAR_MAN.get())
                                                .map(pillarman -> pillarman.getEvolutionStage() > 1).orElse(false)).orElse(false))
                                        && !(INonStandPower.getNonStandPowerOptional(target).map(
                                        power -> power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> (d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get() && d.getPreviousDataNbt().getInt("PillarmanStage") > 1) ||
                                                d.getPreviousPowerType() == ModPowers.ZOMBIE.get() ||
                                                (d.getPreviousPowerType() == ModPowers.VAMPIRISM.get())).orElse(false)).orElse(false));
                        CommonReflection.setTargetConditions(targetGoal, new EntityPredicate().range(CommonReflection.getTargetDistance(targetGoal)).selector(
                                oldPredicate != null ? oldPredicate.and(undeadPredicate) : undeadPredicate));
                    }
                }
            }
        }
    }

    @Inject(method = "consumeEnergyOnHeal", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/entity/LivingEntity;isAlive()Z"), remap = false)
    private static void consumeEnergyOnHeal(LivingHealEvent event, CallbackInfo ci) {
        LivingEntity entity = event.getEntityLiving();
        INonStandPower.getNonStandPowerOptional(entity).ifPresent(power -> {
            if (power.getType() == JokerPowerInit.JOKER.get() && (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.VAMPIRISM.get()).orElse(false)
                    || (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false)
                    && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousDataNbt().getInt("PillarmanStage") > 1).orElse(false)))) {
                float healCost = healCost(entity.level);
                if (healCost > 0) {
                    float actualHeal = Math.min(event.getAmount(), power.getEnergy() / healCost);
                    actualHeal = Math.min(actualHeal, entity.getMaxHealth() - entity.getHealth());
                    if (actualHeal > 0) {
                        power.consumeEnergy(Math.min(actualHeal, entity.getMaxHealth() - entity.getHealth()) * healCost);
                        event.setAmount(actualHeal);
                    }
                    else {
                        event.setCanceled(true);
                    }
                }
            }
        });
    }
}

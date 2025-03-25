package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.action.non_stand.PillarmanBladeDashAttack;
import com.github.standobyte.jojo.action.non_stand.VampirismClawLacerate;
import com.github.standobyte.jojo.action.player.ContinuousActionInstance;
import com.github.standobyte.jojo.capability.entity.PlayerUtilCap;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.init.ModSounds;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.pillarman.PillarmanData;
import com.github.standobyte.jojo.power.impl.nonstand.type.pillarman.PillarmanUtil;
import com.github.standobyte.jojo.util.general.MathUtil;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.github.standobyte.jojo.action.non_stand.PillarmanBladeDashAttack.slashHitbox;

@Mixin(value = PillarmanBladeDashAttack.Instance.class, remap = false)
public abstract class PBDInstanceMixin extends ContinuousActionInstance<PillarmanBladeDashAttack, INonStandPower> {

    public PBDInstanceMixin(LivingEntity user, PlayerUtilCap userCap, INonStandPower playerPower, PillarmanBladeDashAttack action) {
        super(user, userCap, playerPower, action);
    }
    @Shadow
    Set<UUID> damagedEntities;
    //TODO waiting stando to fix field init
    @Shadow
    PillarmanData pillarman;

    /**
     * @author dikiytechies
     * @reason too hard to inject
     */
    @Overwrite
    public void playerTick() {
        List<LivingEntity> targets = user.level.getEntitiesOfClass(LivingEntity.class, slashHitbox(user),
                entity -> !entity.is(user) && user.canAttack(entity));
        for (LivingEntity target : targets) {
            if (damagedEntities.add(target.getUUID())) {
                boolean kickDamage = dealPhysicalDamage(user.level, user, target);
                if (kickDamage) {
                    Vector3d vecToTarget = target.position().subtract(user.position());
                    boolean left = MathHelper.wrapDegrees(
                            user.yBodyRot - MathUtil.yRotDegFromVec(vecToTarget))
                            < 0;
                    float knockbackYRot = (60F + user.getRandom().nextFloat() * 30F) * (left ? 1 : -1);
                    knockbackYRot += (float) -MathHelper.atan2(vecToTarget.x, vecToTarget.z) * MathUtil.RAD_TO_DEG;
                    DamageUtil.knockback((LivingEntity) target, 0.75F, knockbackYRot);
                    PillarmanUtil.sparkEffect(target, 60);
                }
            }
        }
        switch (getTick()) {
            case 1:
                if (user.level.isClientSide()) {
                    user.level.playSound(ClientUtil.getClientPlayer(), user.getX(), user.getEyeY(), user.getZ(),
                            ModSounds.HAMON_SYO_SWING.get(), user.getSoundSource(), 1.0f, 1.0f);
                    user.swing(Hand.MAIN_HAND, true);
                    if (playerPower.getType() == ModPowers.PILLAR_MAN.get()) {
                        pillarman.setBladesVisible(true);
                    } else if (playerPower.getType() == JokerPowerInit.JOKER.get() && playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false))
                        playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(j -> j.setPillarmanBladesVisible(true));
                }
                break;
            case 15:
                if (playerPower.getType() == ModPowers.PILLAR_MAN.get()) {
                    pillarman.setBladesVisible(false);
                } else if (playerPower.getType() == JokerPowerInit.JOKER.get() && playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()).orElse(false))
                    playerPower.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(j -> j.setPillarmanBladesVisible(false));
                stopAction();
                break;
        }
    }
    @Unique
    private static boolean dealPhysicalDamage(World world, LivingEntity user, Entity target) {
        return target.hurt(new EntityDamageSource(user instanceof PlayerEntity ? "player" : "mob", user),
                DamageUtil.addArmorPiercing(VampirismClawLacerate.getDamage(world, user) + 1F, 15F, (LivingEntity) target));
    }
}

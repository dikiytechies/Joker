package com.dikiytechies.joker.power.impl.nonstand.type;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.client.controls.ControlScheme;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.vampirism.ModVampirismActions;
import com.github.standobyte.jojo.init.power.non_stand.zombie.ModZombieActions;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.general.GeneralUtil;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Objects;

public class JokerPowerType extends NonStandPowerType<JokerData> {
    private ResourceLocation[] iconTexture = new ResourceLocation[4];

    public JokerPowerType(Action<INonStandPower>[] startingAttacks, Action<INonStandPower>[] startingAbilities, Action<INonStandPower> defaultQuickAccess) {
        super(startingAttacks, startingAbilities, defaultQuickAccess, JokerData::new);
    }

    @Override
    public void afterClear(INonStandPower power) {
        super.afterClear(power);
    }

    @Override
    public float getMaxEnergy(INonStandPower power) {
        if (INonStandPower.getNonStandPowerOptional(power.getUser()).isPresent()) {
            int stage = INonStandPower.getNonStandPowerOptional(power.getUser()).map(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getStage)).get().get();
            switch (stage) {
                case 1:
                    return 750;
                case 2:
                    return 1200;
                case 3:
                    return 1500;
                default:
                    return 500;
            }
        }
        return 500;
    }

    @Override
    public void tickUser(LivingEntity entity, INonStandPower power) {
        if (INonStandPower.getNonStandPowerOptional(power.getUser()).isPresent()) {
            if (power.getEnergy() == power.getMaxEnergy() && INonStandPower.getNonStandPowerOptional(power.getUser()).map(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getStage)).get().get() != 3) {
                INonStandPower.getNonStandPowerOptional(power.getUser()).ifPresent(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).ifPresent(joker -> joker.setStage(joker.getStage() + 1)));
            }
            if (INonStandPower.getNonStandPowerOptional(power.getUser()).map(p -> p.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.VAMPIRISM.get() || d.getPreviousPowerType() == ModPowers.ZOMBIE.get()).orElse(false)).orElse(false)) vampirismTick(entity, power);
        }
    }
    private void vampirismTick(LivingEntity entity, INonStandPower power) {
        if (!entity.level.isClientSide()) {
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity) entity).getFoodData().setFoodLevel(17);
            }
            entity.setAirSupply(entity.getMaxAirSupply());

            int difficulty = entity.level.getDifficulty().getId();
            int bloodLevel = bloodLevel(power, difficulty);
            if (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).get().refreshVampBloodLevel(bloodLevel)) {
                updatePassiveEffects(entity, power);
            }
        }
    }
    private static int bloodLevel(INonStandPower power, int difficulty) {
        if (difficulty == 0) {
            return -1;
        }
        float powerMultiplier = 1.0f;
        if (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.VAMPIRISM.get()).orElse(false)) powerMultiplier = 5.0f;
        if (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d -> d.getPreviousPowerType() == ModPowers.ZOMBIE.get()).orElse(false)) powerMultiplier = 7.5f;
        int bloodLevel = Math.min((int) (power.getEnergy() / power.getMaxEnergy() * powerMultiplier), 4);
        bloodLevel += difficulty;
        if (!power.getTypeSpecificData(JokerPowerInit.JOKER.get()).get().getPreviousDataNbt().getBoolean("VampireFullPower")) {
            bloodLevel = Math.max(bloodLevel - 2, 1);
        }
        return bloodLevel;
    }

    @Override
    public float tickEnergy(INonStandPower power) {
        if (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(d ->
                d.getPreviousPowerType() == ModPowers.VAMPIRISM.get() ||
                d.getPreviousPowerType() == ModPowers.ZOMBIE.get()).orElse(false)) {
            World world = power.getUser().level;
            float inc = -GeneralUtil.getOrLast(
                            JojoModConfig.getCommonConfigInstance(world.isClientSide()).bloodTickDown.get(), world.getDifficulty().getId())
                    .floatValue();
            if (power.isUserCreative()) {
                inc = Math.max(inc, 0);
            }
            return power.getEnergy() + inc;
        }
        return power.getEnergy();
    }

    @Override
    public boolean isLeapUnlocked(INonStandPower power) {
        if (INonStandPower.getNonStandPowerOptional(power.getUser()).isPresent()) {
            int stage = INonStandPower.getNonStandPowerOptional(power.getUser()).map(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getStage)).get().get();
            return stage > 1;
        }
        return false;
    }

    @Override
    public void onLeap(INonStandPower power) {
        LivingEntity entity = power.getUser();
        if (!power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getPreviousPowerType).isPresent() && ((power.getUser() instanceof PlayerEntity && !((PlayerEntity) power.getUser()).abilities.instabuild) || !(power.getUser() instanceof PlayerEntity)))
            entity.hurt(new EntityDamageSource("injure", power.getUser()).bypassArmor().bypassInvul().bypassMagic(), 3.5f);
        entity.hurtMarked = false;
    }


    @Override
    public float getLeapStrength(INonStandPower power) {
        if (power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getPreviousPowerType).isPresent()) {
            NonStandPowerType<?> oldPower = power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getPreviousPowerType).get();
            if (oldPower == ModPowers.HAMON.get()) {
                return 2.5f;
            } else if (oldPower == ModPowers.PILLAR_MAN.get()) {
                return 1.5f;
            } else if (oldPower == ModPowers.VAMPIRISM.get()) {
                return 2.0f;
            } else if (oldPower == ModPowers.ZOMBIE.get()) {
                return 1.2f;
            }
        }
        return 2.0f;
    }

    @Override
    public int getLeapCooldownPeriod() {
        return 25;
    }

    @Override
    protected void initPassiveEffects() {
        initAllPossibleEffects(
                () -> Effects.HEALTH_BOOST,
                () -> Effects.REGENERATION,
                () -> Effects.DAMAGE_BOOST,
                () -> Effects.MOVEMENT_SPEED,
                () -> Effects.DIG_SPEED,
                () -> Effects.JUMP,
                () -> Effects.NIGHT_VISION,

                () -> Effects.MOVEMENT_SLOWDOWN,
                () -> Effects.DIG_SLOWDOWN,
                () -> Effects.WEAKNESS,
                () -> Effects.BLINDNESS);
    }
    @Override
    public int getPassiveEffectLevel(Effect effect, INonStandPower power) {
        LivingEntity entity = power.getUser();
        JokerData jokerData = power.getTypeSpecificData(this).get();
        if (jokerData.getPreviousPowerType() == ModPowers.VAMPIRISM.get()) {
            int difficulty = entity.level.getDifficulty().getId();
            int bloodLevel = bloodLevel(power, difficulty);

            if (effect == Effects.HEALTH_BOOST) return difficulty * 5 - 1;
            if (effect == Effects.REGENERATION) return Math.min(bloodLevel - 2, 4);
            if (effect == Effects.DAMAGE_BOOST) return bloodLevel - 5;
            if (effect == Effects.MOVEMENT_SPEED) return bloodLevel - 4;
            if (effect == Effects.DIG_SPEED) return bloodLevel - 4;
            if (effect == Effects.JUMP) return bloodLevel - 4;
            if (effect == Effects.NIGHT_VISION) return 0;
            return -1;
        } else if (jokerData.getPreviousPowerType() == ModPowers.ZOMBIE.get()) {
            int difficulty = entity.level.getDifficulty().getId();
            int bloodLevel = bloodLevel(power, difficulty);
            if (effect == Effects.HEALTH_BOOST)                                 return difficulty * 2;
            if (effect == Effects.DAMAGE_BOOST)                                 return bloodLevel - 5;
            if (effect == Effects.MOVEMENT_SPEED)                               return bloodLevel - 5;
            if (effect == Effects.DIG_SPEED)                                    return bloodLevel - 5;
            if (effect == Effects.JUMP)                                         return bloodLevel - 5;
            if (effect == Effects.NIGHT_VISION)                                 return 0;
        }
        return -1;
    }

    @Override
    public boolean isReplaceableWith(NonStandPowerType<?> newType) {
        return false;
    }

    @Override
    public boolean keepOnDeath(INonStandPower power) {
        return true;
    }

    @Override
    public void clAddMissingActions(ControlScheme controlScheme, INonStandPower power) {
        super.clAddMissingActions(controlScheme, power);
        JokerData data = power.getTypeSpecificData(this).get();
        if (data.getPreviousPowerType() == ModPowers.HAMON.get()) {

        } else if (data.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()) {

        } else if (data.getPreviousPowerType() == ModPowers.VAMPIRISM.get()) {
            controlScheme.addIfMissing(ControlScheme.Hotbar.LEFT_CLICK, ModVampirismActions.VAMPIRISM_CLAW_LACERATE.get());
            controlScheme.addIfMissing(ControlScheme.Hotbar.LEFT_CLICK, ModVampirismActions.VAMPIRISM_BLOOD_DRAIN.get());

            if (data.getPreviousDataNbt().getBoolean("VampireFullPower")) {
                controlScheme.addIfMissing(ControlScheme.Hotbar.LEFT_CLICK, ModVampirismActions.VAMPIRISM_FREEZE.get());
                controlScheme.addIfMissing(ControlScheme.Hotbar.LEFT_CLICK, ModVampirismActions.VAMPIRISM_SPACE_RIPPER_STINGY_EYES.get());
                controlScheme.addIfMissing(ControlScheme.Hotbar.RIGHT_CLICK, ModVampirismActions.VAMPIRISM_BLOOD_GIFT.get());
                controlScheme.addIfMissing(ControlScheme.Hotbar.RIGHT_CLICK, ModVampirismActions.VAMPIRISM_ZOMBIE_SUMMON.get());
                controlScheme.addIfMissing(ControlScheme.Hotbar.RIGHT_CLICK, ModVampirismActions.VAMPIRISM_DARK_AURA.get());
            }
            if (data.getPreviousDataNbt().getBoolean("VampireHamonUser")) controlScheme.addIfMissing(ControlScheme.Hotbar.RIGHT_CLICK, ModVampirismActions.VAMPIRISM_HAMON_SUICIDE.get());
        } else if (data.getPreviousPowerType() == ModPowers.ZOMBIE.get()) {
            controlScheme.addIfMissing(ControlScheme.Hotbar.LEFT_CLICK, ModVampirismActions.VAMPIRISM_CLAW_LACERATE.get());
            controlScheme.addIfMissing(ControlScheme.Hotbar.LEFT_CLICK, ModZombieActions.ZOMBIE_DEVOUR.get());
        }
    }

    @Override
    public boolean isActionLegalInHud(Action<INonStandPower> action, INonStandPower power) {
        JokerData data = power.getTypeSpecificData(this).get();
        if (data.getPreviousPowerType() == ModPowers.HAMON.get()) {

        } else if (data.getPreviousPowerType() == ModPowers.PILLAR_MAN.get()) {

        } else if (data.getPreviousPowerType() == ModPowers.VAMPIRISM.get()) {
            if (action == ModVampirismActions.VAMPIRISM_CLAW_LACERATE.get() || action == ModVampirismActions.VAMPIRISM_BLOOD_DRAIN.get() ||
            (data.getPreviousDataNbt().getBoolean("VampireFullPower") &&
                action == ModVampirismActions.VAMPIRISM_FREEZE.get() ||
                action == ModVampirismActions.VAMPIRISM_SPACE_RIPPER_STINGY_EYES.get() ||
                action == ModVampirismActions.VAMPIRISM_ZOMBIE_SUMMON.get() ||
                    action == ModVampirismActions.VAMPIRISM_DARK_AURA.get() ||
                    action == ModVampirismActions.VAMPIRISM_BLOOD_GIFT.get()) ||
            (data.getPreviousDataNbt().getBoolean("VampireHamonUser") && action == ModVampirismActions.VAMPIRISM_HAMON_SUICIDE.get())) return true;
        } else if (data.getPreviousPowerType() == ModPowers.ZOMBIE.get()) {
            if (
            action == ModVampirismActions.VAMPIRISM_CLAW_LACERATE.get() ||
            action == ModZombieActions.ZOMBIE_DEVOUR.get()) return true;
        }
        return super.isActionLegalInHud(action, power);
    }

    @Override
    public ResourceLocation getIconTexture(@Nullable INonStandPower power) {
        if (INonStandPower.getNonStandPowerOptional(power.getUser()).isPresent()) {
            int stage = INonStandPower.getNonStandPowerOptional(power.getUser()).map(pow -> pow.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(JokerData::getStage)).get().get();
            if (iconTexture[stage] == null) {
                iconTexture[stage] = JojoModUtil.makeTextureLocation("power", getRegistryName().getNamespace(),
                        getRegistryName().getPath() + "_" + Objects.toString(stage));
            }
            return this.iconTexture[stage];
        }
        return JojoModUtil.makeTextureLocation("power", getRegistryName().getNamespace(),
                getRegistryName().getPath());
    }
    @Override
    public float getTargetResolveMultiplier(INonStandPower power, IStandPower attackingStand) {
        return 0;
    }
}
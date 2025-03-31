package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
import com.github.standobyte.jojo.init.power.non_stand.hamon.ModHamonSkills;
import com.github.standobyte.jojo.power.impl.nonstand.TypeSpecificData;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData;
import com.github.standobyte.jojo.power.impl.nonstand.type.hamon.MainHamonSkillsManager;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//todo this shit ain't working
@Mixin(value = HamonData.class, remap = false)
public abstract class HamonDataMixin extends TypeSpecificData {
    @Shadow private float breathingTrainingDayBonus;
    @Shadow private int canSkipTrainingDays;
    @Shadow private float breathStability;
    @Shadow private float prevBreathStability;
    @Shadow private int noEnergyDecayTicks;
    @Shadow public abstract float getMaxBreathStability();
    @Shadow private int ticksMaskWithNoHamonBreath;
    @Shadow private int ticksNoBreathStabilityInc;
    @Shadow private boolean tcsa;
    @Shadow private int hamonStrengthPoints;
    @Shadow private int hamonStrengthLevel;
    @Shadow private int hamonControlPoints;
    @Shadow private int hamonControlLevel;
    @Shadow private float pointsIncFrac;
    @Shadow private float breathingTrainingLevel;
    @Shadow private void recalcHamonDamage() {};
    @Shadow private MainHamonSkillsManager hamonSkills;

    @Inject(method = "onPowerGiven", at = @At("HEAD"), cancellable = true, remap = false)
    public void restoreData(NonStandPowerType<?> oldType, TypeSpecificData oldData, CallbackInfo ci) {
        if (oldType == JokerPowerInit.JOKER.get()) {
            JokerData data = (JokerData) oldData;
            this.readNBT(((JokerData) oldData).getPreviousDataNbt());
            if (!power.getUser().level.isClientSide()) {
                power.setEnergy(data.getPreviousDataNbt().getFloat("Energy"));
            }
            ci.cancel();
        }
    }

    @Inject(method = "readNBT", at = @At(value = "INVOKE", target = "Lcom/github/standobyte/jojo/power/impl/nonstand/type/hamon/MainHamonSkillsManager;fromNbt(Lnet/minecraft/nbt/CompoundNBT;)V"), cancellable = true, remap = false)
    public void readNBT(CompoundNBT nbt, CallbackInfo ci) {
        if (power.getType() == JokerPowerInit.JOKER.get() && power.getTypeSpecificData(JokerPowerInit.JOKER.get()).map(j -> j.getPreviousPowerType() == ModPowers.HAMON.get()).orElse(false)) {
            CompoundNBT exercises = nbt.getCompound("Exercises");
            breathingTrainingDayBonus = nbt.getFloat("TrainingBonus");
            canSkipTrainingDays = nbt.getInt("CanSkipDays");
            breathStability = nbt.contains("BreathStability") ? nbt.getFloat("BreathStability") : getMaxBreathStability();
            prevBreathStability = breathStability;
            noEnergyDecayTicks = nbt.getInt("EnergyTicks");
            ticksMaskWithNoHamonBreath = nbt.getInt("MaskNoBreathTicks");
            ticksNoBreathStabilityInc = nbt.getInt("NoBreathIncTicks");
            tcsa = nbt.getBoolean("TCSA");
            ci.cancel();
        }
    }
}

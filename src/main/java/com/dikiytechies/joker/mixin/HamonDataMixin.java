package com.dikiytechies.joker.mixin;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.init.power.non_stand.ModPowers;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;

import static com.github.standobyte.jojo.power.impl.nonstand.type.hamon.HamonData.levelFromPoints;

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
    @Shadow public void setExerciseTicks(int[] ticks, boolean clientSide) {}
    @Shadow private EnumMap<HamonData.Exercise, Integer> exerciseTicks;

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

    @Inject(method = "writeNBT", at = @At("HEAD"), cancellable = true, remap = false)
    public void writeNBT(CallbackInfoReturnable<CompoundNBT> cir) {
        if (power == null) {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("StrengthPoints", hamonStrengthPoints);
            nbt.putInt("ControlPoints", hamonControlPoints);
            nbt.putFloat("PointsIncFrac", pointsIncFrac);
            nbt.putFloat("BreathingTechnique", breathingTrainingLevel);
            nbt.put("Skills", ((MainHamonSkillsManagerAccessor) hamonSkills).skillsToNbt());
            CompoundNBT exercises = new CompoundNBT();
            for (HamonData.Exercise exercise : HamonData.Exercise.values()) {
                exercises.putInt(exercise.toString(), Math.min(exerciseTicks.get(exercise), exercise.getMaxTicks((HamonData) ((Object) this))));
            }
            nbt.put("Exercises", exercises);
            nbt.putFloat("TrainingBonus", breathingTrainingDayBonus);
            nbt.putFloat("CanSkipDays", canSkipTrainingDays);
            nbt.putFloat("BreathStability", breathStability);
            nbt.putInt("EnergyTicks", noEnergyDecayTicks);
            nbt.putInt("MaskNoBreathTicks", ticksMaskWithNoHamonBreath);
            nbt.putInt("NoBreathIncTicks", ticksNoBreathStabilityInc);
            nbt.putBoolean("TCSA", tcsa);
            cir.setReturnValue(nbt);
            cir.cancel();
        }
    }

    @Inject(method = "readNBT", at = @At("HEAD"), cancellable = true, remap = false)
    public void readNBT(CompoundNBT nbt, CallbackInfo ci) {
        if (power == null) {
            hamonStrengthPoints = nbt.getInt("StrengthPoints");
            hamonStrengthLevel = levelFromPoints(hamonStrengthPoints);
            hamonControlPoints = nbt.getInt("ControlPoints");
            hamonControlLevel = levelFromPoints(hamonControlPoints);
            pointsIncFrac = nbt.getFloat("PointsIncFrac");
            breathingTrainingLevel = nbt.getFloat("BreathingTechnique");
            recalcHamonDamage();
            ((MainHamonSkillsManagerAccessor) hamonSkills).skillsFromNbt(nbt.getCompound("Skills"));
            CompoundNBT exercises = nbt.getCompound("Exercises");
            int[] exercisesNbt = new int[HamonData.Exercise.values().length];
            for (HamonData.Exercise exercise : HamonData.Exercise.values()) {
                exercisesNbt[exercise.ordinal()] = exercises.getInt(exercise.toString());
            }
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

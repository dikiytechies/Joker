package com.dikiytechies.joker.entity.mob;

import com.dikiytechies.joker.client.ui.screen.EffectSelectionScreen;
import com.dikiytechies.joker.init.AddonItems;
import com.dikiytechies.joker.init.Sounds;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.fromserver.TrJokerStatePacket;
import com.github.standobyte.jojo.init.ModItems;
import com.github.standobyte.jojo.potion.StatusEffect;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

public class JokerIggyEntity extends MobEntity implements INPC, IAnimatable, IEntityAdditionalSpawnData {
    public int animatedTicks;
    private boolean sleepy;
    private boolean isCasting;
    private int ticksLeft;
    private PlayerEntity castTarget;
    private boolean isSmoking;
    private boolean isCoughing;

    private AnimationFactory factory = new AnimationFactory(this);
    public JokerIggyEntity(EntityType<? extends JokerIggyEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksLeft > 0) {
            ticksLeft--;
            tickCasting();
            tickSmoking();
            tickCoughing();
        }
        if (level.isClientSide()) {
            animatedTicks++;
            if (animatedTicks % 40 == 0) {
                animatedTicks = 0;
            }
        }
    }

    private void tickCasting() {
        if (this.isCasting && ticksLeft == 30) {
            level.playSound(null, castTarget.blockPosition(), Sounds.JOKER_IGGY_RINGTONE.get(), SoundCategory.RECORDS, 1.0f, 1.0f);
        }
        if (this.isCasting && ticksLeft == 0) {
            this.isCasting = false;
            List<StatusEffect> effectList = new LinkedList<>();
            for (EffectSelectionScreen.EffectTypes effectType : EffectSelectionScreen.EffectTypes.values()) {
                if (!castTarget.hasEffect(effectType.effect)) {
                    effectList.add(effectType.effect);
                }
            }
            if (effectList.size() > 1) {
                if (!level.isClientSide()) {
                    castTarget.addEffect(new EffectInstance(effectList.get(random.nextInt(effectList.size())), 168000, 0));
                    level.playSound(null, castTarget.blockPosition(), SoundEvents.PLAYER_LEVELUP, castTarget.getSoundSource(), 1.0f, 1.0f);
                    if (random.nextInt() < 10) {
                        level.getServer().getPlayerList().getPlayers().forEach(sp -> {
                            setJokerSmoking(true, sp, 150);
                        });
                    }
                }
            } else {
                for (EffectSelectionScreen.EffectTypes effectType : EffectSelectionScreen.EffectTypes.values()) {
                    if (!level.isClientSide()) castTarget.removeEffect(effectType.effect);
                }
                setCoughing(true, 25);
            }
            this.castTarget = null;
        }
    }
    private void tickSmoking() {
        if (this.isSmoking && ticksLeft == 0) {
            isSmoking = false;
        }
    }
    private void tickCoughing() {
        if (this.isCoughing && ticksLeft == 0) {
            isCoughing = false;
            ItemStack maskStack = new ItemStack(AddonItems.MASK.get());
            maskStack.enchant(Enchantments.BINDING_CURSE, 1);
            ItemEntity mask = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), maskStack);
            float f = -MathHelper.sin(this.getRotationVector().y * ((float)Math.PI / 180F)) * MathHelper.cos(this.getRotationVector().x * ((float)Math.PI / 180F));
            float f1 = -MathHelper.sin((this.getRotationVector().x + 0) * ((float)Math.PI / 180F));
            float f2 = MathHelper.cos(this.getRotationVector().y * ((float)Math.PI / 180F)) * MathHelper.cos(this.getRotationVector().x * ((float)Math.PI / 180F));
            mask.setDeltaMovement(f, f1, f2);
            level.addFreshEntity(mask);
        }
    }

    public void setJokerSleepy(boolean sleepy, PlayerEntity player) {
        this.sleepy = sleepy;
        if (player instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrJokerStatePacket(this.getId(), sleepy, player.getId(), TrJokerStatePacket.JokerDataType.SLEEPY), (ServerPlayerEntity) player);
        }
    }
    public boolean isSleepy() { return sleepy; }
    public void setCasting(boolean isCasting, int ticksLeft, PlayerEntity castTarget) {
        this.isCasting = isCasting;
        this.ticksLeft = ticksLeft;
        this.castTarget = castTarget;
        level.playSound(null, this.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, this.getSoundSource(), 1.0f, 1.0f);
        level.playSound(null, this.blockPosition(), Sounds.JOKER_IGGY_LAUGH.get(), this.getSoundSource(), 1.0f, 1.0f);
    }
    public void setJokerSmoking(boolean isSmoking, PlayerEntity player, int ticksLeft) {
        this.isSmoking = isSmoking;
        this.ticksLeft = ticksLeft;
        if (player instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrJokerStatePacket(this.getId(), isSmoking, player.getId(), TrJokerStatePacket.JokerDataType.SMOKING, ticksLeft), (ServerPlayerEntity) player);
        }
    }
    public boolean isSmoking() { return isSmoking; }
    public void setCoughing(boolean isCoughing, int ticksLeft) {
        this.isCoughing = isCoughing;
        this.ticksLeft = ticksLeft;
        level.playSound(null, this.blockPosition(), SoundEvents.COW_AMBIENT, this.getSoundSource(), 1.0f, 1.0f);
    }
    @Override
    public boolean removeWhenFarAway(double distanceFromPlayer) {
        return false;
    }
    // todo fix shadow consume
    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        lookAt(player, player.getRotationVector().x, player.getRotationVector().y);
        boolean prevSleep = sleepy;
        if (!level.isClientSide() && level.isDay() != sleepy) {
            level.getServer().getPlayerList().getPlayers().forEach(sp -> {
                setJokerSleepy(level.isDay(), sp);
            });
        }
        if (prevSleep == isSleepy() && !isSleepy() && !isCasting && !isSmoking && INonStandPower.getNonStandPowerOptional(player).map(p -> p.getType() != JokerPowerInit.JOKER.get()).orElse(false)) {
            if (player.getItemInHand(Hand.MAIN_HAND).getItem() == ModItems.AJA_STONE.get()) {
                player.getItemInHand(Hand.MAIN_HAND).shrink(1);
                player.swing(Hand.MAIN_HAND);
                setCasting(true, 40, player);
                return ActionResultType.CONSUME;
            } else if (player.getItemInHand(Hand.MAIN_HAND).getItem() == Items.AIR && player.getItemInHand(Hand.OFF_HAND).getItem() == ModItems.AJA_STONE.get()) {
                if (!player.abilities.instabuild) player.getItemInHand(Hand.OFF_HAND).shrink(1);
                player.swing(Hand.OFF_HAND);
                setCasting(true, 40, player);
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.SUCCESS;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        try {
            if (isSmoking) {
                animate(event, "animation.joker_iggy.smoking", "animation.joker_iggy.smoking_idle", "animation.joker_iggy.cast");
                return PlayState.CONTINUE;
            }
            if (isCoughing) {
                animate(event, "animation.joker_iggy.cough", "animation.joker_iggy.cough", "animation.joker_iggy.cast");
                return PlayState.CONTINUE;
            }
            if (event.isMoving() && !sleepy || isCasting) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.cast", true));
                return PlayState.CONTINUE;
            }
            if (sleepy) {
                animate(event, "animation.joker_iggy.sleep", "animation.joker_iggy.sleep_idle", "animation.joker_iggy.sleep_idle");
            } else if (event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.sleep_idle") ||
                    event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.awake")) {
                animate(event, "animation.joker_iggy.awake", "animation.joker_iggy.idle", "animation.joker_iggy.cast");
            } else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.idle", true));
            return PlayState.CONTINUE;
        } catch (NullPointerException e) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.idle", true));
            return PlayState.CONTINUE;
        }
    }
    @Deprecated
    private <E extends IAnimatable> void makeJokerSleep(AnimationEvent<E> event) {
        if (event.getController().getCurrentAnimation() != null && ((event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.sleep") &&
                event.getController().getAnimationState().equals(AnimationState.Stopped)) ||
                event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.sleep_idle"))) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.sleep_idle", true));
        } else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.sleep"));
    }
    @Deprecated
    private <E extends IAnimatable> void makeJokerAwake(AnimationEvent<E> event) {
        if (event.getController().getCurrentAnimation() != null && ((event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.sleep") &&
                event.getController().getAnimationState().equals(AnimationState.Stopped)) ||
                event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.cast"))) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.idle", true));
        } else if (event.getController().getCurrentAnimation() != null && !(event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.cast") || event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.idle"))) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.awake"));
        }
    }
    private <E extends IAnimatable> boolean animate(AnimationEvent<E> event, String animation, String idle, String to) {
        if (event.getController().getCurrentAnimation() != null && ((event.getController().getCurrentAnimation().animationName.equals(animation) &&
                event.getController().getAnimationState().equals(AnimationState.Stopped)) ||
                event.getController().getCurrentAnimation().animationName.equals(to))) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(idle, true));
            return true;
        } else if (event.getController().getCurrentAnimation() != null && !(event.getController().getCurrentAnimation().animationName.equals(to) || event.getController().getCurrentAnimation().animationName.equals(idle))) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation(animation));
            return true;
        }
        return false;
    }
    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() { return factory; }
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason,
                                           @Nullable ILivingEntityData additionalData, @Nullable CompoundNBT nbt) {
        setPersistenceRequired();
        return super.finalizeSpawn(world, difficulty, reason, additionalData, nbt);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource != DamageSource.OUT_OF_WORLD) return true;
        return super.isInvulnerableTo(damageSource);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        CompoundNBT jokerData = new CompoundNBT();
        jokerData.putBoolean("IsSleepy", sleepy);
        jokerData.putInt("TicksLeft", ticksLeft);
        jokerData.putBoolean("IsCoughing", isCoughing);
        jokerData.putBoolean("IsSmoking", isSmoking);
        nbt.put("JokerIggyData", jokerData);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        CompoundNBT jokerData = nbt.getCompound("JokerIggyData");
        sleepy = jokerData.getBoolean("IsSleepy");
        ticksLeft = jokerData.getInt("TicksLeft");
        isCoughing = jokerData.getBoolean("IsCoughing");
        isSmoking = jokerData.getBoolean("IsSmoking");
    }

    @Deprecated
    @Override
    public void writeSpawnData(PacketBuffer buffer) {

    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {

    }

    @Deprecated
    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MobEntity.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.ATTACK_SPEED, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.5D)
                .add(ForgeMod.SWIM_SPEED.get(), 2.0D);
    }
}

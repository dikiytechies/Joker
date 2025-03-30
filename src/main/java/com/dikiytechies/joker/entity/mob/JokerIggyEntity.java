package com.dikiytechies.joker.entity.mob;

import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.fromserver.TrJokerSleepStatePacket;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.init.ModItems;
import com.github.standobyte.jojo.item.AjaStoneItem;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.AirItem;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
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

public class JokerIggyEntity extends MobEntity implements INPC, IAnimatable {
    private boolean sleepy;
    private AnimationFactory factory = new AnimationFactory(this);
    public JokerIggyEntity(EntityType<? extends JokerIggyEntity> type, World world) {
        super(type, world);
    }

    public void setJokerSleepy(boolean sleepy, PlayerEntity player) {
        this.sleepy = sleepy;
        if (player instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrJokerSleepStatePacket(this.getId(), sleepy, player.getId()), (ServerPlayerEntity) player);
        }
    }
    public boolean isSleepy() { return sleepy; }

    @Override
    public boolean removeWhenFarAway(double distanceFromPlayer) {
        return false;
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        boolean prevSleep = sleepy;
        if (!level.isClientSide()) setJokerSleepy(level.isDay(), player);
        if (prevSleep == isSleepy() && !isSleepy()) {
            if (player.getItemInHand(Hand.MAIN_HAND).getItem() == ModItems.AJA_STONE.get()) {
                player.getItemInHand(Hand.MAIN_HAND).shrink(1);
                player.swing(Hand.MAIN_HAND);
                return ActionResultType.CONSUME;
            } else if (player.getItemInHand(Hand.MAIN_HAND).getItem() == Items.AIR && player.getItemInHand(Hand.OFF_HAND).getItem() == ModItems.AJA_STONE.get()) {
                if (!player.abilities.instabuild) player.getItemInHand(Hand.OFF_HAND).shrink(1);
                player.swing(Hand.OFF_HAND);
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.SUCCESS;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (event.isMoving() && !sleepy) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.cast", true));
            return PlayState.CONTINUE;
        }
        //event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.idle"));
        if (sleepy) {
            makeJokerSleep(event);
        } else makeJokerAwake(event);
        return PlayState.CONTINUE;
    }
    private <E extends IAnimatable> void makeJokerSleep(AnimationEvent<E> event) {
        if (event.getController().getCurrentAnimation() != null && ((event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.sleep") &&
                event.getController().getAnimationState().equals(AnimationState.Stopped)) ||
                event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.sleep_idle"))) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.sleep_idle", true));
        } else event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.sleep"));
    }
    private <E extends IAnimatable> void makeJokerAwake(AnimationEvent<E> event) {
        if (event.getController().getCurrentAnimation() != null && ((event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.sleep") &&
                event.getController().getAnimationState().equals(AnimationState.Stopped)) ||
                event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.cast"))) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.idle", true));
        } else if (!(event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.cast") || event.getController().getCurrentAnimation().animationName.equals("animation.joker_iggy.idle"))) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.joker_iggy.awake"));
        }
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

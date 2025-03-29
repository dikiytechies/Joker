package com.dikiytechies.joker.capability;

import com.dikiytechies.joker.client.ui.screen.EffectSelectionScreen;
import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.fromserver.*;
import com.dikiytechies.joker.potion.GreedStatusEffect;
import com.dikiytechies.joker.potion.PrideStatusEffect;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerPowerType;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.github.standobyte.jojo.power.impl.nonstand.NonStandPower;
import com.github.standobyte.jojo.util.mc.MCUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public class JokerUtilCap implements INBTSerializable<CompoundNBT> {
    private final LivingEntity livingEntity;
    private boolean swanSong; //upside of sloth
    private float borrowedHealth; //upside of sloth
    private float delayedDamage; //downside of sloth
    private short damageDelay; //downside of sloth
    public final short DELAY = 35; //downside of sloth
    private double stolenAmount;
    private int modifierTicksLeft = 0;
    private int maxTicks;
    private int prideTargetStacks;
    private int prideTargetStacksTicksLeft;
    private int prideAttackerStacks;
    private int prideAttackerStacksTicksLeft;
    private final int PRIDE_TICKS_ATTACKER = 900;
    private final int PRIDE_TICKS_TARGET = 140;
    private PrideStatusEffect.MultiCastType multiCastType;
    private int multiCastTicksLeft;
    private LivingEntity multiCastTarget;
    private EffectSelectionScreen.EffectTypes favorite;
    private EffectSelectionScreen.EffectTypes activeEffect;
    private float greedMaxHealth;
    public JokerUtilCap(LivingEntity livingEntity) { this.livingEntity = livingEntity; }

    public void setSwanSong(boolean value) {
        this.swanSong = value;
        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;
            AddonPackets.sendToClient(new TrSlothEffectPacket(livingEntity.getId(), swanSong, borrowedHealth), player);
        }
    }
    public boolean isSwanSong() {
        return swanSong;
    }
    public void setBorrowedHealth(float value) {
        this.borrowedHealth = value;
        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;
            AddonPackets.sendToClient(new TrSlothEffectPacket(livingEntity.getId(), swanSong, borrowedHealth), player);
        }
    }
    public float getBorrowedHealth() { return borrowedHealth; }
    public void setDelayedDamage(float delayedDamage) {
        this.delayedDamage = delayedDamage;
        this.damageDelay = DELAY;
        if (livingEntity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) livingEntity;
            System.out.println(livingEntity.getId() + " " + delayedDamage + " " + player);
            //todo fix serverplayer == livingentity crash
            AddonPackets.sendToClient(new TrSlothDebuffPacket(livingEntity.getId(), this.delayedDamage), player);
        }
    }
    public void addDelayedDamage(float delayedDamage) {
        if (delayedDamage == 0) {
            setDelayedDamage(delayedDamage);
        } else {
            setDelayedDamage(delayedDamage + getDelayedDamage());
        }
    }
    public float getDelayedDamage() { return delayedDamage; }
    public void delayTick() { damageDelay--; }
    public short getDamageDelay() { return damageDelay; }
    public int getEnvyTicksLeft() { return modifierTicksLeft; }
    public int getMaxEnvyTicks() { return maxTicks; }
    public double getStolenAmount() { return stolenAmount; }
    public void setStolenAmount(double amount) {
        this.stolenAmount = amount;
        this.maxTicks = Math.max(modifierTicksLeft, maxTicks);
        if (livingEntity instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrEnvyStealPacket(livingEntity.getId(), amount, modifierTicksLeft), (ServerPlayerEntity) livingEntity);
        }
    }
    public void setStolenAttributesAmount(double amount, int ticks) {
        this.stolenAmount = amount;
        this.modifierTicksLeft = Math.max(modifierTicksLeft, ticks);
        this.maxTicks = Math.max(modifierTicksLeft, maxTicks);
        if (livingEntity instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrEnvyStealPacket(livingEntity.getId(), amount, ticks), (ServerPlayerEntity) livingEntity);
        }
    }
    public void setStolenAttributesAmount(double amount, int ticks, boolean force) {
        this.stolenAmount = amount;
        if (!force) {
            this.modifierTicksLeft = Math.max(modifierTicksLeft, ticks);
        } else this.modifierTicksLeft = ticks;
        this.maxTicks = Math.max(modifierTicksLeft, maxTicks);
        if (livingEntity instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrEnvyStealPacket(livingEntity.getId(), amount, ticks), (ServerPlayerEntity) livingEntity);
        }
    }
    public void addStolenAmount(double amount, int ticks) {
        setStolenAttributesAmount(amount + getStolenAmount(), ticks);
    }
    public void envyTick() {
        if (modifierTicksLeft > 0 && maxTicks != 0) {
            if (Math.abs(modifierTicksLeft / (maxTicks / 2)) < 1 || Math.abs(modifierTicksLeft / (maxTicks / 2)) == 2) {
                updateModifiers(getStolenAmount() * modifierTicksLeft / (double) (maxTicks / 2));
            }
            this.modifierTicksLeft--;
        } else if (maxTicks > 0) {
            removeModifiers();
            maxTicks = 0;
            setStolenAttributesAmount(0, 0);
        }
    }
    private static final AttributeModifier MAX_HEALTH = new AttributeModifier(UUID.fromString("c6b590b1-d3a4-4c9c-bc8d-971867eba956"), "Stolen max health", 0.02, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier FOLLOW_RANGE = new AttributeModifier(UUID.fromString("852753d9-25e4-4940-b9a3-8ab9229142d3"), "Stolen follow range", 0.08, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier KNOCKBACK_RESISTANCE = new AttributeModifier(UUID.fromString("8fd2ef4e-6c8c-4dbb-a262-b9a9986cb4a8"), "Stolen knockback resistance", 0.01, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier MOVEMENT_SPEED = new AttributeModifier(UUID.fromString("ee45f510-02df-4b9a-a8c8-635e4edaf971"), "Stolen movement speed", 0.04, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier FLYING_SPEED = new AttributeModifier(UUID.fromString("84b7cf86-452d-41b3-a571-5de6d3ec9e08"), "Stolen flying speed", 0.04, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ATTACK_DAMAGE = new AttributeModifier(UUID.fromString("38ba9e02-9c71-429e-ae3a-7b9e77cbd240"), "Stolen attack damage", 0.02, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ATTACK_KNOCKBACK = new AttributeModifier(UUID.fromString("feec8a67-2757-435d-b8c8-4843c8d0f3b4"), "Stolen attack knockback", 0.01, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ATTACK_SPEED = new AttributeModifier(UUID.fromString("1e75f3a4-147d-4787-b870-dbe3bebfa387"), "Stolen attack speed", 0.04, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ARMOR = new AttributeModifier(UUID.fromString("0452cd66-07b5-4ba1-928f-36e38eeb1b87"), "Stolen armor", 0.014, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ARMOR_TOUGNHESS = new AttributeModifier(UUID.fromString("5d253268-0737-4bda-bc76-eea166e4710e"), "Stolen armor toughness", 0.01, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier LUCK = new AttributeModifier(UUID.fromString("fe4bc6d8-1381-454f-82c6-69bbe38ab726"), "Stolen luck", 0.08, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier REINFORCEMENT_CHANCE = new AttributeModifier(UUID.fromString("8072c623-dc2e-4099-8f22-85e9df7e70cc"), "Stolen spawn reinforcement chance", 0.08, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier JUMP_STRENGTH = new AttributeModifier(UUID.fromString("e82b644e-638c-446e-8fa6-69df4f13aada"), "Stolen jump strength", 0.08, AttributeModifier.Operation.MULTIPLY_BASE);
    private void updateModifiers(double amount) {
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.FOLLOW_RANGE, FOLLOW_RANGE, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.FLYING_SPEED, FLYING_SPEED, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.ARMOR, ARMOR, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGNHESS, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.LUCK, LUCK, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.SPAWN_REINFORCEMENTS_CHANCE, REINFORCEMENT_CHANCE, amount);
        MCUtil.applyAttributeModifierMultiplied(livingEntity, Attributes.JUMP_STRENGTH, JUMP_STRENGTH, amount);
    }
    private void removeModifiers() {
        MCUtil.removeAttributeModifier(livingEntity, Attributes.MAX_HEALTH, MAX_HEALTH);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.FOLLOW_RANGE, FOLLOW_RANGE);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.FLYING_SPEED, FLYING_SPEED);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.ARMOR, ARMOR);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGNHESS);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.LUCK, LUCK);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.SPAWN_REINFORCEMENTS_CHANCE, REINFORCEMENT_CHANCE);
        MCUtil.removeAttributeModifier(livingEntity, Attributes.JUMP_STRENGTH, JUMP_STRENGTH);
    }

    public void setPrideStacks(int stacks, TrPrideStacksPacket.StackType stackType) {
        if (stackType == TrPrideStacksPacket.StackType.TARGET) {
            this.prideTargetStacks = stacks;
            this.prideTargetStacksTicksLeft = Math.max(this.prideTargetStacksTicksLeft, PRIDE_TICKS_TARGET);
        } else if (stackType == TrPrideStacksPacket.StackType.ATTACKER) {
            this.prideAttackerStacks = stacks;
            this.prideAttackerStacksTicksLeft = Math.max(this.prideAttackerStacksTicksLeft, PRIDE_TICKS_ATTACKER);
        } else if (stackType == TrPrideStacksPacket.StackType.TARGET_TICK) {
            this.prideTargetStacksTicksLeft = stacks;
        } else if (stackType == TrPrideStacksPacket.StackType.ATTACKER_TICK)
            this.prideAttackerStacksTicksLeft = stacks;
        if (livingEntity instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrPrideStacksPacket(livingEntity.getId(), stacks, stackType), (ServerPlayerEntity) livingEntity);
        }
    }

    public void setPrideTargetStacks(int stacks) {
        setPrideStacks(stacks, TrPrideStacksPacket.StackType.TARGET);
    }

    public void setPrideAttackerStacks(int stacks) {
        setPrideStacks(stacks, TrPrideStacksPacket.StackType.ATTACKER);
    }

    public void addPrideTargetStacks(int stacks) {
        setPrideTargetStacks(getPrideTargetStacks() + stacks);
    }

    public void addPrideAttackerStacks(int stacks) {
        setPrideAttackerStacks(getPrideAttackerStacks() + stacks);
    }

    public int getPrideTargetStacks() { return prideTargetStacks; }
    public int getPrideAttackerStacks() { return prideAttackerStacks; }
    public int getPrideTargetTicks() { return prideTargetStacksTicksLeft; }
    public int getPrideAttackerTicks() { return prideAttackerStacksTicksLeft; }
    public void setPrideTargetStacksTicks(int ticks) {
        setPrideStacks(ticks, TrPrideStacksPacket.StackType.TARGET_TICK);
    }
    public void setPrideAttackerTicks(int ticks) {
        setPrideStacks(ticks, TrPrideStacksPacket.StackType.ATTACKER_TICK);
    }

    public void prideTick() {
        if (prideTargetStacksTicksLeft > 0) {
            prideTargetStacksTicksLeft--;
            if (prideTargetStacksTicksLeft == 0) {
                setPrideTargetStacks(0);
            }
        }
        if (prideAttackerStacksTicksLeft > 0) {
            prideAttackerStacksTicksLeft--;
            if (prideAttackerStacksTicksLeft == 0) {
                setPrideAttackerStacks(0);
            }
        }//todo multicast for attacks and abilities
        if (multiCastTicksLeft > 0) {
            if (multiCastType.delay - multiCastTicksLeft == 0) {
                if (PrideStatusEffect.getMultiCastSound(multiCastType) != null) {
                    livingEntity.level.playSound(null, livingEntity.blockPosition(), PrideStatusEffect.getMultiCastSound(multiCastType), livingEntity.getSoundSource(), 1.0f, 1.0f);
                    livingEntity.swing(Hand.MAIN_HAND);
                }
            } else if (multiCastTicksLeft == 1) {
                setPrideMultiCast(PrideStatusEffect.MultiCastType.X1);
                multiCastTicksLeft++;
            } else if (PrideStatusEffect.MultiCastType.X3.delay - multiCastTicksLeft == 0) {
                livingEntity.swing(Hand.MAIN_HAND);
            } else if (PrideStatusEffect.MultiCastType.X2.delay - multiCastTicksLeft == 0) {
                livingEntity.swing(Hand.MAIN_HAND);
            }
            multiCastTicksLeft--;
        }
    }

    public void setPrideMultiCast(PrideStatusEffect.MultiCastType type) {
        this.multiCastType = type;
        this.multiCastTicksLeft = type.delay;
        if (livingEntity instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrPrideMultiCastPacket(livingEntity.getId(), type), (ServerPlayerEntity) livingEntity);
        }
    }
    public PrideStatusEffect.MultiCastType getMultiCastType() { return multiCastType; }
    public int getMultiCastTicks() { return multiCastTicksLeft; }
    public void setMultiCastTarget(LivingEntity target) { this.multiCastTarget = target; }
    public LivingEntity getMultiCastTarget() { return this.multiCastTarget; }

    public void setFavoriteEffect(EffectSelectionScreen.EffectTypes type) {
        this.favorite = type;
        if (livingEntity instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrFavoriteEffectPacket(livingEntity.getId(), type), (ServerPlayerEntity) livingEntity);
        }
    }

    public EffectSelectionScreen.EffectTypes getFavoriteEffect() { return favorite; }

    public void setActiveEffect(EffectSelectionScreen.EffectTypes type) {
        this.activeEffect = type;
        if (livingEntity instanceof ServerPlayerEntity) {
            AddonPackets.sendToClient(new TrActiveEffectPacket(livingEntity.getId(), type), (ServerPlayerEntity) livingEntity);
        }
    }

    public EffectSelectionScreen.EffectTypes getActiveEffect() { return activeEffect; }

    public void onClone(JokerUtilCap old) {
        this.swanSong = old.swanSong;
        this.stolenAmount = old.stolenAmount;
        this.modifierTicksLeft = old.modifierTicksLeft;
        this.favorite = old.favorite;
    }


    // Sync all the data that should be available to all players
    public void syncWithAnyPlayer(ServerPlayerEntity player) {
    }

    // Sync all the data that only this player needs to know
    public void syncWithEntityOnly(ServerPlayerEntity player) {
        AddonPackets.sendToClient(new TrSlothEffectPacket(livingEntity.getId(), swanSong, borrowedHealth), player);
        if (favorite != null) AddonPackets.sendToClient(new TrFavoriteEffectPacket(livingEntity.getId(), favorite), player);
        if (activeEffect != null) AddonPackets.sendToClient(new TrActiveEffectPacket(livingEntity.getId(), activeEffect), player);
    }
    public void updateSynced(ServerPlayerEntity player) {
        updateModifiers(stolenAmount);
        MCUtil.applyAttributeModifier(player, Attributes.MAX_HEALTH, new AttributeModifier(GreedStatusEffect.HEALTH_ATTRIBUTE_MODIFIER_ID, "Greed max health", greedMaxHealth, AttributeModifier.Operation.ADDITION));
        MCUtil.applyAttributeModifier(player, Attributes.ARMOR, new AttributeModifier(GreedStatusEffect.ARMOR_ATTRIBUTE_MODIFIER_ID, "Greed armor", -greedMaxHealth, AttributeModifier.Operation.ADDITION));
    }
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("SwanSong", swanSong);
        CompoundNBT stolen = new CompoundNBT();
        stolen.putDouble("StolenAttributesAmount", stolenAmount);
        stolen.putInt("TicksLeft", modifierTicksLeft);
        stolen.putInt("MaxTicksLeft", maxTicks);
        nbt.put("Envy", stolen);
        CompoundNBT pride = new CompoundNBT();
        CompoundNBT prideStacks = new CompoundNBT();
        prideStacks.putInt("AttackerStacks", prideAttackerStacks);
        prideStacks.putInt("AttackerTicksLeft", prideAttackerStacksTicksLeft);
        prideStacks.putInt("TargetStacks", prideTargetStacks);
        prideStacks.putInt("TargetTicksLeft", prideTargetStacksTicksLeft);
        pride.put("PrideStacks", prideStacks);
        if (multiCastType != null) {
            CompoundNBT multicast = new CompoundNBT();
            MCUtil.nbtPutEnum(multicast, "MultiCast", multiCastType);
            multicast.putInt("TicksLeft", multiCastTicksLeft);
            pride.put("PrideMultiCast", multicast);
        }
        nbt.put("Pride", pride);
        if (favorite != null) MCUtil.nbtPutEnum(nbt, "FavoriteEffect", favorite);
        if (activeEffect != null) MCUtil.nbtPutEnum(nbt, "ActiveEffect", activeEffect);
        if (livingEntity instanceof PlayerEntity) nbt.putFloat("GreedMaxHealth", livingEntity.getMaxHealth() - GreedStatusEffect.getMaxHealthWithoutGreed(livingEntity));
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.swanSong = nbt.getBoolean("SwanSong");
        this.stolenAmount = nbt.getCompound("Envy").getDouble("StolenAttributesAmount");
        this.modifierTicksLeft = nbt.getCompound("Envy").getInt("TicksLeft");
        this.maxTicks = nbt.getCompound("Envy").getInt("MaxTicksLeft");
        CompoundNBT stacks = nbt.getCompound("Pride").getCompound("PrideStacks");
        this.prideAttackerStacks = stacks.getInt("AttackerStacks");
        this.prideAttackerStacksTicksLeft = stacks.getInt("AttackerTicksLeft");
        this.prideTargetStacks = stacks.getInt("TargetStacks");
        this.prideTargetStacksTicksLeft = stacks.getInt("TargetTicksLeft");
        CompoundNBT multicast = nbt.getCompound("Pride").getCompound("PrideMultiCast");
        if (MCUtil.nbtGetEnum(multicast, "MultiCast", PrideStatusEffect.MultiCastType.class) != null) this.multiCastType = MCUtil.nbtGetEnum(multicast, "MultiCast", PrideStatusEffect.MultiCastType.class);
        this.multiCastTicksLeft = multicast.getInt("TicksLeft");
        this.favorite = MCUtil.nbtGetEnum(nbt, "FavoriteEffect", EffectSelectionScreen.EffectTypes.class);
        this.activeEffect = MCUtil.nbtGetEnum(nbt, "ActiveEffect", EffectSelectionScreen.EffectTypes.class);
        this.greedMaxHealth = nbt.getFloat("GreedMaxHealth");
    }
}

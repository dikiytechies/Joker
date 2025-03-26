package com.dikiytechies.joker.capability;

import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.fromserver.TrEnvyStealPacket;
import com.dikiytechies.joker.network.packets.fromserver.TrSlothDebuffPacket;
import com.dikiytechies.joker.network.packets.fromserver.TrSlothEffectPacket;
import com.github.standobyte.jojo.util.mc.MCUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.JsonUtils;

import javax.annotation.Nullable;
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

    public void onClone(JokerUtilCap old) {
        this.swanSong = old.swanSong;
        this.stolenAmount = old.stolenAmount;
        this.modifierTicksLeft = old.modifierTicksLeft;
    }


    // Sync all the data that should be available to all players
    public void syncWithAnyPlayer(ServerPlayerEntity player) {
    }

    // Sync all the data that only this player needs to know
    public void syncWithEntityOnly(ServerPlayerEntity player) {
        AddonPackets.sendToClient(new TrSlothEffectPacket(livingEntity.getId(), swanSong, borrowedHealth), player);
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
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.swanSong = nbt.getBoolean("SwanSong");
        this.stolenAmount = nbt.getCompound("Envy").getDouble("StolenAttributesAmount");
        this.modifierTicksLeft = nbt.getCompound("Envy").getInt("TicksLeft");
        this.maxTicks = nbt.getCompound("Envy").getInt("MaxTicksLeft");
    }
}

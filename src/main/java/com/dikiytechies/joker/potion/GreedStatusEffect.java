package com.dikiytechies.joker.potion;

import com.github.standobyte.jojo.potion.StatusEffect;
import com.github.standobyte.jojo.util.mc.MCUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.EffectType;

import java.util.UUID;

public class GreedStatusEffect extends StatusEffect {
    public static final UUID HEALTH_ATTRIBUTE_MODIFIER_ID = UUID.fromString("005ddb04-a053-4f9c-af25-edef72f20318");
    public static final UUID ARMOR_ATTRIBUTE_MODIFIER_ID = UUID.fromString("d545f0ab-d189-4128-8ca0-5ede4d8ffba1");
    private boolean assigned = false;
    public GreedStatusEffect(EffectType type, int liquidColor) { super(type, liquidColor); }

    public static float getMaxHealthWithoutGreed(LivingEntity entity) {
        return (float) MCUtil.calcValueWithoutModifiers(entity.getAttribute(Attributes.MAX_HEALTH), HEALTH_ATTRIBUTE_MODIFIER_ID);
    }
    public static float getMaxArmorWithoutGreed(LivingEntity entity) {
        return (float) Math.max(MCUtil.calcValueWithoutModifiers(entity.getAttribute(Attributes.ARMOR), ARMOR_ATTRIBUTE_MODIFIER_ID), 0);
    }
}

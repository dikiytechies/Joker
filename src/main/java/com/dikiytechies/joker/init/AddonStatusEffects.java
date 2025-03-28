package com.dikiytechies.joker.init;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.potion.*;
import com.github.standobyte.jojo.potion.StatusEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AddonStatusEffects {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, AddonMain.MOD_ID);

    public static final RegistryObject<StatusEffect> SLOTH = EFFECTS.register("sloth",
            () -> new SlothStatusEffect(EffectType.NEUTRAL, 0x7cbd8b).setUncurable());
    public static final RegistryObject<StatusEffect> WRATH = EFFECTS.register("wrath",
            () -> new StatusEffect(EffectType.NEUTRAL, 0xcb5136).setUncurable());
    public static final RegistryObject<StatusEffect> LUST = EFFECTS.register("lust",
            () -> new LustStatusEffect(EffectType.NEUTRAL, 0xdd45ba).setUncurable());
    public static final RegistryObject<StatusEffect> GLUTTONY = EFFECTS.register("gluttony",
            () -> new GluttonyStatusEffect(EffectType.NEUTRAL, 0xcf8638).setUncurable());
    public static final RegistryObject<StatusEffect> ENVY = EFFECTS.register("envy",
            () -> new StatusEffect(EffectType.NEUTRAL, 0x5d825a).setUncurable());
    public static final RegistryObject<StatusEffect> GREED = EFFECTS.register("greed",
            () -> new GreedStatusEffect(EffectType.NEUTRAL, 0xf9f914).setUncurable());
    public static final RegistryObject<StatusEffect> PRIDE = EFFECTS.register("pride",
            () -> new PrideStatusEffect(EffectType.NEUTRAL, 0x1483f9).setUncurable());
}

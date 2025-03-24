package com.dikiytechies.joker.init;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.potion.SlothStatusEffect;
import com.github.standobyte.jojo.potion.StatusEffect;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AddonStatusEffects {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, AddonMain.MOD_ID);

    public static final RegistryObject<Effect> SLOTH = EFFECTS.register("sloth",
            () -> new SlothStatusEffect(EffectType.NEUTRAL, 0x7cbd8b).setUncurable());
}

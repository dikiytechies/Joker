package com.dikiytechies.joker.potion;

import com.dikiytechies.joker.init.Sounds;
import com.github.standobyte.jojo.potion.StatusEffect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.SoundEvent;

import java.util.Random;

public class PrideStatusEffect extends StatusEffect {
    public PrideStatusEffect(EffectType type, int liquidColor) { super(type, liquidColor); }

    public static MultiCastType applyMultiCast(int amplifier) {
        Random random = new Random();//62 38 15 || 50 25 6.25
        int maxChance = 50 + 12 * amplifier;
        int randomInt = random.nextInt(100);
        for (int i = 0; i < 3; i++) {
            if (randomInt < Math.min(99, 100 * Math.pow((double) maxChance / 100, 3 - i))) {
                return MultiCastType.values()[3-i];
            }
        }
        return MultiCastType.X1;
    }

    public static SoundEvent getMultiCastSound(MultiCastType type) {
        switch (type) {
            case X2:
                return Sounds.PRIDE_MULTICAST_X2.get();
            case X3:
                return Sounds.PRIDE_MULTICAST_X3.get();
            case X4:
                return Sounds.PRIDE_MULTICAST_X4.get();
            default:
                return null;
        }
    }

    public static enum MultiCastType {
        X1(0),
        X2(4),
        X3(8),
        X4(12);
        public final int delay;
        MultiCastType(int delay) {
            this.delay = delay;
        }
    }
}

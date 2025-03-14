package com.dikiytechies.joker.init;

import com.dikiytechies.joker.action.non_stand.JokerAction;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerPowerType;
import net.minecraftforge.fml.RegistryObject;

import static com.github.standobyte.jojo.init.power.ModCommonRegisters.NON_STAND_POWERS;


public class JokerPowerInit {
    public static void loadRegistryObjects() {}
    public static final RegistryObject<JokerPowerType> JOKER = NON_STAND_POWERS.register("joker", () -> new JokerPowerType(
            new JokerAction[] {},
            new JokerAction[] {},
            null
    ));
}

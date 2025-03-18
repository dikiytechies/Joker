package com.dikiytechies.joker.init.power.non_stand.joker;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.action.non_stand.JokerAction;
import com.dikiytechies.joker.action.non_stand.JokerSociopathy;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerPowerType;
import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.power.impl.nonstand.type.NonStandPowerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;



public class JokerPowerInit {
    public static void loadRegistryObjects() {}

    public static final DeferredRegister<NonStandPowerType<?>> NON_STAND_POWER = DeferredRegister.create(
            (Class<NonStandPowerType<?>>) ((Class<?>) NonStandPowerType.class), AddonMain.MOD_ID);
    public static final DeferredRegister<Action<?>> ACTIONS = DeferredRegister.create(
            (Class<Action<?>>) ((Class<?>) Action.class), AddonMain.MOD_ID);

    public static final RegistryObject<JokerAction> SOCIOPATHY = ACTIONS.register("sociopathy", () ->
            new JokerSociopathy(new JokerAction.Builder()));


    public static final RegistryObject<JokerPowerType> JOKER = NON_STAND_POWER.register("joker", () -> new JokerPowerType(
            new JokerAction[] {},
            new JokerAction[] {
                    SOCIOPATHY.get()
            },
            null
    ));
}

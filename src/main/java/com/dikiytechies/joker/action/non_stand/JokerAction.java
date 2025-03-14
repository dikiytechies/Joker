package com.dikiytechies.joker.action.non_stand;

import com.dikiytechies.joker.init.power.non_stand.joker.JokerPowerInit;
import com.dikiytechies.joker.power.impl.nonstand.type.JokerData;
import com.github.standobyte.jojo.action.non_stand.NonStandAction;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;

public class JokerAction extends NonStandAction {
    protected int stage;
    public JokerAction(Builder builder) {
        super(builder);
        stage = 0;
    }


    @Override
    public boolean isUnlocked(INonStandPower power) {
        JokerData jokerData = power.getTypeSpecificData(JokerPowerInit.JOKER.get()).get();
        if (this.stage <= jokerData.getStage()) return true;
        return super.isUnlocked(power);
    }

    public int getStage() { return stage; }
}

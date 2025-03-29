package com.dikiytechies.joker.util;

import com.dikiytechies.joker.client.ui.screen.EffectSelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

public class ClientUtil {
    public static void openTargetSelection(PlayerEntity player) {
        Minecraft.getInstance().setScreen(new EffectSelectionScreen(player));
    }
}

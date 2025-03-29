package com.dikiytechies.joker.client.ui.screen;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.client.ui.widget.EffectButton;
import com.dikiytechies.joker.init.AddonStatusEffects;
import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.toserver.ClAddEffectPacket;
import com.github.standobyte.jojo.potion.StatusEffect;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class EffectSelectionScreen extends Screen {
    private final PlayerEntity player;
    public EffectSelectionScreen(PlayerEntity player) {
        super(NarratorChatListener.NO_TITLE);
        this.player = player;
    }
    @Override
    protected void init() {
        super.init();
        initCircle();
    }
    private void initCircle() {
        int size = 30;
        int offset = height / 5;
        int yCenter = height / 2;
        int xCenter = width / 2;
        for (double i = -Math.PI / 2; i < Math.PI * 2 - Math.PI / 2; i += Math.PI / 3.5) {
            int finalI = (int) ((i + Math.PI / 2) / (Math.PI / 3.5));
            this.addButton(new EffectButton(xCenter - size / 2 + (int) (offset * Math.cos(i)), yCenter - size / 2 + (int) (offset * Math.sin(i)), size, size, new TranslationTextComponent(""), EffectTypes.values()[finalI], this, player, button -> {}));
        }
    }
    public static enum EffectTypes {
        WRATH(AddonStatusEffects.WRATH.get()),
        ENVY(AddonStatusEffects.ENVY.get()),
        PRIDE(AddonStatusEffects.PRIDE.get()),
        LUST(AddonStatusEffects.LUST.get()),
        GLUTTONY(AddonStatusEffects.GLUTTONY.get()),
        SLOTH(AddonStatusEffects.SLOTH.get()),
        GREED(AddonStatusEffects.GREED.get());
        public final StatusEffect effect;
        EffectTypes(StatusEffect effect) { this.effect = effect; }
    }
    @Override
    public boolean isPauseScreen() { return false; }
}

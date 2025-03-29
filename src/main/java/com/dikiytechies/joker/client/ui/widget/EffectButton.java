package com.dikiytechies.joker.client.ui.widget;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.capability.JokerUtilCapProvider;
import com.dikiytechies.joker.client.ui.screen.EffectSelectionScreen;
import com.dikiytechies.joker.network.AddonPackets;
import com.dikiytechies.joker.network.packets.toserver.ClAddEffectPacket;
import com.dikiytechies.joker.network.packets.toserver.ClFavoriteEffectPacket;
import com.github.standobyte.jojo.JojoMod;
import com.github.standobyte.jojo.client.InputHandler;
import com.github.standobyte.jojo.client.ui.BlitFloat;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class EffectButton extends Button {
    private final ResourceLocation icon;
    private final Screen parent;
    private final PlayerEntity player;
    private final EffectSelectionScreen.EffectTypes effectType;

    public EffectButton(int x, int y, int width, int height, ITextComponent text, EffectSelectionScreen.EffectTypes type, Screen parent, PlayerEntity player, IPressable event) {
        super(x, y, width, height, text, event);
        this.icon = getIcon(type);
        this.parent = parent;
        this.player = player;
        this.effectType = type;
    }
    public ResourceLocation getIcon(EffectSelectionScreen.EffectTypes type) {
        return type.effect.getRegistryName();
    }
    public void renderIcon(MatrixStack matrixStack, float x, float y) {
        Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(icon.getNamespace(), "textures/mob_effect/" + icon.getPath() + ".png"));
        BlitFloat.blitFloat(matrixStack, x, y, 0, 0, width * 0.75f, height * 0.75f, width * 0.75f, height * 0.75f);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float value) {
        super.render(matrixStack, x, y, value);
        if (this.visible) renderIcon(matrixStack, x + width * 0.125f, y + height * 0.125f);
        if (isMouseOver(mouseX, mouseY)) {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bind(EFFECT_CHOOSE_LOCATION);
            if (!player.getCapability(JokerUtilCapProvider.CAPABILITY).map(cap -> cap.getFavoriteEffect() == effectType).orElse(false)) {
                this.blit(matrixStack, x, y, 0, 30, width, height, 80, 80);
            } else this.blit(matrixStack, x, y, 30, 0, width, height, 80, 80);
        }
    }
    public static final ResourceLocation EFFECT_CHOOSE_LOCATION = new ResourceLocation(AddonMain.MOD_ID, "textures/gui/effect_choose.png");
    @Override
    public void renderButton(MatrixStack matrixStack, int x, int y, float value) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(EFFECT_CHOOSE_LOCATION);
        if (!player.getCapability(JokerUtilCapProvider.CAPABILITY).map(cap -> cap.getFavoriteEffect() == effectType).orElse(false)) {
            this.blit(matrixStack, x, y, 0, 0, width, height, 80, 80);
        } else this.blit(matrixStack, x, y, 30, 30, width, height, 80, 80);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (isMouseOver(mouseX, mouseY)) {
            InputHandler.MouseButton button = InputHandler.MouseButton.getButtonFromId(buttonId);
            if (button == null) return false;
            if (this.clicked(mouseX, mouseY)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick(mouseX, mouseY);
                switch (button) {
                    case LEFT:
                        AddonPackets.sendToServer(new ClAddEffectPacket(effectType));
                        parent.onClose();
                        return true;
                    case RIGHT:
                        AddonPackets.sendToServer(new ClFavoriteEffectPacket(effectType));
                        return true;
                }
            }
        }
        return false;
    }
}

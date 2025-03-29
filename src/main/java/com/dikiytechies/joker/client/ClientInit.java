package com.dikiytechies.joker.client;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.client.render.entity.layerrender.GoldLayer;
import com.dikiytechies.joker.client.render.entity.model.GreedArmorModel;
import com.github.standobyte.jojo.client.render.entity.layerrenderer.HamonBurnLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {
    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        Minecraft mc = event.getMinecraftSupplier().get();


        event.enqueueWork(() -> {
            Map<String, PlayerRenderer> skinMap = mc.getEntityRenderDispatcher().getSkinMap();
            addLayers(skinMap.get("default"), false);
            addLayers(skinMap.get("slim"), true);
            mc.getEntityRenderDispatcher().renderers.values().forEach(ClientInit::addLayersToEntities);
        });

        MinecraftForge.EVENT_BUS.register(new ClientHandler());
    }


    private static void addLayers(PlayerRenderer renderer, boolean slim) {
        renderer.addLayer(new GoldLayer<>(renderer, slim));
        addLivingLayers(renderer);
        addBipedLayers(renderer);
    }

    private static <T extends LivingEntity, M extends BipedModel<T>> void addLayersToEntities(EntityRenderer<?> renderer) {
        if (renderer instanceof LivingRenderer<?, ?>) {
            addLivingLayers((LivingRenderer<T, ?>) renderer);
            if (((LivingRenderer<?, ?>) renderer).getModel() instanceof BipedModel<?>) {
                addBipedLayers((LivingRenderer<T, M>) renderer);
            }
        }
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void addLivingLayers(@NotNull LivingRenderer<T, M> renderer) {

        renderer.addLayer(new HamonBurnLayer<>(renderer));
    }
    private static <T extends LivingEntity, M extends BipedModel<T>> void addBipedLayers(LivingRenderer<T, M> renderer) {
    }
}

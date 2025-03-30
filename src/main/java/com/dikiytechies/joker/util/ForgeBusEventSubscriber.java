package com.dikiytechies.joker.util;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.command.JokeCommand;
import com.dikiytechies.joker.init.AddonStructures;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID)
public class ForgeBusEventSubscriber {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        JokeCommand.register(dispatcher);
    }
    public static final Map<Supplier<StructureFeature<?, ?>>, Predicate<BiomeLoadingEvent>> structureBiomes = new HashMap<>();
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        List<Supplier<StructureFeature<?, ?>>> structureStarts = event.getGeneration().getStructures();
        for (Map.Entry<Supplier<StructureFeature<?, ?>>, Predicate<BiomeLoadingEvent>> entry : structureBiomes.entrySet()) {
            if (entry.getValue() != null && entry.getValue().test(event)) {
                structureStarts.add(entry.getKey());
            }
        }
    }
}

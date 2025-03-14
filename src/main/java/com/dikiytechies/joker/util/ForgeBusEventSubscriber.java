package com.dikiytechies.joker.util;

import com.dikiytechies.joker.AddonMain;
import com.dikiytechies.joker.command.JokeCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID)
public class ForgeBusEventSubscriber {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        JokeCommand.register(dispatcher);
    }
}

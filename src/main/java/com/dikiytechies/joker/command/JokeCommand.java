package com.dikiytechies.joker.command;

import com.dikiytechies.joker.init.JokerPowerInit;
import com.github.standobyte.jojo.power.impl.nonstand.INonStandPower;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class JokeCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("joke").executes(ctx -> joke(ctx.getSource())));
    }
    protected static int joke(CommandSource source) throws CommandSyntaxException {
        INonStandPower.getNonStandPowerOptional(source.getPlayerOrException()).ifPresent(p -> p.givePower(JokerPowerInit.JOKER.get()));
        return 0;
    }
}

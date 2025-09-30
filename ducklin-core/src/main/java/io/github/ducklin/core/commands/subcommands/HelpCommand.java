package io.github.ducklin.core.commands.subcommands;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;

import io.github.ducklin.core.commands.SlimefunCommand;
import io.github.ducklin.core.commands.SubCommand;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

class HelpCommand extends SubCommand {

    @ParametersAreNonnullByDefault
    HelpCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "help", false);
    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {
        cmd.sendHelp(sender);
    }

}

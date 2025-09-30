package io.github.ducklin.core.commands.subcommands;

import javax.annotation.ParametersAreNonnullByDefault;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.ducklin.core.commands.SlimefunCommand;
import io.github.ducklin.core.commands.SubCommand;
import io.github.ducklin.core.guide.SlimefunGuide;
import io.github.ducklin.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;

class OpenGuideCommand extends SubCommand {

    @ParametersAreNonnullByDefault
    OpenGuideCommand(Slimefun plugin, SlimefunCommand cmd) {
        super(plugin, cmd, "open_guide", false);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            if (sender.hasPermission("slimefun.command.open_guide")) {
                SlimefunGuide.openGuide(player, SlimefunGuideMode.SURVIVAL_MODE);
            } else {
                Slimefun.getLocalization().sendMessage(sender, "messages.no-permission", true);
            }
        } else {
            Slimefun.getLocalization().sendMessage(sender, "messages.only-players", true);
        }
    }

}

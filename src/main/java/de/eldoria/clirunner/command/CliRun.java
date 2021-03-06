/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.clirunner.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class CliRun implements TabExecutor {
    private final Plugin plugin;
    private final File log;
    private static final String[] DISALLOWED_ARGS = {"&&", "&", "||", ";", ","};
    private static final String[] DISALLOWED_CHARACTER = {";"};

    public CliRun(Plugin plugin) {
        this.plugin = plugin;
        log = Path.of(plugin.getDataFolder().getPath(), "log.txt").toFile();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;
        var section = plugin.getConfig().getConfigurationSection(args[0]);

        if (section == null) {
            sender.sendMessage("Script configuration not found");
            return true;
        }

        var shell = section.getString("shell");
        var file = section.getString("file");
        var allowArgs = section.getBoolean("allowArgs");

        List<String> commands = new ArrayList<>();
        commands.add(shell);
        commands.add(file);
        if (allowArgs && args.length > 1) {
            commands.addAll(Arrays.stream(Arrays.copyOfRange(args, 1, args.length)).toList());
        }

        for (var arg : DISALLOWED_ARGS) {
            if (commands.stream().anyMatch(arg::equals)) {
                sender.sendMessage("Expression " + arg + " is not allowed.");
                return true;
            }
        }

        for (var arg : DISALLOWED_CHARACTER) {
            if (commands.stream().anyMatch(arg::contains)) {
                sender.sendMessage("Expression " + arg + " is not allowed.");
                return true;
            }
        }

        var scripts = new ProcessBuilder()
                .directory(Path.of(plugin.getDataFolder().getAbsolutePath(), "scripts").toFile())
                .redirectOutput(ProcessBuilder.Redirect.appendTo(log))
                .command(commands);

        try {
            scripts.start();
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not execute script.", e);
            sender.sendMessage("??4Something went wrong!");
        }

        sender.sendMessage("??2Executed.");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return plugin.getConfig().getKeys(false).stream().filter(r -> r.startsWith(args[0])).collect(Collectors.toList());
    }
}

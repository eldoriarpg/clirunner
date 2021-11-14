/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.clirunner;

import de.eldoria.clirunner.command.CliRun;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

public class CliRunner extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            Files.createDirectories(getDataFolder().toPath().resolve("scripts"));
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not create scripts directory", e);
        }

        getCommand("clirun").setExecutor(new CliRun(this));
    }
}

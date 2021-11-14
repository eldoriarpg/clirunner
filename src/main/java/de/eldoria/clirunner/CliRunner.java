/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2021 EldoriaRPG Team and Contributor
 */

package de.eldoria.clirunner;

import de.eldoria.clirunner.command.CliRun;
import org.bukkit.plugin.java.JavaPlugin;

public class CliRunner extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        getCommand("clirun").setExecutor(new CliRun(this));
    }
}

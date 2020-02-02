package com.mysticalmachines.simplevotelistener;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.sponge.event.VotifierEvent;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Plugin(
        id = "simplevotelistener",
        name = "Simplevotelistener",
        description = "Simple Vote Listener for Sponge",
        url = "https://mysticalmachines.com",
        authors = {
                "Rubbertjuh"
        },
        dependencies = {
                @Dependency(id = "nuvotifier")
        }
)
public class Simplevotelistener {

    public List<String> executeCommandsOnVote;
    private ConfigurationNode config;
    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConf;
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    @Inject
    private PluginContainer pluginContainer;
    @Inject
    private Logger logger;

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        try {
            loadConfig();
            this.executeCommandsOnVote = config.getNode("commands").getList(TypeToken.of(String.class));
            for (String command :
                    this.executeCommandsOnVote) {
                System.out.println("Command: " + command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        this.logger.info("Simple Vote Listener Started");
    }

    @Listener
    public synchronized void onVote(VotifierEvent event) {
        Vote vote = event.getVote();
        this.logger.info(vote.toString());
        if (Sponge.getGame().getServer().getPlayer(vote.getUsername()).isPresent()) {
            this.executeConfiguredCommands(Sponge.getServer().getPlayer(vote.getUsername()).get());
        } else {
            this.logger.info("Player: " + vote.getUsername() + " was not online, or something went wrong!");
        }
    }
    /**
     * Load the default config file, simplevotelistener.conf.
     */
    private void loadConfig() {
        try {
            if (!defaultConf.exists()) {
                pluginContainer.getAsset("simplevotelistener.conf").get().copyToFile(defaultConf.toPath());
            }

            this.config = loader.load();
        } catch (IOException e) {
            logger.warn("[Simple Vote Listener] Main configuration file could not be loaded/created/changed!");
        }
    }

    private void executeConfiguredCommands(Player player) {
        for (String command :
                this.executeCommandsOnVote) {
            Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command
                    .replace("%player%", player.getName()));
        }
    }
}

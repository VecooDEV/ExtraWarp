package com.vecoo.extrawarp;

import com.vecoo.extrawarp.command.WarpCommand;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.config.ServerConfig;
import com.vecoo.extrawarp.storage.warp.WarpProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtraWarp implements ModInitializer {
    public static final String MOD_ID = "extrawarp";
    private static final Logger LOGGER = LogManager.getLogger("ExtraWarp");

    private static ExtraWarp instance;

    private ServerConfig config;
    private LocaleConfig locale;

    private WarpProvider warpProvider;

    private MinecraftServer server;

    @Override
    public void onInitialize() {
        instance = this;

        this.loadConfig();

        CommandRegistrationCallback.EVENT.register(WarpCommand::register);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
            this.loadStorage();
        });
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
            this.locale = new LocaleConfig();
            this.locale.init();
        } catch (Exception e) {
            LOGGER.error("[ExtraWarp] Error load config.", e);
        }
    }

    public void loadStorage() {
        try {
            this.warpProvider = new WarpProvider("/%directory%/storage/ExtraWarp/", this.server);
            this.warpProvider.init();
        } catch (Exception e) {
            LOGGER.error("[ExtraWarp] Error load storage.", e);
        }
    }

    public static ExtraWarp getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public ServerConfig getConfig() {
        return instance.config;
    }

    public LocaleConfig getLocale() {
        return instance.locale;
    }

    public WarpProvider getWarpProvider() {
        return instance.warpProvider;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}
package com.vecoo.extrawarp;

import com.mojang.logging.LogUtils;
import com.vecoo.extrawarp.command.WarpCommand;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.config.ServerConfig;
import com.vecoo.extrawarp.storage.WarpProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public class ExtraWarp implements ModInitializer {
    public static final String MOD_ID = "extrawarp";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static ExtraWarp instance;

    private ServerConfig config;
    private LocaleConfig localeConfig;

    private WarpProvider warpProvider;

    private MinecraftServer server;

    @Override
    public void onInitialize() {
        instance = this;

        loadConfig();

        CommandRegistrationCallback.EVENT.register(WarpCommand::register);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            this.server = server;
            loadStorage();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.warpProvider.save());
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
            this.localeConfig = new LocaleConfig();
            this.localeConfig.init();
        } catch (Exception e) {
            LOGGER.error("Error load config.", e);
        }
    }

    public void loadStorage() {
        try {
            if (this.warpProvider == null) {
                this.warpProvider = new WarpProvider("/%directory%/storage/ExtraWarp/", this.server);
            }

            this.warpProvider.init();
        } catch (Exception e) {
            LOGGER.error("Error load storage.", e);
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

    public LocaleConfig getLocaleConfig() {
        return instance.localeConfig;
    }

    public WarpProvider getWarpProvider() {
        return instance.warpProvider;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}
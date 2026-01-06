package com.vecoo.extrawarp;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.config.YamlConfigFactory;
import com.vecoo.extrawarp.command.WarpCommand;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.config.ServerConfig;
import com.vecoo.extrawarp.service.WarpService;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public class ExtraWarp implements ModInitializer {
    public static final String MOD_ID = "extrawarp";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Getter
    private static ExtraWarp instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;

    private WarpService warpService;

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
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.warpService.save());
    }

    public void loadConfig() {
        this.serverConfig = YamlConfigFactory.load(ServerConfig.class, "config/ExtraWarp/config.yml");
        this.localeConfig = YamlConfigFactory.load(LocaleConfig.class, "config/ExtraWarp/locale.yml");
    }

    private void loadStorage() {
        try {
            this.warpService = new WarpService("/%directory%/storage/ExtraWarp/", this.server);
            this.warpService.init();
        } catch (Exception e) {
            LOGGER.error("Error load storage.", e);
        }
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public ServerConfig getServerConfig() {
        return instance.serverConfig;
    }

    public LocaleConfig getLocaleConfig() {
        return instance.localeConfig;
    }

    public WarpService getWarpService() {
        return instance.warpService;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}
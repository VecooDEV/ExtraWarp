package com.vecoo.extrawarp;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.loader.YamlLoader;
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

import java.io.IOException;

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
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> this.warpService.save(true));
    }

    public void loadConfig() {
        try {
            this.serverConfig = YamlLoader.load(ServerConfig.class, "config/extrawarp/config.yml", false);
            this.localeConfig = YamlLoader.load(LocaleConfig.class, "config/extrawarp/locale.yml", false);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void loadStorage() {
        this.warpService = new WarpService("%directory%/storage/extrawarp/warps/", this.server);

        try {
            this.warpService.init();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
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
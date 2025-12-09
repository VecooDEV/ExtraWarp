package com.vecoo.extrawarp;

import com.mojang.logging.LogUtils;
import com.vecoo.extrawarp.command.WarpCommand;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.config.ServerConfig;
import com.vecoo.extrawarp.storage.WarpProvider;
import com.vecoo.extrawarp.util.PermissionNodes;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import org.slf4j.Logger;

@Mod(ExtraWarp.MOD_ID)
public class ExtraWarp {
    public static final String MOD_ID = "extrawarp";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static ExtraWarp instance;

    private ServerConfig config;
    private LocaleConfig localeConfig;

    private WarpProvider warpProvider;

    private MinecraftServer server;

    public ExtraWarp() {
        instance = this;

        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.registerPermission(event);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        WarpCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        this.server = event.getServer();
        loadStorage();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        this.warpProvider.save();
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
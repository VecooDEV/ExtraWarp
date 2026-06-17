package com.vecoo.extrawarp;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.config.YamlConfigFactory;
import com.vecoo.extralib.loader.YamlLoader;
import com.vecoo.extrawarp.command.WarpCommand;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.config.ServerConfig;
import com.vecoo.extrawarp.service.WarpService;
import com.vecoo.extrawarp.util.PermissionNodes;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import org.slf4j.Logger;

import java.io.IOException;

@Mod(ExtraWarp.MOD_ID)
public class ExtraWarp {
    public static final String MOD_ID = "extrawarp";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Getter
    private static ExtraWarp instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;

    private WarpService warpService;

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
        this.warpService.save(true);
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
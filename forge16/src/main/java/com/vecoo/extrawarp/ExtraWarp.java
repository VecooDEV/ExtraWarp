package com.vecoo.extrawarp;

import com.vecoo.extralib.config.YamlConfigFactory;
import com.vecoo.extrawarp.command.WarpCommand;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.config.ServerConfig;
import com.vecoo.extrawarp.service.WarpService;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExtraWarp.MOD_ID)
public class ExtraWarp {
    public static final String MOD_ID = "extrawarp";
    private static final Logger LOGGER = LogManager.getLogger();

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
    public void onRegisterCommands(RegisterCommandsEvent event) {
        WarpCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onFMLServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();
        loadStorage();

        PermissionAPI.registerNode("minecraft.command.warp", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.set", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.pset", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.delete", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.private", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.invite", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.uninvite", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.blacklist", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.public", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.rename", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.welcome", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.reload", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.assets", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.assets.player", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.top", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.info", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.warp.update", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("extrawarp.bypass", DefaultPermissionLevel.OP, "");

        for (String node : this.serverConfig.getPermissionList()) {
            PermissionAPI.registerNode(node, DefaultPermissionLevel.OP, "");
        }
    }

    @SubscribeEvent
    public void onFMLServerStopping(FMLServerStoppingEvent event) {
        this.warpService.save();
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
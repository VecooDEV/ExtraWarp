package com.vecoo.extrawarp;

import com.vecoo.extrawarp.command.WarpCommand;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.config.ServerConfig;
import com.vecoo.extrawarp.storage.warp.WarpProvider;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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
    private static final Logger LOGGER = LogManager.getLogger("ExtraWarp");

    private static ExtraWarp instance;

    private ServerConfig config;
    private LocaleConfig locale;

    private WarpProvider warpProvider;

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
    public void onServerStarting(FMLServerStartingEvent event) {
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

        for (String node : this.config.getPermissionListingList()) {
            PermissionAPI.registerNode(node, DefaultPermissionLevel.OP, "");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerStopping(FMLServerStoppingEvent event) {
        this.warpProvider.write();
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
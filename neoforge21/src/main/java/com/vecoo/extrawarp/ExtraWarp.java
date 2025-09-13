package com.vecoo.extrawarp;

import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrawarp.command.WarpCommand;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.config.ServerConfig;
import com.vecoo.extrawarp.storage.warp.WarpProvider;
import com.vecoo.extrawarp.util.PermissionNodes;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
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

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.permissionList.add(PermissionNodes.WARP_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_ASSETS_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_ASSETS_PLAYER_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_INFO_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_RELOAD_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_TOP_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_UPDATE_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.PRIVATE_WARP_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_BLACKLIST_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_INVITE_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_SET_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_WELCOME_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_RENAME_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_PUBLIC_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_UNINVITE_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_PRIVATE_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.WARP_DELETE_COMMAND);

        for (String node : config.getPermissionListingList()) {
            PermissionNode<Boolean> permissionNode = UtilPermission.getPermissionNode(node);

            PermissionNodes.permissionList.add(permissionNode);
            PermissionNodes.permissionListModify.add(permissionNode);
        }

        for (PermissionNode<?> node : PermissionNodes.permissionList) {
            if (!event.getNodes().contains(node)) {
                event.addNodes(node);
            }
        }
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerStopping(ServerStoppingEvent event) {
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
package com.vecoo.extrawarp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.api.factory.ExtraWarpFactory;
import com.vecoo.extrawarp.config.LocaleConfig;
import com.vecoo.extrawarp.storage.warp.Warp;
import com.vecoo.extrawarp.util.PermissionNodes;
import com.vecoo.extrawarp.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.UsernameCache;

import java.util.*;
import java.util.stream.Collectors;

public class WarpCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("warp")
                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_COMMAND))
                .then(Commands.argument("name", StringArgumentType.string())
                        .executes(e -> execute(StringArgumentType.getString(e, "name"), e.getSource().getPlayerOrException())))

                .then(Commands.literal("set")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_SET_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executeSet(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("pset")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.PRIVATE_WARP_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executePrivateSet(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("delete")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_DELETE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeDelete(StringArgumentType.getString(e, "warp"), e.getSource()))))

                .then(Commands.literal("private")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_PRIVATE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (!warp.isLocked()) {
                                            if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                builder.suggest(warp.getName());
                                            }
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executePrivate(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("invite")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_INVITE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("player", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (String nick : s.getSource().getOnlinePlayerNames()) {
                                                if (nick.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(nick);
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(e -> executeInvite(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "player"), e.getSource().getPlayerOrException())))))

                .then(Commands.literal("uninvite")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_UNINVITE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("player", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                for (UUID uuid : warp.getInvitePlayers()) {
                                                    String name = UtilPlayer.getPlayerName(uuid);
                                                    if (name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                        builder.suggest(name);
                                                    }
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(e -> executeUnInvite(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "player"), e.getSource().getPlayerOrException())))))

                .then(Commands.literal("blacklist")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_BLACKLIST_COMMAND))
                        .then(Commands.literal("add")
                                .then(Commands.argument("warp", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(warp.getName());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("player", StringArgumentType.string())
                                                .suggests((s, builder) -> {
                                                    for (String nick : s.getSource().getOnlinePlayerNames()) {
                                                        if (nick.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                            builder.suggest(nick);
                                                        }
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(e -> executeAddBlacklist(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "player"), e.getSource().getPlayerOrException())))))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("warp", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(warp.getName());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("player", StringArgumentType.string())
                                                .suggests((s, builder) -> {
                                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                        for (UUID uuid : warp.getBlacklistPlayers()) {
                                                            String name = UtilPlayer.getPlayerName(uuid);
                                                            if (name.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                                builder.suggest(name);
                                                            }
                                                        }
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(e -> executeRemoveBlacklist(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "player"), e.getSource().getPlayerOrException()))))))

                .then(Commands.literal("public")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_PUBLIC_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.isLocked()) {
                                            if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                builder.suggest(warp.getName());
                                            }
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executePublic(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException()))))

                .then(Commands.literal("rename")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_RENAME_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(e -> executeRename(StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "name"), e.getSource())))))

                .then(Commands.literal("welcome")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_WELCOME_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeRemoveWelcome(StringArgumentType.getString(e, "warp"), e.getSource()))
                                .then(Commands.argument("message", MessageArgument.message())
                                        .executes(e -> executeSetWelcome(StringArgumentType.getString(e, "warp"), MessageArgument.getMessage(e, "message"), e.getSource())))))

                .then(Commands.literal("help")
                        .executes(e -> executeHelp(e.getSource())))

                .then(Commands.literal("reload")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_RELOAD_COMMAND))
                        .executes(e -> executeReload(e.getSource())))

                .then(Commands.literal("assets")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_ASSETS_COMMAND))
                        .executes(e -> executeAssets(e.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_ASSETS_PLAYER_COMMAND))
                                .suggests((s, builder) -> {
                                    for (String nick : s.getSource().getOnlinePlayerNames()) {
                                        if (nick.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(nick);
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeAssetsPlayer(e.getSource(), StringArgumentType.getString(e, "player")))))

                .then(Commands.literal("top")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_TOP_COMMAND))
                        .executes(e -> executeTop(e.getSource())))

                .then(Commands.literal("info")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_INFO_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executeInfo(StringArgumentType.getString(e, "warp"), e.getSource()))))

                .then(Commands.literal("update")
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.WARP_UPDATE_COMMAND))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpFactory.WarpProvider.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeUpdate(StringArgumentType.getString(e, "warp"), e.getSource().getPlayerOrException())))));
    }

    private static final List<String> COMMAND_ARGUMENT = Arrays.asList("set", "pset", "delete", "private", "invite", "uninvite", "blacklist", "public", "rename", "welcome", "help", "reload", "assets", "info", "update");

    private static int execute(String name, ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID) && warp.isLocked() && !warp.getInvitePlayers().contains(playerUUID) && !player.hasPermissions(4)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPrivate()
                    .replace("%warp%", name)));
            return 0;
        }

        if (warp.getBlacklistPlayers().contains(playerUUID) && !player.hasPermissions(4)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getBlacklistWarp()
                    .replace("%warp%", name)));
            return 0;
        }

        ServerLevel world = UtilWorld.getWorldByName(warp.getDimensionName());

        if (world == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getX() >= world.getWorldBorder().getMaxX() || warp.getY() < 1 || warp.getY() > world.getMaxBuildHeight() || warp.getZ() >= world.getWorldBorder().getMaxZ()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpBorder()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!ExtraWarpFactory.teleportWarp(player, warp)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.getUniquePlayers().contains(playerUUID)) {
            warp.addUniquePlayer(playerUUID);
        }

        if (warp.getWelcomeText().isEmpty()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getTeleportWarp()
                    .replace("%warp%", warp.getName())));
        } else {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getAddWelcome() + warp.getWelcomeText()));
        }

        return 1;
    }

    private static int executeSet(String name, ServerPlayer player) {
        int maxWarpsPlayer = Utils.maxCountWarp(player);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (ExtraWarpFactory.WarpProvider.getWarpsByPlayer(player.getUUID()).size() >= maxWarpsPlayer && !player.hasPermissions(4)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarpsPlayer))));
            return 0;
        }

        if (COMMAND_ARGUMENT.contains(name)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getInvalidWarpArgument()));
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getConfig().getMaxMaxCharactersWarp()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpMaxCharacters()));
            return 0;
        }

        if (ExtraWarpFactory.WarpProvider.hasWarpByName(name)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!ExtraWarpFactory.WarpProvider.addWarp(new Warp(name, player, false))) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSetWarp()
                .replace("%warp%", name)).copy().append(UtilChat.clickableMessageCommand(localeConfig.getSetWarpAdditional(),
                "/warp private " + name).copy().withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, UtilChat.formatMessage(localeConfig.getHoverSetToPrivateWarp() + name))))));
        return 1;
    }

    private static int executePrivateSet(String name, ServerPlayer player) {
        int maxWarpsPlayer = Utils.maxCountWarp(player);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (ExtraWarpFactory.WarpProvider.getWarpsByPlayer(player.getUUID()).size() >= maxWarpsPlayer && !player.hasPermissions(4)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarpsPlayer))));
            return 0;
        }

        if (COMMAND_ARGUMENT.contains(name)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getInvalidWarpArgument()));
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getConfig().getMaxMaxCharactersWarp()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpMaxCharacters()));
            return 0;
        }

        if (ExtraWarpFactory.WarpProvider.hasWarpByName(name)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!ExtraWarpFactory.WarpProvider.addWarp(new Warp(name, player, true))) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)));
            return 0;
        }

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSetWarpPrivate()
                .replace("%warp%", name)));
        return 1;
    }

    private static int executeDelete(String name, CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID()) && !source.hasPermission(4) && source.getEntity() != null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!ExtraWarpFactory.WarpProvider.removeWarp(warp)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)));
            return 0;
        }

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpRemoved()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeInfo(String name, CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        String textLocked = warp.isLocked() ? localeConfig.getLocked() : localeConfig.getUnlocked();

        List<String> playerInviteName = new ArrayList<>();
        List<String> playerBlacklistName = new ArrayList<>();

        for (UUID playerUUID : warp.getInvitePlayers()) {
            if (UsernameCache.containsUUID(playerUUID)) {
                playerInviteName.add(UtilPlayer.getPlayerName(playerUUID));
            }
        }

        for (UUID playerUUID : warp.getBlacklistPlayers()) {
            if (UsernameCache.containsUUID(playerUUID)) {
                playerBlacklistName.add(UtilPlayer.getPlayerName(playerUUID));
            }
        }

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getInfoWarp()
                .replace("%warp%", warp.getName())
                .replace("%owner%", UtilPlayer.getPlayerName(warp.getOwnerUUID()))
                .replace("%x%", String.valueOf(warp.getX()))
                .replace("%y%", String.valueOf(warp.getY()))
                .replace("%z%", String.valueOf(warp.getZ()))
                .replace("%dimension%", warp.getDimensionName())
                .replace("%invitePlayers%", playerInviteName.toString())
                .replace("%blacklistPlayers%", playerBlacklistName.toString())
                .replace("%count%", String.valueOf(warp.getUniquePlayers().size()))
                .replace("%locked%", textLocked)));
        return 1;
    }

    private static int executeUpdate(String name, ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID()) && !player.hasPermissions(4)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.updatePosition(player);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpUpdate()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeRename(String name, String newName, CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID()) && !source.hasPermission(4)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getName().equals(newName)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getEqualsRename()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.setName(newName);

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpRename()
                .replace("%warp%", warp.getName())
                .replace("%name%", newName)));

        return 1;
    }

    private static int executePrivate(String name, ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (warp.isLocked()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPrivated()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID()) && !player.hasPermissions(4)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.setLocked(true);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPrivateWarp()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executePublic(String name, ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!warp.isLocked()) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPubliced()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID()) && !player.hasPermissions(4)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.setLocked(false);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPublicWarp()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeInvite(String name, String playerName, ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!UtilPlayer.hasUUID(playerName)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (playerUUID.equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getInvitePlayers().contains(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)));
            return 0;
        }

        warp.addInvitePlayer(targetUUID);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)));

        UtilPlayer.sendMessageUuid(targetUUID, UtilChat.formatMessage(localeConfig.getInvitedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", player.getName().getString())));
        return 1;
    }

    private static int executeUnInvite(String name, String playerName, ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!UtilPlayer.hasUUID(playerName)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (player.getUUID().equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.getInvitePlayers().contains(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)));
            return 0;
        }

        warp.removeInvitePlayer(targetUUID);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getUnInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)));
        return 1;
    }

    private static int executeAddBlacklist(String name, String playerName, ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!UtilPlayer.hasUUID(playerName)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (playerUUID.equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getBlacklistPlayers().contains(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)));
            return 0;
        }

        warp.addBlacklistPlayer(targetUUID);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getBlacklistAddedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)));
        return 1;
    }

    private static int executeRemoveBlacklist(String name, String playerName, ServerPlayer player) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (!UtilPlayer.hasUUID(playerName)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        UUID playerUUID = player.getUUID();

        if (!warp.getOwnerUUID().equals(playerUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        UUID targetUUID = UtilPlayer.getUUID(playerName);

        if (playerUUID.equals(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (!warp.getBlacklistPlayers().contains(targetUUID)) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", playerName)));
            return 0;
        }

        warp.removeBlacklistPlayer(targetUUID);

        player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getBlacklistRemovedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", playerName)));
        return 1;
    }

    private static int executeAssets(ServerPlayer player) {
        Set<Warp> warps = ExtraWarpFactory.WarpProvider.getWarpsByPlayer(player.getUUID());
        Set<Warp> publicWarps = new HashSet<>();
        Set<Warp> privateWarps = new HashSet<>();

        for (Warp warp : warps) {
            if (!warp.isLocked()) {
                publicWarps.add(warp);
            } else {
                privateWarps.add(warp);
            }
        }

        player.sendSystemMessage(UtilChat.formatMessage(ExtraWarp.getInstance().getLocale().getWarpAssets()
                .replace("%count%", String.valueOf(warps.size()))
                .replace("%maxCount%", String.valueOf(Utils.maxCountWarp(player)))
                .replace("%publicWarps%", publicWarps.stream().map(Warp::getName).collect(Collectors.joining(", ")))
                .replace("%privateWarps%", privateWarps.stream().map(Warp::getName).collect(Collectors.joining(", ")))));
        return 1;
    }

    private static int executeAssetsPlayer(CommandSourceStack source, String playerName) {
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (!UtilPlayer.hasUUID(playerName)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", playerName)));
            return 0;
        }

        UUID playerUUID = UtilPlayer.getUUID(playerName);

        Set<Warp> warps = ExtraWarpFactory.WarpProvider.getWarpsByPlayer(playerUUID);
        Set<Warp> publicWarps = new HashSet<>();
        Set<Warp> privateWarps = new HashSet<>();

        for (Warp warp : warps) {
            if (!warp.isLocked()) {
                publicWarps.add(warp);
            } else {
                privateWarps.add(warp);
            }
        }

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpAssetsPlayer()
                .replace("%player%", UtilPlayer.getPlayerName(playerUUID))
                .replace("%publicWarps%", publicWarps.stream().map(Warp::getName).collect(Collectors.joining(", ")))
                .replace("%privateWarps%", privateWarps.stream().map(Warp::getName).collect(Collectors.joining(", ")))));
        return 1;
    }

    private static int executeTop(CommandSourceStack source) {
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();
        List<Warp> warps = new ArrayList<>(ExtraWarpFactory.WarpProvider.getWarps());
        warps.sort(Comparator.comparingInt(Warp::getUniquePlayersCount).reversed());

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getTopWarpTitle()));

        int count = 0;
        for (Warp warp : warps) {
            if (warp.isLocked()) {
                continue;
            }

            source.sendSystemMessage(UtilChat.clickableMessageCommand(localeConfig.getTopWarp()
                    .replace("%place%", localeConfig.getPlaces().get(count))
                    .replace("%warp%", warp.getName())
                    .replace("%player%", UtilPlayer.getPlayerName(warp.getOwnerUUID())), "/warp " + warp.getName()).copy().withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, UtilChat.formatMessage(localeConfig.getHoverTopWarp() + warp.getName())))));
            count++;
            if (count >= 10) {
                break;
            }
        }
        return 1;
    }

    private static int executeSetWelcome(String name, Component component, CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID()) && !source.hasPermission(4)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.setWelcomeText(component.getString());

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpSetWelcome()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeRemoveWelcome(String name, CommandSourceStack source) {
        Warp warp = ExtraWarpFactory.WarpProvider.getWarpByName(name);
        LocaleConfig localeConfig = ExtraWarp.getInstance().getLocale();

        if (warp == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)));
            return 0;
        }

        if (source.getEntity() != null && !warp.getOwnerUUID().equals(source.getEntity().getUUID()) && !source.hasPermission(4)) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        if (warp.getWelcomeText().isEmpty()) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpWelcomeEmpty()
                    .replace("%warp%", warp.getName())));
            return 0;
        }

        warp.setWelcomeText("");

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getWarpRemoveWelcome()
                .replace("%warp%", warp.getName())));
        return 1;
    }

    private static int executeHelp(CommandSourceStack source) {
        source.sendSystemMessage(UtilChat.formatMessage(ExtraWarp.getInstance().getLocale().getHelp()));
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        ExtraWarp.getInstance().loadConfig();
        ExtraWarp.getInstance().loadStorage();

        source.sendSystemMessage(UtilChat.formatMessage(ExtraWarp.getInstance().getLocale().getReload()));
        return 1;
    }
}
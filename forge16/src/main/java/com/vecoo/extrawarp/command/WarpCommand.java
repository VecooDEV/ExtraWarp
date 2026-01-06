package com.vecoo.extrawarp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.player.UtilPlayer;
import com.vecoo.extralib.server.UtilCommand;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import com.vecoo.extrawarp.api.service.ExtraWarpService;
import com.vecoo.extrawarp.service.Warp;
import com.vecoo.extrawarp.util.Utils;
import lombok.val;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class WarpCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("warp")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp"))
                .then(Commands.argument("warp", StringArgumentType.string())
                        .executes(e -> executeWarp(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "warp"))))

                .then(Commands.literal("set")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.set"))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(e -> executeSet(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "name")))))

                .then(Commands.literal("pset")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.pset"))
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(e -> executePrivateSet(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "name")))))

                .then(Commands.literal("delete")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.delete"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeDelete(e.getSource(), StringArgumentType.getString(e, "warp")))))

                .then(Commands.literal("private")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.private"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (!warp.isLocked() && warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executePrivate(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "warp")))))

                .then(Commands.literal("invite")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.invite"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("player", StringArgumentType.string())
                                        .suggests(UtilCommand.suggestOnlinePlayers())
                                        .executes(e -> executeInvite(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player"), StringArgumentType.getString(e, "warp"))))))

                .then(Commands.literal("uninvite")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.uninvite"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("player", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                for (UUID playerUUID : warp.getInvitePlayers()) {
                                                    val playerName = UtilPlayer.getPlayerName(playerUUID);

                                                    if (playerName.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                        builder.suggest(playerName);
                                                    }
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(e -> executeUnInvite(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player"), StringArgumentType.getString(e, "warp"))))))

                .then(Commands.literal("blacklist")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.blacklist"))
                        .then(Commands.literal("add")
                                .then(Commands.argument("warp", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(warp.getName());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("player", StringArgumentType.string())
                                                .suggests(UtilCommand.suggestOnlinePlayers())
                                                .executes(e -> executeAddBlacklist(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player"), StringArgumentType.getString(e, "warp"))))))

                        .then(Commands.literal("remove")
                                .then(Commands.argument("warp", StringArgumentType.string())
                                        .suggests((s, builder) -> {
                                            for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                    builder.suggest(warp.getName());
                                                }
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(Commands.argument("player", StringArgumentType.string())
                                                .suggests((s, builder) -> {
                                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                                        for (UUID playerUUID : warp.getBlacklistPlayers()) {
                                                            val playerName = UtilPlayer.getPlayerName(playerUUID);

                                                            if (playerName.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                                                builder.suggest(playerName);
                                                            }
                                                        }
                                                    }
                                                    return builder.buildFuture();
                                                })
                                                .executes(e -> executeRemoveBlacklist(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "player"), StringArgumentType.getString(e, "warp")))))))

                .then(Commands.literal("public")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.public"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.isLocked() && warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executePublic(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "warp")))))

                .then(Commands.literal("rename")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.rename"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(e -> executeRename(e.getSource(), StringArgumentType.getString(e, "warp"), StringArgumentType.getString(e, "name"))))))

                .then(Commands.literal("welcome")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.welcome"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeRemoveWelcome(e.getSource(), StringArgumentType.getString(e, "warp")))
                                .then(Commands.argument("message", MessageArgument.message())
                                        .executes(e -> executeSetWelcome(e.getSource(), StringArgumentType.getString(e, "warp"), MessageArgument.getMessage(e, "message"))))))

                .then(Commands.literal("help")
                        .executes(e -> executeHelp(e.getSource())))

                .then(Commands.literal("reload")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.reload"))
                        .executes(e -> executeReload(e.getSource())))

                .then(Commands.literal("assets")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.assets"))
                        .executes(e -> executeAssets(e.getSource().getPlayerOrException()))

                        .then(Commands.argument("player", StringArgumentType.string())
                                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.assets.player"))
                                .suggests(UtilCommand.suggestOnlinePlayers())
                                .executes(e -> executeAssetsPlayer(e.getSource(), StringArgumentType.getString(e, "player")))))

                .then(Commands.literal("top")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.top"))
                        .executes(e -> executeTop(e.getSource())))

                .then(Commands.literal("info")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.info"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .executes(e -> executeInfo(e.getSource(), StringArgumentType.getString(e, "warp")))))

                .then(Commands.literal("update")
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.warp.update"))
                        .then(Commands.argument("warp", StringArgumentType.string())
                                .suggests((s, builder) -> {
                                    for (Warp warp : ExtraWarpService.getWarpsByPlayer(s.getSource().getPlayerOrException().getUUID())) {
                                        if (warp.getName().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                                            builder.suggest(warp.getName());
                                        }
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(e -> executeUpdate(e.getSource().getPlayerOrException(), StringArgumentType.getString(e, "warp"))))));
    }

    private static int executeWarp(@Nonnull ServerPlayerEntity player, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!isPlayerInvitedWarp(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPrivate()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (isPlayerBlacklistedWarp(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getBlacklistWarp()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        val world = UtilWorld.findWorldByName(warp.getDimensionName());

        if (world == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotDimension()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (isWarpBeyondWorld(warp, world)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpBorder()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!ExtraWarpService.teleportWarp(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        warp.addUniquePlayer(player.getUUID());

        if (warp.getWelcomeText().isEmpty()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getTeleportWarp()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
        } else {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getAddWelcome() + warp.getWelcomeText()), Util.NIL_UUID);
        }

        return 1;
    }

    private static int executeSet(@Nonnull ServerPlayerEntity player, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val maxWarps = Utils.maxCountWarp(player);

        if (isLimitWarp(player, maxWarps)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarps))), Util.NIL_UUID);
            return 0;
        }

        if (Utils.isBlockedNameWarp(name)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getInvalidWarpArgument()), Util.NIL_UUID);
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getServerConfig().getMaxCharactersWarp()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpMaxCharacters()), Util.NIL_UUID);
            return 0;
        }

        if (ExtraWarpService.hasWarpByName(name)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        ExtraWarpService.addWarp(new Warp(name, player, false));
        player.sendMessage(UtilChat.formatMessage(localeConfig.getSetWarp()
                .replace("%warp%", name)).append(UtilChat.clickableMessageCommand(localeConfig.getSetWarpAdditional(),
                        "/warp private " + name)
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT, UtilChat.formatMessage(localeConfig.getHoverSetToPrivateWarp() + name))))), Util.NIL_UUID);
        return 1;
    }

    private static int executePrivateSet(@Nonnull ServerPlayerEntity player, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val maxWarps = Utils.maxCountWarp(player);

        if (isLimitWarp(player, maxWarps)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getMaxWarp()
                    .replace("%count%", String.valueOf(maxWarps))), Util.NIL_UUID);
            return 0;
        }

        if (Utils.isBlockedNameWarp(name)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getInvalidWarpArgument()), Util.NIL_UUID);
            return 0;
        }

        if (name.length() > ExtraWarp.getInstance().getServerConfig().getMaxCharactersWarp()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpMaxCharacters()), Util.NIL_UUID);
            return 0;
        }

        if (ExtraWarpService.hasWarpByName(name)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        ExtraWarpService.addWarp(new Warp(name, player, true));
        player.sendMessage(UtilChat.formatMessage(localeConfig.getSetWarpPrivate()
                .replace("%warp%", name)), Util.NIL_UUID);
        return 1;
    }

    private static int executeDelete(@Nonnull CommandSource source, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        if (!isWarpOwner(source, warp)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        if (!ExtraWarpService.removeWarp(warp)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpError()
                    .replace("%warp%", name)), false);
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpRemoved()
                .replace("%warp%", warp.getName())), false);
        return 1;
    }

    private static int executeInfo(@Nonnull CommandSource source, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        val playerInviteName = new HashSet<>();
        val playerBlacklistName = new HashSet<>();

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

        val player = source.getEntity();
        boolean hideXYZ = warp.isLocked() && player != null && !UtilPermission.hasPermission((PlayerEntity) player, "extrawarp.bypass")
                          && !warp.getOwnerUUID().equals(player.getUUID()) && !warp.getInvitePlayers().contains(player.getUUID());

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getInfoWarp()
                .replace("%warp%", warp.getName())
                .replace("%owner%", UtilPlayer.getPlayerName(warp.getOwnerUUID()))
                .replace("%x%", hideXYZ ? "-" : String.valueOf(warp.getX()))
                .replace("%y%", hideXYZ ? "-" : String.valueOf(warp.getY()))
                .replace("%z%", hideXYZ ? "-" : String.valueOf(warp.getZ()))
                .replace("%dimension%", warp.getDimensionName())
                .replace("%invitePlayers%", playerInviteName.toString())
                .replace("%blacklistPlayers%", playerBlacklistName.toString())
                .replace("%count%", String.valueOf(warp.getUniquePlayers().size()))
                .replace("%locked%", warp.isLocked() ? localeConfig.getLocked() : localeConfig.getUnlocked())), false);
        return 1;
    }

    private static int executeUpdate(@Nonnull ServerPlayerEntity player, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        val world = UtilWorld.findWorldByName(warp.getDimensionName());

        if (world == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotDimension()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (isWarpBeyondWorld(warp, world)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpBorder()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        warp.updatePosition(player);
        player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpUpdate()
                .replace("%warp%", warp.getName())), Util.NIL_UUID);
        return 1;
    }

    private static int executeRename(@Nonnull CommandSource source, @Nonnull String name, @Nonnull String newName) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        if (!isWarpOwner(source, warp)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        if (warp.getName().equals(newName)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getEqualsRename()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        if (ExtraWarpService.hasWarpByName(newName)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", newName)), false);
            return 0;
        }

        warp.setName(newName);
        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpRename()
                .replace("%warp%", warp.getName())
                .replace("%name%", newName)), false);
        return 1;
    }

    private static int executePrivate(@Nonnull ServerPlayerEntity player, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (warp.isLocked()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPrivated()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        warp.setLocked(true);
        player.sendMessage(UtilChat.formatMessage(localeConfig.getPrivateWarp()
                .replace("%warp%", warp.getName())), Util.NIL_UUID);
        return 1;
    }

    private static int executePublic(@Nonnull ServerPlayerEntity player, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpExist()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        if (!warp.isLocked()) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPubliced()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        warp.setLocked(false);
        player.sendMessage(UtilChat.formatMessage(localeConfig.getPublicWarp()
                .replace("%warp%", warp.getName())), Util.NIL_UUID);
        return 1;
    }

    private static int executeInvite(@Nonnull ServerPlayerEntity player, @Nonnull String target, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        val targetUUID = UtilPlayer.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!warp.addInvitePlayer(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", target)), Util.NIL_UUID);

        UtilPlayer.sendMessageUuid(targetUUID, localeConfig.getInvitedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", player.getName().getString()));
        return 1;
    }

    private static int executeUnInvite(@Nonnull ServerPlayerEntity player, @Nonnull String target, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        val targetUUID = UtilPlayer.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!warp.removeInvitePlayer(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getUnInviteWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeAddBlacklist(@Nonnull ServerPlayerEntity player, @Nonnull String target, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        if (!isWarpOwner(player, warp)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!warp.addBlacklistPlayer(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getBlacklistAddedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeRemoveBlacklist(@Nonnull ServerPlayerEntity player, @Nonnull String target, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), Util.NIL_UUID);
            return 0;
        }

        UUID targetUUID = UtilPlayer.findUUID(target);

        if (targetUUID == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        if (!warp.getOwnerUUID().equals(player.getUUID())) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (player.getUUID().equals(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpNotYourself()
                    .replace("%warp%", warp.getName())), Util.NIL_UUID);
            return 0;
        }

        if (!warp.removeBlacklistPlayer(targetUUID)) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getWarpPlayerAlready()
                    .replace("%warp%", warp.getName())
                    .replace("%player%", target)), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(localeConfig.getBlacklistRemovedWarp()
                .replace("%warp%", warp.getName())
                .replace("%player%", target)), Util.NIL_UUID);
        return 1;
    }

    private static int executeAssets(@Nonnull ServerPlayerEntity player) {
        val warps = ExtraWarpService.getWarpsByPlayer(player.getUUID());

        val publicWarps = warps.stream()
                .filter(warp -> !warp.isLocked())
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        val privateWarps = warps.stream()
                .filter(Warp::isLocked)
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        player.sendMessage(UtilChat.formatMessage(ExtraWarp.getInstance().getLocaleConfig().getWarpAssets()
                .replace("%count%", String.valueOf(warps.size()))
                .replace("%maxCount%", String.valueOf(Utils.maxCountWarp(player)))
                .replace("%publicWarps%", publicWarps)
                .replace("%privateWarps%", privateWarps)), Util.NIL_UUID);
        return 1;
    }

    private static int executeAssetsPlayer(@Nonnull CommandSource source, @Nonnull String target) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val targetUUID = UtilPlayer.findUUID(target);

        if (targetUUID == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getPlayerNotFound()
                    .replace("%player%", target)), false);
            return 0;
        }

        val warps = ExtraWarpService.getWarpsByPlayer(targetUUID);

        val publicWarps = warps.stream()
                .filter(warp -> !warp.isLocked())
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        val privateWarps = warps.stream()
                .filter(Warp::isLocked)
                .map(Warp::getName)
                .sorted()
                .collect(Collectors.joining(", "));

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpAssetsPlayer()
                .replace("%player%", target)
                .replace("%publicWarps%", publicWarps)
                .replace("%privateWarps%", privateWarps)), false);
        return 1;
    }

    private static int executeTop(@Nonnull CommandSource source) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val topWarps = ExtraWarpService.getWarps().stream()
                .filter(warp -> !warp.isLocked())
                .sorted(Comparator.comparingInt((Warp warp) -> warp.getUniquePlayers().size()).reversed())
                .limit(10)
                .collect(Collectors.toList());

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getTopWarpTitle()), false);

        for (int i = 0; i < topWarps.size(); i++) {
            val warp = topWarps.get(i);

            source.sendSuccess(UtilChat.clickableMessageCommand(localeConfig.getTopWarp()
                            .replace("%place%", localeConfig.getPlaces().get(i))
                            .replace("%warp%", warp.getName())
                            .replace("%player%", UtilPlayer.getPlayerName(warp.getOwnerUUID())),
                    "/warp " + warp.getName()).copy().withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    UtilChat.formatMessage(localeConfig.getHoverTopWarp() + warp.getName())))), false);
        }

        return 1;
    }

    private static int executeSetWelcome(@Nonnull CommandSource source, @Nonnull String name, ITextComponent component) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        if (!isWarpOwner(source, warp)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        warp.setWelcomeText(component.getString());
        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpSetWelcome()
                .replace("%warp%", warp.getName())), false);
        return 1;
    }

    private static int executeRemoveWelcome(@Nonnull CommandSource source, @Nonnull String name) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();
        val warp = ExtraWarpService.findWarpByName(name);

        if (warp == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotFound()
                    .replace("%warp%", name)), false);
            return 0;
        }

        if (!isWarpOwner(source, warp)) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpNotOwner()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        if (warp.getWelcomeText().isEmpty()) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpWelcomeEmpty()
                    .replace("%warp%", warp.getName())), false);
            return 0;
        }

        warp.setWelcomeText(null);
        source.sendSuccess(UtilChat.formatMessage(localeConfig.getWarpRemoveWelcome()
                .replace("%warp%", warp.getName())), false);
        return 1;
    }

    private static int executeHelp(@Nonnull CommandSource source) {
        source.sendSuccess(UtilChat.formatMessage(ExtraWarp.getInstance().getLocaleConfig().getHelp()), false);
        return 1;
    }

    private static int executeReload(@Nonnull CommandSource source) {
        val localeConfig = ExtraWarp.getInstance().getLocaleConfig();

        try {
            ExtraWarp.getInstance().loadConfig();
        } catch (Exception e) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getErrorReload()), false);
            ExtraWarp.getLogger().error(e.getMessage());
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getReload()), false);
        return 1;
    }

    private static boolean isPlayerBlacklistedWarp(@Nonnull ServerPlayerEntity player, @Nonnull Warp warp) {
        return warp.getBlacklistPlayers().contains(player.getUUID()) && !UtilPermission.hasPermission(player, "extrawarp.bypass");
    }

    private static boolean isPlayerInvitedWarp(@Nonnull ServerPlayerEntity player, @Nonnull Warp warp) {
        return !warp.isLocked() || warp.getOwnerUUID().equals(player.getUUID()) || warp.getInvitePlayers().contains(player.getUUID())
               || UtilPermission.hasPermission(player, "extrawarp.bypass");
    }

    private static boolean isLimitWarp(@Nonnull ServerPlayerEntity player, int maxCount) {
        return ExtraWarpService.getWarpsByPlayer(player.getUUID()).size() >= maxCount
               && !UtilPermission.hasPermission(player, "extrawarp.bypass");
    }

    private static boolean isWarpOwner(@Nonnull CommandSource source, @Nonnull Warp warp) {
        return source.getEntity() == null || warp.getOwnerUUID().equals(source.getEntity().getUUID())
               || UtilPermission.hasPermission(source, "extrawarp.bypass");
    }

    private static boolean isWarpOwner(@Nonnull ServerPlayerEntity player, @Nonnull Warp warp) {
        return warp.getOwnerUUID().equals(player.getUUID()) || UtilPermission.hasPermission(player, "extrawarp.bypass");
    }

    private static boolean isWarpBeyondWorld(@Nonnull Warp warp, @Nonnull World world) {
        return warp.getX() >= world.getWorldBorder().getMaxX() || warp.getY() < 1 ||
               warp.getY() > world.getMaxBuildHeight() || warp.getZ() >= world.getWorldBorder().getMaxZ();
    }
}
package com.vecoo.extrawarp.config;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extrawarp.ExtraWarp;

import java.util.Arrays;
import java.util.List;

public class LocaleConfig {
    private static final int CURRENT_CONFIG_VERSION = 1;

    private int configVersion = 1;
    private String reload = "&e(!) Configs have been reloaded.";
    private String setWarp = "&e(!) You have successfully set warp %warp%.";
    private String setWarpAdditional = " &eClick me to private warp.";
    private String setWarpPrivate = "&e(!) You have successfully set private warp %warp%.";
    private String teleportWarp = "&e(!) You have successfully teleported to warp %warp%.";
    private String privateWarp = "&e(!) You have successfully made the warp %warp% private.";
    private String publicWarp = "&e(!) You have successfully made the warp %warp% public.";
    private String inviteWarp = "&e(!) Player %player% has been successfully invited to your warp %warp%.";
    private String invitedWarp = "&e(!) Player %player% has invited you to warp %warp%.";
    private String unInviteWarp = "&e(!) Player %player% has been successfully uninvited to your warp %warp%.";
    private String blacklistAddedWarp = "&e(!) Player %player% has been successfully added blacklist to your warp %warp%.";
    private String blacklistRemovedWarp = "&e(!) Player %player% has been successfully removed blacklist to your warp %warp%.";
    private String warpRemoved = "&e(!) Warp %warp% successfully removed.";
    private String warpUpdate = "&e(!) You have successfully updated the warp %warp% position.";
    private String warpSetWelcome = "&e(!) You have successfully installed a welcome message on warp %warp%.";
    private String warpRemoveWelcome = "&e(!) You have successfully removed the welcome message from the warp %warp%.";
    private String warpRename = "&e(!) You have successfully changed the warp name from %warp% to %name%.";
    private String help = """
            &eInformation the ExtraWarp:
            /warp help - help with the ExtraWarp mod.
            /warp <name> - teleport to warp.
            /warp set <name> - set public warp at your position.
            /warp pset <name> - set private warp at your position.
            /warp delete <name> - delete your warp.
            /warp private <name> - make your warp private.
            /warp public <name> - make your warp public.
            /warp invite <name> <player> - invite a player to your warp.
            /warp uninvite <name> <player> - revoke an invitation to your warp.
            /warp blacklist add <name> <player> - add a player to your warp's blacklist.
            /warp blacklist remove <name> <player> - remove a player from your warp's blacklist.
            /warp rename <name> <newName> - rename your warp.
            /warp welcome - remove the welcome message from your warp.
            /warp welcome <message> - add a welcome message to your warp.
            /warp info <name> - view information about the warp.
            /warp update <name> - update the position of your warp.
            /warp assets - list of your warps.
            /warp top - list of popular warp""";
    private String topWarpTitle = "&e&lTop warps:";
    private String warpAssets = """
            &e&lYour warps (%count%/%maxCount%):
            &r&e- Public: %publicWarps%
            &e- Private: %privateWarps%""";
    private String warpAssetsPlayer = """
            &e&lWarps player %player%:
            &r&e- Public: %publicWarps%
            &e- Private: %privateWarps%""";
    private String infoWarp = """
            &e&lInfo warp %warp% (%locked%&e&l):
            &r&e- Owner: %owner%
            - X: %x%, Y: %y%, Z: %z% (%dimension%&e)
            - Invite players: %invitePlayers%
            - Blacklist players: %blacklistPlayers%
            - Unique players: %count%""";

    private String warpNotOwner = "&c(!) You are not the owner of the warp %warp%.";
    private String warpNotFound = "&c(!) Warp %warp% not found.";
    private String warpExist = "&c(!) Warp %warp% already exists.";
    private String warpWelcomeEmpty = "&c(!) You cannot delete the warp %warp% greeting message because it does not exist.";
    private String warpNotYourself = "&c(!) You cannot add/remove yourself to warp %warp% lists.";
    private String warpPrivated = "&c(!) Warp %warp% already private.";
    private String warpPubliced = "&c(!) Warp %warp% already public.";
    private String warpPrivate = "&c(!) Warp %warp% is private.";
    private String warpBorder = "&c(!) Warp %warp% is located beyond the world";
    private String warpNotDimension = "&c(!) Warp %warp% is not a dimension.";
    private String blacklistWarp = "&c(!) You are on warp %warp% blacklist.";
    private String warpPlayerAlready = "&c(!) Player %player% is already on one of the warp %warp% lists.";
    private String warpError = "&c(!) Error teleporting to warp %warp%. Try again or ask the warp owner to change the respawn point.";
    private String playerNotFound = "&c(!) Player %player% not found.";
    private String equalsRename = "&c(!) You cannot change the name of the warp %warp% because it is the same as what you plan to rename it to.";
    private String invalidWarpArgument = "&c(!) Your warp cannot contain arguments to the command itself in its name.";
    private String warpMaxCharacters = "&c(!) Your warp cannot contain more than 10 characters.";
    private String maxWarp = "&c(!) You cannot create a warp because your limit of all warps is - %count%.";

    private String topWarp = "&e%place%. %warp% by %player%";
    private String hoverSetToPrivateWarp = "/warp private ";
    private String hoverTopWarp = "/warp ";
    private List<String> places = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    private String addWelcome = "&e(!) ";
    private String locked = "&4Private&r&l";
    private String unlocked = "&2Public&r&l";

    public int getConfigVersion() {
        return this.configVersion;
    }

    public String getReload() {
        return this.reload;
    }

    public String getSetWarp() {
        return this.setWarp;
    }

    public String getTeleportWarp() {
        return this.teleportWarp;
    }

    public String getWarpExist() {
        return this.warpExist;
    }

    public String getWarpNotFound() {
        return this.warpNotFound;
    }

    public String getInfoWarp() {
        return this.infoWarp;
    }

    public String getPrivateWarp() {
        return this.privateWarp;
    }

    public String getWarpPrivate() {
        return this.warpPrivate;
    }

    public String getBlacklistWarp() {
        return this.blacklistWarp;
    }

    public String getWarpNotOwner() {
        return this.warpNotOwner;
    }

    public String getWarpRemoved() {
        return this.warpRemoved;
    }

    public String getWarpAssets() {
        return this.warpAssets;
    }

    public String getWarpAssetsPlayer() {
        return this.warpAssetsPlayer;
    }

    public String getSetWarpPrivate() {
        return this.setWarpPrivate;
    }

    public String getWarpUpdate() {
        return this.warpUpdate;
    }

    public String getPublicWarp() {
        return this.publicWarp;
    }

    public String getTopWarp() {
        return this.topWarp;
    }

    public String getTopWarpTitle() {
        return this.topWarpTitle;
    }

    public List<String> getPlaces() {
        return this.places;
    }

    public String getHoverTopWarp() {
        return this.hoverTopWarp;
    }

    public String getWarpNotYourself() {
        return this.warpNotYourself;
    }

    public String getWarpNotDimension() {
        return this.warpNotDimension;
    }

    public String getWarpPlayerAlready() {
        return this.warpPlayerAlready;
    }

    public String getBlacklistAddedWarp() {
        return this.blacklistAddedWarp;
    }

    public String getBlacklistRemovedWarp() {
        return this.blacklistRemovedWarp;
    }

    public String getWarpBorder() {
        return this.warpBorder;
    }

    public String getInviteWarp() {
        return this.inviteWarp;
    }

    public String getUnInviteWarp() {
        return this.unInviteWarp;
    }

    public String getPlayerNotFound() {
        return this.playerNotFound;
    }

    public String getWarpError() {
        return this.warpError;
    }

    public String getInvalidWarpArgument() {
        return this.invalidWarpArgument;
    }

    public String getWarpMaxCharacters() {
        return this.warpMaxCharacters;
    }

    public String getMaxWarp() {
        return this.maxWarp;
    }

    public String getWarpRename() {
        return this.warpRename;
    }

    public String getWarpSetWelcome() {
        return this.warpSetWelcome;
    }

    public String getHelp() {
        return this.help;
    }

    public String getWarpRemoveWelcome() {
        return this.warpRemoveWelcome;
    }

    public String getWarpWelcomeEmpty() {
        return this.warpWelcomeEmpty;
    }

    public String getEqualsRename() {
        return this.equalsRename;
    }

    public String getInvitedWarp() {
        return this.invitedWarp;
    }

    public String getLocked() {
        return this.locked;
    }

    public String getUnlocked() {
        return this.unlocked;
    }

    public String getAddWelcome() {
        return this.addWelcome;
    }

    public String getSetWarpAdditional() {
        return this.setWarpAdditional;
    }

    public String getHoverSetToPrivateWarp() {
        return this.hoverSetToPrivateWarp;
    }

    public String getWarpPrivated() {
        return this.warpPrivated;
    }

    public String getWarpPubliced() {
        return this.warpPubliced;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ExtraWarp/", "locale.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/ExtraWarp/", "locale.json", el -> {
            LocaleConfig config = UtilGson.newGson().fromJson(el, LocaleConfig.class);

            this.configVersion = config.getConfigVersion();
            this.reload = config.getReload();
            this.teleportWarp = config.getTeleportWarp();
            this.setWarp = config.getSetWarp();
            this.warpNotFound = config.getWarpNotFound();
            this.warpExist = config.getWarpExist();
            this.privateWarp = config.getPrivateWarp();
            this.warpPrivate = config.getWarpPrivate();
            this.infoWarp = config.getInfoWarp();
            this.blacklistWarp = config.getBlacklistWarp();
            this.warpNotOwner = config.getWarpNotOwner();
            this.warpRemoved = config.getWarpRemoved();
            this.warpAssets = config.getWarpAssets();
            this.warpAssetsPlayer = config.getWarpAssetsPlayer();
            this.setWarpPrivate = config.getSetWarpPrivate();
            this.warpUpdate = config.getWarpUpdate();
            this.publicWarp = config.getPublicWarp();
            this.topWarpTitle = config.getTopWarpTitle();
            this.topWarp = config.getTopWarp();
            this.warpNotYourself = config.getWarpNotYourself();
            this.warpPlayerAlready = config.getWarpPlayerAlready();
            this.inviteWarp = config.getInviteWarp();
            this.unInviteWarp = config.getUnInviteWarp();
            this.blacklistAddedWarp = config.getBlacklistAddedWarp();
            this.blacklistRemovedWarp = config.getBlacklistRemovedWarp();
            this.playerNotFound = config.getPlayerNotFound();
            this.warpError = config.getWarpError();
            this.invalidWarpArgument = config.getInvalidWarpArgument();
            this.warpMaxCharacters = config.getWarpMaxCharacters();
            this.maxWarp = config.getMaxWarp();
            this.warpRename = config.getWarpRename();
            this.warpSetWelcome = config.getWarpSetWelcome();
            this.help = config.getHelp();
            this.warpRemoveWelcome = config.getWarpRemoveWelcome();
            this.warpWelcomeEmpty = config.getWarpWelcomeEmpty();
            this.warpBorder = config.getWarpBorder();
            this.warpNotDimension = config.getWarpNotDimension();
            this.equalsRename = config.getEqualsRename();
            this.invitedWarp = config.getInvitedWarp();
            this.locked = config.getLocked();
            this.unlocked = config.getUnlocked();
            this.addWelcome = config.getAddWelcome();
            this.places = config.getPlaces();
            this.hoverTopWarp = config.getHoverTopWarp();
            this.hoverSetToPrivateWarp = config.getHoverSetToPrivateWarp();
            this.setWarpAdditional = config.getSetWarpAdditional();
            this.warpPrivated = config.getWarpPrivated();
            this.warpPubliced = config.getWarpPubliced();
        }).join();

        if (!completed) {
            ExtraWarp.getLogger().error("Error init locale config, generating new locale config.");
            write();
        }
    }
}
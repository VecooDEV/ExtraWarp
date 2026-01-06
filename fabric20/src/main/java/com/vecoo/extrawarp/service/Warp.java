package com.vecoo.extrawarp.service;

import com.vecoo.extrawarp.ExtraWarp;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString
public class Warp {
    @NotNull
    private String name;
    private double x, y, z;
    private float xRot, yRot;
    @NotNull
    private String dimensionName;
    @NotNull
    private final UUID ownerUUID;
    @NotNull
    private final Set<UUID> invitePlayers, blacklistPlayers, uniquePlayers;
    @NotNull
    private String welcomeText;
    private boolean locked;

    public Warp(@NotNull String name, @NotNull ServerPlayer player, boolean isLocked) {
        this.name = name;
        this.x = getFormatted(player.getX());
        this.y = getFormatted(player.getY());
        this.z = getFormatted(player.getZ());
        this.xRot = getFormatted(player.getXRot());
        this.yRot = getFormatted(player.getYRot());
        this.dimensionName = player.level().dimension().location().getPath();
        this.ownerUUID = player.getUUID();
        this.invitePlayers = new HashSet<>();
        this.blacklistPlayers = new HashSet<>();
        this.uniquePlayers = new HashSet<>();
        this.welcomeText = "";
        this.locked = isLocked;
    }

    public void setName(@NotNull String name) {
        this.name = name;
        ExtraWarp.getInstance().getWarpService().markDirty();
    }

    public void setCoordinatePosition(double x, double y, double z, float xRot, float yRot) {
        this.x = getFormatted(x);
        this.y = getFormatted(y);
        this.z = getFormatted(z);
        this.xRot = getFormatted(xRot);
        this.yRot = getFormatted(yRot);
        ExtraWarp.getInstance().getWarpService().markDirty();
    }

    public void updatePosition(@NotNull ServerPlayer player) {
        this.x = getFormatted(player.getX());
        this.y = getFormatted(player.getY());
        this.z = getFormatted(player.getZ());
        this.xRot = getFormatted(player.getXRot());
        this.yRot = getFormatted(player.getYRot());
        this.dimensionName = player.level().dimension().location().getPath();
        ExtraWarp.getInstance().getWarpService().markDirty();
    }

    public void setDimensionName(@NotNull String dimensionName) {
        this.dimensionName = dimensionName;
        ExtraWarp.getInstance().getWarpService().markDirty();
    }

    public boolean addInvitePlayer(@NotNull UUID playerUUID) {
        if (!this.invitePlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpService().markDirty();
        return true;
    }

    public boolean removeInvitePlayer(@NotNull UUID playerUUID) {
        if (!this.invitePlayers.remove(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpService().markDirty();
        return true;
    }

    public boolean addBlacklistPlayer(@NotNull UUID playerUUID) {
        if (!this.blacklistPlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpService().markDirty();
        return true;
    }

    public boolean removeBlacklistPlayer(@NotNull UUID playerUUID) {
        if (!this.blacklistPlayers.remove(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpService().markDirty();
        return true;
    }

    public boolean addUniquePlayer(@NotNull UUID playerUUID) {
        if (!this.uniquePlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpService().markDirty();
        return true;
    }

    public void setWelcomeText(@Nullable String text) {
        this.welcomeText = text == null ? "" : text;
        ExtraWarp.getInstance().getWarpService().markDirty();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        ExtraWarp.getInstance().getWarpService().markDirty();
    }

    private double getFormatted(double value) {
        return Double.parseDouble(String.format("%.2f", value));
    }

    private float getFormatted(float value) {
        return Math.round(value * 100F) / 100F;
    }
}
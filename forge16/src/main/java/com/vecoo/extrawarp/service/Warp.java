package com.vecoo.extrawarp.service;

import com.vecoo.extrawarp.ExtraWarp;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString
public class Warp {
    @Nonnull
    private String name;
    private double x, y, z;
    private float xRot, yRot;
    @Nonnull
    private String dimensionName;
    @Nonnull
    private final UUID ownerUUID;
    @Nonnull
    private final Set<UUID> invitePlayers, blacklistPlayers, uniquePlayers;
    @Nonnull
    private String welcomeText;
    private boolean locked;

    public Warp(@Nonnull String name, @Nonnull ServerPlayerEntity player, boolean isLocked) {
        this.name = name;
        this.x = getFormatted(player.getX());
        this.y = getFormatted(player.getY());
        this.z = getFormatted(player.getZ());
        this.xRot = getFormatted(player.xRot);
        this.yRot = getFormatted(player.yRot);
        this.dimensionName = player.level.dimension().location().getPath();
        this.ownerUUID = player.getUUID();
        this.invitePlayers = new HashSet<>();
        this.blacklistPlayers = new HashSet<>();
        this.uniquePlayers = new HashSet<>();
        this.welcomeText = "";
        this.locked = isLocked;
    }

    public void setName(@Nonnull String name) {
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

    public void updatePosition(@Nonnull ServerPlayerEntity player) {
        this.x = getFormatted(player.getX());
        this.y = getFormatted(player.getY());
        this.z = getFormatted(player.getZ());
        this.xRot = getFormatted(player.xRot);
        this.yRot = getFormatted(player.yRot);
        this.dimensionName = player.level.dimension().location().getPath();
        ExtraWarp.getInstance().getWarpService().markDirty();
    }

    public void setDimensionName(@Nonnull String dimensionName) {
        this.dimensionName = dimensionName;
        ExtraWarp.getInstance().getWarpService().markDirty();
    }

    public boolean addInvitePlayer(@Nonnull UUID playerUUID) {
        if (!this.invitePlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpService().markDirty();
        return true;
    }

    public boolean removeInvitePlayer(@Nonnull UUID playerUUID) {
        if (!this.invitePlayers.remove(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpService().markDirty();
        return true;
    }

    public boolean addBlacklistPlayer(@Nonnull UUID playerUUID) {
        if (!this.blacklistPlayers.add(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpService().markDirty();
        return true;
    }

    public boolean removeBlacklistPlayer(@Nonnull UUID playerUUID) {
        if (!this.blacklistPlayers.remove(playerUUID)) {
            return false;
        }

        ExtraWarp.getInstance().getWarpService().markDirty();
        return true;
    }

    public boolean addUniquePlayer(@Nonnull UUID playerUUID) {
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
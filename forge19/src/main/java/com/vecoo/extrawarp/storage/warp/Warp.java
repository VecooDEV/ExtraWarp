package com.vecoo.extrawarp.storage.warp;

import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class Warp {
    private String name;
    private List<String> coordinatePosition;
    private String dimensionName;
    private final UUID ownerUUID;
    private final Set<UUID> invitePlayers;
    private final Set<UUID> blacklistPlayers;
    private final Set<UUID> uniquePlayers;
    private String welcomeText;
    private int uniquePlayersCount;
    private boolean locked;

    public Warp(String name, ServerPlayer player, boolean isLocked) {
        this.name = name;
        this.coordinatePosition = Arrays.asList(String.format("%.2f", player.getX()), String.format("%.2f", player.getY()), String.format("%.2f", player.getZ()), String.format("%.2f", player.getXRot()), String.format("%.2f", player.getYRot()));
        this.dimensionName = player.getLevel().dimension().location().getPath();
        this.ownerUUID = player.getUUID();
        this.invitePlayers = new HashSet<>();
        this.blacklistPlayers = new HashSet<>();
        this.uniquePlayers = new HashSet<>();
        this.uniquePlayersCount = 0;
        this.welcomeText = "";
        this.locked = isLocked;
    }

    public String getName() {
        return this.name;
    }

    public float getX() {
        return Float.parseFloat(this.coordinatePosition.get(0));
    }

    public float getY() {
        return Float.parseFloat(this.coordinatePosition.get(1));
    }

    public float getZ() {
        return Float.parseFloat(this.coordinatePosition.get(2));
    }

    public float getXRot() {
        return Float.parseFloat(this.coordinatePosition.get(3));
    }

    public float getYRot() {
        return Float.parseFloat(this.coordinatePosition.get(4));
    }

    public String getDimensionName() {
        return this.dimensionName;
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    public Set<UUID> getInvitePlayers() {
        return this.invitePlayers;
    }

    public Set<UUID> getBlacklistPlayers() {
        return this.blacklistPlayers;
    }

    public Set<UUID> getUniquePlayers() {
        return this.uniquePlayers;
    }

    public int getUniquePlayersCount() {
        return this.uniquePlayersCount;
    }

    public String getWelcomeText() {
        return this.welcomeText;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinatePosition(float x, float y, float z, float xRot, float yRot) {
        this.coordinatePosition = Arrays.asList(String.format("%.2f", x), String.format("%.2f", y), String.format("%.2f", z), String.format("%.2f", xRot), String.format("%.2f", yRot));
    }

    public void updatePosition(ServerPlayer player) {
        this.dimensionName = player.getLevel().dimension().location().getPath();
        this.coordinatePosition = Arrays.asList(String.format("%.2f", player.getX()), String.format("%.2f", player.getY()), String.format("%.2f", player.getZ()), String.format("%.2f", player.getXRot()), String.format("%.2f", player.getYRot()));
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public void addInvitePlayer(UUID playerUUID) {
        this.invitePlayers.add(playerUUID);
    }

    public void removeInvitePlayer(UUID playerUUID) {
        this.invitePlayers.remove(playerUUID);
    }

    public void addBlacklistPlayer(UUID playerUUID) {
        this.blacklistPlayers.add(playerUUID);
    }

    public void removeBlacklistPlayer(UUID playerUUID) {
        this.blacklistPlayers.remove(playerUUID);
    }

    public void addUniquePlayer(UUID playerUUID) {
        this.uniquePlayers.add(playerUUID);
        this.uniquePlayersCount++;
    }

    public void setWelcomeText(String text) {
        this.welcomeText = text;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || getClass() != object.getClass()) {
            return false;
        }


        return this.name.equalsIgnoreCase(((Warp) object).name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
package com.vecoo.extrawarp.storage.warp;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.server.MinecraftServer;

import java.util.HashSet;
import java.util.Set;

public class WarpProvider {
    private transient final String filePath;
    private final Set<Warp> warps;

    public WarpProvider(String filePath, MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);

        this.warps = new HashSet<>();
    }

    public Set<Warp> getStorage() {
        return this.warps;
    }

    public boolean addWarp(Warp warp) {
        if (!this.warps.add(warp)) {
            ExtraWarp.getLogger().error("[ExtraWarp] An error occurred while creating the warp: " + warp.getName());
            return false;
        }

        return true;
    }

    public boolean removeWarp(Warp warp) {
        if (!this.warps.remove(warp)) {
            ExtraWarp.getLogger().error("[ExtraWarp] An error occurred while remove the warp: " + warp.getName());
            return false;
        }

        return true;
    }

    public void write() {
        UtilGson.writeFileAsync(this.filePath, "WarpStorage.json", UtilGson.newGson().toJson(this)).join();
    }

    private void writeInterval() {
        TaskTimer.builder()
                .withoutDelay()
                .interval(90 * 20L)
                .infinite()
                .consume(task -> {
                    if (ExtraWarp.getInstance().getServer().isRunning()) {
                        UtilGson.writeFileAsync(this.filePath, "WarpStorage.json", UtilGson.newGson().toJson(this));
                    }
                })
                .build();
    }

    public void init() {
        UtilGson.readFileAsync(this.filePath, "WarpStorage.json", el -> this.warps.addAll(UtilGson.newGson().fromJson(el, WarpProvider.class).getStorage())).join();
        writeInterval();
    }
}
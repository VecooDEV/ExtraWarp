package com.vecoo.extrawarp.service;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WarpService {
    private transient final String filePath;
    private final List<Warp> warps;

    private transient volatile boolean dirty = false;

    public WarpService(@NotNull String filePath, @NotNull MinecraftServer server) {
        this.filePath = UtilWorld.resolveWorldDirectory(filePath, server);

        this.warps = new ArrayList<>();
    }

    @NotNull
    public List<Warp> getWarps() {
        return this.warps;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void addWarp(@NotNull Warp warp) {
        this.warps.add(warp);
        this.dirty = true;
    }

    public boolean removeWarp(@NotNull Warp warp) {
        if (!this.warps.remove(warp)) {
            ExtraWarp.getLogger().error("An error occurred while remove the warp {}.", warp.getName());
            return false;
        }

        this.dirty = true;
        return true;
    }

    public void save() {
        UtilGson.writeFileAsync(this.filePath, "warps.json", UtilGson.getGson().toJson(this)).join();
    }

    private void saveInterval() {
        TaskTimer.builder()
                .withoutDelay()
                .interval(120 * 20L)
                .infinite()
                .consume(task -> {
                    if (ExtraWarp.getInstance().getServer().isRunning() && this.dirty) {
                        UtilGson.writeFileAsync(this.filePath, "warps.json",
                                UtilGson.getGson().toJson(this)).thenRun(() -> this.dirty = false);
                    }
                })
                .build();
    }

    public void init() {
        this.warps.clear();

        UtilGson.readFileAsync(this.filePath, "warps.json",
                el -> this.warps.addAll(UtilGson.getGson().fromJson(el, WarpService.class).getWarps())).join();

        saveInterval();
    }
}
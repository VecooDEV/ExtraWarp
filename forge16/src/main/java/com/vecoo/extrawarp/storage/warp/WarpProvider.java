package com.vecoo.extrawarp.storage.warp;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extralib.task.TaskTimer;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrawarp.ExtraWarp;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class WarpProvider {
    private transient final String filePath;
    private final Set<Warp> warps;

    private transient boolean intervalStarted = false;
    private transient volatile boolean dirty = false;

    public WarpProvider(@Nonnull String filePath, @Nonnull MinecraftServer server) {
        this.filePath = UtilWorld.worldDirectory(filePath, server);

        this.warps = new HashSet<>();
    }

    @Nonnull
    public Set<Warp> getStorage() {
        return this.warps;
    }

    public void updateStorage() {
        this.dirty = true;
    }

    public boolean addWarp(@Nonnull Warp warp) {
        if (!this.warps.add(warp)) {
            ExtraWarp.getLogger().error("An error occurred while creating the warp: " + warp.getName());
            return false;
        }

        this.dirty = true;
        return true;
    }

    public boolean removeWarp(@Nonnull Warp warp) {
        if (!this.warps.remove(warp)) {
            ExtraWarp.getLogger().error("An error occurred while remove the warp: " + warp.getName());
            return false;
        }

        this.dirty = true;
        return true;
    }

    public void write() {
        UtilGson.writeFileAsync(this.filePath, "WarpStorage.json", UtilGson.newGson().toJson(this)).join();
    }

    private void writeInterval() {
        if (!this.intervalStarted) {
            TaskTimer.builder()
                    .withoutDelay()
                    .interval(150 * 20L)
                    .infinite()
                    .consume(task -> {
                        if (ExtraWarp.getInstance().getServer().isRunning() && this.dirty) {
                            UtilGson.writeFileAsync(this.filePath, "WarpStorage.json",
                                    UtilGson.newGson().toJson(this)).thenRun(() -> this.dirty = false);
                        }
                    })
                    .build();


            this.intervalStarted = true;
        }
    }

    public void init() {
        UtilGson.readFileAsync(this.filePath, "WarpStorage.json",
                el -> this.warps.addAll(UtilGson.newGson().fromJson(el, WarpProvider.class).getStorage())).join();

        writeInterval();
    }
}
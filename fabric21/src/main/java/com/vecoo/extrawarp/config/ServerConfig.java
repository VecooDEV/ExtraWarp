package com.vecoo.extrawarp.config;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extrawarp.ExtraWarp;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerConfig {
    private int baseCountWarp = 2;
    private int maxMaxCharactersWarp = 10;
    private List<String> permissionListingList = Arrays.asList("extrawarp.count.3", "extrawarp.count.5", "extrawarp.count.7");

    public int getBaseCountWarp() {
        return this.baseCountWarp;
    }

    public int getMaxMaxCharactersWarp() {
        return this.maxMaxCharactersWarp;
    }

    public List<String> getPermissionListingList() {
        return this.permissionListingList;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ExtraWarp/", "config.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ExtraWarp/", "config.json", el -> {
                ServerConfig config = UtilGson.newGson().fromJson(el, ServerConfig.class);

                this.baseCountWarp = config.getBaseCountWarp();
                this.maxMaxCharactersWarp = config.getMaxMaxCharactersWarp();
                this.permissionListingList = config.getPermissionListingList();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ExtraWarp.getLogger().error("[ExtraWarp] Error in config.");
            write();
        }
    }

}

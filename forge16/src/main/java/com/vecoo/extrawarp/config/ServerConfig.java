package com.vecoo.extrawarp.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.gson.UtilGson;

import java.util.Set;

public class ServerConfig {
    private int baseCountWarp = 2;
    private int maxMaxCharactersWarp = 10;
    private Set<String> permissionListingList = Sets.newHashSet("extrawarp.count.3", "extrawarp.count.5", "extrawarp.count.7");

    public int getBaseCountWarp() {
        return this.baseCountWarp;
    }

    public int getMaxMaxCharactersWarp() {
        return this.maxMaxCharactersWarp;
    }

    public Set<String> getPermissionListingList() {
        return this.permissionListingList;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ExtraWarp/", "config.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/ExtraWarp/", "config.json", el -> {
            ServerConfig config = UtilGson.newGson().fromJson(el, ServerConfig.class);

            this.baseCountWarp = config.getBaseCountWarp();
            this.maxMaxCharactersWarp = config.getMaxMaxCharactersWarp();
            this.permissionListingList = config.getPermissionListingList();
        }).join();

        if (!completed) {
            write();
        }
    }
}

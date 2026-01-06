package com.vecoo.extrawarp.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

import java.util.Set;

@Getter
@ConfigSerializable
public class ServerConfig {
    private int baseCountWarp = 2;
    private int maxCharactersWarp = 10;
    private Set<String> blockedNamesWarp = Sets.newHashSet("Name");
    private Set<String> permissionList = Sets.newHashSet("extrawarp.count.3", "extrawarp.count.5", "extrawarp.count.7");
}
package com.vecoo.extrawarp.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.meta.Comment;
import lombok.Getter;

import java.util.Set;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class ServerConfig {
    @Comment("Maximum number of warps per player in base value.")
    private int baseCountWarp = 2;
    @Comment("Maximum number of characters in a warp name.")
    private int maxCharactersWarp = 12;
    @Comment("Blocked Warp Names.")
    private Set<String> blockedNamesWarp = Sets.newHashSet("Name");
    @Comment("Permissions to expand warp limits.")
    private Set<String> permissionList = Sets.newHashSet("extrawarp.count.3", "extrawarp.count.5", "extrawarp.count.7");
}

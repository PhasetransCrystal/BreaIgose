package com.phasetranscrystal.igose;

import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.filter.FilterType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class NewRegistries {
    public static final Registry<IGOContentType<?>> CONTENT_TYPE = new RegistryBuilder<>(Keys.CONTENT_TYPE).sync(true).create();
    public static final Registry<FilterType<?>> FILTER_TYPE = new RegistryBuilder<>(Keys.FILTER_TYPE).sync(true).create();

    public static class Keys{
        public static final ResourceKey<Registry<IGOContentType<?>>> CONTENT_TYPE = ResourceKey.createRegistryKey(BreaIgose.location("content_type"));
        public static final ResourceKey<Registry<FilterType<?>>> FILTER_TYPE = ResourceKey.createRegistryKey(BreaIgose.location("filter_type"));
    }
}

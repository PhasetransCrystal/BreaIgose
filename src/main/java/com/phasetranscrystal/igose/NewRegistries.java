package com.phasetranscrystal.igose;

import com.phasetranscrystal.igose.content_type.IContentType;
import com.phasetranscrystal.igose.filter.FilterType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class NewRegistries {
    public static final Registry<IContentType<?>> CONTENT_TYPE = new RegistryBuilder<>(Keys.CONTENT_TYPE).sync(true).create();
    public static final Registry<FilterType<?>> FILTER_TYPE = new RegistryBuilder<>(Keys.FILTER_TYPE).sync(true).create();

    public static class Keys{
        public static final ResourceKey<Registry<IContentType<?>>> CONTENT_TYPE = ResourceKey.createRegistryKey(BreaIgose.location("content_type"));
        public static final ResourceKey<Registry<FilterType<?>>> FILTER_TYPE = ResourceKey.createRegistryKey(BreaIgose.location("filter_type"));
    }
}

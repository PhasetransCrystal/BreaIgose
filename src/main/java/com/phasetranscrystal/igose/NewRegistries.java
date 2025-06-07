package com.phasetranscrystal.igose;

import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.filter.FilterType;
import com.phasetranscrystal.igose.supplier.transformer.IGOTransformer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class NewRegistries {
    public static final Registry<IGOContentType<?>> CONTENT_TYPE = new RegistryBuilder<>(Keys.CONTENT_TYPE).sync(true).create();
    public static final Registry<FilterType<?>> FILTER_TYPE = new RegistryBuilder<>(Keys.FILTER_TYPE).sync(true).create();
    public static final Registry<IGOTransformer<?,?>> TRANSFORMER = new RegistryBuilder<>(Keys.TRANSFORMER).create();

    public static class Keys{
        public static final ResourceKey<Registry<IGOContentType<?>>> CONTENT_TYPE = ResourceKey.createRegistryKey(BreaIgose.location("content_type"));
        public static final ResourceKey<Registry<FilterType<?>>> FILTER_TYPE = ResourceKey.createRegistryKey(BreaIgose.location("filter_type"));
        public static final ResourceKey<Registry<IGOTransformer<?,?>>> TRANSFORMER = ResourceKey.createRegistryKey(BreaIgose.location("transformer"));
    }
}

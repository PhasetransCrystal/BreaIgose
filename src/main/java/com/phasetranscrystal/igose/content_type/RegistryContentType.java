package com.phasetranscrystal.igose.content_type;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;

public abstract class RegistryContentType<T, R> implements IContentType<T> {

    public abstract Registry<R> getRegistry();

    public abstract R transform(T t);

    public Optional<ResourceKey<R>> getRK(T t) {
        return getRegistry().getResourceKey(transform(t));
    }

    public ResourceLocation getRL(T t) {
        return getRegistry().getKey(transform(t));
    }

    public boolean is(ResourceLocation key, T t) {
        return is(TagKey.create(getRegistry().key(), key), t);
    }

    public boolean is(TagKey<R> key, T t) {
        return getRegistry().getOrCreateTag(key).contains(getRegistry().wrapAsHolder(transform(t)));
    }

}

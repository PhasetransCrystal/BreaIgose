package com.phasetranscrystal.igose.content_type;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;

public interface RegistryContentType<T, R> extends IContentType<T> {

    Registry<R> getRegistry();

    R transform(T t);

    default Optional<ResourceKey<R>> getRK(T t) {
        return getRegistry().getResourceKey(transform(t));
    }

    default ResourceLocation getRL(T t) {
        return getRegistry().getKey(transform(t));
    }

    default boolean is(ResourceLocation key, T t) {
        return is(TagKey.create(getRegistry().key(), key), t);
    }

    default boolean is(TagKey<R> key, T t) {
        return getRegistry().getOrCreateTag(key).contains(getRegistry().wrapAsHolder(transform(t)));
    }

}

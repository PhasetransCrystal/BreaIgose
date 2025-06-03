package com.phasetranscrystal.igose.content_type;

import com.mojang.serialization.Codec;
import com.phasetranscrystal.igose.NewRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface IGOContentType<T> {
    Class<T> getContentClass();

    Codec<T> codec();

    //---[数据比对]---

    default boolean isSame(T obj1, T obj2) {
        return copy(obj1).equals(copy(obj2));
    }

    default boolean classMatch(Object obj) {
        return getContentClass().isInstance(obj);
    }

    boolean isEmpty(T value);

    T copy(T obj);

    T createEmpty();

    default ContentStack<T> createEmptyStack() {
        return new ContentStack<>(this, createEmpty(), 0);
    }

    T wildcard(T root);

    default ContentStack<T> createStack(T root, double count) {
        return new ContentStack<>(this, root, count);
    }

    default ContentStack<T> createStack(T root) {
        return createStack(root, 1);
    }

    default ContentStack<T> createCopyStack(T root, double count) {
        return new ContentStack<>(this, copy(root), count);
    }

    default ResourceKey<IGOContentType<?>> getResourceKey() {
        return NewRegistries.CONTENT_TYPE.getResourceKey(this).get();
    }

    default ResourceLocation getLocation() {
        return getResourceKey().location();
    }

}

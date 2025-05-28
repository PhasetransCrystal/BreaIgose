package com.phasetranscrystal.igose.filter;

import com.mojang.serialization.Codec;
import com.phasetranscrystal.igose.NewRegistries;
import com.phasetranscrystal.igose.content_type.IContentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

public record FilterType<T extends IFilter<T>>(Codec<T> codec, Optional<Supplier<IContentType<T>>> rootFiltrationType) {

    public FilterType(Codec<T> codec) {
        this(codec, Optional.empty());
    }

    public boolean contentTypeMatch(IContentType<?> contentType) {
        return rootFiltrationType.isEmpty() || rootFiltrationType.get().get() == contentType;
    }

    public boolean contentTypeMatch(IContentType.Stack<?> stack) {
        return contentTypeMatch(stack.getType());
    }

    public ResourceKey<FilterType<?>> getResourceKey() {
        return NewRegistries.FILTER_TYPE.getResourceKey(this).get();
    }

    public ResourceLocation getLocation() {
        return getResourceKey().location();
    }


}

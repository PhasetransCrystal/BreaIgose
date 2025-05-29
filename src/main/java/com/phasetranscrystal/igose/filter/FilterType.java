package com.phasetranscrystal.igose.filter;

import com.mojang.serialization.Codec;
import com.phasetranscrystal.igose.NewRegistries;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

public record FilterType<T extends IGOFilter<T>>(Codec<T> codec, Optional<Supplier<IGOContentType<T>>> rootFiltrationType) {

    public FilterType(Codec<T> codec) {
        this(codec, Optional.empty());
    }

    public boolean contentTypeMatch(IGOContentType<?> contentType) {
        return rootFiltrationType.isEmpty() || rootFiltrationType.get().get() == contentType;
    }

    public boolean contentTypeMatch(IGOContentType.Stack<?> stack) {
        return contentTypeMatch(stack.getType());
    }

    public ResourceKey<FilterType<?>> getResourceKey() {
        return NewRegistries.FILTER_TYPE.getResourceKey(this).get();
    }

    public ResourceLocation getLocation() {
        return getResourceKey().location();
    }

    public String getNameKey() {
        return getLocation().toLanguageKey("brea.igose.filter","name");
    }

    public String getExplainKey() {
        return getLocation().toLanguageKey("brea.igose.filter","explain");
    }
}

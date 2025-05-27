package com.phasetranscrystal.igose.filter;

import com.mojang.serialization.Codec;
import com.phasetranscrystal.igose.content_type.IContentType;

import java.util.Optional;
import java.util.function.Supplier;

public record FilterType<T, C extends IFilter<T>>(Codec<C> codec, Optional<Supplier<IContentType<T>>> rootFiltrationType) {

    public FilterType(Codec<C> codec) {
        this(codec, Optional.empty());
    }

    

}

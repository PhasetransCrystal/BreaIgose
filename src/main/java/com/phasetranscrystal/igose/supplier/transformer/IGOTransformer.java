package com.phasetranscrystal.igose.supplier.transformer;

import com.phasetranscrystal.igose.NewRegistries;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.supplier.IGOSupplier;
import com.phasetranscrystal.igose.supplier.MergedIGOSupplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class IGOTransformer<F, T> {
    public final IGOContentType<F> fromType;
    public final IGOContentType<T> toType;

    public IGOTransformer(IGOContentType<F> fromType, IGOContentType<T> toType) {
        this.fromType = fromType;
        this.toType = toType;
    }

    protected abstract IGOSupplier<T> execute(IGOSupplier<F> origin);

    public Optional<IGOSupplier<T>> transformByCheck(IGOSupplier<?> origin){
        if(origin.getType() == fromType) return transform((IGOSupplier<F>)origin);
        return Optional.empty();
    }

    public Optional<IGOSupplier<T>> transform(IGOSupplier<F> origin){
        if(origin.disableTransform(this)) return Optional.empty();
        return Optional.of(execute(origin));
    };

    /**
     * 当所有供应器都未转化出有效的
     */
    public Optional<MergedIGOSupplier<T>> transformAll(IGOSupplier<F> atLeastOne, IGOSupplier<F>... suppliers) {
        List<IGOSupplier<T>> list = Stream.concat(Stream.of(atLeastOne), Arrays.stream(suppliers))
                .map(this::transform).flatMap(Optional::stream).filter(Objects::nonNull).filter(IGOSupplier::notEmpty).toList();
        return list.isEmpty() ? Optional.empty() : Optional.of(new MergedIGOSupplier.Stable<>(list, atLeastOne.isSnapshot()));
    }

    public ResourceKey<IGOTransformer<?, ?>> getResourceKey() {
        return NewRegistries.TRANSFORMER.getResourceKey(this).get();
    }

    public ResourceLocation getLocation() {
        return getResourceKey().location();
    }
}

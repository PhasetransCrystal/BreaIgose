package com.phasetranscrystal.igose;

import com.phasetranscrystal.nonard.migrate.ingame_obj_se.supplier.IGOSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public record IGODispatcher<F, T>(Class<F> fromClass, Class<T> toClass,
                                  Function<IGOSupplier<F>, IGOSupplier<T>> transformer) implements IGOSupplier.Dispatcher<F, T> {
    @Override
    public Class<F> originalTargetClass() {
        return fromClass;
    }

    @Override
    public Class<T> resultTargetClass() {
        return toClass;
    }

    @Override
    public IGOSupplier<T> transform(IGOSupplier<F> obj) {
        return transformer.apply(obj);
    }

    public List<IGOSupplier<T>> transformAll(List<IGOSupplier<?>> objs) {
        if (objs == null || objs.isEmpty()) {
            return List.of();
        }
        List<IGOSupplier<T>> result = new ArrayList<>();
        objs.stream().filter(c -> c.targetClass().equals(fromClass))
                .map(f -> transformer.apply((IGOSupplier<F>) f))
                .filter(Objects::nonNull)
                .forEach(result::add);
        return result;
    }
}

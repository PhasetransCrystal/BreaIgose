package com.phasetranscrystal.igose;

import com.phasetranscrystal.nonard.migrate.ingame_obj_se.supplier.IGOSupplier;

public interface IGObjectConverter<F, T> {
    Class<F> convertFromClass();

    Class<T> convertToClass();

    IGOSupplier<T> convertFrom(IGOSupplier<F> from);
}

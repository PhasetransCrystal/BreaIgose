package com.phasetranscrystal.igose.supplier;

import javax.annotation.Nonnull;
import java.util.List;

public interface IGOMultiSupplier<T> {
    default boolean isStable() {
        return false;
    }

    @Nonnull
    List<IGOSupplier<T>> getSuppliers();

    // to igos group //TODO
//    @Nonnull
//    default <T> List<IGOSupplier<T>> getSuppliers(@Nonnull Class<T> clazz) {
//        return getSuppliers().stream().filter(sup -> sup.targetClass() == clazz).map(sup -> (IGOSupplier<T>) sup).toList();
//    }

    boolean canAddSupplier(IGOSupplier<T> supplier);

    boolean canRemoveSupplier(IGOSupplier<T> supplier);

    boolean addSupplier(IGOSupplier<T> supplier);

    boolean removeSupplier(IGOSupplier<T> supplier);

    boolean containsSupplier(IGOSupplier<T> supplier);

}

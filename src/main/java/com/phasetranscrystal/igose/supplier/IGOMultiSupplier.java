package com.phasetranscrystal.igose.supplier;

import org.apache.commons.lang3.function.ToBooleanBiFunction;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiConsumer;

public interface IGOMultiSupplier<T> extends IGOSupplier<T> {
    default boolean isStable() {
        return false;
    }

    @Nonnull
    List<IGOSupplier<T>> getSuppliers();

    @Override
    default boolean foreachSlot(Int2BooleanBiFunction<IGOSupplier<T>> consumer) {
        for (IGOSupplier<T> supplier : getSuppliers()) {
            if(supplier.foreachSlot(consumer)) return true;
        }
        return false;
    }

    boolean canAddSupplier(IGOSupplier<T> supplier);

    boolean canRemoveSupplier(IGOSupplier<T> supplier);

    boolean addSupplier(IGOSupplier<T> supplier);

    default boolean addSupplierNullable(IGOSupplier<T> supplier){
        return supplier != null && addSupplier(supplier);
    };

    boolean removeSupplier(IGOSupplier<T> supplier);

    boolean containsSupplier(IGOSupplier<T> supplier);

}

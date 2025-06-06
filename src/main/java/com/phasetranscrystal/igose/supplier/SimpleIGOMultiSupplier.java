package com.phasetranscrystal.igose.supplier;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleIGOMultiSupplier<T> extends SimpleIGOSupplier<T> implements IGOMultiSupplier<T>{
    protected final List<IGOSupplier<T>> suppliers = new ArrayList<>();

    public SimpleIGOMultiSupplier(boolean isSnapshot) {
        super(isSnapshot, false);
    }

    @Override
    public @NotNull List<IGOSupplier<T>> getSuppliers() {
        return new ArrayList<>(suppliers);
    }

    @Override
    public boolean canAddSupplier(IGOSupplier<T> supplier) {
        return !isStable() && !containsSupplier(supplier);
    }

    @Override
    public boolean canRemoveSupplier(IGOSupplier<T> supplier) {
        return !isStable() && containsSupplier(supplier);
    }

    @Override
    public boolean addSupplier(IGOSupplier<T> supplier) {
        if(!canAddSupplier(supplier)) return false;
        supplier.addChangeFeedback(this);
        return suppliers.add(supplier);
    }

    @Override
    public boolean removeSupplier(IGOSupplier<T> supplier) {
        if(!canRemoveSupplier(supplier)) return false;
        supplier.removeChangeFeedback(this);
        return suppliers.remove(supplier);
    }

    @Override
    public boolean isEmpty() {
        return suppliers.isEmpty() || suppliers.stream().allMatch(IGOSupplier::isEmpty);
    }

    @Override
    public boolean containsSupplier(IGOSupplier<T> supplier) {
        return suppliers.contains(supplier);
    }
}

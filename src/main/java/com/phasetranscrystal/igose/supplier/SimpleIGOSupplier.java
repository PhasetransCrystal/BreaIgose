package com.phasetranscrystal.igose.supplier;

import com.phasetranscrystal.igose.extractor.ExtractResultPreview;

import java.util.ArrayList;
import java.util.List;

public abstract class SimpleIGOSupplier<T> implements IGOSupplier<T> {
    protected final List<IGOSupplier<T>> parent = new ArrayList<>();
    protected ExtractResultPreview<T> lastResultPreview;
    public final boolean isSnapshot;
    public final boolean isRoot;

    public SimpleIGOSupplier(boolean isSnapshot, boolean isRoot) {
        this.isSnapshot = isSnapshot;
        this.isRoot = isRoot;
    }

    @Override
    public void bootstrapResultPreview(ExtractResultPreview<T> resultPreview) {
        this.lastResultPreview = resultPreview;
    }

    @Override
    public void boostrapChange() {
        this.lastResultPreview = null;
        parent.forEach(IGOSupplier::boostrapChange);
    }

    @Override
    public boolean isSnapshot() {
        return isSnapshot;
    }

    @Override
    public void addChangeFeedback(IGOSupplier<T> supplier) {
        this.parent.add(supplier);
    }

    @Override
    public void removeChangeFeedback(IGOSupplier<T> supplier) {
        this.parent.remove(supplier);
    }

    @Override
    public boolean checkAvailability(ExtractResultPreview<T> resultPreview) {
        return resultPreview == lastResultPreview;
    }
}

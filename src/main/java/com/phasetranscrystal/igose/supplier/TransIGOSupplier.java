package com.phasetranscrystal.igose.supplier;

import com.phasetranscrystal.igose.supplier.transformer.IGOTransformer;

public abstract class TransIGOSupplier<F,T> extends SimpleIGOSupplier<T>{
    public final IGOSupplier<F> transFrom;
    public final IGOTransformer<F,T> transformer;

    public TransIGOSupplier(IGOSupplier<F> transFrom, IGOTransformer<F,T> transformer, boolean isSnapshot) {
        super(isSnapshot,false);
        this.transFrom = transFrom;
        this.transformer = transformer;
    }

    public IGOSupplier<F> getTransFrom() {
        return transFrom;
    }

    public IGOTransformer<F,T> getTransformer() {
        return transformer;
    }
}

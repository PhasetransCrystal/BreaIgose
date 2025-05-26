package com.phasetranscrystal.igose.content_type;

public class DoubleContentType implements IContentType<Double> {
    @Override
    public Class<Double> getContentClass() {
        return Double.class;
    }

    @Override
    public boolean isSame(Double obj1, Double obj2) {
        return true;
    }

    @Override
    public boolean isEmpty(Double value) {
        return value == 0;
    }

    @Override
    public Double copy(Double obj) {
        return obj;
    }

    @Override
    public Double createEmpty() {
        return 0.0;
    }

    @Override
    public Double wildcard(Double root) {
        return 0.0;
    }
}

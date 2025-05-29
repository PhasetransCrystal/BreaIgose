package com.phasetranscrystal.igose.extractor;

import com.phasetranscrystal.igose.filter.IGOFilter;

public class SimpleExtractor implements IGOExtractor{
    public final IGOFilter<?> filter;
    private double requestCount;

    public SimpleExtractor(IGOFilter<?> filter){
        this.filter = filter;
    }

    public SimpleExtractor(double requestCount, IGOFilter<?> filter){
        this.requestCount = requestCount;
        this.filter = filter;
    }

    @Override
    public IGOFilter<?> targetFilter() {
        return filter;
    }

    @Override
    public double requestCount() {
        return requestCount;
    }

    @Override
    public boolean setRequestCount(double count) {
        if(count <= 0 || count == requestCount) return false;
        requestCount = count;
        return true;
    }

    @Override
    public IGOExtractor copyWithRequestCount(double count) {
        return new SimpleExtractor(count, filter);
    }
}

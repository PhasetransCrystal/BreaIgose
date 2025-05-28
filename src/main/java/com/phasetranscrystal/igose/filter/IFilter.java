package com.phasetranscrystal.igose.filter;

import com.phasetranscrystal.igose.content_type.IContentType;

public interface IFilter<T extends IFilter<T>> {

    FilterType<T> getFilterType();

    default boolean contentTypeMatch(IContentType<?> contentType) {
        return getFilterType().contentTypeMatch(contentType);
    }

}

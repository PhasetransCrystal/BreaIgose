package com.phasetranscrystal.igose.filter;

import com.phasetranscrystal.igose.content_type.IContentType;

public interface IFilter<T> {

    IContentType<T> contentType();

}

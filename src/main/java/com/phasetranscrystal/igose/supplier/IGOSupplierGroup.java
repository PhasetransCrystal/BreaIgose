package com.phasetranscrystal.igose.supplier;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.igose.content_type.IGOContentType;

import java.util.HashMap;
import java.util.Map;

//TODO 整体快照 转换快照源重定向(SnapshotRedirectCache) 数据更改feedback extractor执行器
public class IGOSupplierGroup {
    public final Map<IGOContentType<?>, IGOSupplier<?>> suppliers;

    public IGOSupplierGroup(Map<IGOContentType<?>, IGOSupplier<?>> map) {
        ImmutableMap.Builder<IGOContentType<?>, IGOSupplier<?>> builder = ImmutableMap.builder();
        map.entrySet().stream()
                .filter(e -> e.getValue().getType() == e.getKey())
                .forEach(e -> builder.put(e.getKey(), e.getValue()));
        this.suppliers = builder.build();
    }

}

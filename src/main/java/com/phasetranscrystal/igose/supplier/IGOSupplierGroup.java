package com.phasetranscrystal.igose.supplier;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.extractor.ExtractResultPreview;
import com.phasetranscrystal.igose.extractor.IGOExtractorGroup;

import java.util.HashMap;
import java.util.Map;

//TODO 整体快照 转换快照源重定向(SnapshotRedirectCache) 数据更改feedback extractor执行器
public class IGOSupplierGroup {
    public final Map<IGOContentType<?>, IGOSupplier<?>> suppliers;
    public final boolean isSnapshot;

    public static IGOSupplierGroup create(Map<IGOContentType<?>, IGOSupplier<?>> map) {
        return new IGOSupplierGroup(map, false);
    }

    protected IGOSupplierGroup(Map<IGOContentType<?>, IGOSupplier<?>> map, boolean isSnapshot) {
        ImmutableMap.Builder<IGOContentType<?>, IGOSupplier<?>> builder = ImmutableMap.builder();
        map.entrySet().stream()
                .filter(e -> e.getValue().getType() == e.getKey())
                .forEach(e -> builder.put(e.getKey(), e.getValue()));
        this.suppliers = builder.build();
        this.isSnapshot = isSnapshot;
    }

    public boolean isEmpty() {
        return suppliers.isEmpty() || suppliers.values().stream().allMatch(IGOSupplier::isEmpty);
    }

    public IGOSupplierGroup createSnapshot() {
        Map<IGOContentType<?>, IGOSupplier<?>> cache = new HashMap<>();
        suppliers.forEach((t, s) -> cache.put(t, s.createSnapshot()));
        return new IGOSupplierGroup(cache, true);
    }

    public ExtractResultPreview.Grouped extractBy(IGOExtractorGroup group, boolean greedy, boolean allowTransform) {
        return group.extractBySnapshot(this,greedy,allowTransform);
    }



}

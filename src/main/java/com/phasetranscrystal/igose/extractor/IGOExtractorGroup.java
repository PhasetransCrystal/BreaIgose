package com.phasetranscrystal.igose.extractor;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.supplier.IGOSupplier;
import com.phasetranscrystal.igose.supplier.IGOSupplierGroup;

import java.util.HashMap;
import java.util.Map;

public class IGOExtractorGroup {
    protected final Map<IGOContentType<?>, IGOExtractorSet<?>> extractors = new HashMap<>();

    public IGOExtractorGroup() {
    }

    public IGOExtractorGroup(Map<IGOContentType<?>, IGOExtractorSet<?>> extractors) {
        extractors.entrySet().stream().filter(e -> e.getValue().contentType == e.getKey())
                .forEach(e -> this.extractors.put(e.getKey(), e.getValue()));
    }

    public <T> IGOExtractorGroup(IGOContentType<T> type, IGOExtractorSet<T> set) {
        extractors.put(type, set);
    }

    public IGOExtractorGroup(IGOContentType<?> type, IGOExtractor extractor) {
        extractors.put(type, new IGOExtractorSet<>(type, extractor));
    }

    public <T> IGOExtractorSet<T> set(IGOExtractorSet<T> set) {
        return (IGOExtractorSet<T>) extractors.put(set.contentType, set);
    }

    public <T> void merge(IGOExtractorSet<T> set) {
        if (extractors.containsKey(set.contentType))
            ((IGOExtractorSet<T>) extractors.get(set.contentType)).merge(set);
        else
            extractors.put(set.contentType, set);
    }

    public boolean add(IGOExtractor extractor, IGOContentType<?> type){
        return extractors.computeIfAbsent(type,IGOExtractorSet::new).addExtractor(extractor);
    }

    public boolean remove(IGOExtractor extractor, IGOContentType<?> type){
        return extractors.containsKey(type) && extractors.get(type).removeExtractor(extractor);
    }

    public ImmutableMap<IGOContentType<?>, IGOExtractorSet<?>> getExtractors() {
        return ImmutableMap.copyOf(extractors);
    }

    public ImmutableMap<IGOContentType<?>, IGOSupplier<?>> extractBySnapshot(IGOSupplierGroup group, boolean greedy) {

    }
}

package com.phasetranscrystal.igose.extractor;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.igose.BreaIgose;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.supplier.IGOSupplier;
import com.phasetranscrystal.igose.supplier.IGOSupplierGroup;
import com.phasetranscrystal.igose.supplier.MergedIGOSupplier;
import org.jetbrains.annotations.Nullable;

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

    public boolean add(IGOExtractor extractor, IGOContentType<?> type) {
        return extractors.computeIfAbsent(type, IGOExtractorSet::new).addExtractor(extractor);
    }

    public boolean remove(IGOExtractor extractor, IGOContentType<?> type) {
        return extractors.containsKey(type) && extractors.get(type).removeExtractor(extractor);
    }

    public ImmutableMap<IGOContentType<?>, IGOExtractorSet<?>> getExtractors() {
        return ImmutableMap.copyOf(extractors);
    }


    public ExtractResultPreview.Grouped extractBySnapshot(IGOSupplierGroup group, boolean greedy, boolean allowTransform) {
        Map<IGOContentType<?>, ExtractResultPreview<?>> map = new HashMap<>();
        extractors.forEach((type, set) -> {
            IGOSupplier merged = allowTransform ? transform(type, group.suppliers.values(),(IGOSupplier)group.suppliers.get(type)) : group.suppliers.get(type);

            if (merged != null && !merged.isEmpty()) {
                map.put(type, set.extract(merged,merged.createSnapshot(),greedy));
            }
        });
        return new ExtractResultPreview.Grouped(map, greedy, allowTransform);
    }

    public static <T> MergedIGOSupplier<T> transform(IGOContentType<T> contentType, Iterable<IGOSupplier<?>> suppliers, IGOSupplier<T> add) {
        MergedIGOSupplier<T> merged = new MergedIGOSupplier<>(contentType, false);
        merged.addSupplierNullable(add);
        for (IGOSupplier<?> s : suppliers) {
            BreaIgose.getTransformer(s, contentType)
                    .flatMap(t -> t.transformByCheck(s))
                    .ifPresent(merged::addSupplier);
        }
        return merged;
    }
}

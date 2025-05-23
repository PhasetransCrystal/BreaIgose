package com.phasetranscrystal.igose.extractor;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;

//TODO
public record ExtractResult<T>(boolean greedy, List<Child<T>> children) {

    public ExtractResult(ExtractResultPreview<T> byPreview) {
        this(byPreview.greedy, byPreview.children.stream().map(Child::new).toList());
    }

    public IGOExtractorGroup<T> createFallback() {
        return children.stream().map(Child::createFallback).flatMap(Optional::stream).filter(Objects::nonNull)
                .collect(Collector.of(IGOExtractorGroup::new, IGOExtractorGroup::addExtractor, (a, b) -> a.addExtractors(b.extractors)));
    }

    public record Child<T>(IGOExtractor<T> extractor, boolean valid, double requiredCount, double extractedCount,
                           ImmutableMap<Integer, T> objByIndex) {

        public Child(ExtractResultPreview.Child<T> child) {
            this(child.extractor, true, child.requiredCount, child.extractedCount, child.extractedByIndex);
        }

        public boolean matched() {
            return requiredCount <= extractedCount;
        }

        public Optional<IGOExtractor<T>> createFallback() {
            return matched() ? Optional.empty() : extractor.copyWithRequestCount(valid ? requiredCount - extractedCount : requiredCount);
        }

    }
}

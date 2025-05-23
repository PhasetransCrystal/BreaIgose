package com.phasetranscrystal.igose.extractor;


import com.mojang.datafixers.util.Either;
import com.phasetranscrystal.nonard.migrate.ingame_obj_se.supplier.IGOSupplier;

import java.util.*;
import java.util.stream.Collector;

public class IGOExtractorGroup<T> {

    public final List<IGOExtractor<T>> extractors = new ArrayList<>();

    public IGOExtractorGroup() {
    }

    public IGOExtractorGroup(IGOExtractor<T> extractor) {
        extractors.add(extractor);
    }

    public IGOExtractorGroup(List<IGOExtractor<T>> extractors) {
        this.extractors.addAll(extractors);
    }

    @SafeVarargs
    public IGOExtractorGroup(IGOExtractor<T>... extractors) {
        this.extractors.addAll(Arrays.asList(extractors));
    }

    public IGOExtractorGroup<T> addExtractor(IGOExtractor<T> extractor) {
        extractors.add(extractor);
        return this;
    }

    public IGOExtractorGroup<T> addExtractors(List<IGOExtractor<T>> extractors) {
        this.extractors.addAll(extractors);
        return this;
    }

    public IGOExtractorGroup<T> removeExtractor(IGOExtractor<T> extractor) {
        extractors.remove(extractor);
        return this;
    }

    public IGOExtractorGroup<T> removeExtractors(List<IGOExtractor<T>> extractors) {
        this.extractors.removeAll(extractors);
        return this;
    }

    public ExtractResultPreview<T> extractFrom(IGOSupplier<T> supplier, boolean greedy) {
        IGOSupplier<T> snapshot = supplier.createSnapshot();
        List<ExtractResultPreview<T>> results = new ArrayList<>();
        for (IGOExtractor<T> extractor : extractors) {
            results.add(extractor.extractWithoutSimulate(snapshot, greedy));
        }
        ExtractResultPreview<T> result = new ExtractResultPreview<>(supplier, greedy, results.stream().flatMap(preview -> preview.children.stream()).toList());
        supplier.bootstrapResultPreview(result);
        return result;
    }

    public Either<ExtractResult<T>, ExtractResultPreview<T>> extractIfAllMatch(IGOSupplier<T> supplier, boolean greedy) {
        ExtractResultPreview<T> preview = extractFrom(supplier, greedy);
        if (preview.allExtractorMatched()) return Either.left(preview.directlyExecute());
        else return Either.right(preview);
    }

    public static <T> List<Optional<IGOExtractor<T>>> groupUnmatched(ExtractResultPreview<T> preview) {
        return preview.findNotMatched().stream().map(c -> c.extractor.copyWithRequestCount(c.requiredCount - c.extractedCount)).toList();
    }

    public static <T> IGOExtractorGroup<T> groupUnmatchedToIGOEGroup(ExtractResultPreview<T> preview) {
        return groupUnmatched(preview).stream().flatMap(Optional::stream).filter(Objects::nonNull)
                .collect(Collector.of(IGOExtractorGroup::new, IGOExtractorGroup::addExtractor, (a, b) -> a.addExtractors(b.extractors)));
    }

}

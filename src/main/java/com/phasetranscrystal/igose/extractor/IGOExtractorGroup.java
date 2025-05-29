package com.phasetranscrystal.igose.extractor;


import com.mojang.datafixers.util.Either;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.supplier.IGOSupplier;

import java.util.*;
import java.util.stream.Collector;

public class IGOExtractorGroup<T> {
    public final IGOContentType<T> contentType;
    protected final List<IGOExtractor> extractors = new ArrayList<>();

    public IGOExtractorGroup(IGOContentType<T> contentType) {
        this.contentType = contentType;
    }

    public IGOExtractorGroup(IGOExtractor extractor, IGOContentType<T> contentType) {
        this.contentType = contentType;
        if (extractor.canExtractFromType(contentType))
            extractors.add(extractor);
    }

    public IGOExtractorGroup(List<IGOExtractor> extractors, IGOContentType<T> contentType) {
        this.contentType = contentType;
        extractors.stream().filter(e -> e.canExtractFromType(contentType)).forEach(this.extractors::add);
    }

    public IGOExtractorGroup(IGOContentType<T> contentType, IGOExtractor... extractors) {
        this(Arrays.asList(extractors), contentType);
    }

    public boolean addExtractor(IGOExtractor extractor) {
        if (extractors.contains(extractor) || !extractor.canExtractFromType(contentType)) return false;
        extractors.add(extractor);
        return true;
    }

    public IGOExtractorGroup<T> merge(IGOExtractorGroup<T> other) {
        if (other.contentType == this.contentType) {
            other.extractors.stream().filter(e -> !this.extractors.contains(e)).forEach(this.extractors::add);
        }
        return this;
    }

    public List<IGOExtractor> addExtractors(List<IGOExtractor> extractors) {
        return extractors.stream().filter(this::addExtractor).toList();
    }

    public boolean removeExtractor(IGOExtractor extractor) {
        return extractors.remove(extractor);
    }

    public List<IGOExtractor> removeExtractors(List<IGOExtractor> extractors) {
        return extractors.stream().filter(this::removeExtractor).toList();
    }

    public boolean containsExtractor(IGOExtractor extractor) {
        return extractors.contains(extractor);
    }

    public List<IGOExtractor> getExtractors() {
        return new ArrayList<>(extractors);
    }

    public ExtractResultPreview<T> extractFrom(IGOSupplier<T> supplier, boolean greedy) {
        IGOSupplier<T> snapshot = supplier.createSnapshot();
        List<ExtractResultPreview<T>> results = new ArrayList<>();
        for (IGOExtractor extractor : extractors) {
            results.add(extractor.extractWithoutSimulate(snapshot, greedy));
        }
        ExtractResultPreview<T> result = new ExtractResultPreview<>(supplier, greedy, results.stream().flatMap(preview -> preview.children.stream()).toList());
        supplier.bootstrapResultPreview(result);
        return result;
    }

    public Either<ExtractResult<T>, ExtractResultPreview<T>> extractIfAllMatch(IGOSupplier<T> supplier, boolean greedy) {
        ExtractResultPreview<T> preview = extractFrom(supplier, greedy);
        if (preview.allExtractorMatched()) return Either.left(preview.uncheckedExecute());
        else return Either.right(preview);
    }

    public static <T> List<IGOExtractor> groupUnmatched(ExtractResultPreview<T> preview) {
        return preview.findNotMatched().stream().map(c -> c.extractor.copyWithRequestCount(c.requiredCount - c.extractedCount)).toList();
    }

    public static <T> IGOExtractorGroup<T> groupUnmatchedToIGOEGroup(ExtractResultPreview<T> preview) {
        IGOExtractorGroup<T> extractorGroup = new IGOExtractorGroup<>(preview.root.getType());
        groupUnmatched(preview).forEach(extractorGroup::addExtractor);
        return extractorGroup;
    }

}

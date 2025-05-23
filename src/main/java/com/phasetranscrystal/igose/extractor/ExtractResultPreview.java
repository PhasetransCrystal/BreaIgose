package com.phasetranscrystal.igose.extractor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.phasetranscrystal.nonard.migrate.ingame_obj_se.supplier.IGOSupplier;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ExtractResultPreview<T> {
    public final IGOSupplier<T> root;
    public final boolean greedy;
    public final List<Child<T>> children;

    public ExtractResultPreview(IGOSupplier<T> root, boolean greedy, List<Child<T>> children) {
        this.root = root;
        this.greedy = greedy;
        this.children = List.copyOf(children);
    }

    public ExtractResult<T> asDirectResult() {
        return new ExtractResult<>(this);
    }

    //TODO EXECUTE
    public ExtractResult<T> execute(){

    }

    ExtractResult<T> directlyExecute(){

    }


    public ExtractResultPreview(IGOSupplier<T> root, boolean greedy, IGOExtractor<T> extractor, double requiredCount, double extractedCount, ImmutableMap<Integer, T> extractedByIndex, Consumer<IGOSupplier<T>> executor) {
        this(root, greedy, List.of(new Child<>(extractor, extractedCount, requiredCount, extractedByIndex, executor)));
    }

    public ExtractResultPreview(IGOSupplier<T> root, boolean greedy, IGOExtractor<T> extractor, double requiredCount, double extractedCount, ImmutableMap<Integer, T> extractedByIndex,
                                ImmutableMap<Integer, Double> extractedByCount, Consumer<IGOSupplier<T>> executor) {
        this(root, greedy, List.of(new Child<>(extractor, extractedCount, requiredCount, extractedByIndex, extractedByCount, executor)));
    }

    public boolean allExtractorMatched() {
        return children.stream().allMatch(Child::matched);
    }

    public boolean anyExtractorMatched() {
        return children.stream().anyMatch(Child::matched);
    }

    public List<Child<T>> findNotMatched() {
        return children.stream().filter(c -> !c.matched()).toList();
    }


    public static class Child<T> {
        public final IGOExtractor<T> extractor;
        public final double requiredCount;
        public final double extractedCount;
        public final ImmutableMap<Integer, T> extractedByIndex;
        public final ImmutableMap<Integer, Double> extractedByCount;
        protected final Consumer<IGOSupplier<T>> executor;

        public Child(IGOExtractor<T> extractor, double required, double extractedCount, ImmutableMap<Integer, T> extractedByIndex,
                     ImmutableMap<Integer, Double> extractedByCount, Consumer<IGOSupplier<T>> executor) {
            this.extractor = extractor;
            this.requiredCount = required;
            this.extractedCount = extractedCount;
            this.extractedByIndex = extractedByIndex;
            this.extractedByCount = extractedByCount;
            this.executor = executor;
        }

        public Child(IGOExtractor<T> extractor, double required, double extractedCount, ImmutableMap<Integer, T> extractedByIndex, Consumer<IGOSupplier<T>> executor) {
            this(extractor, required, extractedCount, extractedByIndex,
                    ImmutableMap.ofEntries((Map.Entry<Integer, Double>[]) extractedByIndex.entrySet().stream().map(e -> Pair.of(e.getKey(), extractor.getCountFromTarget(e.getValue()))).toArray()),
                    executor);
        }

        public boolean matched() {
            return extractedCount >= requiredCount;
        }
    }
}

package com.phasetranscrystal.igose.extractor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.phasetranscrystal.igose.content_type.ContentStack;
import com.phasetranscrystal.igose.supplier.IGOSupplier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    /**
     * 根据供应器进行可用性检查，然后执行。
     *
     * @return 提取结果，若为empty则表示检查不成功。
     */
    public Optional<ExtractResult<T>> execute() {
        if (root.checkAvailability(this)) {
            return Optional.of(uncheckedExecute());
        }
        return Optional.empty();
    }

    ExtractResult<T> uncheckedExecute() {
        children.stream().map(c -> c.executor).forEach(c -> c.accept(root));
        return asDirectResult();
    }


    public ExtractResultPreview(IGOSupplier<T> root, boolean greedy, IGOExtractor extractor, double requiredCount, double extractedCount, ImmutableMap<Integer, ContentStack<T>> extractedByIndex, Consumer<IGOSupplier<T>> executor) {
        this(root, greedy, List.of(new Child<>(extractor, extractedCount, requiredCount, extractedByIndex, executor)));
    }

    public ExtractResultPreview(IGOSupplier<T> root, boolean greedy, IGOExtractor extractor, double requiredCount, double extractedCount, ImmutableMap<Integer, ContentStack<T>> extractedByIndex,
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
        public final IGOExtractor extractor;
        public final double requiredCount;
        public final double extractedCount;
        public final ImmutableMap<Integer, ContentStack<T>> extractedByIndex;
        public final ImmutableMap<Integer, Double> extractedByCount;
        protected final Consumer<IGOSupplier<T>> executor;

        public Child(IGOExtractor extractor, double required, double extractedCount, ImmutableMap<Integer, ContentStack<T>> extractedByIndex,
                     ImmutableMap<Integer, Double> extractedByCount, Consumer<IGOSupplier<T>> executor) {
            this.extractor = extractor;
            this.requiredCount = required;
            this.extractedCount = extractedCount;
            this.extractedByIndex = extractedByIndex;
            this.extractedByCount = extractedByCount;
            this.executor = executor;
        }

        public Child(IGOExtractor extractor, double required, double extractedCount, ImmutableMap<Integer, ContentStack<T>> extractedByIndex, Consumer<IGOSupplier<T>> executor) {
            this(extractor, required, extractedCount, extractedByIndex,
                    ImmutableMap.ofEntries((Map.Entry<Integer, Double>[]) extractedByIndex.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue().getCount())).toArray()),
                    executor);
        }

        public boolean matched() {
            return extractedCount >= requiredCount;
        }
    }
}

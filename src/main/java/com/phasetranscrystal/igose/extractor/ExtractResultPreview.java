package com.phasetranscrystal.igose.extractor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.phasetranscrystal.igose.content_type.ContentStack;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.supplier.IGOSupplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ExtractResultPreview<T> {
    public final IGOSupplier<T> root;
    public final boolean greedy;

    public List<Child<T>> getChildren() {
        return children;
    }

    public boolean isGreedy() {
        return greedy;
    }

    public IGOSupplier<T> getRoot() {
        return root;
    }

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

    protected boolean checkAvailability() {
        return root.checkAvailability(this);
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

    public static class Grouped {
        public final Map<IGOContentType<?>, ExtractResultPreview<?>> resultPreviewMap;
        public final boolean isGreedy;
        public final boolean enableTransform;

        public Grouped(Map<IGOContentType<?>, ExtractResultPreview<?>> resultPreviewMap, boolean isGreedy, boolean enableTransform) {
            this.resultPreviewMap = ImmutableMap.copyOf(resultPreviewMap);
            this.isGreedy = isGreedy;
            this.enableTransform = enableTransform;
        }

        public ExtractResult.Grouped asDirectResult() {
            Map<IGOContentType<?>, ExtractResult<?>> map = new HashMap<>();
            resultPreviewMap.forEach((key, value) -> map.put(key, value.asDirectResult()));
            return new ExtractResult.Grouped(isGreedy, enableTransform, map);
        }

        /**
         * 根据供应器进行可用性检查，然后执行。
         *
         * @return 提取结果，若为empty则表示检查不成功。
         */
        public Optional<ExtractResult.Grouped> execute() {
            if (resultPreviewMap.values().stream().allMatch(ExtractResultPreview::checkAvailability)) {
                return Optional.of(uncheckedExecute());
            }
            return Optional.empty();
        }

        ExtractResult.Grouped uncheckedExecute() {
            resultPreviewMap.values().forEach(ExtractResultPreview::uncheckedExecute);
            return asDirectResult();
        }

        public boolean allExtractorMatched(IGOContentType<?> type) {
            return Optional.ofNullable(resultPreviewMap.get(type)).map(ExtractResultPreview::allExtractorMatched).orElse(false);
        }

        public boolean allExtractorMatched() {
            return resultPreviewMap.values().stream().allMatch(ExtractResultPreview::allExtractorMatched);
        }

        public boolean anyExtractorMatched(IGOContentType<?> type) {
            return Optional.ofNullable(resultPreviewMap.get(type)).map(ExtractResultPreview::anyExtractorMatched).orElse(false);
        }

        public boolean anyExtractorMatched() {
            return resultPreviewMap.values().stream().anyMatch(ExtractResultPreview::anyExtractorMatched);
        }

        public Multimap<IGOContentType<?>, Child<?>> findNotMatched() {
            ImmutableMultimap.Builder<IGOContentType<?>, Child<?>> builder = new ImmutableMultimap.Builder<>();
            resultPreviewMap.forEach((t, p) -> builder.putAll(t, p.findNotMatched()));
            return builder.build();
        }
    }
}

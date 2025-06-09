package com.phasetranscrystal.igose.extractor;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.igose.BreaIgose;
import com.phasetranscrystal.igose.content_type.ContentStack;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.filter.IGOFilter;
import com.phasetranscrystal.igose.supplier.IGOSupplier;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public interface IGOExtractor {
    ResourceLocation NAME = BreaIgose.location("igm_extractor");

    IGOFilter<?> targetFilter();

    double requestCount();

    boolean setRequestCount(double count);

    IGOExtractor copyWithRequestCount(double count);

    default IGOExtractor copy() {
        return copyWithRequestCount(requestCount());
    }

    default boolean canExtractFromType(IGOContentType<?> type) {
        return targetFilter().contentTypeMatch(type);
    }

    default boolean canExtractFrom(ContentStack<?> root) {
        return targetFilter().filter(root);
    }

//    ?
//    Consumer<IGOSupplier<T>> createExtractionExecutor(T root);

    default <T> ExtractResultPreview<T> extractBySnapshot(IGOSupplier<T> supplier, boolean greedy) {
        ExtractResultPreview<T> result = extract(supplier.createSnapshot(), supplier, greedy);
        supplier.bootstrapResultPreview(result);
        return result;
    }

    /**
     * 从资源供应器中提取对象。<br>
     *
     * @param supplier 资源的供应器
     * @param greedy   是否为贪婪提取，可能对例如原版桶之类的有数量分层限制的对象有用
     */
    //在greedy情况下，是否需要重新回滚以减少多余的消耗？
    default <T> ExtractResultPreview<T> extract(IGOSupplier<T> supplier, @Nullable IGOSupplier<T> root, boolean greedy) {
        if(supplier.overrideExtract()) return supplier.extractOverride(this, root ,greedy);

        AtomicReference<Double> count = new AtomicReference<>(requestCount());
        double originCount = count.get();
        Int2ObjectMap<ContentStack<T>> map = new Int2ObjectOpenHashMap<>();
        Int2DoubleMap valueMap = new Int2DoubleOpenHashMap();

        supplier.foreachSlot((index, sup) -> {
            if (!sup.isVariable(index) || !canExtractFrom(supplier.get(index))) return false;

            ContentStack<T> value = supplier.extractCount(index, count.get(), greedy);
            if (value.isEmpty()) return false;

            double consumed = value.getCount();
            if (consumed <= 0) return false;

            count.updateAndGet(v -> v - consumed);
            map.put(index, value);
            valueMap.put(index, consumed);

            return count.get() <= 0;
        });

        count.set(originCount - count.get());
        ImmutableMap<Integer, ContentStack<T>> objMapResult = ImmutableMap.copyOf(map);
        ImmutableMap<Integer, Double> countMapResult = ImmutableMap.copyOf(valueMap);
        return new ExtractResultPreview<>(supplier, greedy, this, originCount, count.get(), objMapResult, countMapResult, createExecutor(countMapResult, objMapResult, root != null ? root : supplier, greedy, originCount, count.get()));
    }

    default <T> Consumer<IGOSupplier<T>> createExecutor(ImmutableMap<Integer, Double> valueMap, ImmutableMap<Integer, ContentStack<T>> objMap,
                                                        IGOSupplier<T> supplier, boolean isGreedy, double requiredCount, double extractedCount) {
        return sup -> valueMap.forEach(sup::removeCount);
    }
}

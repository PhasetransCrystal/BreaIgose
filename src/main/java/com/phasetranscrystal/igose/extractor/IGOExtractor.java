package com.phasetranscrystal.igose.extractor;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.phasetranscrystal.igose.BreaIgose;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.filter.IGOFilter;
import com.phasetranscrystal.igose.supplier.IGOSupplier;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

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

    default boolean canExtractFrom(IGOContentType.Stack<?> root) {
        return targetFilter().filter(root);
    }

//    ?
//    Consumer<IGOSupplier<T>> createExtractionExecutor(T root);

    default <T> ExtractResultPreview<T> extract(IGOSupplier<T> supplier, boolean greedy) {
        ExtractResultPreview<T> result = extractWithoutSimulate(supplier.createSnapshot(), greedy);
        supplier.bootstrapResultPreview(result);
        return result;
    }

    /**
     * 从资源供应器中提取对象
     *
     * @param supplier 资源的供应器
     * @param greedy   是否为贪婪提取，可能对例如原版桶之类的有数量分层限制的对象有用
     */
    //在greedy情况下，是否需要重新回滚以减少多余的消耗？
    default <T> ExtractResultPreview<T> extractWithoutSimulate(IGOSupplier<T> supplier, boolean greedy) {
        double count = requestCount(), originCount = count;
        Int2ObjectMap<IGOContentType.Stack<T>> map = new Int2ObjectOpenHashMap<>();
        Int2DoubleMap valueMap = new Int2DoubleOpenHashMap();

        for (int index = 0; index < supplier.size(); index++) {
            if (!supplier.isVariable(index) || !canExtractFrom(supplier.get(index))) continue;

            IGOContentType.Stack<T> value = supplier.extractCount(index, count, greedy);
            if (value.isEmpty()) continue;

            double consumed = value.getCount();
            if (consumed <= 0) continue;

            count -= consumed;
            map.put(index, value);
            valueMap.put(index, consumed);

            if (count <= 0) break;
        }

        count = originCount - count;
        ImmutableMap<Integer, IGOContentType.Stack<T>> objMapResult = ImmutableMap.copyOf(map);
        ImmutableMap<Integer, Double> countMapResult = ImmutableMap.copyOf(valueMap);
        return new ExtractResultPreview<>(supplier, greedy, this, originCount, count, objMapResult, countMapResult, createExecutor(countMapResult, objMapResult, supplier, greedy, originCount, count));
    }

    default <T> Consumer<IGOSupplier<T>> createExecutor(ImmutableMap<Integer, Double> valueMap, ImmutableMap<Integer, IGOContentType.Stack<T>> objMap,
                                                    IGOSupplier<T> supplier, boolean isGreedy, double requiredCount, double extractedCount) {
        return sup -> valueMap.forEach(sup::removeCount);
    }
}

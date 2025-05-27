package com.phasetranscrystal.igose.extractor;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.phasetranscrystal.nonard.ArkdustNonatomic;
import com.phasetranscrystal.nonard.migrate.ingame_obj_se.supplier.IGOSupplier;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public interface IGOExtractor<T> {
    ResourceLocation NAME = ArkdustNonatomic.location("igm_extractor");

    Codec<IGOExtractor<T>> codec();

    Class<T> targetClass();

    Predicate<T> targetPredicate();

    ToDoubleFunction<T> getCountFromTarget();

    default double getCountFromTarget(T obj) {
        return getCountFromTarget().applyAsDouble(obj);
    }

    default boolean isTargetNotEmpty(T obj) {
        return getCountFromTarget(obj) > 0;
    }

    double requestCount();

    boolean setRequestCount(double count);

    Optional<IGOExtractor<T>> copyWithRequestCount(double count);

    default Optional<IGOExtractor<T>> copy() {
        return copyWithRequestCount(requestCount());
    }

    ;

    boolean canExtractFrom(T root);

    Consumer<IGOSupplier<T>> createExtractionExecutor(T root);

    default ExtractResultPreview<T> extract(IGOSupplier<T> supplier, boolean greedy) {
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
    //TODO 贪婪模式再回收重构
    //TODO 多供应器修改捕捉后对判断快照是否应该回退in need
    default ExtractResultPreview<T> extractWithoutSimulate(IGOSupplier<T> supplier, boolean greedy) {
        double count = requestCount(), originCount = count;
        Int2ObjectMap<T> map = new Int2ObjectOpenHashMap<>();
        Int2DoubleMap valueMap = new Int2DoubleOpenHashMap();

        for (int index = 0; index < supplier.size(); index++) {
            if (!supplier.isVariable(index) || !canExtractFrom(supplier.get(index))) continue;

            Optional<T> value = supplier.extractCount(index, count, greedy);
            if (value.isEmpty()) continue;

            double consumed = getCountFromTarget(value.get());
            if (consumed <= 0) continue;

            count -= consumed;
            map.put(index, value.get());
            valueMap.put(index, consumed);

            if (count <= 0) break;
        }

        count = originCount - count;
        ImmutableMap<Integer, T> objMapResult = ImmutableMap.copyOf(map);
        ImmutableMap<Integer, Double> countMapResult = ImmutableMap.copyOf(valueMap);
        return new ExtractResultPreview<>(supplier, greedy, this, originCount, count, objMapResult, countMapResult, createExecutor(countMapResult, objMapResult, supplier, greedy, originCount, count));
    }

    default Consumer<IGOSupplier<T>> createExecutor(ImmutableMap<Integer, Double> valueMap, ImmutableMap<Integer, T> objMap,
                                                    IGOSupplier<T> supplier, boolean isGreedy, double requiredCount, double extractedCount) {
        return sup -> valueMap.forEach(sup::removeCount);
    }
}

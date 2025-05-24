package com.phasetranscrystal.igose.content_type;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;

import java.util.Optional;

public interface IContentType<T> {
    Class<T> getContentClass();

    MapCodec<T> codec();

    //---[数据比对]---

    boolean isSame(T obj1, T obj2);

    default boolean isEmpty(T obj){
        return getCountFrom(obj) <= 0;
    };

    default boolean isFull(T obj){
        return getCountFrom(obj) >= getMaxCountFrom(obj);
    }

    double getCountFrom(T obj);

    double getMaxCountFrom(T obj);

    boolean stackable(T origin, T stack);

    T copy(T obj, double count);

    default T copy(T obj) {
        return copy(obj, getCountFrom(obj));
    }

    default T normalizeCopy(T obj) {
        return copy(obj, 1);
    }

    T createEmpty();

    default double stackInCount(T origin, T stack) {
        if (isFull(origin) || !isSame(origin, stack)) {
            return 0;
        }

        return getMaxCountFrom(origin) - getCountFrom(stack);
    }

    //被堆叠结果-堆叠结果
    default Pair<T, T> stackWith(T origin, T stack) {
        if (isFull(origin) || !isSame(origin, stack)) {
            return Pair.of(origin, stack);
        }

        double currentCount = getCountFrom(origin) + getCountFrom(stack);
        double maxCount = getMaxCountFrom(origin);

        // 处理总量未超过上限的情况
        if (currentCount <= maxCount) {
            return setCount(origin, currentCount)
                    .map(t -> Pair.of(t, createEmpty()))
                    .orElse(Pair.of(origin, stack));
        }

        // 处理需要拆分的情况
        return setCount(origin, maxCount)
                .flatMap(updatedOrigin ->
                        setCount(stack, currentCount - maxCount)
                                .map(updatedStack -> Pair.of(updatedOrigin, updatedStack))
                )
                .orElse(Pair.of(origin, stack));
    }

    default Pair<T, T> stackWithSimulate(T origin, T stack) {
        return stackWith(copy(origin), copy(stack));
    }

    //---[实例处理]---

    Optional<T> setCount(T obj, double count);

    default Optional<T> normalize(T obj){
        return setCount(obj, 1);
    }

    default Optional<T> addCount(T obj, double count) {
        return setCount(obj, count + getCountFrom(obj));
    }

    default Optional<T> removeCount(T obj, double count) {
        return addCount(obj, -count);
    }
}

package com.phasetranscrystal.igose.supplier;

import com.phasetranscrystal.igose.BreaIgose;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

//TODO contentStack化改造。ContentStack为只读/副本模型，不直接造成变动。
public interface IGOSupplier<T> {
    ResourceLocation NAME = BreaIgose.location("igm_supplier");

    Class<T> targetClass();

    int size();

    T get(int index);

    boolean set(int index, T value);

    boolean setCount(int index, double count);

    //return: object remain that can't add in. empty means no remained.
    Optional<T> add(int index, T value);

    //return: count added
    double addCount(int index, double count);

    //return: count removed
    default double removeCount(int index, double count) {
        return -addCount(index, -count);
    }

    //return: extracted object. empty means nothing extracted.
    Optional<T> extractCount(int index, double count, boolean greedy);

    boolean isVariable();

    default boolean isVariable(int index) {
        return isVariable();
    }

    //---[供应器快照]---

    IGOSupplier<T> createSnapshot();

    default boolean isSnapshot() {
        return false;
    }

    //---[结果预览可用性与变动提醒]---

    boolean checkAvailability(ExtractResultPreview<T> resultPreview);

    void bootstrapResultPreview(ExtractResultPreview<T> resultPreview);

    void addChangeFeedback(IGOSupplier<T> supplier);

    void removeChangeFeedback(IGOSupplier<T> supplier);

    void boostrapChange();

    interface Converter<F, T> {
        ResourceLocation NAME = ArkdustNonatomic.location("igm_supplier_dispatcher");

        Class<F> originalTargetClass();

        Class<T> resultTargetClass();

        IGOSupplier<T> transform(IGOSupplier<F> obj);
    }

//    // 组供应器
//    public record SupplierGroup(List<IGOSupplier<?>> suppliers) {
//        public <T> List<IGOSupplier<T>> findSuppliers(Class<T> type) {
//            return suppliers.stream()
//                    .flatMap(s -> ConversionRegistry.findConversion(s, type).stream())
//                    .collect(Collectors.toList());
//        }
//    }
}

package com.phasetranscrystal.igose.supplier;

import com.phasetranscrystal.igose.BreaIgose;
import com.phasetranscrystal.igose.content_type.IContentType;
import com.phasetranscrystal.igose.extractor.ExtractResultPreview;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IGOSupplier<T> {
    ResourceLocation NAME = BreaIgose.location("igm_supplier");

    IContentType<T> getType();

    int size();

    //copied data.
    IContentType.Stack<T> get(int index);

    boolean set(int index, IContentType.Stack<T> value);

    default boolean set(int index, T value, double count) {
        return set(index, new IContentType.Stack<>(getType(), value, count));
    }

    boolean setCount(int index, double count);

    //return: object remain that can't add in. empty means no remained.
    IContentType.Stack<T> add(int index, IContentType.Stack<T> value);

    default IContentType.Stack<T> add(int index, T value, double count) {
        return add(index, new IContentType.Stack<>(getType(), value, count));
    }

    //return: count added
    double addCount(int index, double count);

    //return: count removed
    default double removeCount(int index, double count) {
        return -addCount(index, -count);
    }

    //return: extracted object. empty means nothing extracted.
    IContentType.Stack<T> extractCount(int index, double count, boolean greedy);

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

    default void doExecute(ExtractResultPreview<T> resultPreview) {

    }

    void bootstrapResultPreview(ExtractResultPreview<T> resultPreview);

    void addChangeFeedback(IGOSupplier<T> supplier);

    void removeChangeFeedback(IGOSupplier<T> supplier);

    void boostrapChange();

//    interface Converter<F, T> {
//        ResourceLocation NAME = BreaIgose.location("igm_supplier_dispatcher");
//
//        Class<F> originalTargetClass();
//
//        Class<T> resultTargetClass();
//
//        IGOSupplier<T> transform(IGOSupplier<F> obj);
//    }

//    // 组供应器
//    public record SupplierGroup(List<IGOSupplier<?>> suppliers) {
//        public <T> List<IGOSupplier<T>> findSuppliers(Class<T> type) {
//            return suppliers.stream()
//                    .flatMap(s -> ConversionRegistry.findConversion(s, type).stream())
//                    .collect(Collectors.toList());
//        }
//    }
}

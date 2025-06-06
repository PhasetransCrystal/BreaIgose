package com.phasetranscrystal.igose.supplier;

import com.phasetranscrystal.igose.BreaIgose;
import com.phasetranscrystal.igose.content_type.ContentStack;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.extractor.ExtractResultPreview;
import com.phasetranscrystal.igose.extractor.IGOExtractor;
import com.phasetranscrystal.igose.extractor.IGOExtractorSet;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.ToBooleanBiFunction;

import java.util.Optional;

public interface IGOSupplier<T> {
    ResourceLocation NAME = BreaIgose.location("igm_supplier");

    IGOContentType<T> getType();

    int size();

    //copied data.
    ContentStack<T> get(int index);

    /**遍历供应器中的所有子槽位。
     * @param consumer 对每个槽位内容执行器。提供其所处的供应器与对应的索引。当返回值为true时，打断遍历。
     * @return 遍历是否被中断。
     * */
    default boolean foreachSlot(Int2BooleanBiFunction<IGOSupplier<T>> consumer){
        for(int i = 0; i < size(); i++){
            if(consumer.applyAsBoolean(i,this)) return true;
        }
        return false;
    };

    boolean set(int index, ContentStack<T> value);

    default boolean set(int index, T value, double count) {
        return set(index, new ContentStack<>(getType(), value, count));
    }

    boolean setCount(int index, double count);

    //return: object remain that can't add in. empty means no remained.
    ContentStack<T> add(int index, ContentStack<T> value);

    default ContentStack<T> add(int index, T value, double count) {
        return add(index, new ContentStack<>(getType(), value, count));
    }

    //return: count added
    double addCount(int index, double count);

    //return: count removed
    default double removeCount(int index, double count) {
        return -addCount(index, -count);
    }

    //return: extracted object. empty means nothing extracted.
    ContentStack<T> extractCount(int index, double count, boolean greedy);

    /**
     * 是否为空：表示<font: color = red>没有任何可能的交互槽位，而非所有槽位内容为空</font>。
     */
    boolean isEmpty();

    default boolean notEmpty(){
        return !isEmpty();
    }

    boolean isVariable();

    default boolean isVariable(int index) {
        return isVariable();
    }

    //---[执行]---

    default Optional<ExtractResultPreview<T>> extractBy(IGOExtractor extractor, boolean greedy) {
        if (extractor.canExtractFromType(getType())) return Optional.of(extractor.extractBySnapshot(this, greedy));
        return Optional.empty();
    }

    default ExtractResultPreview<T> extractBy(IGOExtractorSet<T> extractor, boolean greedy) {
        return extractor.extractBySnapshot(this, greedy);
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

    interface Int2BooleanBiFunction<T> {
        boolean applyAsBoolean(int i, T o);
    }

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

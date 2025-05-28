package com.phasetranscrystal.igose.supplier;

import com.google.common.collect.ImmutableList;
import com.phasetranscrystal.igose.content_type.IContentType;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MergedIGOSupplier<T> extends SimpleIGOMultiSupplier<T> {
    public static final Logger LOGGER = LogManager.getLogger("BreaIgose:IGOSupplier:Merged");
    public final IContentType<T> typeCache;

    public MergedIGOSupplier(List<IGOSupplier<T>> suppliers, boolean isSnapshot) {
        super(isSnapshot);
        if (suppliers.isEmpty()) {
            LOGGER.error("Can't create without target class support. Use MergedIGOSupplier.<init>(Class<T>) to create an empty instance.");
            throw new IllegalArgumentException();
        }
        this.suppliers.addAll(suppliers);
        this.typeCache = suppliers.getFirst().getType();
    }

    @SafeVarargs
    public MergedIGOSupplier(boolean isSnapshot,IGOSupplier<T>... suppliers) {
        this(List.of(suppliers), isSnapshot);
    }

    public MergedIGOSupplier(IContentType<T> type, boolean isSnapshot) {
        super(isSnapshot);
        this.typeCache = type;
    }

    //only use for create snapshot
    protected MergedIGOSupplier(List<IGOSupplier<T>> listMutable, IContentType<T> type) {
        this(listMutable, type, true);
    }

    protected MergedIGOSupplier(List<IGOSupplier<T>> listMutable, IContentType<T> type, boolean isSnapshot) {
        super(isSnapshot);
        listMutable.stream().map(IGOSupplier::createSnapshot).forEach(this.suppliers::add);
        this.typeCache = type;
    }

    //--[IGOS]--

    @Override
    public IContentType<T> getType() {
        return typeCache;
    }

    @Override
    public int size() {
        return suppliers.stream().map(IGOSupplier::size).reduce(0, Integer::sum);
    }

    @Override
    public IGOSupplier<T> createSnapshot() {
        return new MergedIGOSupplier<>(this.suppliers, getType());
    }

    @Override
    public IContentType.Stack<T> get(int index) {
        ObjectIntPair<IGOSupplier<T>> pair = indexTarget(index);
        return pair.left().get(pair.rightInt());
    }

    @Override
    public boolean set(int index, IContentType.Stack<T> value) {
        if (!isVariable(index)) return false;
        ObjectIntPair<IGOSupplier<T>> pair = indexTarget(index);
        return pair.left().set(pair.rightInt(), value);
    }

    @Override
    public boolean setCount(int index, double count) {
        if (!isVariable(index)) return false;
        ObjectIntPair<IGOSupplier<T>> pair = indexTarget(index);
        return pair.left().setCount(pair.rightInt(), count);
    }

    @Override
    public IContentType.Stack<T> add(int index, IContentType.Stack<T> value) {
        if (!isVariable(index)) return value;
        ObjectIntPair<IGOSupplier<T>> pair = indexTarget(index);
        return pair.left().add(pair.rightInt(), value);
    }

    @Override
    public double addCount(int index, double count) {
        if (!isVariable(index)) return 0;
        ObjectIntPair<IGOSupplier<T>> pair = indexTarget(index);
        return pair.left().addCount(pair.rightInt(), count);
    }

    @Override
    public IContentType.Stack<T> extractCount(int index, double count, boolean greedy) {
        if (!isVariable(index)) return getType().createEmptyStack();
        ObjectIntPair<IGOSupplier<T>> pair = indexTarget(index);
        return pair.left().extractCount(pair.rightInt(), count, greedy);
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isVariable(int index) {
        if(index < 0 || index >= size()) return false;
        ObjectIntPair<IGOSupplier<T>> pair = indexTarget(index);
        return pair.first().isVariable(pair.rightInt());
    }

    //--[信息处理]--

    public ObjectIntPair<IGOSupplier<T>> indexTarget(int index) {
        for (IGOSupplier<T> sup : suppliers) {
            if (index >= sup.size()) {
                index -= sup.size();
            } else return ObjectIntPair.of(sup, index);
        }
        LOGGER.error("try to get MergedIGOSupplier[{}] while its size is {}. Is there any IGOS changed mistakenly?", index + size(), size());
        LOGGER.error("Instance details: {}", this);
        throw new IndexOutOfBoundsException();
    }

    protected List<IGOSupplier<T>> buildContentList(List<IGOSupplier<T>> suppliers) {
        return new ArrayList<>(suppliers);
    }

    @Override
    public String toString() {
        return "BreaIgose-IGOS-Merged{" +
                "suppliers=" + suppliers +
                ", isSnapshot=" + isSnapshot +
                '}';
    }

    /**创建一个稳定的组合供应器。如果你的每个子供应器的大小不变且子供应器总数也不变的话，可以用这个来提高效率。
     * */
    public static class Stable<T> extends MergedIGOSupplier<T> {
        public final int length;
        public final List<ObjectIntPair<IGOSupplier<T>>> executor;

        public Stable(List<IGOSupplier<T>> suppliers, boolean isSnapshot) {
            super(suppliers, isSnapshot);
            this.length = size();
            this.executor = init();
        }

        Stable(List<IGOSupplier<T>> suppliers, IContentType<T> clazz, boolean isSnapshot) {
            super(suppliers, clazz, isSnapshot);
            this.length = size();
            this.executor = init();
        }

        private List<ObjectIntPair<IGOSupplier<T>>> init(){
            ImmutableList.Builder<ObjectIntPair<IGOSupplier<T>>> builder = new ImmutableList.Builder<>();
            for(int i = 0; i < size(); i++) {
                builder.add(indexTarget(i));
            }
            return builder.build();
        }

        @Override
        public int size() {
            return length;
        }

        @Override
        public IGOSupplier<T> createSnapshot() {
            return new Stable<>(this.suppliers, getType(), true);
        }

        @Override
        public IContentType.Stack<T> get(int index) {
            return executor.get(index).first().get(executor.get(index).rightInt());
        }

        @Override
        public boolean set(int index, IContentType.Stack<T> value) {
            if (!isVariable(index)) return false;
            return executor.get(index).left().set(executor.get(index).rightInt(), value);
        }

        @Override
        public boolean setCount(int index, double count) {
            if (!isVariable(index)) return false;
            return executor.get(index).left().setCount(executor.get(index).rightInt(), count);
        }

        @Override
        public IContentType.Stack<T> add(int index, IContentType.Stack<T> value) {
            if (!isVariable(index)) return value;
            return executor.get(index).left().add(executor.get(index).rightInt(), value);
        }

        @Override
        public double addCount(int index, double count) {
            if (!isVariable(index)) return 0;
            return executor.get(index).left().addCount(executor.get(index).rightInt(), count);
        }

        @Override
        public IContentType.Stack<T> extractCount(int index, double count, boolean greedy) {
            if (!isVariable(index)) return getType().createEmptyStack();
            return executor.get(index).left().extractCount(executor.get(index).rightInt(), count, greedy);
        }

        @Override
        public boolean isVariable(int index) {
            return index >= 0 && size() > index && executor.get(index).first().isVariable(executor.get(index).rightInt());
        }

        @Override
        @Deprecated
        public boolean addSupplier(IGOSupplier<T> supplier) {
            return false;
        }

        @Override
        @Deprecated
        public boolean removeSupplier(IGOSupplier<T> supplier) {
            return false;
        }

        @Override
        public boolean canAddSupplier(IGOSupplier<T> supplier) {
            return false;
        }

        @Override
        public boolean canRemoveSupplier(IGOSupplier<T> supplier) {
            return false;
        }
    }
}

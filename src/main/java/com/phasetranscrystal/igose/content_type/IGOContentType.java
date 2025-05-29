package com.phasetranscrystal.igose.content_type;

import com.mojang.serialization.Codec;
import com.phasetranscrystal.igose.NewRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface IGOContentType<T> {
    Class<T> getContentClass();

    Codec<T> codec();

    //---[数据比对]---

    default boolean isSame(T obj1, T obj2) {
        return copy(obj1).equals(copy(obj2));
    }

    default boolean classMatch(Object obj) {
        return getContentClass().isInstance(obj);
    }

    boolean isEmpty(T value);

    T copy(T obj);

    T createEmpty();

    default Stack<T> createEmptyStack() {
        return new Stack<>(this, createEmpty(), 0);
    }

    T wildcard(T root);

    default Stack<T> createStack(T root, double count) {
        return new Stack<>(this, root, count);
    }

    default Stack<T> createStack(T root) {
        return createStack(root, 1);
    }

    default Stack<T> createCopyStack(T root, double count) {
        return new Stack<>(this, copy(root), count);
    }

    default ResourceKey<IGOContentType<?>> getResourceKey() {
        return NewRegistries.CONTENT_TYPE.getResourceKey(this).get();
    }

    default ResourceLocation getLocation() {
        return getResourceKey().location();
    }

    /**
     * ContentStack
     * 此对象可被视为特征与数量的分离，以ItemStack为例，前者可以视为物品与附加数据和混合，后者是物品数量。
     * 此对象在正常情况下是复制对象，不直接对数据造成更改。
     * 更改请使用{@link com.phasetranscrystal.igose.supplier.IGOSupplier#set(int, Object, double)}之类。
     */
    class Stack<T> {
        public final IGOContentType<T> type;
        private T identity;
        private double count;

        public Stack(IGOContentType<T> type, T identity, double count) {
            this.type = type;
            this.identity = identity == null ? type.createEmpty() : identity;
            setCount(count);
        }

        public boolean isEmpty() {
            return identity == null || type.isEmpty(identity) || count == 0;
        }

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = Math.max(count, 0);
        }

        public T getIdentity() {
            return identity;
        }

        public void setIdentity(T identity) {
            this.identity = identity == null ? this.identity : identity;
        }

        public IGOContentType<T> getType() {
            return type;
        }

        public Stack<T> normalize() {
            this.count = 1;
            return this;
        }

        public Stack<T> wildcard() {
            this.identity = this.type.wildcard(identity);
            return this;
        }

        public Stack<T> copy() {
            return new Stack<>(this.type, this.type.copy(identity), count);
        }

        public boolean sameWith(Stack<T> other) {
            return type.isSame(this.identity, other.identity);
        }
    }
}

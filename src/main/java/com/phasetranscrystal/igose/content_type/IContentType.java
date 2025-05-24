package com.phasetranscrystal.igose.content_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public interface IContentType<T> {
    Class<T> getContentClass();

    //---[数据比对]---

    boolean isSame(T obj1, T obj2);

    T copy(T obj);

    T createEmpty();

    T wildcard(T root);

    default Stack<T> createStack(T root, double count) {
        return new Stack<>(this, root, count);
    }

    default Stack<T> createStack(T root) {
        return createStack(root, 1);
    }

    class Stack<T> {
        public final IContentType<T> type;
        private T identity;
        private double count;

        public Stack(IContentType<T> type, T identity, double count) {
            this.type = type;
            this.identity = identity == null ? type.createEmpty() : identity;
            setCount(count);
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

        public IContentType<T> getType() {
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

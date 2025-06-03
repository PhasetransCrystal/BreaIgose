package com.phasetranscrystal.igose.content_type;

/**
 * ContentStack
 * 此对象可被视为特征与数量的分离，以ItemStack为例，前者可以视为物品与附加数据和混合，后者是物品数量。
 * 此对象在正常情况下是复制对象，不直接对数据造成更改。
 * 更改请使用{@link com.phasetranscrystal.igose.supplier.IGOSupplier#set(int, Object, double)}之类。
 */
public class ContentStack<T> {
    public final IGOContentType<T> type;
    private T identity;
    private double count;

    public ContentStack(IGOContentType<T> type, T identity, double count) {
        this.type = type;
        this.identity = identity == null ? type.createEmpty() : identity;
        setCount(count);
    }

    /**
     * 该堆是否为空，也就是什么内容也不具有。
     * 这并不意味着identity一定为null
     *
     * @return 该stack是否为空
     */
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

    public ContentStack<T> normalize() {
        this.count = 1;
        return this;
    }

    public ContentStack<T> wildcard() {
        this.identity = this.type.wildcard(identity);
        return this;
    }

    /**
     * 复制一个新实例，与原实例保持内容一致但无关联
     */
    public ContentStack<T> copy() {
        return new ContentStack<>(this.type, this.type.copy(identity), count);
    }

    public boolean sameWith(ContentStack<T> other) {
        return type.isSame(this.identity, other.identity);
    }
}

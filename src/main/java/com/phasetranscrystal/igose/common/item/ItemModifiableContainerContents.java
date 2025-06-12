package com.phasetranscrystal.igose.common.item;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.phasetranscrystal.igose.ICopiable;
import com.phasetranscrystal.igose.Registries;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

//copied from ItemContainerContents
public class ItemModifiableContainerContents {
    private static final int NO_SLOT = -1;
    private static final int MAX_SIZE = 256;
    public static final Codec<ItemStack> ITEM_STACK_CODEC_ALLOW0 = RecordCodecBuilder.create(
            p_347288_ -> p_347288_.group(
                    ItemStack.ITEM_NON_AIR_CODEC.fieldOf("id").forGetter(ItemStack::getItemHolder),
                    ExtraCodecs.intRange(0, Integer.MAX_VALUE).fieldOf("count").orElse(0).forGetter(ItemStack::getCount),
                    DataComponentPatch.CODEC.fieldOf("components").forGetter(ItemStack::getComponentsPatch)
            ).apply(p_347288_, ItemStack::new)
    );
    public static final ItemModifiableContainerContents EMPTY = new ItemModifiableContainerContents(false, NonNullList.create());
    public static final Codec<ItemModifiableContainerContents> CODEC = RecordCodecBuilder.create(i -> i.group(
            ITEM_STACK_CODEC_ALLOW0.listOf().fieldOf("items").forGetter(c -> c.items),
            Codec.BOOL.fieldOf("look0AsEmpty").forGetter(c -> c.regard0AsEmpty)
    ).apply(i, ItemModifiableContainerContents::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemModifiableContainerContents> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(MAX_SIZE)), c -> c.items,
            ByteBufCodecs.BOOL, c -> c.regard0AsEmpty,
            ItemModifiableContainerContents::new
    );

    public final boolean regard0AsEmpty;
    public final int size;
    private final NonNullList<ItemStack> items;
    private final int hashCode;

    public ItemModifiableContainerContents(boolean regard0AsEmpty, NonNullList<ItemStack> items) {
        this.regard0AsEmpty = regard0AsEmpty;
        if (items.size() > 256) {
            throw new IllegalArgumentException("Got " + items.size() + " items, but maximum is 256");
        } else {
            this.size = items.size();
            this.items = NonNullList.copyOf(items);
            this.hashCode = ItemStack.hashStackList(items);
        }
    }

    public ItemModifiableContainerContents(int size, boolean regard0AsEmpty) {
        this(regard0AsEmpty, NonNullList.withSize(size, ItemStack.EMPTY));
    }

    public ItemModifiableContainerContents(List<ItemStack> items, boolean regard0AsEmpty) {
        this(items.size(), regard0AsEmpty);

        for (int i = 0; i < items.size(); i++) {
            this.items.set(i, items.get(i) == null ? ItemStack.EMPTY : items.get(i));
        }
    }

    public void copyInto(NonNullList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack itemstack = i < this.items.size() ? this.items.get(i) : ItemStack.EMPTY;
            list.set(i, itemstack.copy());
        }
    }

    public void clear(){
        Collections.fill(this.items, ItemStack.EMPTY);
    }

    public ItemStack copyOne() {
        return this.items.isEmpty() ? ItemStack.EMPTY : this.items.getFirst().copy();
    }

    public Stream<ItemStack> stream() {
        return this.items.stream().map(ItemStack::copy);
    }

    public Stream<ItemStack> nonEmptyStream() {
        return this.items.stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy);
    }

    public Iterable<ItemStack> nonEmptyItems() {
        return Iterables.filter(this.items, stack -> !stack.isEmpty());
    }

    public Iterable<ItemStack> nonEmptyItemsCopy() {
        return Iterables.transform(this.nonEmptyItems(), ItemStack::copy);
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof ItemModifiableContainerContents c && ItemStack.listMatches(this.items, c.items));
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    public int getSlotSize() {
        return this.items.size();
    }

    public ItemStack getStack(int index) {
        if (index < 0 || index >= this.items.size()) return ItemStack.EMPTY;
        return this.items.get(index);
    }

    public boolean setStackInSlot(int index, ItemStack stack) {
        if (index < 0 || index >= this.items.size()) return false;
        this.items.set(index, stack.copy());
        return true;
    }

    public ItemStack extract(int index, int count, boolean simulate) {
        if (count <= 0 || index < 0 || index >= this.items.size()) return ItemStack.EMPTY;
        ItemStack itemstack = this.items.get(index);
        if (itemstack.isEmpty()) return ItemStack.EMPTY;
        return simulate ? itemstack.copyWithCount(Math.min(count, itemstack.getCount())) : itemstack.split(count);
    }

    public ItemStack insert(int index, ItemStack stack, boolean simulate) {
        //边界检查与空值处理
        if (index < 0 || index >= this.items.size() || stack.isEmpty())
            return stack;

        ItemStack existingStack = this.items.get(index);

        //检查是否允许堆叠
        if (canMergeStacks(existingStack, stack)) {
            // 计算可转移数量
            int availableSpace = existingStack.getMaxStackSize() - existingStack.getCount();
            int transferCount = Math.min(stack.getCount(), availableSpace);

            // 执行堆叠操作
            if (!simulate) {
                existingStack.grow(transferCount);
            }
            stack.shrink(transferCount);


            // 标记槽位需要更新
            this.setChanged(index);

            // 返回剩余物品（可能为EMPTY）
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
        }

        return stack;
    }

    private boolean canMergeStacks(ItemStack existing, ItemStack input) {
        return (regard0AsEmpty ? existing.isEmpty() : existing.getItem() == Items.AIR) &&
                existing.getCount() < existing.getMaxStackSize() &&
                ItemStack.isSameItemSameComponents(existing, input);
    }


    // 辅助方法：标记槽位变化
    private void setChanged(int index) {
    }

    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= this.items.size())
            return ItemStack.EMPTY;
        else
            return this.items.get(slot);
    }

    public static class Wrapper implements IExtendedItemHandler, ICopiable<Wrapper> {
        protected final ItemStack stack;
        protected final ItemModifiableContainerContents component;

        public Wrapper(ItemStack stack) {
            this.stack = stack;
            this.component = stack.getOrDefault(Registries.MODIFIABLE_CONTAINER, ItemModifiableContainerContents.EMPTY);
        }

        @Override
        public int getSlots() {
            return component.getSlotSize();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return component.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return component.insert(slot, stack, simulate);
        }

        //Unsupported
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return component.extract(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return component.getStackInSlot(slot).getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot >= 0 && slot < component.getSlotSize();
        }

        @Override
        public Wrapper copy() {
            return new Wrapper(stack.copy());
        }

        @Override
        public boolean setStack(int slot, ItemStack stack) {
            return component.setStackInSlot(slot, stack);
        }

        @Override
        public void clearSlot(int slot) {
            component.setStackInSlot(slot, ItemStack.EMPTY);
        }

        @Override
        public void clearAll() {
            component.clear();
        }
    }

}

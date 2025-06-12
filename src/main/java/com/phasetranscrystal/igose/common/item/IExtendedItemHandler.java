package com.phasetranscrystal.igose.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;

public interface IExtendedItemHandler extends IItemHandler {
    boolean setStack(int slot, ItemStack stack);

    void clearSlot(int slot);

    void clearAll();

    default boolean setCount(int slot, int count){
        return setStack(slot, getStackInSlot(slot).copyWithCount(count));
    };

    static NonNullList<ItemStack> asList(IItemHandler handler){
        return NonNullList.withSize(handler.getSlots(), ItemStack.EMPTY);
    }
}

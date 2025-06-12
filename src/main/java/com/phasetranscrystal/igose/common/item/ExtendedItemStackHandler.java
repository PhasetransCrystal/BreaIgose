package com.phasetranscrystal.igose.common.item;

import com.phasetranscrystal.igose.ICopiable;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ExtendedItemStackHandler extends ItemStackHandler implements IExtendedItemHandler, ICopiable<ExtendedItemStackHandler> {
    public ExtendedItemStackHandler() {
        this(1);
    }

    public ExtendedItemStackHandler(int size) {
        super(size);
    }

    public ExtendedItemStackHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    @Override
    public boolean setStack(int slot, ItemStack stack) {
        if(slot >= 0 && slot < getSlots()){
            stacks.set(slot, stack);
            return true;
        }
        return false;
    }

    @Override
    public void clearSlot(int slot) {
        if(slot >= 0 && slot < getSlots()) stacks.set(slot, ItemStack.EMPTY);
    }

    @Override
    public void clearAll() {
        Collections.fill(stacks, ItemStack.EMPTY);
    }

    @Override
    public ExtendedItemStackHandler copy() {
        ExtendedItemStackHandler handler = new ExtendedItemStackHandler(this.getSlots());
        for(int i = 0; i < this.getSlots(); i++){
            handler.setStack(i, stacks.get(i).copy());
        }
        return handler;
    }
}

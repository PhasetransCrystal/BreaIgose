package com.phasetranscrystal.igose.common.item;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class ExtendedInvWrapper extends InvWrapper implements IExtendedItemHandler {
    public ExtendedInvWrapper(Container inv) {
        super(inv);
    }

    @Override
    public boolean setStack(int slot, ItemStack stack) {
        setStackInSlot(slot, stack);
        return true;
    }

    @Override
    public void clearSlot(int slot) {
        setStackInSlot(slot, ItemStack.EMPTY);
    }

    @Override
    public void clearAll() {
        this.getInv().clearContent();
    }

}

package com.phasetranscrystal.igose.common.item;

import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class ExtendedSidedInvWrapper extends SidedInvWrapper implements IExtendedItemHandler {
    public ExtendedSidedInvWrapper(WorldlyContainer inv, @Nullable Direction side) {
        super(inv, side);
    }

    @Override
    public boolean setStack(int slot, ItemStack stack) {
        if (getSlot(inv, slot, side) == -1) return false;
        setStackInSlot(slot, stack);
        return true;
    }

    @Override
    public void clearSlot(int slot) {
        setStack(slot, ItemStack.EMPTY);
    }

    @Override
    public void clearAll() {
        inv.clearContent();
    }
}

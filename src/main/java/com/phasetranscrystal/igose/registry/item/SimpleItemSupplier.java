package com.phasetranscrystal.igose.registry.item;

import com.phasetranscrystal.igose.BreaIgose;
import com.phasetranscrystal.igose.ICopiable;
import com.phasetranscrystal.igose.Registries;
import com.phasetranscrystal.igose.common.item.*;
import com.phasetranscrystal.igose.content_type.ContentStack;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.supplier.IGOSupplier;
import com.phasetranscrystal.igose.supplier.SimpleIGOSupplier;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;
import java.util.stream.IntStream;

public class SimpleItemSupplier<T extends IItemHandler, C extends IItemHandler> extends SimpleIGOSupplier<ItemStack> {
    public static <T extends IItemHandler> SimpleItemSupplier<ExtendedInvWrapper, T> fromContainer(Container container, Function<ExtendedInvWrapper, T> copier) {
        return new SimpleItemSupplier<>(false, new ExtendedInvWrapper(container), copier);
    }

    public static SimpleItemSupplier<ExtendedInvWrapper, ItemStackHandler> fromContainer(Container container) {
        return fromContainer(container, w -> new ItemStackHandler(NonNullList.copyOf(IntStream.range(0, w.getSlots()).mapToObj(w::getStackInSlot).map(ItemStack::copy).toList())));
    }

    public static <T extends IItemHandler> SimpleItemSupplier<ExtendedSidedInvWrapper, T> fromContainer(WorldlyContainer container, Direction direction, Function<ExtendedSidedInvWrapper, T> copier) {
        return new SimpleItemSupplier<>(false, new ExtendedSidedInvWrapper(container, direction), copier);
    }

    public static SimpleItemSupplier<ExtendedSidedInvWrapper, ItemStackHandler> fromContainer(WorldlyContainer container, Direction direction) {
        return fromContainer(container, direction, w -> new ItemStackHandler(NonNullList.copyOf(IntStream.range(0, w.getSlots()).mapToObj(w::getStackInSlot).map(ItemStack::copy).toList())));
    }

    public static <T extends IItemHandler> SimpleItemSupplier<IItemHandler, T> fromItemCapability(ItemStack stack, Function<IItemHandler, T> copier) {
        return new SimpleItemSupplier<>(false, stack.getCapability(Capabilities.ItemHandler.ITEM), copier);
    }

    public static SimpleItemSupplier<IItemHandler, ItemStackHandler> fromItemCapability(ItemStack stack) {
        return fromItemCapability(stack, w -> new ItemStackHandler(NonNullList.copyOf(IntStream.range(0, w.getSlots()).mapToObj(w::getStackInSlot).map(ItemStack::copy).toList())));
    }

    public static SimpleItemSupplier<ComponentWrapper, ComponentWrapper> fromVanillaComponent(ItemStack stack) {
        return new SimpleItemSupplier<>(false, new ComponentWrapper(stack), ComponentWrapper::copy);
    }

    public static SimpleItemSupplier<ItemModifiableContainerContents.Wrapper, ItemModifiableContainerContents.Wrapper> fromIgoseComponent(ItemStack stack) {
        return new SimpleItemSupplier<>(false, new ItemModifiableContainerContents.Wrapper(stack), ItemModifiableContainerContents.Wrapper::copy);
    }

    public static final Function<IItemHandler,IItemHandler> COPIER = h -> new ExtendedItemStackHandler(IExtendedItemHandler.asList(h));


    protected final T handler;
    public final Function<T, C> copier;

    SimpleItemSupplier(boolean isSnapshot, T handler, Function<T, C> copier) {
        super(isSnapshot, true);
        this.handler = handler;
        this.copier = copier;
    }

    @Override
    public IGOContentType<ItemStack> getType() {
        return Registries.ITEM_CONTENT_TYPE.get();
    }

    @Override
    public int size() {
        return handler.getSlots();
    }

    @Override
    public ContentStack<ItemStack> get(int index) {
        return new ContentStack<>(getType(), handler.getStackInSlot(index), handler.getStackInSlot(index).getCount());
    }

    @Override
    public boolean set(int index, ContentStack<ItemStack> value) {
        return handler instanceof IExtendedItemHandler extended && extended.setStack(index, value.getIdentity().copyWithCount((int) value.getCount()));
    }

    @Override
    public boolean setCount(int index, double count) {
        return handler instanceof IExtendedItemHandler extended && extended.setCount(index, (int) count);
    }

    @Override
    public ContentStack<ItemStack> add(int index, ContentStack<ItemStack> value) {
        return ContentStack.item(handler.insertItem(index, value.getIdentity().copyWithCount((int) value.getCount()), false));
    }

    @Override
    public double addCount(int index, double count) {
        return handler.insertItem(index, handler.getStackInSlot(index).copyWithCount((int)count), false).getCount();
    }

    @Override
    public ContentStack<ItemStack> extractCount(int index, double count, boolean greedy) {
        return ContentStack.item(handler.extractItem(index, (int) count, false));
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public boolean isVariable(int index) {
        return index >= 0 && index < handler.getSlots() && handler.isItemValid(index, handler.getStackInSlot(index));
    }

    @Override
    public IGOSupplier<ItemStack> createSnapshot() {
        return new SimpleItemSupplier<>(isSnapshot, copier.apply(handler), COPIER);
    }

    public static class ComponentWrapper implements IExtendedItemHandler, ICopiable<ComponentWrapper> {
        public static final Logger LOGGER = LogManager.getLogger("BreaIgose:Supplier:ItemSupplier/VanillaComponentWrapper");

        protected final ItemStack stack;
        protected final ItemContainerContents component;

        public ComponentWrapper(ItemStack stack) {
            this.stack = stack;
            this.component = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        }

        @Override
        public int getSlots() {
            return component.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            try {
                return component.getStackInSlot(slot);
            } catch (UnsupportedOperationException e) {
                return ItemStack.EMPTY;
            }
        }

        //Unsupported
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            LOGGER.warn("Unable to insert item while Vanilla item container component is not writable.");
            LOGGER.warn("Details: Instance:{}", this);
            LOGGER.warn(new UnsupportedOperationException());
            return stack;
        }

        //Unsupported
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            LOGGER.warn("Unable to extract item while Vanilla item container component is not writable.");
            LOGGER.warn("Details: Instance:{}", this);
            LOGGER.warn(new UnsupportedOperationException());
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return component.getStackInSlot(slot).getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public ComponentWrapper copy() {
            return new ComponentWrapper(stack.copy());
        }

        @Override
        public boolean setStack(int slot, ItemStack stack) {
            LOGGER.warn("Unable to set stack in slot while Vanilla item container component is not writable.");
            LOGGER.warn("Details: Instance:{}", this);
            LOGGER.warn(new UnsupportedOperationException());
            return false;
        }

        @Override
        public void clearSlot(int slot) {
            LOGGER.warn("Unable to clear slot while Vanilla item container component is not writable.");
            LOGGER.warn("Details: Instance:{}", this);
            LOGGER.warn(new UnsupportedOperationException());
        }

        @Override
        public void clearAll() {
            LOGGER.warn("Unable to clear while Vanilla item container component is not writable.");
            LOGGER.warn("Details: Instance:{}", this);
            LOGGER.warn(new UnsupportedOperationException());
        }

        @Override
        public String toString() {
            return "ComponentWrapper{" +
                    "stack=" + stack +
                    ", component=" + component +
                    '}';
        }
    }
}

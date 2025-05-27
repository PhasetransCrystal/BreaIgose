package com.phasetranscrystal.igose.registry;

import com.mojang.serialization.Codec;
import com.phasetranscrystal.igose.content_type.RegistryContentType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemContentType implements RegistryContentType<ItemStack, Item> {
    @Override
    public Registry<Item> getRegistry() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    public Item transform(ItemStack stack) {
        return stack.getItem();
    }

    @Override
    public Class<ItemStack> getContentClass() {
        return ItemStack.class;
    }

    @Override
    public Codec<ItemStack> codec() {
        return ItemStack.CODEC;
    }

    @Override
    public boolean isEmpty(ItemStack value) {
        return value == ItemStack.EMPTY || value.getItem() == Items.AIR;
    }

    @Override
    public ItemStack copy(ItemStack obj) {
        return obj.copy();
    }

    @Override
    public ItemStack createEmpty() {
        return new ItemStack(Items.AIR);
    }

    @Override
    public ItemStack wildcard(ItemStack root) {
        return new ItemStack(root.getItem(), root.getCount());
    }
}

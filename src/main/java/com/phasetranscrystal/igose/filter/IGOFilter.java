package com.phasetranscrystal.igose.filter;

import com.phasetranscrystal.igose.content_type.ContentStack;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public interface IGOFilter<T extends IGOFilter<T>> {

    FilterType<T> getFilterType();

    default boolean contentTypeMatch(IGOContentType<?> contentType) {
        return getFilterType().contentTypeMatch(contentType);
    }

    boolean doFilter(ContentStack<?> stack);

    default boolean filter(ContentStack<?> stack) {
        return contentTypeMatch(stack.getType()) && doFilter(stack);
    }

    default void createTooltips(List<Component> list, TooltipFlag flag) {
        list.add(Component.translatable(getFilterType().getNameKey()).withStyle(ChatFormatting.GOLD));
        if (flag.isAdvanced()) {
            list.add(Component.literal("[Filter id: " + getFilterType().getLocation() + "]").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
        if (flag.hasShiftDown()) {
            list.add(Component.translatable(getFilterType().getExplainKey()).withStyle(ChatFormatting.ITALIC));
        }
    }

}

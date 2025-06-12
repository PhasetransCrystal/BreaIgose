package com.phasetranscrystal.igose;

import com.phasetranscrystal.igose.common.item.ItemModifiableContainerContents;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.registry.item.ItemContentType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Registries {
    public static void bootstrap(IEventBus bus) {
        DATA_COMPONENT_REGISTER.register(bus);
        CONTENT_TYPE_REGISTER.register(bus);
    }

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_REGISTER =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, BreaIgose.MODID);

    public static final DeferredRegister<IGOContentType<?>> CONTENT_TYPE_REGISTER =
            DeferredRegister.create(NewRegistries.CONTENT_TYPE, BreaIgose.MODID);


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemModifiableContainerContents>> MODIFIABLE_CONTAINER =
            DATA_COMPONENT_REGISTER.register("modifiable_container", () -> new DataComponentType.Builder<ItemModifiableContainerContents>()
                    .persistent(ItemModifiableContainerContents.CODEC)
                    .networkSynchronized(ItemModifiableContainerContents.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<IGOContentType<?>, ItemContentType> ITEM_CONTENT_TYPE =
            CONTENT_TYPE_REGISTER.register("item",ItemContentType::new);

}

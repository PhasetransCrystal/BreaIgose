package com.phasetranscrystal.igose;

import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.supplier.transformer.IGOTransformer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod(BreaIgose.MODID)
public class BreaIgose {
    public static final String MODID = "brea_igose";

    public BreaIgose(IEventBus modEventBus, ModContainer modContainer) {
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public <T> List<IGOTransformer<?,T>> getTransformers(IGOContentType<T> type) {
        return ModBusEventConsumer.transformers.containsKey(type) ?
                (List<IGOTransformer<?,T>>)(List) List.copyOf(ModBusEventConsumer.transformers.get(type).values()) :
                List.of();
    }
}

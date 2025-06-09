package com.phasetranscrystal.igose;

import com.google.common.collect.ImmutableMap;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.supplier.IGOSupplier;
import com.phasetranscrystal.igose.supplier.transformer.IGOTransformer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod(BreaIgose.MODID)
public class BreaIgose {
    public static final String MODID = "brea_igose";

    public BreaIgose(IEventBus modEventBus, ModContainer modContainer) {
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static Map<IGOContentType<?>, Map<IGOContentType<?>, IGOTransformer<?, ?>>> getTransformers() {
        return ModBusEventConsumer.transformers;
    }

    public static <T> List<IGOTransformer<?, T>> getTransformers(IGOContentType<T> type) {
        return getTransformers().containsKey(type) ?
                (List<IGOTransformer<?, T>>) (List) List.copyOf(getTransformers().get(type).values()) :
                List.of();
    }

    public static <F, T> Optional<IGOTransformer<F, T>> getTransformer(IGOContentType<F> from, IGOContentType<T> to) {
        return Optional.ofNullable(getTransformers().get(to)).map(m -> (IGOTransformer<F, T>) m.get(from));
    }

    public static <F, T> Optional<IGOTransformer<F, T>> getTransformer(IGOSupplier<F> from, IGOContentType<T> to) {
        return getTransformer(from.getType(), to);
    }
}

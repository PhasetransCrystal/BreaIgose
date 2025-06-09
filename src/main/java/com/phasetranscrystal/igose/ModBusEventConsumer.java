package com.phasetranscrystal.igose;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.phasetranscrystal.igose.content_type.IGOContentType;
import com.phasetranscrystal.igose.supplier.transformer.IGOTransformer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = BreaIgose.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEventConsumer {
    public static final Supplier<Map<IGOContentType<?>, Map<IGOContentType<?>, IGOTransformer<?, ?>>>> transformers =
            Suppliers.memoize(() -> {
                HashMap<IGOContentType<?>, Map<IGOContentType<?>, IGOTransformer<?, ?>>> transformers = new HashMap<>();
                for (IGOTransformer<?, ?> transformer : NewRegistries.TRANSFORMER) {
                    transformers.computeIfAbsent(transformer.toType, t -> new HashMap<>()).put(transformer.fromType, transformer);
                }
                ImmutableMap.Builder<IGOContentType<?>, Map<IGOContentType<?>, IGOTransformer<?, ?>>> builder = ImmutableMap.builder();
                transformers.forEach((type, transformer) -> builder.put(type, ImmutableMap.copyOf(transformer)));
                return builder.build();
            });

    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event) {
        event.register(NewRegistries.CONTENT_TYPE);
        event.register(NewRegistries.FILTER_TYPE);
        event.register(NewRegistries.TRANSFORMER);
    }

    @SubscribeEvent
    public static void setup(FMLCommonSetupEvent event) {

    }
}

package com.phasetranscrystal.igose;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;

@EventBusSubscriber(modid = BreaIgose.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEventConsumer {

    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event) {
        event.register(NewRegistries.CONTENT_TYPE);
        event.register(NewRegistries.FILTER_TYPE);
    }
}

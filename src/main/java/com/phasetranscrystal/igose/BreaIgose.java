package com.phasetranscrystal.igose;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(BreaIgose.MODID)
public class BreaIgose {
    public static final String MODID = "brea_igose";

    public BreaIgose(IEventBus modEventBus, ModContainer modContainer) {
    }

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}

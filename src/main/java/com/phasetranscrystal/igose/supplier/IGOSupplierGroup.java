package com.phasetranscrystal.igose.supplier;

import java.util.HashMap;
import java.util.Map;

public class IGOSupplierGroup {
    public final Map<Class<?>, IGOSupplier<?>> suppliers = new HashMap<>();
    public IGOSupplierGroup(Map<Class<?>, IGOSupplier<?>> map) {
        map.entrySet().stream()
                .filter(e -> e.getValue().getType() == e.getKey())
                .forEach(e -> suppliers.put(e.getKey(), e.getValue()));
    }

}

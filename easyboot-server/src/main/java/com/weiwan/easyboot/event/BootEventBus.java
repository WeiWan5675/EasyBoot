package com.weiwan.easyboot.event;

import com.google.common.eventbus.EventBus;

/**
 * @author xiaozhennan
 */
public class BootEventBus {

    private static final String EVENT_BUS_NAME = "Boot";
    private static final EventBus INSTANCE = new EventBus(EVENT_BUS_NAME);

    private BootEventBus() {}

    public static void publish(Object object) {
        INSTANCE.post(object);
    }

    public static void register(Object object) {
        INSTANCE.register(object);
    }
}

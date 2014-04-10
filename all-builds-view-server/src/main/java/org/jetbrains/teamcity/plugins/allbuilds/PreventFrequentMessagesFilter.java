package org.jetbrains.teamcity.plugins.allbuilds;

import org.atmosphere.cpr.BroadcastFilter;

import java.util.concurrent.atomic.AtomicLong;

public class PreventFrequentMessagesFilter implements BroadcastFilter {

    private static final int UPDATE_MESSAGES_INTERVAL = 1000;

    private AtomicLong lastBroadcastTime = new AtomicLong();

    @Override
    public BroadcastAction filter(Object originalMessage, Object message) {
        if (message instanceof BuildUpdateMessage) {
            if (((BuildUpdateMessage) message).getType() == BuildUpdateMessage.UpdateType.UPDATED) {
                if (System.currentTimeMillis() - lastBroadcastTime.get() > UPDATE_MESSAGES_INTERVAL) {
                    lastBroadcastTime.set(System.currentTimeMillis());
                    return new BroadcastAction(BroadcastAction.ACTION.CONTINUE, message);
                } else {
                    return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
                }
            }
        }
        return new BroadcastAction(message);
    }
}


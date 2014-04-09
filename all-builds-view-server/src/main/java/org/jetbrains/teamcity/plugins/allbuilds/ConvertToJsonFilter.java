package org.jetbrains.teamcity.plugins.allbuilds;

import org.atmosphere.cpr.BroadcastFilter;

public class ConvertToJsonFilter implements BroadcastFilter {
    @Override
    public BroadcastAction filter(Object originalMessage, Object message) {
        if (message instanceof BuildUpdateMessage) {
            return new BroadcastAction(((BuildUpdateMessage) message).asJson());
        }
        return new BroadcastAction(message);
    }
}

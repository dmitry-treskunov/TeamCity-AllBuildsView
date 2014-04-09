package org.jetbrains.teamcity.plugins.allbuilds;

import org.atmosphere.cpr.*;

public class PreventFrequentMessagesFilter extends BroadcastFilterAdapter {

    private static final int LONG_POLLING_INTERVAL_IN_MILLIS = 2000;
    private static final int WEBSOCKET_INTERVAL_IN_MILLIS = 500;

    @Override
    public BroadcastAction filter(AtmosphereResource r, Object originalMessage, Object message) {
        if (message instanceof BuildUpdateMessage) {
            if (((BuildUpdateMessage) message).getType() == BuildUpdateMessage.UpdateType.UPDATED) {
                boolean shouldSend;
                switch (r.transport()) {
                    case WEBSOCKET:
                        shouldSend = System.currentTimeMillis() - getLastMessageTime(r) > WEBSOCKET_INTERVAL_IN_MILLIS;
                        break;
                    case LONG_POLLING:
                        shouldSend = System.currentTimeMillis() - getLastMessageTime(r) > LONG_POLLING_INTERVAL_IN_MILLIS ;
                        break;
                    default:
                        shouldSend = false;
                }

                if (shouldSend) {
                    updateLastMessageTime(r);
                    return new BroadcastAction(BroadcastAction.ACTION.CONTINUE, message);
                } else {
                    return new BroadcastAction(BroadcastAction.ACTION.ABORT, message);
                }
            }
        }
        return new BroadcastAction(message);
    }

    private Long getLastMessageTime(AtmosphereResource r) {
        AtmosphereResourceSessionFactory factory = AtmosphereResourceSessionFactory.getDefault();
        AtmosphereResourceSession session = factory.getSession(r);
        Long lastBroadcastTime = session.getAttribute("lastBroadcastTime", Long.class);
        return lastBroadcastTime != null ? lastBroadcastTime : 0;
    }

    private void updateLastMessageTime(AtmosphereResource r) {
        AtmosphereResourceSessionFactory factory = AtmosphereResourceSessionFactory.getDefault();
        AtmosphereResourceSession session = factory.getSession(r);
        session.setAttribute("lastBroadcastTime", System.currentTimeMillis());
    }
}

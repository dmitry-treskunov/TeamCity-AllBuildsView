package org.jetbrains.teamcity.plugins.allbuilds;

import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.util.EventDispatcher;
import org.atmosphere.cpr.*;
import org.atmosphere.handler.AbstractReflectorAtmosphereHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.jetbrains.teamcity.plugins.allbuilds.BuildUpdateMessage.UpdateType;

public class BuildUpdatesHandler extends AbstractReflectorAtmosphereHandler {

    private final BuildUpdateMessageSerializer serializer;

    public BuildUpdatesHandler(final EventDispatcher<BuildServerListener> dispatcher, BuildUpdateMessageSerializer serializer) {
        this.serializer = serializer;
        dispatcher.addListener(new BuildsNotificationsListener());
    }

    @Override
    public void onRequest(AtmosphereResource atmosphereResource) throws IOException {
        AtmosphereRequest req = atmosphereResource.getRequest();
        if (req.getMethod().equalsIgnoreCase("GET")) {
            atmosphereResource.setSerializer(serializer);
            if (atmosphereResource.transport() == AtmosphereResource.TRANSPORT.WEBSOCKET) {
                BroadcasterFactory.getDefault().lookup("/buildsUpdates/ws").addAtmosphereResource(atmosphereResource);
            } else if (atmosphereResource.transport() == AtmosphereResource.TRANSPORT.LONG_POLLING) {
                BroadcasterFactory.getDefault().lookup("/buildsUpdates/polling").addAtmosphereResource(atmosphereResource);
            }
        }
    }

    @Override
    public void destroy() {
    }

    private class BuildsNotificationsListener extends BuildServerAdapter {
        @Override
        public void buildStarted(@NotNull SRunningBuild build) {
            broadcast(new BuildUpdateMessage(UpdateType.STARTED, build));
        }

        @Override
        public void buildChangedStatus(@NotNull SRunningBuild build, Status oldStatus, Status newStatus) {
            broadcast(new BuildUpdateMessage(UpdateType.UPDATED, build));
        }

        @Override
        public void messageReceived(@NotNull SRunningBuild build, @NotNull BuildMessage1 message) {
            broadcast(new BuildUpdateMessage(UpdateType.UPDATED, build));
        }

        @Override
        public void buildInterrupted(@NotNull SRunningBuild build) {
            broadcast(new BuildUpdateMessage(UpdateType.FINISHED, build));
        }

        @Override
        public void buildFinished(@NotNull SRunningBuild build) {
            broadcast(new BuildUpdateMessage(UpdateType.FINISHED, build));
        }

        private void broadcast(BuildUpdateMessage message) {
            MetaBroadcaster.getDefault().broadcastTo("/buildsUpdates/*", message);
        }
    }
}

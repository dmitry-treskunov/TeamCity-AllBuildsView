package org.jetbrains.teamcity.plugins.allbuilds;

import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.util.EventDispatcher;
import org.atmosphere.cpr.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BuildUpdatesHandler implements AtmosphereHandler {

    public BuildUpdatesHandler(final EventDispatcher<BuildServerListener> dispatcher) {
        dispatcher.addListener(new BuildsNotificationsListener());
    }

    @Override
    public void onRequest(AtmosphereResource atmosphereResource) throws IOException {
        AtmosphereRequest req = atmosphereResource.getRequest();
        if (req.getMethod().equalsIgnoreCase("GET")) {
            atmosphereResource.suspend();
            Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup("buildsUpdates", true);
            broadcaster.addAtmosphereResource(atmosphereResource);
        }
    }

    @Override
    public void onStateChange(AtmosphereResourceEvent event) throws IOException {
        AtmosphereResource r = event.getResource();
        AtmosphereResponse res = r.getResponse();
        if (r.isSuspended()) {
            res.getWriter().write(event.getMessage().toString());
            switch (r.transport()) {
                case LONG_POLLING:
                    event.getResource().resume();
                    break;
                case WEBSOCKET:
                    res.getWriter().flush();
                    break;
            }
        }
    }

    @Override
    public void destroy() {

    }

    private class BuildsNotificationsListener extends BuildServerAdapter {
        @Override
        public void buildStarted(@NotNull SRunningBuild build) {
            broadcast(createNewBuildJson(build));
        }

        @Override
        public void buildChangedStatus(@NotNull SRunningBuild build, Status oldStatus, Status newStatus) {
            broadcast(createUpdateStatusJson(build, "running"));
        }

        @Override
        public void messageReceived(@NotNull SRunningBuild build, @NotNull BuildMessage1 message) {
            broadcast(createUpdateStatusJson(build, "running"));
        }

        @Override
        public void buildInterrupted(@NotNull SRunningBuild build) {
            broadcast(createUpdateStatusJson(build, "finished"));
        }

        @Override
        public void buildFinished(@NotNull SRunningBuild build) {
            broadcast(createUpdateStatusJson(build, "finished"));
        }

        private void broadcast(String message) {
            Broadcaster broadcaster = BroadcasterFactory.getDefault().lookup("buildsUpdates");
            if (broadcaster != null) {
                broadcaster.broadcast(message);
            }
        }

        private String createNewBuildJson(SRunningBuild build) {
            return "{" +
                        "\"type\":\"buildStarted\"," +
                        "\"build\":{" +
                            "\"id\":" + build.getBuildId() + "," +
                            "\"number\":\"" + build.getBuildNumber() + "\"," +
                            "\"buildType\":{" +
                                "\"projectName\":\"" + build.getBuildType().getProjectName() + "\"," +
                                "\"name\":\"" + build.getBuildTypeName() + "\"" +
                            "}," +
                            "\"agent\":{" +
                                    "\"name\":\"" + build.getAgentName() + "\"" +
                                "}," +
                            "\"statusText\":\"" + build.getStatusDescriptor().getText().replace("\"", "\\\"") + "\"," +
                            "\"status\":\"" + build.getStatusDescriptor().getStatus().getText() + "\"," +
                            "\"state\":\"running\"" +
                        "}" +
                    "}";

        }

        private String createUpdateStatusJson(SRunningBuild build, String state) {
            return "{" +
                        "\"type\":\"statusUpdated\"," +
                        "\"build\":{" +
                            "\"id\":" + build.getBuildId() + "," +
                            "\"statusText\":\"" + build.getStatusDescriptor().getText().replace("\"", "\\\"") + "\"," +
                            "\"status\":\"" + build.getStatusDescriptor().getStatus().getText() + "\"," +
                            "\"state\":\"" + state + "\"" +
                        "}" +
                    "}";
        }
    }
}

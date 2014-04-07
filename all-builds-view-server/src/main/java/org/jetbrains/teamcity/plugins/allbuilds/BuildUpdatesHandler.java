package org.jetbrains.teamcity.plugins.allbuilds;

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
            BroadcasterFactory.getDefault().lookup("buildsUpdates").broadcast(createNewBuildJson(build));
        }

        @Override
        public void buildFinished(@NotNull SRunningBuild build) {
            BroadcasterFactory.getDefault().lookup("buildsUpdates").broadcast(createBuildFinishedJson(build));
        }

        private String createNewBuildJson(SRunningBuild build) {
            return "{\"type\":\"started\"," +
                    "\"build\":" +
                    "{" +
                    "\"id\":" + build.getBuildId() + "," +
                    "\"buildTypeId\":\"" + build.getBuildTypeExternalId() + "\"," +
                    "\"number\":\"" + build.getBuildNumber() + "\"," +
                    "\"status\":\"" + build.getStatusDescriptor().getStatus().getText() + "\"," +
                    "\"state\":\"running\"" +
                    "}" +
                    "}";

        }

        private String createBuildFinishedJson(SRunningBuild build) {
            return "{\"type\":\"finished\"," +
                    "\"build\":" +
                    "{" +
                    "\"id\":" + build.getBuildId() + "," +
                    "\"status\":\"" + build.getStatusDescriptor().getStatus().getText() + "\"," +
                    "\"state\":\"finished\"" +
                    "}" +
                    "}";
        }
    }
}

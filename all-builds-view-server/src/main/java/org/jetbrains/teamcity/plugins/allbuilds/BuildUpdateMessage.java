package org.jetbrains.teamcity.plugins.allbuilds;

import jetbrains.buildServer.serverSide.SRunningBuild;

public class BuildUpdateMessage {

    public enum UpdateType {
        STARTED {
            @Override
            public String createBuildJson(SRunningBuild build) {
                return
                        "{" +
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
                        "}";
            }
        },

        FINISHED {
            @Override
            public String createBuildJson(SRunningBuild build) {
                return
                        "{" +
                            "\"id\":" + build.getBuildId() + "," +
                            "\"statusText\":\"" + build.getStatusDescriptor().getText().replace("\"", "\\\"") + "\"," +
                            "\"status\":\"" + build.getStatusDescriptor().getStatus().getText() + "\"," +
                            "\"state\":\"finished\"" +
                        "}";
            }
        },

        UPDATED {
            @Override
            public String createBuildJson(SRunningBuild build) {
                return
                        "{" +
                            "\"id\":" + build.getBuildId() + "," +
                            "\"statusText\":\"" + build.getStatusDescriptor().getText().replace("\"", "\\\"") + "\"," +
                            "\"status\":\"" + build.getStatusDescriptor().getStatus().getText() + "\"," +
                            "\"state\":\"running\"" +
                        "}";
            }
        };

        public abstract String createBuildJson(SRunningBuild build);
    }

    private UpdateType type;
    private String messageJson;

    public BuildUpdateMessage(UpdateType type, SRunningBuild build) {
        this.type = type;
        this.messageJson = "{\"type\":\""+ type.toString() +"\",\"build\":" + type.createBuildJson(build) + "}";
    }

    public String asJson() {
        return messageJson;
    }

    public UpdateType getType() {
        return type;
    }
}

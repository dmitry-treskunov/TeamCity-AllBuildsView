package org.jetbrains.teamcity.plugins.allbuilds;

import jetbrains.buildServer.serverSide.SRunningBuild;
import org.atmosphere.util.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BuildUpdateMessage {

    public enum UpdateType {
        STARTED {
            @Override
            public String createBuildJson(SRunningBuild build) {
                return
                        "{" +
                            "\"id\":" + build.getBuildId() + "," +
                            "\"number\":\"" + build.getBuildNumber() + "\"," +
                            "\"startDate\":\"" + formatDate(build.getStartDate()) + "\"," +
                            "\"buildType\":{" +
                                "\"projectName\":\"" + escape(build.getBuildType().getProjectName()) + "\"," +
                                "\"name\":\"" + escape(build.getBuildTypeName()) + "\"" +
                            "}," +
                            "\"agent\":{" +
                                "\"name\":\"" + build.getAgentName() + "\"" +
                            "}," +
                            "\"statusText\":\"" + escape(build.getStatusDescriptor().getText()) + "\"," +
                            "\"status\":\"" + escape(build.getStatusDescriptor().getStatus().getText()) + "\"," +
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
                            "\"statusText\":\"" + escape(build.getStatusDescriptor().getText()) + "\"," +
                            "\"status\":\"" + escape(build.getStatusDescriptor().getStatus().getText()) + "\"," +
                            "\"finishDate\":\"" + formatDate(new Date()) + "\"," +
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
                            "\"statusText\":\"" + escape(build.getStatusDescriptor().getText()) + "\"," +
                            "\"status\":\"" + escape(build.getStatusDescriptor().getStatus().getText()) + "\"," +
                            "\"state\":\"running\"" +
                        "}";
            }
        };

        public abstract String createBuildJson(SRunningBuild build);

        String formatDate(Date date) {
            return new SimpleDateFormat("YYYYMMdd'T'HHmmssZ").format(date);
        }

        String escape(String str) {
            try {
                return StringEscapeUtils.escapeJavaScript(str);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
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

package org.jetbrains.teamcity.plugins.allbuilds;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

public class AllBuildsPageExtension extends AdminPage {

    public AllBuildsPageExtension(PagePlaces pagePlaces,
                                  PluginDescriptor pluginDescriptor) {
        super(pagePlaces);
        setPluginName("AllBuildsView");
        setIncludeUrl(pluginDescriptor.getPluginResourcesPath("allBuilds.jsp"));
        setTabTitle("Builds list");
        addJsFile(pluginDescriptor.getPluginResourcesPath("lib/atmosphere.js"));
        addJsFile(pluginDescriptor.getPluginResourcesPath("js/allBuildsView.js"));
        addJsFile(pluginDescriptor.getPluginResourcesPath("js/allBuilds.js"));
        addCssFile(pluginDescriptor.getPluginResourcesPath("css/allBuilds.css"));
        register();
    }

    @NotNull
    @Override
    public String getGroup() {
        return SERVER_RELATED_GROUP;
    }
}

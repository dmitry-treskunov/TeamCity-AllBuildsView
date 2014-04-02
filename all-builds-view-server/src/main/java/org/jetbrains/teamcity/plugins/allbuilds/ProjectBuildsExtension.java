package org.jetbrains.teamcity.plugins.allbuilds;

import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SProject;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.project.ProjectTab;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ProjectBuildsExtension extends ProjectTab {

    public ProjectBuildsExtension(PagePlaces pagePlaces,
                                  ProjectManager projectManager,
                                  PluginDescriptor pluginDescriptor) {

        super("ProjectBuildView", "Builds", pagePlaces, projectManager, pluginDescriptor.getPluginResourcesPath("projectBuilds.jsp"));
    }

    @Override
    protected void fillModel(@NotNull Map<String, Object> stringObjectMap, @NotNull HttpServletRequest request,
                             @NotNull SProject project, @Nullable SUser sUser) {
    }
}

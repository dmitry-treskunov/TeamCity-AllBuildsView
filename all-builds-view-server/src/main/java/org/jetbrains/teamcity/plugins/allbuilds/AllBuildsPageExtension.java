package org.jetbrains.teamcity.plugins.allbuilds;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AllBuildsPageExtension extends BaseController {

    @NotNull
    private final PluginDescriptor descriptor;

    public AllBuildsPageExtension(@NotNull SBuildServer server, WebControllerManager webControllerManager, @NotNull PluginDescriptor descriptor) {
        super(server);
        this.descriptor = descriptor;
        webControllerManager.registerController("/allBuilds.html", this);
    }
    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        return new ModelAndView(descriptor.getPluginResourcesPath("allBuilds.jsp"));
    }
}

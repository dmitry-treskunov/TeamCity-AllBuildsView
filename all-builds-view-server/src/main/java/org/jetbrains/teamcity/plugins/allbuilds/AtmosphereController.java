package org.jetbrains.teamcity.plugins.allbuilds;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AtmosphereController extends BaseController {

    private AtmosphereFramework atmosphereFramework;

    public AtmosphereController(@NotNull SBuildServer server, WebControllerManager webControllerManager, BuildUpdatesHandler buildUpdatesHandler) {
        super(server);
        this.atmosphereFramework = new AtmosphereFramework();
        this.atmosphereFramework.addAtmosphereHandler("/", buildUpdatesHandler);
        this.atmosphereFramework.init();
        webControllerManager.registerController("/subscribeToBuildsUpdate.html", this);

    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        atmosphereFramework.doCometSupport(AtmosphereRequest.wrap(request), AtmosphereResponse.wrap(response));
        return null;
    }
}

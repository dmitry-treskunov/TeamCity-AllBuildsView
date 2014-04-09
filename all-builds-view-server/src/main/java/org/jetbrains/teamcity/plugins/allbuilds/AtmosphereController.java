package org.jetbrains.teamcity.plugins.allbuilds;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.cpr.*;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.IdleResourceInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


public class AtmosphereController extends BaseController {

    private AtmosphereFramework atmosphereFramework;

    public AtmosphereController(@NotNull SBuildServer server, WebControllerManager webControllerManager, BuildUpdatesHandler buildUpdatesHandler) {
        super(server);
        this.atmosphereFramework = createAtmosphereFramework(buildUpdatesHandler);
        webControllerManager.registerController("/subscribeToBuildsUpdate.html", this);

    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", Boolean.TRUE);
        atmosphereFramework.doCometSupport(AtmosphereRequest.wrap(request), AtmosphereResponse.wrap(response));
        return null;
    }

    private AtmosphereFramework createAtmosphereFramework(BuildUpdatesHandler buildUpdatesHandler) {
        AtmosphereFramework atmosphereFramework = new AtmosphereFramework();
        List<AtmosphereInterceptor> interceptors = new ArrayList<AtmosphereInterceptor>();
        interceptors.add(new AtmosphereResourceLifecycleInterceptor());
        interceptors.add(new HeartbeatInterceptor());
        interceptors.add(new IdleResourceInterceptor());
        interceptors.add(new TrackMessageSizeInterceptor());
        atmosphereFramework.addAtmosphereHandler("/", buildUpdatesHandler, interceptors);
        atmosphereFramework.addInitParameter(ApplicationConfig.BROADCASTER_SHARABLE_THREAD_POOLS, "true");
        atmosphereFramework.addInitParameter(ApplicationConfig.BROADCASTER_LIFECYCLE_POLICY, "EMPTY");
        atmosphereFramework.addInitParameter(ApplicationConfig.BROADCAST_FILTER_CLASSES, PreventFrequentMessagesFilter.class.getName());
        atmosphereFramework.addInitParameter(ApplicationConfig.BROADCASTER_CACHE, UUIDBroadcasterCache.class.getName());
        atmosphereFramework.init();
        return atmosphereFramework;
    }
}

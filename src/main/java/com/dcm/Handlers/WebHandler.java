package com.dcm.Handlers;

import java.util.Collections;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.ee10.servlet.SessionHandler;

import org.eclipse.jetty.http.HttpCookie.SameSite;

public class WebHandler {
    
    static public Handler makeWebHandler(UserHandler databaseHandler, String path) {

        ResourceHandler authResourceHandler = new ResourceHandler();
        authResourceHandler.setBaseResource(ResourceFactory.of(authResourceHandler).newResource(path + "auth/"));
        authResourceHandler.setDirAllowed(true);
        authResourceHandler.setWelcomeFiles(Collections.singletonList("login/login.html"));
        authResourceHandler.setAcceptRanges(true);
        LogInCheck logInCheck = new LogInCheck(authResourceHandler);
        ContextHandler authResourceContext = new ContextHandler(logInCheck, "/");

        ResourceHandler publicResourceHandler = new ResourceHandler();
        publicResourceHandler.setBaseResource(ResourceFactory.of(publicResourceHandler).newResource(path + "public/"));
        publicResourceHandler.setDirAllowed(true);
        publicResourceHandler.setAcceptRanges(true);
        ContextHandler publicResourceContext = new ContextHandler(publicResourceHandler, "/public");

        ResourceHandler privateResourceHandler = new ResourceHandler();
        privateResourceHandler.setBaseResource(ResourceFactory.of(privateResourceHandler).newResource(path + "private/"));
        privateResourceHandler.setDirAllowed(true);
        privateResourceHandler.setWelcomeFiles(Collections.singletonList("home/home.html"));
        privateResourceHandler.setAcceptRanges(true);
        AuthFilter authFilter = new AuthFilter(privateResourceHandler);
        ContextHandler privateResourceContext = new ContextHandler(authFilter, "/tab");

        ResourceHandler adminResourceHandler = new ResourceHandler();
        adminResourceHandler.setBaseResource(ResourceFactory.of(adminResourceHandler).newResource(path + "admin/"));
        adminResourceHandler.setDirAllowed(true);
        adminResourceHandler.setAcceptRanges(true);
        AuthFilter adminAuthFilter = new AuthFilter(adminResourceHandler, true);
        ContextHandler adminResourceContext = new ContextHandler(adminAuthFilter, "/admin");

        StartDebateHandler startDebateHandler = new StartDebateHandler();
        AuthFilter startDebateAuthFilter = new AuthFilter(startDebateHandler, true);
        ContextHandler startDebateContext = new ContextHandler(startDebateAuthFilter, "/start");

        ResetHandler resetHandler = new ResetHandler();
        AuthFilter resetAuthFilter = new AuthFilter(resetHandler, true);
        ContextHandler resetContext = new ContextHandler(resetAuthFilter, "/reset");

        GetCurrentDebateHandler getCurrentDebateHandler = new GetCurrentDebateHandler();
        ContextHandler getCurrentDebateContext = new ContextHandler(getCurrentDebateHandler, "/current");

        LoginHandler loginHandler = new LoginHandler();
        ContextHandler loginContext = new ContextHandler(loginHandler, "/login/handler");

        StaticSessionHandler staticSessionHandler = new StaticSessionHandler();
        ContextHandler sessionContext = new ContextHandler(staticSessionHandler, "/session");

        SignOutHandler signOutHandler = new SignOutHandler();
        ContextHandler signOutContext = new ContextHandler(signOutHandler, "/signout");

        ResourceHandler regResourceHandler = new ResourceHandler();
        regResourceHandler.setBaseResource(ResourceFactory.of(regResourceHandler).newResource(path + "join/"));
        regResourceHandler.setDirAllowed(true);
        regResourceHandler.setWelcomeFiles(Collections.singletonList("join.html"));
        regResourceHandler.setAcceptRanges(true);
        AuthFilter regAuthFilter = new AuthFilter(regResourceHandler);
        RegToDebateHandler regToDebateHandler = new RegToDebateHandler(regAuthFilter);
        ContextHandler regToDebateContext = new ContextHandler(regToDebateHandler, "/join");

        JoinHandler joinHandler = new JoinHandler();
        RegToDebateHandler regToDebateHandlerWithJoin = new RegToDebateHandler(joinHandler);
        ContextHandler joinContext = new ContextHandler(regToDebateHandlerWithJoin, "/join/handler");

        ParticipantsHandler participantsHandler = new ParticipantsHandler();
        AuthFilter adminAuthFilterParticipants = new AuthFilter(participantsHandler, true);
        ContextHandler participantsContext = new ContextHandler(adminAuthFilterParticipants, "/admin/participants");

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        
        //set the resource handlers
        contexts.addHandler(authResourceContext);
        contexts.addHandler(publicResourceContext);
        contexts.addHandler(privateResourceContext);
        contexts.addHandler(adminResourceContext);
        // Add the login logic handler to the context collection
        contexts.addHandler(loginContext);
        contexts.addHandler(sessionContext);
        contexts.addHandler(signOutContext);
        contexts.addHandler(regToDebateContext);
        contexts.addHandler(joinContext);
        contexts.addHandler(participantsContext);
        contexts.addHandler(startDebateContext);
        contexts.addHandler(getCurrentDebateContext);
        contexts.addHandler(resetContext);
        
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setHandler(contexts);
        sessionHandler.setSameSite(SameSite.LAX);
        sessionHandler.getSessionCookieConfig().setSecure(true);
        sessionHandler.setSessionPath("/");
        //sessionHandler.setMaxInactiveInterval(3 * 60 * 60); // 3 hour
        return sessionHandler;
    }

}

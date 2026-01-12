package com.dcm.Handlers;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class LogInCheck extends Handler.Wrapper {

    public LogInCheck(Handler handler) {
        super(handler);
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        // Check if the user is authenticated
        if (StaticSessionHandler.isLoggedIn(request)) {
            // User is not authenticated, redirect to login page
            Response.sendRedirect(request, response, callback, "/tab/");
            return true;
        }
        // User is authenticated, continue with the request
        return super.handle(request, response, callback);
    }
    
}

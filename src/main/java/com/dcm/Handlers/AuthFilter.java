package com.dcm.Handlers;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class AuthFilter extends Handler.Wrapper {

    boolean needsAdmin = false;
    
    public AuthFilter(Handler handler, boolean needsAdmin) {
        super(handler);
        this.needsAdmin = needsAdmin;
    }

    public AuthFilter(Handler handler) {
        this(handler, false);
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        // Check if the user is authenticated
        /*if (!StaticSessionHandler.isLoggedIn(request)) {
            //add original URI to session
            StaticSessionHandler.startLogin(request);
            // User is not authenticated, redirect to login page
            Response.sendRedirect(request, response, callback, "/");
            //callback.succeeded();
            return true;
        }*/
        boolean isAdmin = StaticSessionHandler.isAdmin(request);
        if (needsAdmin && !isAdmin) {
            // User is not an admin, redirect to home page
            Response.sendRedirect(request, response, callback, "/tab");
            return true;
        }
        // User is authenticated, continue with the request
        return super.handle(request, response, callback);
    }
    
}

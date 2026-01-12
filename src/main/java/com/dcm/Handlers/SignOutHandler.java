package com.dcm.Handlers;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

public class SignOutHandler extends Handler.Abstract {
    
    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        // Invalidate the session to log out the user
        StaticSessionHandler.logout(request);
        
        // Redirect to the login page or home page
        Response.sendRedirect(request, response, callback, "/login/login.html");
        return true;
    }

}

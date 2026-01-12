package com.dcm.Handlers;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;

import com.dcm.App;

public class ResetHandler extends Handler.Abstract {

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        App.allocator.reset();
        //send 200 OK response
        Response.sendRedirect(request, response, callback, "/admin/create/create.html");
        return true;
    }
    
}

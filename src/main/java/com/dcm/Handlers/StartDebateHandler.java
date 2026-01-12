package com.dcm.Handlers;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;

import com.dcm.App;

public class StartDebateHandler extends Handler.Abstract {

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        //get debates from fields
        Fields fields;
        try {
            fields = Request.getParameters(request);
        } catch (Exception e) {
            // Handle the exception
            e.printStackTrace();
            Response.writeError(request, response, callback, HttpStatus.BAD_REQUEST_400, "Invalid request");
            return true;
        }

        String debates = fields.getValue("debate");
        if (debates != null) {
            App.currentDebate = debates;
            //send 200 OK response
            Response.sendRedirect(request, response, callback, "/admin/create/create.html");
            return true;
        }
        else {
            // Send an error response, completing the callback, and returning true.
            Response.writeError(request, response, callback, HttpStatus.BAD_REQUEST_400, "No debates provided");
            return true;
        }
    }
}
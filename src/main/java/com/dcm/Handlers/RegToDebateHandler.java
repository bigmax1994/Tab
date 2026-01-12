package com.dcm.Handlers;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import com.dcm.App;

public class RegToDebateHandler extends Handler.Wrapper {
    
    public RegToDebateHandler(Handler nextHandler) {
        super(nextHandler);
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        try {
            //get the submitted debate code from the request
            String debateCode = Request.getParameters(request).getValue("code");
            String currentDebateCode = App.allocator.getCurrentDebateCode();
            if (debateCode == null || debateCode.isEmpty() || !debateCode.equals(currentDebateCode)) {
                // If no debate code is provided, redirect to the home page
                Response.sendRedirect(request, response, callback, "/");
                return true;
            }
            
            return super.handle(request, response, callback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
package com.dcm.Handlers;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;

import com.dcm.AllocationHelpers.Player;
import com.dcm.Utils.StringGenerator;
import com.dcm.App;

public class LoginHandler extends Handler.Abstract {

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        
        //create session
        request.getSession(true);

        Fields fields;
        try {
            fields = Request.getParameters(request);
        }catch (Exception e) {
            // Handle the exception
            e.printStackTrace();
            Response.writeError(request, response, callback, HttpStatus.BAD_REQUEST_400, "Invalid request");
            return true;
        }
        String username = StringGenerator.escape(fields.getValue("username"));
        String password = StringGenerator.escape(fields.getValue("password"));

        if (username != null && password != null) {
            // Check if the user is correct
            Player p = App.userHandler.login(username, password);
            if (p != null) {
                // User exists, send a success response
                StaticSessionHandler.setPlayer(request, p);
                //redirect to the orignal URI
                String originalURI = StaticSessionHandler.getOriginalURI(request);

                System.out.println("User " + username + " logged in successfully, redirecting to " + originalURI);
                Response.sendRedirect(request, response, callback, originalURI);
            } else {
                // User does not exist, send an error response
                StaticSessionHandler.failedLogin(request, "Invalid username or password");
                Response.sendRedirect(request, response, callback, "/login/login.html");
            }
        }
        else
        {
            // Send an error response, completing the callback, and returning true.
            Response.sendRedirect(request, response, callback, "/tab/");
        }
        // The callback will be eventually completed in all cases, return true.
        return true;
    }
}

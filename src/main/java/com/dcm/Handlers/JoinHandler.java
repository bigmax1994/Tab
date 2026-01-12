package com.dcm.Handlers;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.http.HttpCookie;

import com.dcm.AllocationHelpers.Player;
import com.dcm.Utils.StringGenerator;

import com.dcm.App;

public class JoinHandler extends Handler.Abstract {

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        
        //create session
        request.getSession(false);

        Fields fields;
        try {
            fields = Request.getParameters(request);
        }catch (Exception e) {
            // Handle the exception
            e.printStackTrace();
            Response.writeError(request, response, callback, HttpStatus.BAD_REQUEST_400, "Invalid request");
            return true;
        }
        String name = StringGenerator.escape(fields.getValue("name"));
        boolean english = "on".equals(fields.getValue("en"));
        boolean german = "on".equals(fields.getValue("de"));
        boolean bp = "on".equals(fields.getValue("bp"));
        boolean opd = "on".equals(fields.getValue("opd"));
        boolean speak = "on".equals(fields.getValue("speak"));
        boolean judge = "on".equals(fields.getValue("judge"));
        int experience = Integer.parseInt(fields.getValue("exp"));
        String nextTournament = StringGenerator.escape(fields.getValue("next-tournament"));
        boolean couldntLastTime = "1".equals(fields.getValue("last-time"));

        if (name != null) {
            Player p = new Player(name, english, german, bp, opd, speak, judge, experience, nextTournament, couldntLastTime);
            if (!StaticSessionHandler.isLoggedIn(request)) {
                StaticSessionHandler.setPlayer(request, p);
            }
            App.allocator.addPlayer(p);
            //set cookie
            String sanitized = p.getName()
                .replaceAll("[^A-Za-z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_+|_+$", "");
            if (sanitized.isEmpty()) sanitized = "player";

            HttpCookie cookie = HttpCookie.build("player", sanitized)
                .path("/")
                .httpOnly(false)
                .sameSite(HttpCookie.SameSite.STRICT)
                .maxAge(60 * 60 * 24) // 1 day
                .build();
            Response.addCookie(response, cookie);

            //redirect to the home page
            Response.sendRedirect(request, response, callback, "/public/joinedDebate/joined.html");
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

package com.dcm.Handlers;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Session;

import java.nio.ByteBuffer;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;

import com.dcm.AllocationHelpers.Player;

public class StaticSessionHandler extends Handler.Abstract {

    static final String KEY_PLAYER = "player";
    static final String KEY_TRIED_LOGIN = "triedLogin";
    static final String KEY_LOGIN_ERROR = "loginError";
    static final String KEY_ORIGINAL_URI = "requestURI";

    static void startLogin(Request request) {
        Session session = request.getSession(true);
        if (session == null) {
            return;
        }
        String originalURI = request.getHttpURI().asString();
        session.setAttribute(KEY_ORIGINAL_URI, originalURI);
    }

    static String getOriginalURI(Request request) {
        Session session = request.getSession(false);
        if (session == null) {
            return "/";
        }
        String originalURI = (String) session.getAttribute(KEY_ORIGINAL_URI);
        if (originalURI != null) {
            session.removeAttribute(KEY_ORIGINAL_URI);
            return originalURI;
        }
        return "/";
    }

    static void failedLogin(Request request, String message) {
        Session session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.setAttribute(KEY_TRIED_LOGIN, true);
        session.setAttribute(KEY_LOGIN_ERROR, message);
    }

    static boolean hasTriedLogin(Request request) {
        Session session = request.getSession(false);
        if (session == null) {
            return false;
        }
        Boolean triedLogin = (Boolean) session.getAttribute(KEY_TRIED_LOGIN);
        if (triedLogin != null && triedLogin) {
            session.setAttribute(KEY_TRIED_LOGIN, false);
            return true;
        } else {
            return false;
        }
    }

    static String getLoginError(Request request) {
        Session session = request.getSession(false);
        if (session == null) {
            return null;
        }
        String error = (String) session.getAttribute(KEY_LOGIN_ERROR);
        if (error != null) {
            session.removeAttribute(KEY_LOGIN_ERROR);
        }
        return error;
    }

    static void setPlayer(Request request, Player player) {
        Session session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.setAttribute(KEY_PLAYER, player);
        session.setAttribute(KEY_TRIED_LOGIN, false);
    }
    static Player getPlayer(Request request) {
        Session session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Player) session.getAttribute(KEY_PLAYER);
    }
    static boolean isLoggedIn(Request request) {
        return getPlayer(request) != null;
    }
    static boolean isAdmin(Request request) {
        Player player = getPlayer(request);
        if (player == null) {
            return false;
        }
        return player.isAdmin();
    }

    static void logout(Request request) {
        Session session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(KEY_PLAYER);
            session.removeAttribute(KEY_TRIED_LOGIN);
            session.removeAttribute(KEY_LOGIN_ERROR);
        }
    }

    @Override
    public boolean handle(Request request, org.eclipse.jetty.server.Response response, Callback callback) {
        boolean failedLogin = hasTriedLogin(request);
        String js = "var failedLogin = " + (failedLogin ? "true" : "false") + ";";
        String loginError = getLoginError(request);
        if (loginError != null) {
            js += "var loginError = '" + loginError + "';";
        } else {
            js += "var loginError = '';";
        }
        if (isLoggedIn(request)) {
            js += "var isAdmin = " + isAdmin(request) + ";";
        }
        byte[] jsBytes = js.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(jsBytes);
        response.write(false, buffer, callback);
        return true;
    }

}

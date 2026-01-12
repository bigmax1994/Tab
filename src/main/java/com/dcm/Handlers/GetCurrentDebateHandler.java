package com.dcm.Handlers;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Callback;
import com.dcm.App;

import java.nio.ByteBuffer;

public class GetCurrentDebateHandler extends Handler.Abstract {

    @Override
    public boolean handle(Request request, Response response, Callback callback) {
        String js = "var currentDebate = `" + App.currentDebate + "`;\n";
        byte[] jsBytes = js.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(jsBytes);
        response.write(false, buffer, callback);
        return true;
    }
}
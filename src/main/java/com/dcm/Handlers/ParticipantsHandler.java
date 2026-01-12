package com.dcm.Handlers;

import java.nio.ByteBuffer;

import org.eclipse.jetty.server.Handler;

import com.dcm.App;
import com.dcm.AllocationHelpers.Player;

public class ParticipantsHandler extends Handler.Abstract {
    
    @Override
    public boolean handle(org.eclipse.jetty.server.Request request, org.eclipse.jetty.server.Response response, org.eclipse.jetty.util.Callback callback) throws Exception {

        String js = "var participants = [";
        for (Player player : App.allocator.getPlayers()) {
            js += "{";
            js += "\"name\": \"" + player.getName() + "\",";
            js += "\"role\": \"";
            if (player.canSpeak()) {
                js += "S";
            }
            if (player.canSpeak() && player.canJudge()) {
                js += "/";
            }
            if (player.canJudge()) {
                js += "J";
            }
            js += "\",";
            js += "\"format\": \"";
            if (player.isBp()) {
                js += "BP";
            }
            if (player.isBp() && player.isOpd()) {
                js += "/";
            }
            if (player.isOpd()) {
                js += "OPD";
            }
            js += "\",";
            js += "\"language\": \"";
            if (player.isEnglish()) {
                js += "En";
            }
            if (player.isEnglish() && player.isGerman()) {
                js += "/";
            }
            if (player.isGerman()) {
                js += "De";
            }
            js += "\",";
            js += "\"experience\": \"";
            if (player.getExperience() == 0) {
                js += "<5";
            } else if (player.getExperience() == 1) {
                js += "5-10";
            } else if (player.getExperience() == 2) {
                js += ">10";
            }
            js += "\",";
            js += "\"nextTournament\": \"";
            if (player.getNextTournament() == null || player.getNextTournament().isEmpty()) {
                js += "N/A";
            } else {
                js += player.getNextTournament();
            }
            js += "\",";
            js += "\"couldnt\": ";
            js += player.couldntLastTime() ? "\"true\"" : "\"-\"";
            js += "},";
        }
        if (js.endsWith(",")) {
            js = js.substring(0, js.length() - 1); // Remove the trailing comma
        }
        js += "]";
        byte[] jsBytes = js.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(jsBytes);
        response.write(false, buffer, callback);
        return true;
    }

}

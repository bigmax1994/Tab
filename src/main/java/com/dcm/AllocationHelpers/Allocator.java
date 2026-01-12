package com.dcm.AllocationHelpers;

import com.dcm.App;
import com.dcm.Utils.StringGenerator;

public class Allocator {

    private static final int debateCodeLength = 10;

    Player[] players;

    String debateCode;

    public Allocator() {
        //default constructor
        this.players = new Player[0];
        generateNewCode();
    }

    public Allocator(Player[] players) {
        //randomize order of players
        this.players = players;
        //generate a random debate code
        generateNewCode();
    }

    public void reset() {
        //reset the allocator
        this.players = new Player[0];
        generateNewCode();
    }

    private void generateNewCode() {
        //generate a new debate code
        this.debateCode = StringGenerator.generateRandomString(debateCodeLength);
        //create pdf with the code
        App.recreatePDF(this.debateCode);
    }

    public void addPlayer(Player player) {
        //add a player to the allocator
        if (player == null) {
            System.out.println("Cannot add null player.");
            return;
        }
        Player[] newPlayers = new Player[players.length + 1];
        int randomIndex = players.length;//random.nextInt(players.length + 1);
        System.arraycopy(players, 0, newPlayers, 0, randomIndex);
        newPlayers[randomIndex] = player;
        System.arraycopy(players, randomIndex, newPlayers, randomIndex + 1, players.length - randomIndex);
        this.players = newPlayers;
    }

    public String getCurrentDebateCode() {
        return debateCode;
    }

    public Player[] getPlayers() {
        return players;
    }

}

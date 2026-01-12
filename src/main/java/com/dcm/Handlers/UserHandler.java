package com.dcm.Handlers;

import com.dcm.AllocationHelpers.Player;

public class UserHandler {
    
    String user;
    String password;

    public UserHandler(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public Player login(String username, String password) {
        if (username.equals(this.user) && password.equals(this.password)) {
            // Special case for max user, return a Player object with default values
            return new Player(0, this.user, 1500, 200, true, true, true, true, true, true, true);
        }
        return null; // Return null if wrong user
    }
}
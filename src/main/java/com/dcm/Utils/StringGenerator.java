package com.dcm.Utils;

import java.security.SecureRandom;

public class StringGenerator {
    
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String generateRandomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public static String escape(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder escaped = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == '\'' || c == '\"' || c == '\\') {
                escaped.append('\\');
            }
            escaped.append(c);
        }
        return escaped.toString();
    }

}

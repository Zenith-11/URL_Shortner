package com.urlshortener.util;

import java.security.SecureRandom;

public class ShortCodeGenerator {


    private static final String BASE62 =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int CODE_LENGTH = 7;

    private static final SecureRandom random = new SecureRandom();

    public static String generate(){
        StringBuilder sb = new StringBuilder(CODE_LENGTH);

        for(int i=0 ;i<CODE_LENGTH ;i++){
            int idx = random.nextInt(BASE62.length());
            sb.append(BASE62.charAt(idx));
        }
        return sb.toString();
    }

}

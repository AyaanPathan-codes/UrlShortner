package com.ayaan.UrlShortner.Utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {
    private static final String ALPHABET =  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int DEFAULT_LENGTH = 7;

    private final SecureRandom random = new SecureRandom();

    public String generate(){
        return generate(DEFAULT_LENGTH);
    }
    public String generate(int length){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++){
            int index = random.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }
}

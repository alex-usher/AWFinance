package com.example.alex_.project;

import java.security.MessageDigest;

public class Crypt {

    public static String Hash512(String toHash){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(toHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<digest.length; i++){
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}


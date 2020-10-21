package com.example.alex_.project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Crypt {

  /**
   * Hashes the given string using SHA-512
   *
   * @param toHash - the string to hash
   * @return - the SHA-512 hash of the given string
   */
  public static String hash512(String toHash) {
    StringBuilder sb = new StringBuilder();

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");
      byte[] digest = md.digest(toHash.getBytes());
      for (byte b : digest) {
        sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return sb.toString();
  }
}

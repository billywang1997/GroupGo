package com.example.myapplication.helper;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncodeHelper {

    // encrypting logic via MD5 method
    public static String encrypt(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");

            StringBuilder str = new StringBuilder();
            byte[] encrypted = md.digest(text.getBytes());

            for (int i = 0; i < encrypted.length; i++) {
                String hexByte = Integer.toHexString(encrypted[i] & 0xff);
                if (hexByte.length() == 1) {
                    str.append("0");
                }
                str.append(hexByte);
            }
            String result = str.toString();
            return result;

        } catch (NoSuchAlgorithmException e) {
            Log.e("Error", "Can't encrypt.");
            return "";
        }
    }
}
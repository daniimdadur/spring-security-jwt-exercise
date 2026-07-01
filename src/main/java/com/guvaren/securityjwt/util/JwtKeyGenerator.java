package com.guvaren.securityjwt.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class JwtKeyGenerator {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");

        // Menentukan ukuran kunci minimal 256-bit
        keyGen.init(256);

        // Menghasilkan SecretKey
        SecretKey secretKey = keyGen.generateKey();

        // Mengubah kunci menjadi string Base64 agar mudah disimpan di application.properties / .env
        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        System.out.println("Secret Key (Base64): " + base64Key);
    }
}

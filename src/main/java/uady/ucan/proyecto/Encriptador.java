package uady.ucan.proyecto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// La lógica de encriptación de contraseñas está en su propia clase ahora.

public class Encriptador {
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String encriptarContraseña(String password) throws NoSuchAlgorithmException {
        MessageDigest dg = MessageDigest.getInstance("SHA-256");
        byte[] hashEncriptado = dg.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashEncriptado);
    }
}

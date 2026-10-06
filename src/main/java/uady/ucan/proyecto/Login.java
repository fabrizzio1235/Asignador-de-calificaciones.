package uady.ucan.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import java.lang.StringBuilder;

import static uady.ucan.proyecto.Alerta.setAlert;

public class Login extends CambioDeMenu {
    @FXML private TextField usuarioEntrada;
    @FXML private PasswordField contraseñaEntrada;
    @FXML private Button botonLogin;

    private RepositorioUsuarios repositorioUsuarios = new RepositorioUsuarios();

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

    @FXML
    public void inicioSesion() throws NoSuchAlgorithmException { // 0: Entrar a la app, 1: Salir
        repositorioUsuarios.leerUsuarios();
        Usuario usuario = repositorioUsuarios.buscarUsuario(usuarioEntrada.getText());

        if (usuario == null) {
            setAlert(Alert.AlertType.WARNING, "Usuario incorrecto.");
            return;
        }

        boolean contraseñaCorrecta = usuario.getPassword().equals(encriptarContraseña(contraseñaEntrada.getText()));
        if (!contraseñaCorrecta) {
            setAlert(Alert.AlertType.WARNING, "Contraseña incorrecta.");
            return;
        }

        menu();
        botonLogin.getScene().getWindow().hide();
    }
}
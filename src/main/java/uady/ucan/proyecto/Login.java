package uady.ucan.proyecto;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Scanner;
import java.lang.StringBuilder;

import static uady.ucan.proyecto.ControladorVentanas.setAlert;

public class Login extends CambioDeMenu {
    @FXML private TextField usuarioEntrada;
    @FXML private PasswordField contraseñaEntrada;
    @FXML private Button botonLogin;

    private ArrayList<Usuario> listaUsuarios = new ArrayList<>();

    public void leerUsuarios() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/users.csv"))) {
            String linea = br.readLine();

            while (linea != null) {
                String[] dato = linea.split(",");
                //Inline Method - Nuevo
                listaUsuarios.add(new Usuario(dato[0], dato[1]));
                linea = br.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

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

    //Extract Method - Nuevo
    private Usuario buscarUsuario(String nickname) {
        for (Usuario u : listaUsuarios) {
            if (u.getNickname().equals(nickname)) {
                return u;
            }
        }
        return null;
    }

    //Substitute Algorithm - Nuevo
    @FXML
    public void inicioSesion() throws NoSuchAlgorithmException { // 0: Entrar a la app, 1: Salir
        leerUsuarios();
        Usuario usuario = buscarUsuario(usuarioEntrada.getText());

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
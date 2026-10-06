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

    // Se instancian RepositorioUsuarios y Encriptador en Login porque solo él hace uso de las clases.
    private RepositorioUsuarios repositorioUsuarios = new RepositorioUsuarios();
    private Encriptador encriptador = new Encriptador();

    @FXML
    public void inicioSesion() throws NoSuchAlgorithmException { // 0: Entrar a la app, 1: Salir
        repositorioUsuarios.leerUsuarios();
        Usuario usuario = repositorioUsuarios.buscarUsuario(usuarioEntrada.getText());

        if (usuario == null) {
            setAlert(Alert.AlertType.WARNING, "Usuario incorrecto.");
            return;
        }

        boolean contraseñaCorrecta = usuario.getPassword().equals(
                encriptador.encriptarContraseña(contraseñaEntrada.getText()));
        if (!contraseñaCorrecta) {
            setAlert(Alert.AlertType.WARNING, "Contraseña incorrecta.");
            return;
        }

        menu();
        botonLogin.getScene().getWindow().hide();
    }
}
package uady.ucan.proyecto;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uady.ucan.proyecto.Alerta.setAlert;

class LoginTest {

    // Enciende el motor gráfico de JavaFX una sola vez para toda la clase.
    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit ya inicializado por otra clase de prueba.
        }
    }

    /*
    Prueba para Substitute Algorithm + Extract Method + Extract Class
    Objetivo: Confirmar que el sistema responda correctamente para el caso de ingresar un usuario incorrecto.
    Resultado esperado: se dispara WARNING "Usuario incorrecto." y nunca se llama a menu().
    Datos: usuarioEntrada = "usuario_inexistente"; el repositorio (mockeado) dice que no existe.
    */
    @Test
    void inicioSesionMuestraAlertaSiUsuarioNoExiste() throws Exception {
        Login loginSpy = spy(new Login());
        doNothing().when(loginSpy).menu();

        RepositorioUsuarios repositorioMock = mock(RepositorioUsuarios.class);
        when(repositorioMock.buscarUsuario("usuario_inexistente")).thenReturn(null);
        inyectarCampoPrivado(loginSpy, "repositorioUsuarios", repositorioMock);

        TextField userField = mock(TextField.class);
        PasswordField passField = mock(PasswordField.class);
        when(userField.getText()).thenReturn("usuario_inexistente");
        when(passField.getText()).thenReturn("1234");

        inyectarCampoPrivado(loginSpy, "usuarioEntrada", userField);
        inyectarCampoPrivado(loginSpy, "contraseñaEntrada", passField);

        try (MockedStatic<Alerta> alertaSimulada = mockStatic(Alerta.class)) {
            loginSpy.inicioSesion();

            alertaSimulada.verify(() -> setAlert(eq(Alert.AlertType.WARNING), eq("Usuario incorrecto.")));
            verify(loginSpy, never()).menu();
        }
    }

    /*
    Prueba para Substitute Algorithm + Extract Method + Extract Class
    Objetivo: Confirmar que un usuario que SÍ existe pero con contraseña incorrecta se sigue rechazando igual.
    Resultado esperado: se dispara WARNING "Contraseña incorrecta." y nunca se llama a menu().
    Datos: usuarioEntrada = "admin" (el repositorio mockeado lo "encuentra"), contraseña incorrecta.
    */
    @Test
    void inicioSesionMuestraAlertaSiContrasenaEsIncorrecta() throws Exception {
        Login loginSpy = spy(new Login());
        doNothing().when(loginSpy).menu();

        // Calculamos el hash real de la contraseña CORRECTA ("1234") usando el
        // Encriptador real (no mockeado, es lógica pura y determinista).
        String hashReal = loginSpy.encriptador.encriptarContraseña("1234");
        Usuario usuarioAdmin = new Usuario("admin", hashReal);

        RepositorioUsuarios repositorioMock = mock(RepositorioUsuarios.class);
        when(repositorioMock.buscarUsuario("admin")).thenReturn(usuarioAdmin);
        inyectarCampoPrivado(loginSpy, "repositorioUsuarios", repositorioMock);

        TextField userField = mock(TextField.class);
        PasswordField passField = mock(PasswordField.class);
        when(userField.getText()).thenReturn("admin");
        when(passField.getText()).thenReturn("claveMala"); // contraseña incorrecta

        inyectarCampoPrivado(loginSpy, "usuarioEntrada", userField);
        inyectarCampoPrivado(loginSpy, "contraseñaEntrada", passField);

        try (MockedStatic<Alerta> alertaSimulada = mockStatic(Alerta.class)) {
            loginSpy.inicioSesion();

            alertaSimulada.verify(() -> setAlert(eq(Alert.AlertType.WARNING), eq("Contraseña incorrecta.")));
            verify(loginSpy, never()).menu();
        }
    }

    /*
    Prueba para Substitute Algorithm + Extract Method + Extract Class
    Objetivo: Confirmar que acepta al usuario correcto (usuario existe y contraseña correcta).
    Resultado esperado: se llama a menu() y se oculta la ventana.
    Datos: usuarioEntrada = "admin", contraseñaEntrada = "1234" (coincide con el hash del Usuario mockeado).
    */
    @Test
    void inicioSesionEntraAlMenuYOcultaVentanaSiCredencialesSonCorrectas() throws Exception {
        Login loginSpy = spy(new Login());
        doNothing().when(loginSpy).menu();

        String hashReal = loginSpy.encriptador.encriptarContraseña("1234");
        Usuario usuarioAdmin = new Usuario("admin", hashReal);

        RepositorioUsuarios repositorioMock = mock(RepositorioUsuarios.class);
        when(repositorioMock.buscarUsuario("admin")).thenReturn(usuarioAdmin);
        inyectarCampoPrivado(loginSpy, "repositorioUsuarios", repositorioMock);

        TextField userField = mock(TextField.class);
        PasswordField passField = mock(PasswordField.class);
        Button botonMock = mock(Button.class);
        Scene sceneMock = mock(Scene.class);
        Window windowMock = mock(Window.class);

        when(userField.getText()).thenReturn("admin");
        when(passField.getText()).thenReturn("1234"); // credenciales correctas

        when(botonMock.getScene()).thenReturn(sceneMock);
        when(sceneMock.getWindow()).thenReturn(windowMock);

        inyectarCampoPrivado(loginSpy, "usuarioEntrada", userField);
        inyectarCampoPrivado(loginSpy, "contraseñaEntrada", passField);
        inyectarCampoPrivado(loginSpy, "botonLogin", botonMock);

        loginSpy.inicioSesion();

        verify(loginSpy).menu();
        verify(windowMock).hide();
    }

    /*
    Prueba para Inline Method + Extract Class
    Objetivo: Confirmar que leerUsuarios() (ahora en RepositorioUsuarios) sigue cargando
    un Usuario por cada línea real del archivo.
    Resultado esperado: la cantidad de Usuario cargados coincide con la cantidad de
    líneas del archivo real users.csv.
    Datos: el archivo real src/main/resources/users.csv, sin modificarlo.
    */
    @Test
    void leerUsuariosCargaUnUsuarioPorCadaLineaDelArchivoReal() throws Exception {
        int lineasEnElArchivo = contarLineas("src/main/resources/users.csv");
        RepositorioUsuarios repositorio = new RepositorioUsuarios();

        repositorio.leerUsuarios();


        ArrayList<Usuario> listaUsuarios = (ArrayList<Usuario>) obtenerCampoPrivado(repositorio, "listaUsuarios");

        assertEquals(lineasEnElArchivo, listaUsuarios.size());
    }

    // Cuenta cuántas líneas tiene un archivo de texto, leyéndolo de verdad desde disco.
    private int contarLineas(String ruta) throws IOException {
        int contador = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            while (br.readLine() != null) contador++;
        }
        return contador;
    }

    // Lee el valor actual de un campo privado de un objeto, usando reflexión.
    private Object obtenerCampoPrivado(Object objetivo, String nombreCampo) throws Exception {
        Field campo = objetivo.getClass().getDeclaredField(nombreCampo);
        campo.setAccessible(true);
        return campo.get(objetivo);
    }

    // Sobrescribe el valor de un campo privado de un objeto, usando reflexión.
    private void inyectarCampoPrivado(Object objetivo, String nombreCampo, Object valor) throws Exception {
        Field campo = objetivo.getClass().getDeclaredField(nombreCampo);
        campo.setAccessible(true);
        campo.set(objetivo, valor);
    }
}
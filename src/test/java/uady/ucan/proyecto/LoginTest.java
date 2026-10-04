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

class LoginTest {

    // Enciende el motor gráfico de JavaFX una sola vez para toda la clase.
    // JavaFX exige el toolkit encendido para usar TextField/Alert, incluso
    // si son falsos. Si otra clase de prueba ya lo encendió, el catch lo ignora.
    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    /*
    Prueba para Substitute Algorithm + Extract Method
    Objetivo: Confirmar que el sistema responda correctamente para el caso de ingresar un usuario incorrecto.
    Resultado esperado: se dispara WARNING "Usuario incorrecto." y nunca se llama a menu().
    Datos: usuarioEntrada = "usuario_inexistente", listaUsuarios vacía (leerUsuarios apagado).
     */
    @Test
    void inicioSesionMuestraAlertaSiUsuarioNoExiste() throws Exception {
        Login loginSpy = spy(new Login());

        doNothing().when(loginSpy).leerUsuarios();
        doNothing().when(loginSpy).menu();

        TextField userField = mock(TextField.class);
        PasswordField passField = mock(PasswordField.class);

        when(userField.getText()).thenReturn("usuario_inexistente");
        when(passField.getText()).thenReturn("1234");

        inyectarCampoPrivado(loginSpy, "usuarioEntrada", userField);
        inyectarCampoPrivado(loginSpy, "contraseñaEntrada", passField);

        // Como apagamos leerUsuarios(), la lista está vacía. El código no encontrará al usuario.

        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {

            loginSpy.inicioSesion(); // Ejecutamos el método real que queremos probar

            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.WARNING), eq("Usuario incorrecto."))
            );
            // Comprobamos que, al fallar, el código NUNCA intentó abrir el menú.
            verify(loginSpy, never()).menu();
        }
    }

    /*
    Prueba para Substitute Algorithm + Extract Method
    Objetivo: Confirmar un usuario que SÍ existe pero con contraseña incorrecta se sigue rechazando igual
    Resultado esperado: se dispara WARNING "Contraseña incorrecta." y nunca se llama a menu().
    Datos: usuarioEntrada = "admin" (existe en listaUsuarios inyectada), contraseña incorrecta.
     */
    @Test
    void inicioSesionMuestraAlertaSiContrasenaEsIncorrecta() throws Exception {
        Login loginSpy = spy(new Login());
        doNothing().when(loginSpy).leerUsuarios();
        doNothing().when(loginSpy).menu();

        TextField userField = mock(TextField.class);
        PasswordField passField = mock(PasswordField.class);
        when(userField.getText()).thenReturn("admin");
        when(passField.getText()).thenReturn("claveMala");

        inyectarCampoPrivado(loginSpy, "usuarioEntrada", userField);
        inyectarCampoPrivado(loginSpy, "contraseñaEntrada", passField);

        // Simulamos la memoria: En vez de leer el CSV, creamos un usuario correcto a mano
        // y se lo metemos a la fuerza a la lista interna para que el código sí lo encuentre.
        ArrayList<Usuario> listaMock = new ArrayList<>();
        String hashReal = loginSpy.encriptarContraseña("1234");
        listaMock.add(new Usuario("admin", hashReal));
        inyectarCampoPrivado(loginSpy, "listaUsuarios", listaMock);

        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {
            loginSpy.inicioSesion();

            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.WARNING), eq("Contraseña incorrecta."))
            );
            verify(loginSpy, never()).menu();
        }
    }

    /*
    Prueba para Substitute Algorithm + Extract Method
    Objetivo: Confirmar que acepta al usuario correcto (usuario existe/contraseña es correcta)
    Resultado esperado: se llama a menu()
    Datos: usuarioEntrada = "admin", contraseñaEntrada = "1234" (coincide con el hash inyectado).
     */
    @Test
    void inicioSesionEntraAlMenuYOcultaVentanaSiCredencialesSonCorrectas() throws Exception {
        Login loginSpy = spy(new Login());
        doNothing().when(loginSpy).leerUsuarios();
        doNothing().when(loginSpy).menu();

        TextField userField = mock(TextField.class);
        PasswordField passField = mock(PasswordField.class);
        Button botonMock = mock(Button.class);
        Scene sceneMock = mock(Scene.class);
        Window windowMock = mock(Window.class);

        when(userField.getText()).thenReturn("admin");
        when(passField.getText()).thenReturn("1234");

        // El código original hace: botonLogin.getScene().getWindow().hide();
        // Para que esa cadena de métodos no lance un NullPointerException, encadenamos nuestros mocks:
        when(botonMock.getScene()).thenReturn(sceneMock);
        when(sceneMock.getWindow()).thenReturn(windowMock);

        inyectarCampoPrivado(loginSpy, "usuarioEntrada", userField);
        inyectarCampoPrivado(loginSpy, "contraseñaEntrada", passField);
        inyectarCampoPrivado(loginSpy, "botonLogin", botonMock); // Inyectamos el botón falso

        ArrayList<Usuario> listaMock = new ArrayList<>();
        String hashReal = loginSpy.encriptarContraseña("1234");
        listaMock.add(new Usuario("admin", hashReal));
        inyectarCampoPrivado(loginSpy, "listaUsuarios", listaMock);

        loginSpy.inicioSesion();

        verify(loginSpy).menu();
        verify(windowMock).hide();
    }

    /*
    Prueba para Inline Method
    Objetivo: Confirmar que, tras eliminar la variable intermedia "usuarioActual" (se agrega
    el Usuario directo a la lista), leerUsuarios() sigue cargando un Usuario por cada línea
    real del archivo.
    Resultado esperado: la cantidad de Usuario cargados en listaUsuarios coincide exactamente
    con la cantidad de líneas del archivo real users.csv.
    Datos: el archivo real src/main/resources/users.csv, sin modificarlo.
     */
    @Test
    void leerUsuariosCargaUnUsuarioPorCadaLineaDelArchivoReal() throws Exception {
        int lineasEnElArchivo = contarLineas("src/main/resources/users.csv");
        Login login = new Login();
        login.leerUsuarios();

        // Extraemos la lista privada para contar cuántos usuarios guardó en memoria
        @SuppressWarnings("unchecked")
        ArrayList<Usuario> listaUsuarios = (ArrayList<Usuario>) obtenerCampoPrivado(login, "listaUsuarios");

        assertEquals(lineasEnElArchivo, listaUsuarios.size());
    }

    // MÉTODOS UTILITARIOS (apoyo)

    // Cuenta cuántas líneas tiene un archivo de texto, leyéndolo de verdad desde disco.
    private int contarLineas(String ruta) throws IOException {
        int contador = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            while (br.readLine() != null) contador++;
        }
        return contador;
    }

    // La "Reflexión" (Reflection API) nos permite hackear la privacidad usando setAccessible(true).

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
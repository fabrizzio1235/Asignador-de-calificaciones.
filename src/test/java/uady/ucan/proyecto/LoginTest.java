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

    // @BeforeAll se ejecuta UNA SOLA VEZ antes de todas las pruebas.
    // JavaFX exige que su motor gráfico (Toolkit) esté encendido para usar TextFields o Alerts,
    // incluso si son falsos. Si no hacemos esto, la Máquina Virtual de Java lanza un error y aborta.
    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Si otra clase de prueba ya encendió el motor, JavaFX lanza un error.
            // Lo atrapamos aquí y lo ignoramos porque no nos importa quién lo encendió, solo que esté encendido.
        }
    }

    // =========================================================================================
    // RED DE SEGURIDAD PARA inicioSesion()
    // Estas tres pruebas juntas protegen la lógica de negocio al aplicar dos refactorizaciones
    // documentadas en el Word:
    // 1. Substitute Algorithm: Para cambiar los dos ciclos 'for' por uno solo
    // 2. Extract Method: Para separar la búsqueda del usuario y la verificación de contraseña
    // Si al hacer esos cambios el código deja de comportarse igual, alguna de estas pruebas fallará.
    // =========================================================================================

    @Test
    void inicioSesionMuestraAlertaSiUsuarioNoExiste() throws Exception {
        // SPY (Espía): A diferencia de un Mock (que es 100% falso), un espía usa la clase Login REAL.
        // Nos permite ejecutar el código de verdad, pero dándonos el poder de "apagar" métodos específicos.
        Login loginSpy = spy(new Login());

        // Aislamos la prueba: le decimos al espía que cuando el código intente ejecutar leerUsuarios() o menu(),
        // no haga absolutamente nada. Así evitamos leer el disco duro o abrir ventanas reales.
        doNothing().when(loginSpy).leerUsuarios();
        doNothing().when(loginSpy).menu();

        // MOCK: Creamos objetos 100% falsos (de mentira) para simular la interfaz gráfica.
        TextField userField = mock(TextField.class);
        PasswordField passField = mock(PasswordField.class);

        // Programamos los objetos falsos: "Cuando el código te pida getText(), responde esto:"
        when(userField.getText()).thenReturn("usuario_inexistente");
        when(passField.getText()).thenReturn("1234");

        // Inyectamos estos componentes falsos dentro de nuestro espía usando Reflexión (explicado abajo)
        inyectarCampoPrivado(loginSpy, "usuarioEntrada", userField);
        inyectarCampoPrivado(loginSpy, "contraseñaEntrada", passField);

        // Como apagamos leerUsuarios(), la lista está vacía. El código no encontrará al usuario.

        // MOCK STATIC: ControladorVentanas.setAlert es un método estático (global).
        // El try-with-resources crea una "cuarentena". Mientras estemos en este bloque,
        // las alertas reales son interceptadas y anotadas en 'alertaSimulada'.
        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {

            loginSpy.inicioSesion(); // Ejecutamos el método real que queremos probar

            // VERIFY: Le preguntamos a la cuarentena si el código intentó mostrar una alerta de WARNING.
            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.WARNING), eq("Usuario incorrecto."))
            );
            // Comprobamos que, al fallar, el código NUNCA intentó abrir el menú.
            verify(loginSpy, never()).menu();
        }
    }

    @Test
    void inicioSesionMuestraAlertaSiContrasenaEsIncorrecta() throws Exception {
        Login loginSpy = spy(new Login());
        doNothing().when(loginSpy).leerUsuarios();
        doNothing().when(loginSpy).menu();

        TextField userField = mock(TextField.class);
        PasswordField passField = mock(PasswordField.class);
        when(userField.getText()).thenReturn("admin");
        when(passField.getText()).thenReturn("claveMala"); // Simulamos que el usuario se equivocó de clave

        inyectarCampoPrivado(loginSpy, "usuarioEntrada", userField);
        inyectarCampoPrivado(loginSpy, "contraseñaEntrada", passField);

        // Simulamos la memoria: En vez de leer el CSV, creamos un usuario correcto a mano
        // y se lo metemos a la fuerza a la lista interna para que el código sí lo encuentre.
        ArrayList<Usuario> listaMock = new ArrayList<>();
        String hashReal = loginSpy.encriptarContraseña("1234"); // Clave correcta encriptada
        listaMock.add(new Usuario("admin", hashReal));
        inyectarCampoPrivado(loginSpy, "listaUsuarios", listaMock);

        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {
            loginSpy.inicioSesion();

            // Verificamos que el código detectó la mala contraseña y mandó el aviso correcto
            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.WARNING), eq("Contraseña incorrecta."))
            );
            verify(loginSpy, never()).menu();
        }
    }

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
        when(passField.getText()).thenReturn("1234"); // Credenciales correctas

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

        // Verificamos el "Camino Feliz": El sistema validó todo, llamó a menu() y ocultó la ventana (hide)
        verify(loginSpy).menu();
        verify(windowMock).hide();
    }

    // =========================================================================================
    // RED DE SEGURIDAD PARA leerUsuarios()
    // Protege la refactorización de Inline Method, donde se eliminará la variable
    // intermedia 'usuarioActual' para agregar el objeto directo a la lista
    // =========================================================================================

    @Test
    void leerUsuariosCargaUnUsuarioPorCadaLineaDelArchivoReal() throws Exception {
        // Usamos nuestro método utilitario de abajo para contar las líneas reales del archivo
        int lineasEnElArchivo = contarLineas("src/main/resources/users.csv");
        Login login = new Login();

        // Ejecutamos la lectura real del CSV
        login.leerUsuarios();

        // Extraemos la lista privada para contar cuántos usuarios guardó en memoria
        @SuppressWarnings("unchecked")
        ArrayList<Usuario> listaUsuarios = (ArrayList<Usuario>) obtenerCampoPrivado(login, "listaUsuarios");

        // AssertEquals verifica que la cantidad de líneas coincida con la cantidad de usuarios guardados
        assertEquals(lineasEnElArchivo, listaUsuarios.size());
    }

    // ==========================================
    // MÉTODOS UTILITARIOS (Magia de Java Reflection)
    // ==========================================

    private int contarLineas(String ruta) throws IOException {
        int contador = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            while (br.readLine() != null) contador++;
        }
        return contador;
    }

    // En Java, las variables 'private' no se pueden leer ni modificar desde afuera de su clase.
    // En las pruebas unitarias necesitamos hacerlo porque no hay una interfaz gráfica que las llene.
    // La "Reflexión" (Reflection API) nos permite hackear esa privacidad usando setAccessible(true).

    // CHAVO ERA PONER GETTERS Y SETTERS AHHH METHODS
    private Object obtenerCampoPrivado(Object objetivo, String nombreCampo) throws Exception {
        Field campo = objetivo.getClass().getDeclaredField(nombreCampo);
        campo.setAccessible(true); // Rompemos la privacidad para poder LEER la variable
        return campo.get(objetivo);
    }

    private void inyectarCampoPrivado(Object objetivo, String nombreCampo, Object valor) throws Exception {
        Field campo = objetivo.getClass().getDeclaredField(nombreCampo);
        campo.setAccessible(true); // Rompemos la privacidad para poder SOBRESCRIBIR la variable
        campo.set(objetivo, valor); // Le metemos nuestros Mocks a la fuerza
    }
}
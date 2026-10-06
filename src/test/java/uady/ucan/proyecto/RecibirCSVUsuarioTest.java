package uady.ucan.proyecto;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static uady.ucan.proyecto.Alerta.setAlert;

class RecibirCSVUsuarioTest {

    private static final String NOMBRE_CSV_PRUEBA = "csv_temporal_de_prueba";
    private Path rutaArchivoPrueba;
    private RecibirCSVUsuario recibir;
    private RepositorioAlumnos alumnos;
    private TextField campoNombreCsv;
    private Label labelMostrar;
    private Label labelRuta;

    // Enciende el motor gráfico de JavaFX una sola vez para toda la clase.
    // JavaFX exige el toolkit encendido para usar TextField/Alert, incluso
    // si son falsos. Si otra clase de prueba ya lo encendió, el catch lo ignora.
    @BeforeAll
    static void iniciarJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    // Crea un CSV temporal real con los alumnos válidos antes de cada prueba
    // en la ruta fija de producción, e inicializa e inyecta los mocks gráficos.
    @BeforeEach
    void prepararEntornoGraficoYArchivo() throws Exception {
        // 1. Crear el CSV temporal con los DOS alumnos de la prueba original
        rutaArchivoPrueba = Path.of("src/main/resources/" + NOMBRE_CSV_PRUEBA + ".csv");
        String contenido = String.join("\n",
                "Matricula,Primer Apellido,Segundo Apellido,Nombres",
                "A001,Perez,Lopez,Juan",
                "A002,Canul,Chan,Maria"
        );
        Files.writeString(rutaArchivoPrueba, contenido);
        alumnos = new RepositorioAlumnos();
        recibir = new RecibirCSVUsuario();
        campoNombreCsv = new TextField();
        labelMostrar = new Label();
        labelRuta = new Label();

        // 3. Inyectar los componentes visuales al controlador
        inyectarCampoPrivado(recibir, "csvUsuario", campoNombreCsv);
        inyectarCampoPrivado(recibir, "mostrarCsvUsuario", labelMostrar);
        inyectarCampoPrivado(recibir, "rutaUsuario", labelRuta);
    }

    // Borra el CSV temporal del disco y resetea la lista estática de alumnos,
    // para garantizar que esta prueba no afecte el estado de ninguna otra.
    @AfterEach
    void limpiarEstado() throws IOException {
        Files.deleteIfExists(rutaArchivoPrueba);
        alumnos.setAlumnos(null);
    }

    /*
    Prueba para Inline Method
    Objetivo: Confirmar que buscarCsvUsuario() sigue leyendo y guardando
    correctamente todos los datos de cada renglón del CSV.
    Resultado esperado: Se cargan los 2 alumnos, con cada campo exacto extraído del archivo temporal.
    Datos: CSV temporal válido con encabezado y dos renglones de alumnos.
     */
    @Test
    void buscarCsvUsuarioCargaLosAlumnosExactamenteConSusDatos() {
        campoNombreCsv.setText(NOMBRE_CSV_PRUEBA);

        recibir.buscarCsvUsuario();

        ArrayList<Alumno> alumnosCargados = alumnos.getAlumnos();
        assertNotNull(alumnosCargados);
        assertEquals(2, alumnosCargados.size());

        assertEquals("A001", alumnosCargados.get(0).getMatricula());
        assertEquals("Perez", alumnosCargados.get(0).getAppellido1());
        assertEquals("Lopez", alumnosCargados.get(0).getAppellido2());
        assertEquals("Juan", alumnosCargados.get(0).getNombres());

        assertEquals("A002", alumnosCargados.get(1).getMatricula());
        assertEquals("Canul", alumnosCargados.get(1).getAppellido1());
        assertEquals("Chan", alumnosCargados.get(1).getAppellido2());
        assertEquals("Maria", alumnosCargados.get(1).getNombres());
    }

    /*
    Prueba para Extract Class
    Objetivo: Confirmar que al leer correctamente un archivo, la actualización del frontend es exitosa.
    Resultado esperado: El label mostrarCsvUsuario toma el nombre del archivo, se pinta de Color.GREEN, y se muestra la ruta absoluta.
    Datos: CSV temporal válido con encabezado y dos renglones de alumnos.
     */
    @Test
    void buscarCsvExitosoActualizaFrontendConColorVerdeYRutaAbsoluta() {
        campoNombreCsv.setText(NOMBRE_CSV_PRUEBA);

        recibir.buscarCsvUsuario();

        assertEquals(NOMBRE_CSV_PRUEBA, labelMostrar.getText(), "El label debe mostrar el nombre del archivo");
        assertEquals(Color.GREEN, labelMostrar.getTextFill(), "El texto debe pintarse de verde");

        String rutaAbsolutaEsperada = new File(rutaArchivoPrueba.toString()).getAbsolutePath();
        assertEquals(rutaAbsolutaEsperada, labelRuta.getText(), "El label debe mostrar la ruta absoluta");
    }

    /*
    Prueba para Extract Class
    Objetivo: Confirmar que si el archivo no existe, no se contamina la lógica de datos y el error se notifica gráficamente.
    Resultado esperado: La lista global se asigna como nula, el label indica "No seleccionado" en Color.RED, la ruta queda vacía y se lanza WARNING.
    Datos: Un string en el TextField simulando un archivo que no existe ("archivoNoExiste").
     */
    @Test
    void buscarCsvFallidoActualizaFrontendConColorRojoYLanzaAlerta() {
        campoNombreCsv.setText("archivoNoExiste");

        try (MockedStatic<Alerta> alertaSimulada = mockStatic(Alerta.class)) {

            recibir.buscarCsvUsuario();

            assertNull(alumnos.getAlumnos(), "La lista de alumnos global debe quedar nula en caso de error");

            assertEquals("No seleccionado", labelMostrar.getText(), "El label debe indicar que no hay selección");
            assertEquals(Color.RED, labelMostrar.getTextFill(), "El texto debe pintarse de rojo");
            assertEquals("", labelRuta.getText(), "La ruta no debe actualizarse");

            alertaSimulada.verify(() -> setAlert(eq(Alert.AlertType.WARNING), anyString())
            );
        }
    }

    // Usa reflexión para meter un valor directamente en un campo privado
    private void inyectarCampoPrivado(Object objetivo, String nombreCampo, Object valor) throws Exception {
        Field campo = objetivo.getClass().getDeclaredField(nombreCampo);
        campo.setAccessible(true);
        campo.set(objetivo, valor);
    }
}


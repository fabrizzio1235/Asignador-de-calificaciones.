/* package uady.ucan.proyecto;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RecibirCSVUsuarioTest {

    private static final String NOMBRE_CSV_PRUEBA = "csv_temporal_de_prueba";
    private Path rutaArchivoPrueba;

    // Enciende el motor gráfico de JavaFX una sola vez para toda la clase.
    // JavaFX exige el toolkit encendido para usar TextField/Alert, incluso
    // si son falsos. Si otra clase de prueba ya lo encendió, el catch lo ignora.
    @BeforeAll
    static void iniciarJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    // Crea un CSV temporal real con dos alumnos válidos antes de cada prueba,
    // en la misma ruta fija que usa el código de producción.
    @BeforeEach
    void crearCsvTemporalYLimpiarEstado() throws IOException {
        rutaArchivoPrueba = Path.of("src/main/resources/" + NOMBRE_CSV_PRUEBA + ".csv");
        String contenido = String.join("\n",
                "Matricula,Primer Apellido,Segundo Apellido,Nombres",
                "A001,Perez,Lopez,Juan",
                "A002,Canul,Chan,Maria"
        );
        Files.writeString(rutaArchivoPrueba, contenido);
    }

    // Borra el CSV temporal del disco y resetea la lista estática de alumnos,
    // para que esta prueba no afecte a ninguna otra.
    @AfterEach
    void borrarCsvTemporalYLimpiarEstado() throws IOException {
        Files.deleteIfExists(rutaArchivoPrueba);
        new RecibirCSVUsuario().setAlumnos(null);
    }

    /*
    Prueba para Inline Method
    Objetivo: Confirmar que, tras eliminar la variable intermedia "alumnito" (se agrega
    el Alumno directo a la lista), buscarCsvUsuario() sigue leyendo y guardando
    correctamente todos los datos de cada renglón del CSV.
    Resultado esperado: se cargan los 2 alumnos, con cada campo exacto, y se actualiza
    correctamente el texto de la interfaz (mostrarCsvUsuario).
    Datos: CSV temporal válido con encabezado con dos renglones de alumnos.

    @Test
    void buscarCsvUsuarioCargaLosAlumnosCuandoElArchivoExisteYEsValido() throws Exception {
        RecibirCSVUsuario recibir = new RecibirCSVUsuario();

        TextField campoNombreCsv = new TextField(NOMBRE_CSV_PRUEBA);
        Label labelMostrar = new Label();
        Label labelRuta = new Label();

        inyectarCampoPrivado(recibir, "csvUsuario", campoNombreCsv);
        inyectarCampoPrivado(recibir, "mostrarCsvUsuario", labelMostrar);
        inyectarCampoPrivado(recibir, "rutaUsuario", labelRuta);

        recibir.buscarCsvUsuario();

        ArrayList<Alumno> alumnosCargados = recibir.getAlumnos();

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
        assertEquals(NOMBRE_CSV_PRUEBA, labelMostrar.getText());
    }

    // Usa reflexión para meter un valor directamente en un campo privado
    private void inyectarCampoPrivado(Object objetivo, String nombreCampo, Object valor) throws Exception {
        Field campo = objetivo.getClass().getDeclaredField(nombreCampo);
        campo.setAccessible(true);
        campo.set(objetivo, valor);
    }
} */
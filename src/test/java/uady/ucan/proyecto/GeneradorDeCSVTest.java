package uady.ucan.proyecto;

import javafx.scene.control.Alert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class GeneradorDeCSVTest {

    private static final String NOMBRE_CSV_PRUEBA = "csv_temporal_de_prueba";
    private GeneradorDeCSV generador;

    @BeforeEach
    void limpiarEstadoCompartido() throws IOException {
        generador = new GeneradorDeCSV();
        generador.setAlumnos(null);
        Files.createDirectories(Path.of("output"));
    }

    @AfterEach
    void limpiarArchivosGenerados() throws IOException {
        Files.deleteIfExists(Path.of("output/" + NOMBRE_CSV_PRUEBA + ".csv"));
        Files.deleteIfExists(Path.of("output/Calificaciones.csv"));
        generador.setAlumnos(null);
    }

    // ==========================================
    // PRUEBAS PARA generarCsv() (Remove Assignments to Parameters)
    // ==========================================

    @Test
    void generarCsvOrdenaPorCalificacionAscendenteYEscribeElArchivo() throws IOException {
        ArrayList<Alumno> alumnos = new ArrayList<>();
        Alumno alto = new Alumno("A001", "Perez", "Lopez", "Juan");
        alto.setCalificacion(95);
        Alumno bajo = new Alumno("A002", "Canul", "Chan", "Maria");
        bajo.setCalificacion(60);
        alumnos.add(alto);
        alumnos.add(bajo);
        generador.setAlumnos(alumnos);

        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {
            generador.generarCsv(NOMBRE_CSV_PRUEBA);

            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.CONFIRMATION), anyString())
            );
        }

        List<String> lineas = Files.readAllLines(Path.of("output/" + NOMBRE_CSV_PRUEBA + ".csv"));
        assertEquals("Matricula,Primer Apellido,Segundo Apellido,Nombres,Calificacion", lineas.get(0));
        assertTrue(lineas.get(1).startsWith("A002"), "A002 (60) debe ser primero");
    }

    @Test
    void generarCsvNoEscribeNadaSiQuedanAlumnosSinCalificar() {
        ArrayList<Alumno> alumnos = new ArrayList<>();
        Alumno a1 = new Alumno("A001", "Perez", "Lopez", "Juan");
        a1.setCalificacion(90);
        Alumno sinCalificar = new Alumno("A002", "Canul", "Chan", "Maria");
        alumnos.add(a1);
        alumnos.add(sinCalificar);
        generador.setAlumnos(alumnos);

        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {
            generador.generarCsv(NOMBRE_CSV_PRUEBA);

            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.WARNING), anyString())
            );
        }

        assertFalse(Files.exists(Path.of("output/" + NOMBRE_CSV_PRUEBA + ".csv")));
    }

    @Test
    void generarCsvUsaNombrePorDefectoCuandoElNombreEstaVacio() {
        ArrayList<Alumno> alumnos = new ArrayList<>();
        Alumno a1 = new Alumno("A001", "Perez", "Lopez", "Juan");
        a1.setCalificacion(100);
        alumnos.add(a1);
        generador.setAlumnos(alumnos);

        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {
            generador.generarCsv("   ");

            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.CONFIRMATION), anyString())
            );
        }

        assertTrue(Files.exists(Path.of("output/Calificaciones.csv")));
    }
}
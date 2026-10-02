package uady.ucan.proyecto;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class GeneradorReportePDFTest {

    private static final String NOMBRE_REPORTE = "reporte_temporal_de_prueba";
    private Path rutaEsperada;

    @BeforeEach
    void prepararAlumnosYCarpetaSalida() throws IOException {
        // El método escribe en "output/", nos aseguramos de que exista
        Files.createDirectories(Path.of("output"));
        rutaEsperada = Path.of("output/" + NOMBRE_REPORTE + ".pdf");
        Files.deleteIfExists(rutaEsperada);
        //CASO DE PRUEBA DOS ALUMNOS (UNO SIN CALIFICAR)
        ArrayList<Alumno> alumnos = new ArrayList<>();
        Alumno a1 = new Alumno("A001", "Perez", "Lopez", "Juan");
        a1.setCalificacion(95);
        Alumno a2 = new Alumno("A002", "Canul", "Chan", "Maria"); // sin calificar -> debe salir "S/C"
        alumnos.add(a1);
        alumnos.add(a2);

        new GeneradorReportePDF().setAlumnos(alumnos);
    }

    @AfterEach
    void limpiar() throws IOException {
        Files.deleteIfExists(rutaEsperada);
        new GeneradorReportePDF().setAlumnos(null); // "alumnos" es static, reseteamos
    }
    //PRUEBA PARA REFACTORIZAR EL EXTRACT METHOD DE GeneradorReportePDF

    @Test
    void generarPdfCreaElArchivoYNotificaExitoConCONFIRMATION() {
        GeneradorReportePDF generador = new GeneradorReportePDF();

        // Mientras este bloque esté activo, CUALQUIER llamada a un método
        // estático de ControladorVentanas (como setAlert) no hace nada real,
        // solo queda registrada para que la podamos verificar.
        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {

            generador.generarPdf(NOMBRE_REPORTE);

            // Comprobamos que SÍ se intentó avisar del éxito con el tipo correcto
            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.CONFIRMATION), anyString())
            );
        }

        // Y comprobamos que el PDF de verdad se haya creado en disco
        assertTrue(Files.exists(rutaEsperada), "El PDF debería existir en " + rutaEsperada);
    }
    //PRUEBA PARA REFACTORIZAR EL EXTRACT METHOD DE GeneradorReportePDF

    @Test
    void generarPdfUsaNombrePorDefectoCuandoElNombreEstaVacio() {
        Path rutaPorDefecto = Path.of("output/Reporte_Calificaciones.pdf");
        try {
            GeneradorReportePDF generador = new GeneradorReportePDF();

            try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {
                generador.generarPdf("   "); // nombre en blanco

                alertaSimulada.verify(() ->
                        ControladorVentanas.setAlert(eq(Alert.AlertType.CONFIRMATION), anyString())
                );
            }

            assertTrue(Files.exists(rutaPorDefecto),
                    "Con nombre vacío debe usar 'Reporte_Calificaciones' por defecto");
        } finally {
            try {
                Files.deleteIfExists(rutaPorDefecto);
            } catch (IOException ignored) {
            }
        }
    }

    // =========================================================================================
    // PRUEBA PARA CERRAR EL CASO 4 (Introduce Explaining Variable)
    // El refactor extrae "alumnitos.get(i)" a una variable dentro del for que llena la tabla.
    // Esta prueba abre el PDF ya generado y lee su texto real, para asegurarnos de que cada
    // celda (matrícula, nombre completo, calificación o "S/C") siga quedando exactamente igual
    // después de ese cambio.
    // =========================================================================================

    @Test
    void generarPdfEscribeLosDatosCorrectosEnLaTablaIncluyendoSC() throws IOException {
        GeneradorReportePDF generador = new GeneradorReportePDF();

        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {
            generador.generarPdf(NOMBRE_REPORTE);

            // Aprovechamos este mismo bloque para confirmar que también
            // se disparó la alerta de éxito, no solo silenciar el aviso del IDE.
            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.CONFIRMATION), anyString())
            );
        }

        String textoPdf = extraerTextoDePdf(rutaEsperada.toString());

        // Alumno CON calificación (95)
        assertTrue(textoPdf.contains("A001"), "Debe contener la matrícula A001");
        assertTrue(textoPdf.contains("Perez Lopez Juan"), "Debe contener el nombre completo de A001");
        assertTrue(textoPdf.contains("95"), "Debe mostrar la calificación 95 de A001");

        // Alumno SIN calificación -> debe mostrar "S/C", nunca el -1 interno
        assertTrue(textoPdf.contains("A002"), "Debe contener la matrícula A002");
        assertTrue(textoPdf.contains("Canul Chan Maria"), "Debe contener el nombre completo de A002");
        assertTrue(textoPdf.contains("S/C"), "El alumno sin calificar debe mostrar S/C");
        assertFalse(textoPdf.contains("-1"), "Nunca debe filtrarse el -1 interno hacia el PDF");
    }

    private String extraerTextoDePdf(String rutaArchivo) throws IOException {
        PdfReader lector = new PdfReader(rutaArchivo);
        try {
            // En esta versión de OpenPDF, PdfTextExtractor se instancia con el
            // PdfReader (no es una clase 100% estática como en otras versiones/forks).
            PdfTextExtractor extractor = new PdfTextExtractor(lector);
            StringBuilder texto = new StringBuilder();
            for (int pagina = 1; pagina <= lector.getNumberOfPages(); pagina++) {
                texto.append(extractor.getTextFromPage(pagina, false));
            }
            return texto.toString();
        } finally {
            lector.close();
        }
    }
}
/* package uady.ucan.proyecto;

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

    // Prepara dos alumnos de prueba (uno calificado, uno sin calificar) y
    // asegura que exista la carpeta "output/" antes de cada prueba.
    @BeforeEach
    void prepararAlumnosYCarpetaSalida() throws IOException {
        Files.createDirectories(Path.of("output"));
        rutaEsperada = Path.of("output/" + NOMBRE_REPORTE + ".pdf");
        Files.deleteIfExists(rutaEsperada);
        ArrayList<Alumno> alumnos = new ArrayList<>();
        Alumno a1 = new Alumno("A001", "Perez", "Lopez", "Juan");
        a1.setCalificacion(95);
        Alumno a2 = new Alumno("A002", "Canul", "Chan", "Maria"); // sin calificar -> debe salir "S/C"
        alumnos.add(a1);
        alumnos.add(a2);

        new GeneradorReportePDF().setAlumnos(alumnos);
    }

    // Borra el PDF generado y resetea la lista estática de alumnos después
    // de cada prueba.
    @AfterEach
    void limpiar() throws IOException {
        Files.deleteIfExists(rutaEsperada);
        new GeneradorReportePDF().setAlumnos(null);
    }

    /*
    Prueba para el Extract Method
    Objetivo: Confirmar que, tras dividir generarPdf() en crearTitulo(), crearSubtitulo()
    y crearTabla(), el método completo sigue produciendo el PDF y notificando el éxito
    exactamente igual que antes de extraer esos métodos.
    Resultado esperado: el archivo PDF existe en disco y se dispara CONFIRMATION.
    Datos: dos alumnos de prueba, nombre de archivo normal (NOMBRE_REPORTE).

    @Test
    void generarPdfCreaElArchivoYNotificaExitoConCONFIRMATION() {
        GeneradorReportePDF generador = new GeneradorReportePDF();

        try (MockedStatic<ControladorVentanas> alertaSimulada = mockStatic(ControladorVentanas.class)) {

            generador.generarPdf(NOMBRE_REPORTE);

            // Comprobamos que SÍ se intentó avisar del éxito con el tipo correcto
            alertaSimulada.verify(() ->
                    ControladorVentanas.setAlert(eq(Alert.AlertType.CONFIRMATION), anyString())
            );
        }

        assertTrue(Files.exists(rutaEsperada), "El PDF debería existir en " + rutaEsperada);
    }

    /*
    Técnica: Remove Assignments to Parameters
    Objetivo: Verificar específicamente la reasignación directa del parámetro pasado para el método generarPDF()
    por la variable nueva "nombreFinal", cuando el nombre llega vacío o en blanco.
    Resultado esperado: se crea el archivo con el nombre por defecto "Reporte_Calificaciones.pdf".
    Datos: dos alumnos de prueba, nombre de archivo " " (solo espacios).

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

    /*
    Pruebas para el Introduce Explaining Variable + Extract Method
    Objetivo: Esta prueba cubre DOS técnicas sobre el mismo método porque ambas tocan
    exactamente el mismo bloque de código (el 'for' que llenaba la tabla):
      - "alumnitos.get(i)" repetido 4 veces
      - generarPDF() Hace muchas cosas
    Resultado esperado: el texto extraído del PDF contiene la matrícula, el nombre
    completo y la calificación (o "S/C") de cada alumno, y nunca el -1 interno.
    Datos: dos alumnos de prueba (uno calificado con 95, uno sin calificar).

    @Test
    void generarPdfEscribeLosDatosCorrectosEnLaTablaIncluyendoLosSinCalificar() throws IOException {
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

    // Abre el PDF ya generado en disco y extrae su texto real, página por página,
    // para poder comparar el contenido contra lo que se esperaba.
    private String extraerTextoDePdf(String rutaArchivo) throws IOException {
        PdfReader lector = new PdfReader(rutaArchivo);
        try {

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
} */
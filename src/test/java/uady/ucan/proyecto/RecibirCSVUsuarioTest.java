    package uady.ucan.proyecto;

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

        @BeforeAll
        static void iniciarJavaFX() {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException e) {
                // Toolkit ya inicializado
            }
        }

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

        @AfterEach
        void borrarCsvTemporalYLimpiarEstado() throws IOException {
            Files.deleteIfExists(rutaArchivoPrueba);
            new RecibirCSVUsuario().setAlumnos(null); // Asegura que la prueba no afecte a otra[cite: 16]
        }

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

        private void inyectarCampoPrivado(Object objetivo, String nombreCampo, Object valor) throws Exception {
            Field campo = objetivo.getClass().getDeclaredField(nombreCampo);
            campo.setAccessible(true);
            campo.set(objetivo, valor);
        }
    }
package uady.ucan.proyecto;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uady.ucan.proyecto.Alerta.setAlert;

class AsignadorDeCalificacionesTest {

    private AsignadorDeCalificaciones asignador;
    private TableView<Alumno> tablaMock;
    private TableColumn<Alumno, String> calificacionMock;
    private EventHandler<TableColumn.CellEditEvent<Alumno, String>> handlerCapturado;
    private RepositorioAlumnos alumnos;

    // Enciende el motor gráfico de JavaFX una sola vez para toda la clase,
    // necesario para inicializar el controlador y los mocks de TableView.
    @BeforeAll
    static void initJFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
        }
    }

    // Prepara un controlador limpio antes de cada prueba, inyecta los mocks
    // de las columnas y la tabla, y captura el EventHandler lambda anónimo
    // para poder dispararlo manualmente con nuestros propios datos de prueba.
    @BeforeEach
    void prepararControladorYCapturarEvento() throws Exception {
        asignador = new AsignadorDeCalificaciones();
        alumnos = new RepositorioAlumnos();
        alumnos.setAlumnos(null);

        tablaMock = mock(TableView.class);
        TableColumn<Alumno, String> matriculaMock = mock(TableColumn.class);
        TableColumn<Alumno, String> alumnoMock = mock(TableColumn.class);
        calificacionMock = mock(TableColumn.class);

        inyectarCampoPrivado(asignador, "tablaAlumnos", tablaMock);
        inyectarCampoPrivado(asignador, "matricula", matriculaMock);
        inyectarCampoPrivado(asignador, "alumno", alumnoMock);
        inyectarCampoPrivado(asignador, "calificacion", calificacionMock);

        asignador.initialize(null, null);

        ArgumentCaptor<EventHandler<TableColumn.CellEditEvent<Alumno, String>>>
                captor = ArgumentCaptor.forClass(EventHandler.class);
        verify(calificacionMock).setOnEditCommit(captor.capture());
        handlerCapturado = captor.getValue();
    }

    /*
    Prueba para Move Method
    Objetivo: Confirmar que al ingresar una calificación válida (entre 0 y 100), el valor se procesa
    y se asigna correctamente al alumno.
    Resultado esperado: La calificación del alumno se actualiza al valor ingresado (85),
    NUNCA se dispara ninguna alerta, y la tabla NUNCA se refresca.
    Datos: Un objeto Alumno de prueba válido y una entrada de texto numérica válida ("85").
     */
    @Test
    void editarCalificacionConValorValidoActualizaAlAlumno() {
        Alumno alumnoPrueba = new Alumno("A001", "Perez", "Lopez", "Juan");
        TableColumn.CellEditEvent<Alumno, String> eventoMock = mock(TableColumn.CellEditEvent.class);

        when(eventoMock.getRowValue()).thenReturn(alumnoPrueba);
        when(eventoMock.getNewValue()).thenReturn("85");

        try (MockedStatic<Alerta> alertaSimulada = mockStatic(Alerta.class)) {
            handlerCapturado.handle(eventoMock);

            assertEquals(85, alumnoPrueba.getCalificacion());

            // Camino válido: ni la alerta ni el refresh deben dispararse.
            alertaSimulada.verify(() -> setAlert(any(), anyString()), never());
            verify(tablaMock, never()).refresh();
        }
    }

    /*
    Prueba para Move Method
    Objetivo: Confirmar que si se ingresa una calificación fuera de los límites permitidos,
    la asignación es rechazada para no corromper la información.
    Resultado esperado: La calificación del alumno se mantiene intacta en -1 (por default),
    SIEMPRE se dispara una alerta WARNING, y SIEMPRE se refresca la tabla para borrar
    el valor inválido que quedó visible en la celda.
    Datos: Un objeto Alumno de prueba y una entrada numérica fuera de rango ("105").
    */
    @Test
    void editarCalificacionConValorFueraDeRangoLanzaAlertaYNoModificaAlumno() {
        Alumno alumnoPrueba = new Alumno("A001", "Perez", "Lopez", "Juan");
        TableColumn.CellEditEvent<Alumno, String> eventoMock = mock(TableColumn.CellEditEvent.class);

        when(eventoMock.getRowValue()).thenReturn(alumnoPrueba);
        when(eventoMock.getNewValue()).thenReturn("105"); // Entrada inválida (fuera de rango)

        try (MockedStatic<Alerta> alertaSimulada = mockStatic(Alerta.class)) {
            handlerCapturado.handle(eventoMock);

            assertEquals(-1, alumnoPrueba.getCalificacion(), "La calificación original no debió cambiar");
            alertaSimulada.verify(() -> setAlert(eq(Alert.AlertType.WARNING), anyString()));
            verify(tablaMock).refresh();
        }
    }

    /*
    Prueba para Move Method
    Objetivo: Confirmar que se captura el NumberFormatException de manera segura si se envían
    caracteres en lugar de números.
    Resultado esperado: La calificación del alumno se mantiene en -1 (por default),
    se dispara una alerta WARNING y la tabla se refresca.
    Datos: Un objeto Alumno de prueba y una entrada de texto no numérica ("reprobadooo!").
     */
    @Test
    void editarCalificacionConTextoNoNumericoLanzaAlertaYNoModificaAlumno() {
        Alumno alumnoPrueba = new Alumno("A001", "Perez", "Lopez", "Juan");
        TableColumn.CellEditEvent<Alumno, String> eventoMock = mock(TableColumn.CellEditEvent.class);

        when(eventoMock.getRowValue()).thenReturn(alumnoPrueba);
        when(eventoMock.getNewValue()).thenReturn("reprobadooo!"); // Entrada inválida (no numérica)

        try (MockedStatic<Alerta> alertaSimulada = mockStatic(Alerta.class)) {
            handlerCapturado.handle(eventoMock);

            assertEquals(-1, alumnoPrueba.getCalificacion(), "La calificación original no debió cambiar");
            alertaSimulada.verify(() -> setAlert(eq(Alert.AlertType.WARNING), anyString()));
            verify(tablaMock).refresh();
        }
    }

    // Método utilitario para reflexión
    private void inyectarCampoPrivado(Object objetivo, String nombreCampo, Object valor) throws Exception {
        Field campo = objetivo.getClass().getDeclaredField(nombreCampo);
        campo.setAccessible(true);
        campo.set(objetivo, valor);
    }
}
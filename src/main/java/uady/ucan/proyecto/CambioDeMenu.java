package uady.ucan.proyecto;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import static uady.ucan.proyecto.Alerta.setAlert;


public class CambioDeMenu extends ControladorVentanas {
    @FXML
    private TextField csvNombre;
    @FXML
    private TextField pdfNombre;
    @FXML
    private Button botonSalir;

    // Se instancia RepositorioAlumnos desde aquí para que todas las clases hijas tengan acceso a ella.
    protected RepositorioAlumnos repositorioAlumnos = new RepositorioAlumnos();

    @FXML
    public void asignarCalificacion () {
        abrirVentana(CALIFICACIONES_VIEW_FXML, "Gestion de Calificaciones");
    }

    @FXML
    public void recibirCSV () {
        abrirVentana(CSV_USUARIO_VIEW_FXML, "Busqueda de CSV");
    }

    @FXML
    public void generar () {
        if(repositorioAlumnos.getAlumnos() == null){
            setAlert(Alert.AlertType.WARNING, "Asegurarse de escoger el archivo CSV");
            return;
        }
        abrirVentana(GENERAR_VIEW_FXML, "Generar CSV/PDF");

    }
    @FXML
    public void menu (){
        abrirVentana(MENU_VIEW_FXML, "Menu Principal");
    }

    @FXML
    public void login() {
        abrirVentana(LOGIN_VIEW_FXML, "Inicio de Sesión");
    }

    public void cerrarSesion () {
        botonSalir.getScene().getWindow().hide();
        login();
    }

    @FXML
    public void salir () {
        Platform.exit();
    }

    @FXML
    public void generarCSV () {
        GeneradorDeCSV csv = new GeneradorDeCSV();
        csv.generarCsv(csvNombre.getText());
    }
    @FXML
    public void generarPDF() {
        GeneradorReportePDF pdf = new GeneradorReportePDF();
        pdf.generarPdf(pdfNombre.getText());
    }
}

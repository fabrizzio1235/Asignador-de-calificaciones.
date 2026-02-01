package uady.ucan.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


public class CambioDeMenu extends ControladorVentanas{

    @FXML
    private TextField csvNombre;
    @FXML
    private TextField pdfNombre;

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
        if(getAlumnos() == null){
            setAlert(Alert.AlertType.WARNING, "Asegurarse de escoger el archivo CSV");
            return;
        }
        abrirVentana(GENERAR_VIEW_FXML, "Generar CSV/PDF");

    }
    @FXML
    public void menu (){
        abrirVentana(MENU_VIEW_FXML, "Menu");
    }
    public void cerrarSesion () {
        //TODO
    }
    @FXML
    public void salir () {
        //TODO
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

package uady.ucan.proyecto;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class ControladorVentanas {

    private static ArrayList <Alumno> alumnos = null;

    public static final String VIEWS_DIRECTORY = "/uady/ucan/proyecto/Views/";
    public static final String LOGIN_VIEW_FXML = VIEWS_DIRECTORY + "Login.fxml";
    public static final String MENU_VIEW_FXML = VIEWS_DIRECTORY + "Menu.fxml";
    public static final String GENERAR_VIEW_FXML = VIEWS_DIRECTORY + "Generar.fxml";
    public static final String CALIFICACIONES_VIEW_FXML = VIEWS_DIRECTORY + "Calificaciones.fxml";
    public static final String CSV_USUARIO_VIEW_FXML = VIEWS_DIRECTORY + "CSVUsuario.fxml";


    static Alert defaultAlert;
    static ButtonType acceptButton = new ButtonType("Aceptar");


    public void abrirVentana(String fxmlFileName, String title) {
        try {
            // Abrir una nueva ventana (core del sistema)
            FXMLLoader fxmlLoader = new FXMLLoader(ControladorVentanas.class.getResource(fxmlFileName));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);


            stage.show();
            stage.centerOnScreen();

        } catch (IOException | NullPointerException e) {
            setAlert(Alert.AlertType.WARNING, "Error al cargar la vista "+ e.getMessage());
        }
    }


    static public void setAlert(Alert.AlertType alertType,String argument){
        defaultAlert = new Alert(alertType);
        defaultAlert.setTitle("Información");
        defaultAlert.setHeaderText(null);
        defaultAlert.getButtonTypes().setAll(acceptButton);
        defaultAlert.setContentText(argument);
        defaultAlert.showAndWait();
    }

    public ArrayList<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(ArrayList<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

}

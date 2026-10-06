package uady.ucan.proyecto;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

import static uady.ucan.proyecto.Alerta.setAlert;

public class ControladorVentanas {

    public static final String VIEWS_DIRECTORY = "/uady/ucan/proyecto/Views/";
    public static final String LOGIN_VIEW_FXML = VIEWS_DIRECTORY + "Login.fxml";
    public static final String MENU_VIEW_FXML = VIEWS_DIRECTORY + "Menu.fxml";
    public static final String GENERAR_VIEW_FXML = VIEWS_DIRECTORY + "Generar.fxml";
    public static final String CALIFICACIONES_VIEW_FXML = VIEWS_DIRECTORY + "Calificaciones.fxml";
    public static final String CSV_USUARIO_VIEW_FXML = VIEWS_DIRECTORY + "CSVUsuario.fxml";

    public void abrirVentana(String fxmlFileName, String title) {
        try {
            Stage st = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(ControladorVentanas.class.getResource(fxmlFileName));
            Scene scene = new Scene(fxmlLoader.load());

            st.setTitle(title);
            st.setScene(scene);


            st.sizeToScene();
            st.centerOnScreen();

            st.show();

        } catch (IOException | NullPointerException e) {
            setAlert(Alert.AlertType.WARNING, "Error al cargar la vista " + e.getMessage());
        }
    }
}

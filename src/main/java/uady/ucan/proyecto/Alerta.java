package uady.ucan.proyecto;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Alerta {
    static Alert defaultAlert;
    static ButtonType acceptButton = new ButtonType("Aceptar");

    static public void setAlert(Alert.AlertType alertType, String argument) {
        defaultAlert = new Alert(alertType);
        defaultAlert.setTitle("Información");
        defaultAlert.setHeaderText(null);
        defaultAlert.getButtonTypes().setAll(acceptButton);
        defaultAlert.setContentText(argument);
        defaultAlert.showAndWait();
    }
}

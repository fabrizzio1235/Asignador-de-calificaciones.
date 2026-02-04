package uady.ucan.proyecto;

import javafx.application.Application;
import javafx.stage.Stage;

import static uady.ucan.proyecto.ControladorVentanas.*;


public class Main extends Application {
    ControladorVentanas cv = new ControladorVentanas();

    @Override
    public void start(Stage stage) {
        cv.abrirVentana(LOGIN_VIEW_FXML,"Iniciar Sesión"); //
    }

    public static void main(String[] args) {
        launch(args);
    }

}


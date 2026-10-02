package uady.ucan.proyecto;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;



public class AsignadorDeCalificaciones extends CambioDeMenu implements Initializable {
    @FXML private TableView<Alumno> tablaAlumnos;
    @FXML private TableColumn<Alumno, String> matricula;
    @FXML private TableColumn<Alumno, String> alumno;
    @FXML private TableColumn<Alumno, String> calificacion;

    private ObservableList<Alumno> alumnos = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<Alumno> listaPadre = getAlumnos();

        if (listaPadre != null) {
            alumnos = FXCollections.observableArrayList(listaPadre);
            tablaAlumnos.setItems(alumnos);
        }
        matricula.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMatricula()));
        alumno.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreCompleto()));
        calificacion.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCalificacion())));

        tablaAlumnos.setEditable(true);
        calificacion.setCellFactory(TextFieldTableCell.forTableColumn());
        calificacion.setOnEditCommit(event -> {
            Alumno alumnoEditado = event.getRowValue();

            try {
                int nuevaCal = Integer.parseInt(event.getNewValue()); //no recibe decimales
                if (nuevaCal >= 0 && nuevaCal <= 100) {
                    alumnoEditado.setCalificacion(nuevaCal);
                } else {
                    setAlert(Alert.AlertType.WARNING, "Calificación invalida --> 0<= calificacion <= 100");
                    tablaAlumnos.refresh();
                }
            } catch (NumberFormatException e) {
                setAlert(Alert.AlertType.WARNING, "Calificación invalida solo acepta enteros --> 0<= calificacion <= 100");
                tablaAlumnos.refresh();
            }
        });
    }

}

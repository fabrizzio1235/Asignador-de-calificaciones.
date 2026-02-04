package uady.ucan.proyecto;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RecibirCSVUsuario extends CambioDeMenu {
    @FXML
    private TextField csvUsuario;
    @FXML
    private Label mostrarCsvUsuario;
    @FXML
    private Label rutaUsuario;

    private ArrayList<Alumno> listaAlumnos;

    @FXML
    public void buscarCsvUsuario() {
        String archivoCalificaciones = "src/main/resources/" + csvUsuario.getText() + ".csv";
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCalificaciones))) {
            String linea;
            br.readLine();
            linea = br.readLine();
            listaAlumnos = new ArrayList<>();
            while (linea != null) {

                String[] renglon = linea.split(",");

                if (renglon.length == 4) {
                    Alumno alumnito = new Alumno(renglon[0], renglon[1], renglon[2], renglon[3]);
                    listaAlumnos.add(alumnito);
                }
                linea = br.readLine();
            }

        } catch (IOException e) {
            setAlumnos(null);
            mostrarCsvUsuario.setText("No seleccionado");
            mostrarCsvUsuario.setTextFill(Color.RED);
            setAlert(Alert.AlertType.WARNING, "Error al buscar archivo: "+ e.getMessage());
            return;

        }
        setAlumnos(listaAlumnos);
        mostrarCsvUsuario.setText(csvUsuario.getText());
        mostrarCsvUsuario.setTextFill(Color.GREEN);
        rutaUsuario.setText(new File(archivoCalificaciones).getAbsolutePath());
    }




}

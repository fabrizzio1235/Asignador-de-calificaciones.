package uady.ucan.proyecto;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import static uady.ucan.proyecto.Alerta.setAlert;

public class GeneradorDeCSV extends CambioDeMenu {


    @FXML
    public void generarCsv(String csvNombre) {
        //Remove Assignments to Parameters - Nuevo
        String nombreFinal = (csvNombre == null || csvNombre.trim().isEmpty()) ? "Calificaciones" : csvNombre;

        if(repositorioAlumnos.todosCalificados()) {
            ArrayList<Alumno> alumnos = repositorioAlumnos.getAlumnos();
            alumnos.sort(Comparator.comparingInt(Alumno::getCalificacion));
            System.out.println("Escriba el nombre que desea para el archivo de salida:");
            String nombreArchivo = "output/"+ nombreFinal + ".csv";

            try (BufferedWriter wr = new BufferedWriter(new FileWriter(nombreArchivo))) {
                wr.write("Matricula,Primer Apellido,Segundo Apellido,Nombres,Calificacion");
                wr.newLine();
                for (Alumno alumnillo : alumnos) {
                    wr.write(alumnillo.toString());
                    wr.newLine();
                }
                setAlert(Alert.AlertType.CONFIRMATION, "Archivo CSV creado con exito en: " + new File(nombreArchivo).getAbsolutePath());
            } catch (IOException e) {
               setAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
            }
        } else {
            setAlert(Alert.AlertType.WARNING, "Quedan alumnos por calificar.");
        }
    }
}
// Construir una clase que se encargue de manejar al ArrayList
// de los Alumnos, y que se comunique con todas las demás. Dicha
// clase tendrá la función todosCalificados.
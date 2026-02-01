package uady.ucan.proyecto;
import javafx.scene.control.Alert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class GeneradorDeCSV  extends CambioDeMenu{

    public void generarCsv(String csvNombre) {
        if(csvNombre == null || csvNombre.trim().isEmpty()){csvNombre ="Calificaciones";}
        if(todosCalificados()) {
            ArrayList<Alumno> alumnos = getAlumnos();
            alumnos.sort(Comparator.comparingInt(Alumno::getCalificacion));
            System.out.println("Escriba el nombre que desea para el archivo de salida:");
            String nombreArchivo = "output/"+ csvNombre + ".csv";

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
            setAlert(Alert.AlertType.WARNING, "Quedan alumnos por calificar");
        }
    }
    public boolean todosCalificados() {
        if(getAlumnos() == null) {return false;}
        for (Alumno a : getAlumnos()) {
            if (a.getCalificacion() == -1) {
                return false;
            }
        }
        return true;
    }
}

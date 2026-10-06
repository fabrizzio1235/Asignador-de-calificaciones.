package uady.ucan.proyecto;

import java.util.ArrayList;

public class RepositorioAlumnos {
    private static ArrayList<Alumno> listaAlumnos;

    public ArrayList<Alumno> getAlumnos() {
        return listaAlumnos;
    }

    public void setAlumnos(ArrayList<Alumno> listaAlumnos) {
        this.listaAlumnos = listaAlumnos;
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

package uady.ucan.proyecto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RepositorioUsuarios {
    private ArrayList<Alumno> listaAlumnos = null;

    private ArrayList<Usuario> listaUsuarios = new ArrayList<>();

    public void leerUsuarios() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/users.csv"))) {
            String linea = br.readLine();

            while (linea != null) {
                String[] dato = linea.split(",");
                //Inline Method - Nuevo
                listaUsuarios.add(new Usuario(dato[0], dato[1]));
                linea = br.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public Usuario buscarUsuario(String nickname) {
        for (Usuario u : listaUsuarios) {
            if (u.getNickname().equals(nickname)) {
                return u;
            }
        }
        return null;
    }
}

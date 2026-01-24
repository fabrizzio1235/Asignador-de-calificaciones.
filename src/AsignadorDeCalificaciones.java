import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Comparator;

public class AsignadorDeCalificaciones {
    private ArrayList<Alumno> listaAlumnos = new ArrayList<>();
    Scanner sc = new Scanner(System.in);

    public boolean leerCalificaciones() {
        System.out.println("Escriba el nombre del archivo de las calificaciones:");
        String archivoCalificaciones = "src/" + sc.nextLine() + ".csv";
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCalificaciones))) {
            String linea;
            br.readLine();
            linea = br.readLine();

            while (linea != null) {
                String[] renglon = linea.split(",");

                if (renglon.length == 4) {
                    Alumno alumnito = new Alumno(renglon[0], renglon[1], renglon[2], renglon[3]);
                    listaAlumnos.add(alumnito);
                }
                linea = br.readLine();
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void asignarCalificacion() {
        System.out.println("====| Asignador de Calificaciones |====");
        int op = 1;
        int cal = -1;
        String matricula;
        do {
            System.out.print("1-.Asignar otra calificación\n2-.Generar CSV\n3-.Salir\nIngerese un número: ");
            if (sc.hasNextInt()) {
                op = sc.nextInt();
                sc.nextLine();
            }

            if (op == 2) {
                generarCsv();
            } else if(op == 1) {
                System.out.print("Ingrese matricula de alumno: ");
                matricula = sc.nextLine();
                System.out.print("Asigne su calificacion: ");
                if (!sc.hasNextInt()) {
                    System.out.println("Error: Debe ingresar un número entero.");
                    sc.nextLine();
                    continue;
                }

                cal = sc.nextInt();
                sc.nextLine();

                if (cal < 0 || cal > 100) {
                    System.out.println("Calificacion invalida, válido del 1 al 100");
                } else {
                    boolean encontrado = false;
                    for (Alumno alumno : listaAlumnos) {
                        if (alumno.getMatricula().equalsIgnoreCase(matricula)) {
                            System.out.print(alumno);
                            alumno.setCalificacion(cal);
                            System.out.println(" --> " + alumno);
                            encontrado = true;
                            break;
                        }
                    }
                    if (!encontrado) {
                        System.out.println(matricula + " no encontrado");
                    }
                }
            } else if (op == 3) {
                System.out.println("Regresando...");
            } else {
                System.out.println("Ingrese una opción válida");
            }
        } while (op != 2 && op != 3);
    }

    public ArrayList todosCalificados() {
        ArrayList<String> sinCalificar = new ArrayList<>();
        for (Alumno a : listaAlumnos) {
            if (a.getCalificacion() == -1) {
                sinCalificar.add(a.getMatricula());
            }
        }
        return sinCalificar;
    }


    public void generarCsv() {
        if(validarCSV()) {
            listaAlumnos.sort(Comparator.comparingInt(Alumno::getCalificacion));
            System.out.println("Escriba el nombre que desea para el archivo de salida:");
            String nombreArchivo = sc.nextLine() + ".csv";

            try (BufferedWriter wr = new BufferedWriter(new FileWriter(nombreArchivo))) {
                wr.write("Matricula,Primer Apellido,Segundo Apellido,Nombres,Calificacion");
                wr.newLine();
                for (Alumno alumnillo : listaAlumnos) {
                    wr.write(alumnillo.toString());
                    wr.newLine();
                }

                System.out.println("Archivo CSV creado con exito en: " + new File(nombreArchivo).getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
    public boolean validarCSV() {
        ArrayList<String> restantes = todosCalificados();
        if(restantes.isEmpty()) {
            System.out.println("Todos los alumnos calificados, generando csv...");
            return true;
        } else {
            System.out.print("Error: Alumnos sin calificacion restantes ->");
            for (String mat : restantes) {
                System.out.print(mat + ",");
            }
            System.out.println();
            return false;
        }
    }

    public ArrayList<Alumno> getListaAlumnos() {
        return listaAlumnos;
    }
}

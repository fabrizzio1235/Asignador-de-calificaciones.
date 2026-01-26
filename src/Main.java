import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Login lg = new Login();
        AsignadorDeCalificaciones ad = new AsignadorDeCalificaciones();
        GeneradorReportePDF reportePDF = new GeneradorReportePDF();
        Scanner sc = new Scanner(System.in);

        int op = 1, op1 = 4;
        do {
            if(op == 0) {
                if(op1 == 4) {if(!ad.leerCalificaciones()){continue;}}
                System.out.print("1-.Asignar calificaciones\n2-.Generar CSV\n3-.Generar Reporte\n4-.Usar otro CSV\n5-.Cerrar sesión\n6-.Salir de la Aplicación\nEscriba una opción: ");

                if (sc.hasNextInt()) {op1 = sc.nextInt();}
                else {op1 = 7;sc.nextLine();}

                switch (op1) {
                    case 1 -> ad.asignarCalificacion();
                    case 2 -> ad.generarCsv();
                    case 3 -> {
                        System.out.println("Generando Reporte en PDF...");
                        reportePDF.generarPDF(ad.getListaAlumnos());
                        System.out.println("Reporte PDF creado con exito en: " + new File("Reporte_De_Calificaciones.pdf").getAbsolutePath());
                    }
                    case 4 -> {}
                    case 5 -> {
                        System.out.println("Saliendo de la sesión...");
                        op = 1;
                    }
                    case 6 -> {}
                    default -> System.out.println("Insertar opción válida 1 al 6");
                }
            } else {
                op = lg.login();
                if(op == 1){
                    op1 = 6;
                }
            }
        } while (op1 != 6 );

        System.out.println("Saliendo de la Aplicación...");
    }
}


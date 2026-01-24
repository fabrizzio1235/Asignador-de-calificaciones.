import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        Login lg = new Login();
        AsignadorDeCalificaciones ad = new AsignadorDeCalificaciones();
        Scanner sc = new Scanner(System.in);

        int op = lg.login(); int op1 = 1;

        if (op == 0) {
            while(!ad.leerCalificaciones()) {
                System.out.println("Salir: 0, Intentar nuevamente: Cualquier num"); //Por mejorar
                op1 = sc.nextInt();
                if(op1==0) {
                    System.out.println("Saliendo de la aplicación...");
                    return;
                }
            }
            ad.asignarCalificacion();
            ad.generarCsv();
        }
    }
}

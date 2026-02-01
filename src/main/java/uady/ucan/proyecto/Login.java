package uady.ucan.proyecto;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Scanner;
import java.lang.StringBuilder;

public class Login {
    ArrayList<Usuario> listaUsuarios = new ArrayList<>();
    Scanner sc = new Scanner(System.in);

    public void leerUsuarios() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/users.csv"))) {
            String linea = br.readLine();

            while (linea != null) {
                String[] dato = linea.split(",");
                Usuario usuarioActual = new Usuario(dato[0], dato[1]);
                listaUsuarios.add(usuarioActual);
                linea = br.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String encriptarContraseña(String password) throws NoSuchAlgorithmException {
        MessageDigest dg = MessageDigest.getInstance("SHA-256");
        byte[] hashEncriptado = dg.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashEncriptado);
    }

    public void crearUsuario() throws NoSuchAlgorithmException {
        System.out.println("Ingresa tu nuevo nombre de usuario:");
        String nuevoNickname = sc.nextLine();
        System.out.println("Ingresa tu nueva contraseña:");
        String nuevaContraseña = sc.nextLine();

        nuevaContraseña = encriptarContraseña(nuevaContraseña);
        Usuario nuevoUsuario = new Usuario(nuevoNickname, nuevaContraseña);
        listaUsuarios.add(nuevoUsuario);

        try (BufferedWriter wr = new BufferedWriter(new FileWriter("src/main/resources/users.csv", true))) {
            wr.newLine();
            wr.write(nuevoNickname+','+nuevaContraseña);
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public int login() throws NoSuchAlgorithmException { // 0: Entrar a la app, 1: Salir
        leerUsuarios();

        while (true) {
            System.out.println("Bienvenido al Editor de Calificaciones. Ingrese usuario y contraseña:");

            System.out.println("Usuario: ");
            String usuarioEntrada = sc.nextLine(); // El nuestro es admin

            boolean usuarioEncontrado = false;
            for (Usuario u: listaUsuarios) {
                if (u.getNickname().equals(usuarioEntrada)) {
                    usuarioEncontrado = true;
                    break;
                }
            }

            if (!usuarioEncontrado) {
                System.out.println("main.java.Usuario inexistente. Desea crear una cuenta nueva?\nSi: 0 \nNo: 1\nSalir: 2");
                int opcionUsuario = sc.nextInt();
                sc.nextLine();

                if (opcionUsuario == 0) {
                    crearUsuario();
                    continue;
                }

                if (opcionUsuario == 2) {
                    return 1;
                }

            }

            else {
                System.out.println("Contraseña: ");
                String contraseñaEntrada = sc.nextLine(); // El nuestro es "prueba", convertido a hash con SHA-256

                for (Usuario u: listaUsuarios) {
                    if (u.getNickname().equals(usuarioEntrada) && u.getPassword().equals(encriptarContraseña(contraseñaEntrada))) {
                        System.out.println("Entrando a la aplicacion...");
                        return 0;
                    }
                }
                System.out.println("Contraseña invalida. Salir?\nSi: 0\nNo: 1");
                int opcionUsuario = sc.nextInt();
                sc.nextLine();

                if (opcionUsuario == 0) {
                    return 1;
                }
            }


        }
    }
}

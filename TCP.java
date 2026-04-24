import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCP {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        Socket socket = null;
        int PUERTO = 9999;

        try {
            System.out.println("1. Crear sala | 2. Unirse");
            int opcion = Integer.parseInt(teclado.nextLine());

            if (opcion == 1) {
                System.out.println("Tu IP local es: " + InetAddress.getLocalHost().getHostAddress());
                ServerSocket serverSocket = new ServerSocket(PUERTO);
                System.out.println("Esperando en puerto " + PUERTO + "...");
                socket = serverSocket.accept();
                System.out.println("¡AMIGO CONECTADO DESDE: " + socket.getInetAddress() + "!");
            } else {
                System.out.print("IP del amigo: ");
                String ip = teclado.nextLine();
                socket = new Socket(ip, PUERTO);
                System.out.println("¡Te has conectado correctamente!");
            }
                
            // --- CONFIGURACIÓN DE LECTURA (Recibir mensajes) ---
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread hiloEscucha = new Thread(() -> {
                try {
                    String mensaje;
                    while ((mensaje = entrada.readLine()) != null) {
                        System.out.println("\n[Amigo]: " + mensaje);
                        System.out.print("[Tú]: "); // Mantiene el prompt limpio
                    }
                } catch (IOException e) {
                    System.out.println("\nConexión perdida.");
                }
            });
            hiloEscucha.start();

            // --- CONFIGURACIÓN DE ENVÍO (Mandar mensajes) ---
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Escribe tus mensajes abajo:");
            
            while (true) {
                System.out.print("[Tú]: ");
                String miMensaje = teclado.nextLine();
                if (miMensaje.equalsIgnoreCase("salir")) break;
                salida.println(miMensaje);
            }

            socket.close();
        } catch (Exception e) {
            System.out.println("Error crítico: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
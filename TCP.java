import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TCP {

    // Busca la IP real de red (no 127.0.0.1)
    private static String obtenerIPReal() {
        try {
            java.util.Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;

                java.util.Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Solo IPv4 y que no sea loopback
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("No se pudo detectar la IP: " + e.getMessage());
        }
        return "No encontrada";
    }

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        Socket socket = null;
        int PUERTO = 9999;

        try {
            System.out.println("=== CHAT TCP ===");
            System.out.println("1. Crear sala | 2. Unirse");
            int opcion = Integer.parseInt(teclado.nextLine().trim());

            if (opcion == 1) {
                String miIP = obtenerIPReal();
                System.out.println("Tu IP es: " + miIP);
                System.out.println("Esperando conexión en puerto " + PUERTO + "...");

                ServerSocket serverSocket = new ServerSocket(PUERTO);
                socket = serverSocket.accept();
                serverSocket.close(); // Ya no necesitamos seguir escuchando
                System.out.println("¡Amigo conectado desde: " + socket.getInetAddress().getHostAddress() + "!");

            } else {
                System.out.print("IP del amigo: ");
                String ip = teclado.nextLine().trim();
                socket = new Socket(ip, PUERTO);
                System.out.println("¡Conectado correctamente!");
            }

            // Streams de comunicación
            final BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), "UTF-8"));
            final PrintWriter salida = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            final Socket socketFinal = socket;

            // Hilo para RECIBIR mensajes
            Thread hiloEscucha = new Thread(() -> {
                try {
                    String mensaje;
                    while ((mensaje = entrada.readLine()) != null) {
                        System.out.println("\r[Amigo]: " + mensaje);
                        System.out.print("[Tú]: ");
                        System.out.flush();
                    }
                } catch (IOException e) {
                    if (!socketFinal.isClosed()) {
                        System.out.println("\nConexión perdida con el amigo.");
                    }
                }
            });

            hiloEscucha.setDaemon(true); // Se cierra solo cuando el main termina
            hiloEscucha.start();

            // Hilo principal: ENVIAR mensajes
            System.out.println("¡Chat listo! Escribe 'salir' para terminar.\n");
            while (true) {
                System.out.print("[Tú]: ");
                String miMensaje = teclado.nextLine();

                if (miMensaje.equalsIgnoreCase("salir")) {
                    System.out.println("Cerrando chat...");
                    break;
                }

                if (!miMensaje.trim().isEmpty()) {
                    salida.println(miMensaje);
                    // Verificar que el stream no tenga error
                    if (salida.checkError()) {
                        System.out.println("Error al enviar. La conexión puede estar caída.");
                        break;
                    }
                }
            }

            socket.close();

        } catch (NumberFormatException e) {
            System.out.println("Opción inválida. Escribe 1 o 2.");
        } catch (ConnectException e) {
            System.out.println("No se pudo conectar. Verifica que la IP sea correcta y que el amigo esté esperando.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
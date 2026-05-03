import java.io.*;
import java.net.*;
import java.util.*;

public class TCP {

    private static String obtenerIPReal() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress())
                        return addr.getHostAddress();
                }
            }
        } catch (SocketException e) {
            System.out.println("No se pudo detectar la IP.");
        }
        return "No encontrada";
    }

    private static Matriz pedirClave(Scanner tecJlado) {
        System.out.println("\n--- Ingresa la clave de la matriz 2x2 ---");
        System.out.println("(Ambos lados deben usar los mismos valores)");
        System.out.print("  a (fila 1, col 1): "); int a = Integer.parseInt(teclado.nextLine().trim());
        System.out.print("  b (fila 1, col 2): "); int b = Integer.parseInt(teclado.nextLine().trim());
        System.out.print("  c (fila 2, col 1): "); int c = Integer.parseInt(teclado.nextLine().trim());
        System.out.print("  d (fila 2, col 2): "); int d = Integer.parseInt(teclado.nextLine().trim());

        Matriz m = new Matriz(a, b, c, d);
        m.generarMatriz();
        m.crearAlfabeto();
        m.crearAlfabetoInverso();
        return m;
    }

    private static Socket establecerConexion(Scanner teclado, int puerto) throws IOException {
        System.out.println("\n1. Crear sala  |  2. Unirse a sala");
        int opcion = Integer.parseInt(teclado.nextLine().trim());

        if (opcion == 1) {
            System.out.println("Tu IP es: " + obtenerIPReal());
            System.out.println("Esperando conexion en puerto " + puerto + "...");
            ServerSocket serverSocket = new ServerSocket(puerto);
            Socket s = serverSocket.accept();
            serverSocket.close();
            System.out.println("Amigo conectado desde: " + s.getInetAddress().getHostAddress());
            return s;
        } else {
            System.out.print("IP del amigo: ");
            String ip = teclado.nextLine().trim();
            Socket s = new Socket(ip, puerto);
            System.out.println("Conectado correctamente.");
            return s;
        }
    }

    // ==================== MODO 1: Recibir un mensaje ====================
    private static void modoRecibirMensaje(Scanner teclado, int puerto) throws IOException {
        System.out.println("\n=== MODO: RECIBIR MENSAJE CIFRADO ===");
        Matriz m = pedirClave(teclado);

        Socket socket = establecerConexion(teclado, puerto);
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        System.out.println("\nEsperando mensaje...");
        String mensajeCifrado = entrada.readLine();
        System.out.println("Mensaje cifrado  : " + mensajeCifrado);
        System.out.println("Mensaje original : " + m.decodificar(mensajeCifrado, m.matriz).trim());

        socket.close();
    }

    // ==================== MODO 2: Enviar un mensaje ====================
    private static void modoEnviarMensaje(Scanner teclado, int puerto) throws IOException {
        System.out.println("\n=== MODO: ENVIAR MENSAJE CIFRADO ===");
        Matriz m = pedirClave(teclado);

        Socket socket = establecerConexion(teclado, puerto);
        PrintWriter salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

        System.out.print("\nEscribe el mensaje (letras A-Z, espacios o puntos): ");
        String mensaje = teclado.nextLine();
        String cifrado = m.codificar(mensaje, m.matriz);

        if (cifrado == null) {
            System.out.println("Error al cifrar. Verifica que el mensaje no tenga caracteres especiales.");
        } else {
            salida.println(cifrado);
            System.out.println("Mensaje cifrado enviado: " + cifrado);
        }

        socket.close();
    }

    // ==================== MODO 3: Chat cifrado ====================
    private static void modoChat(Scanner teclado, int puerto) throws IOException {
        System.out.println("\n=== MODO: CHAT CIFRADO ===");
        Matriz m = pedirClave(teclado);

        // Validar que la clave tenga inverso antes de conectar
        try {
            m.inversoModular(m.determinante(m.matriz));
        } catch (IllegalArgumentException e) {
            System.out.println("Error con la clave: " + e.getMessage());
            return;
        }

        Socket socket = establecerConexion(teclado, puerto);
        final BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        final PrintWriter salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        final Socket socketFinal = socket;

        // Hilo receptor: descifra cada mensaje al llegar
        Thread hiloEscucha = new Thread(() -> {
            try {
                String mensajeCifrado;
                while ((mensajeCifrado = entrada.readLine()) != null) {
                    String claro = m.decodificar(mensajeCifrado, m.matriz);
                    System.out.println("\r[Amigo cifrado] " + mensajeCifrado);
                    System.out.println(" [Amigo claro ] " + (claro != null ? claro.trim() : "(error al descifrar)"));
                    System.out.print("[Tu]: ");
                    System.out.flush();
                }
            } catch (IOException e) {
                if (!socketFinal.isClosed()) System.out.println("\nConexion perdida.");
            }
        });
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();

        System.out.println("Chat listo. Escribe 'salir' para terminar.");
        System.out.println("Nota: usa solo letras A-Z, espacios o puntos.\n");

        while (true) {
            System.out.print("[Tu]: ");
            String mensaje = teclado.nextLine();
            if (mensaje.equalsIgnoreCase("salir")) break;
            if (mensaje.trim().isEmpty()) continue;

            String cifrado = m.codificar(mensaje, m.matriz);
            if (cifrado == null) {
                System.out.println("  No se pudo cifrar. Revisa los caracteres del mensaje.");
                continue;
            }
            salida.println(cifrado);
            if (salida.checkError()) { System.out.println("Error al enviar. Conexion caida."); break; }
        }

        socket.close();
    }

    // ==================== MAIN ====================
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        int PUERTO = 9999;

        System.out.println("================================");
        System.out.println("   CHAT CIFRADO - HILL CIPHER  ");
        System.out.println("================================");
        System.out.println("1. Recibir un mensaje cifrado");
        System.out.println("2. Enviar un mensaje cifrado");
        System.out.println("3. Chat completo (cifrado ida y vuelta)");
        System.out.print("Opcion: ");

        try {
            int modo = Integer.parseInt(teclado.nextLine().trim());
            switch (modo) {
                case 1 -> modoRecibirMensaje(teclado, PUERTO);
                case 2 -> modoEnviarMensaje(teclado, PUERTO);
                case 3 -> modoChat(teclado, PUERTO);
                default -> System.out.println("Opcion invalida. Elige 1, 2 o 3.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Escribe solo el numero de la opcion.");
        } catch (ConnectException e) {
            System.out.println("No se pudo conectar. Verifica la IP y que el otro lado este esperando.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
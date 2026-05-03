import java.io.*;
import java.net.*;
import java.util.*;

public class TCP {

    // ==================== UTILIDADES ====================

    public static String obtenerIPReal() {
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

    // Usado por JFrame: recibe la Matriz ya construida
    public static void iniciarChatTerminal(Matriz m) {
        new ChatTerminal(m).setVisible(true);
    }

    // ==================== MODO 1: Recibir un mensaje (consola) ====================
    public static void modoRecibirMensaje(Matriz m, int puerto) throws IOException {
        Scanner teclado = new Scanner(System.in);
        Socket socket = establecerConexion(teclado, puerto);
        BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        System.out.println("\nEsperando mensaje...");
        String mensajeCifrado = entrada.readLine();
        System.out.println("Mensaje cifrado  : " + mensajeCifrado);
        System.out.println("Mensaje original : " + m.decodificar(mensajeCifrado, m.matriz).trim());

        socket.close();
    }

    // ==================== MODO 2: Enviar un mensaje (consola) ====================
    public static void modoEnviarMensaje(Matriz m, int puerto) throws IOException {
        Scanner teclado = new Scanner(System.in);
        Socket socket = establecerConexion(teclado, puerto);
        PrintWriter salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

        System.out.print("\nEscribe el mensaje (letras A-Z, espacios o puntos): ");
        String mensaje = teclado.nextLine();
        String cifrado = m.codificar(mensaje, m.matriz);

        if (cifrado == null) {
            System.out.println("Error al cifrar.");
        } else {
            salida.println(cifrado);
            System.out.println("Enviado: " + cifrado);
        }
        socket.close();
    }

    // ==================== CONEXION ====================
    private static Matriz pedirClave(Scanner teclado) {
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

    // ==================== MAIN (consola standalone) ====================
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
            Matriz m = pedirClave(teclado);
            switch (modo) {
                case 1 -> modoRecibirMensaje(m, PUERTO);
                case 2 -> modoEnviarMensaje(m, PUERTO);
                case 3 -> iniciarChatTerminal(m);
                default -> System.out.println("Opcion invalida.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatTerminal extends JFrame {

    // ── Colores y fuente ──────────────────────────────────────────────────────
    private static final Color BG          = new Color(0,   0,   0);
    private static final Color GREEN       = new Color(0,   255, 70);
    private static final Color GREEN_DIM   = new Color(0,   180, 50);
    private static final Color GREEN_DARK  = new Color(0,    60, 20);
    private static final Color AMBER       = new Color(255, 200,  0);
    private static final Color RED_ERR     = new Color(255,  50, 50);
    private static final Font  MONO        = new Font("Courier New", Font.BOLD, 13);
    private static final Font  MONO_SM     = new Font("Courier New", Font.PLAIN, 12);
    private static final int   PUERTO      = 9999;

    // ── Estado ────────────────────────────────────────────────────────────────
    private final Matriz      m;
    private Socket            socket;
    private PrintWriter       salida;
    private boolean           conectado        = false;
    private boolean           esServidor       = false;
    private String            modoSeleccionado = "CHAT"; // CHAT | ENVIAR | RECIBIR
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    // ── Componentes ───────────────────────────────────────────────────────────
    private JTextArea         pantalla;
    private JTextField        inputMensaje;
    private JTextField        inputIP;
    private JButton           btnServidor;
    private JButton           btnCliente;
    private JButton           btnEnviar;
    private JLabel            lblEstado;
    private JLabel            lblIP;
    private JPanel            panelConexion;
    private JPanel            panelChat;
    private javax.swing.Timer cursorTimer;
    private boolean           cursorVisible = true;

    public ChatTerminal(Matriz m) {
        this.m = m;
        m.generarMatriz();
        m.crearAlfabeto();
        m.crearAlfabetoInverso();
        construirUI();
        iniciarEfectoBoot();
    }

    // ── Construcción de UI ────────────────────────────────────────────────────
    private void construirUI() {
        setTitle("[ CIPHER TERMINAL v1.0 ]");
        setSize(820, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(10, 14, 6, 14));

        JLabel titulo = new JLabel("[ HILL CIPHER SECURE TERMINAL ]");
        titulo.setFont(new Font("Courier New", Font.BOLD, 16));
        titulo.setForeground(GREEN);

        lblEstado = new JLabel("● OFFLINE");
        lblEstado.setFont(MONO_SM);
        lblEstado.setForeground(RED_ERR);
        lblEstado.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(titulo,   BorderLayout.WEST);
        header.add(lblEstado, BorderLayout.EAST);

        // Línea separadora verde
        JPanel lineaSep = new JPanel();
        lineaSep.setBackground(GREEN_DARK);
        lineaSep.setPreferredSize(new Dimension(820, 1));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);
        topPanel.add(header,  BorderLayout.NORTH);
        topPanel.add(lineaSep, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // ── Pantalla principal ────────────────────────────────────────────────
        pantalla = new JTextArea();
        pantalla.setBackground(BG);
        pantalla.setForeground(GREEN);
        pantalla.setFont(MONO_SM);
        pantalla.setEditable(false);
        pantalla.setCaretColor(GREEN);
        pantalla.setLineWrap(true);
        pantalla.setWrapStyleWord(false);
        pantalla.setBorder(new EmptyBorder(8, 12, 8, 12));

        JScrollPane scroll = new JScrollPane(pantalla);
        scroll.setBackground(BG);
        scroll.setBorder(BorderFactory.createLineBorder(GREEN_DARK, 1));
        scroll.getViewport().setBackground(BG);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setBackground(BG);
        scroll.getVerticalScrollBar().setForeground(GREEN_DARK);

        add(scroll, BorderLayout.CENTER);

        // ── Panel inferior (conexión + chat) ──────────────────────────────────
        JPanel bottom = new JPanel(new CardLayout());
        bottom.setBackground(BG);

        panelConexion = construirPanelConexion();
        panelChat     = construirPanelChat();

        bottom.add(panelConexion, "conexion");
        bottom.add(panelChat,     "chat");

        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel construirPanelConexion() {
        JPanel p = new JPanel(null);
        p.setBackground(BG);
        p.setPreferredSize(new Dimension(820, 140));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GREEN_DARK));

        // ── FILA 1: elegir modo ───────────────────────────────────────────────
        JLabel lblModo = new JLabel("> SELECCIONA MODO:");
        lblModo.setFont(MONO);
        lblModo.setForeground(GREEN_DIM);
        lblModo.setBounds(14, 10, 300, 22);
        p.add(lblModo);

        JButton btnModoChat    = hacerBoton("[ CHAT        ]", 14,  38, 190, 28);
        JButton btnModoEnviar  = hacerBoton("[ SOLO ENVIAR ]", 214, 38, 190, 28);
        JButton btnModoRecibir = hacerBoton("[ SOLO RECIBIR]", 414, 38, 190, 28);
        p.add(btnModoChat);
        p.add(btnModoEnviar);
        p.add(btnModoRecibir);

        // resaltar el modo activo por defecto
        btnModoChat.setForeground(GREEN);
        btnModoChat.setBackground(GREEN_DARK);

        btnModoChat.addActionListener(e -> {
            modoSeleccionado = "CHAT";
            btnModoChat.setForeground(GREEN);    btnModoChat.setBackground(GREEN_DARK);
            btnModoEnviar.setForeground(GREEN_DIM); btnModoEnviar.setBackground(BG);
            btnModoRecibir.setForeground(GREEN_DIM); btnModoRecibir.setBackground(BG);
        });
        btnModoEnviar.addActionListener(e -> {
            modoSeleccionado = "ENVIAR";
            btnModoEnviar.setForeground(GREEN);  btnModoEnviar.setBackground(GREEN_DARK);
            btnModoChat.setForeground(GREEN_DIM);   btnModoChat.setBackground(BG);
            btnModoRecibir.setForeground(GREEN_DIM); btnModoRecibir.setBackground(BG);
        });
        btnModoRecibir.addActionListener(e -> {
            modoSeleccionado = "RECIBIR";
            btnModoRecibir.setForeground(GREEN); btnModoRecibir.setBackground(GREEN_DARK);
            btnModoChat.setForeground(GREEN_DIM);   btnModoChat.setBackground(BG);
            btnModoEnviar.setForeground(GREEN_DIM);  btnModoEnviar.setBackground(BG);
        });

        // separador fino entre filas
        JPanel sep = new JPanel();
        sep.setBackground(GREEN_DARK);
        sep.setBounds(14, 74, 792, 1);
        p.add(sep);

        // ── FILA 2: elegir rol ────────────────────────────────────────────────
        JLabel lblRol = new JLabel("> SELECCIONA ROL:");
        lblRol.setFont(MONO);
        lblRol.setForeground(GREEN_DIM);
        lblRol.setBounds(14, 82, 300, 22);
        p.add(lblRol);

        btnServidor = hacerBoton("[ SERVIDOR ]", 14,  108, 190, 28);
        btnCliente  = hacerBoton("[ CLIENTE  ]", 214, 108, 190, 28);
        p.add(btnServidor);
        p.add(btnCliente);

        lblIP = new JLabel("");
        lblIP.setFont(MONO_SM);
        lblIP.setForeground(AMBER);
        lblIP.setBounds(420, 108, 390, 28);
        p.add(lblIP);

        inputIP = new JTextField();
        inputIP.setBackground(BG);
        inputIP.setForeground(GREEN);
        inputIP.setCaretColor(GREEN);
        inputIP.setFont(MONO);
        inputIP.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GREEN_DARK),
            new EmptyBorder(2, 6, 2, 6)));
        inputIP.setBounds(420, 108, 270, 28);
        inputIP.setVisible(false);
        p.add(inputIP);

        JButton btnConectar = hacerBoton("[ CONECTAR ]", 700, 108, 110, 28);
        btnConectar.setVisible(false);
        p.add(btnConectar);

        btnServidor.addActionListener(e -> {
            esServidor = true;
            btnServidor.setForeground(GREEN);  btnServidor.setBackground(GREEN_DARK);
            btnCliente.setForeground(GREEN_DIM); btnCliente.setBackground(BG);
            inputIP.setVisible(false);
            btnConectar.setVisible(false);
            lblIP.setText("> TU IP: " + TCP.obtenerIPReal() + "  — esperando...");
            lblIP.setVisible(true);
            new Thread(() -> iniciarComoServidor()).start();
        });

        btnCliente.addActionListener(e -> {
            esServidor = false;
            btnCliente.setForeground(GREEN);   btnCliente.setBackground(GREEN_DARK);
            btnServidor.setForeground(GREEN_DIM); btnServidor.setBackground(BG);
            lblIP.setVisible(false);
            inputIP.setVisible(true);
            btnConectar.setVisible(true);
            inputIP.requestFocus();
        });

        btnConectar.addActionListener(e -> {
            String ip = inputIP.getText().trim();
            if (!ip.isEmpty()) new Thread(() -> iniciarComoCliente(ip)).start();
        });
        inputIP.addActionListener(e -> {
            String ip = inputIP.getText().trim();
            if (!ip.isEmpty()) new Thread(() -> iniciarComoCliente(ip)).start();
        });

        return p;
    }

    private JPanel construirPanelChat() {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setBackground(BG);
        p.setPreferredSize(new Dimension(820, 50));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GREEN_DARK),
            new EmptyBorder(8, 12, 8, 12)));

        JLabel prompt = new JLabel("> ");
        prompt.setFont(MONO);
        prompt.setForeground(GREEN);

        inputMensaje = new JTextField();
        inputMensaje.setBackground(BG);
        inputMensaje.setForeground(GREEN);
        inputMensaje.setCaretColor(GREEN);
        inputMensaje.setFont(MONO);
        inputMensaje.setBorder(BorderFactory.createLineBorder(GREEN_DARK));
        inputMensaje.addActionListener(e -> enviarMensaje());

        btnEnviar = hacerBoton("[ SEND ]", 0, 0, 90, 34);
        btnEnviar.addActionListener(e -> enviarMensaje());

        p.add(prompt,       BorderLayout.WEST);
        p.add(inputMensaje, BorderLayout.CENTER);
        p.add(btnEnviar,    BorderLayout.EAST);

        return p;
    }

    private JButton hacerBoton(String texto, int x, int y, int w, int h) {
        JButton b = new JButton(texto);
        b.setFont(MONO);
        b.setForeground(GREEN_DIM);
        b.setBackground(BG);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(GREEN_DARK));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        b.setBounds(x, y, w, h);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(GREEN); b.setBorder(BorderFactory.createLineBorder(GREEN)); }
            public void mouseExited(MouseEvent e)  { b.setForeground(GREEN_DIM); b.setBorder(BorderFactory.createLineBorder(GREEN_DARK)); }
        });
        return b;
    }

    // ── Efecto boot ───────────────────────────────────────────────────────────
    private void iniciarEfectoBoot() {
        String[] lineas = {
            "INITIALIZING HILL CIPHER PROTOCOL...",
            "LOADING ENCRYPTION MATRIX............[OK]",
            "VERIFYING MATRIX DETERMINANT.........[OK]",
            "SECURE CHANNEL READY.",
            "─────────────────────────────────────────────",
            "SELECT ROLE TO BEGIN CONNECTION.",
            ""
        };
        javax.swing.Timer t = new javax.swing.Timer(120, null);
        int[] idx = {0};
        t.addActionListener(e -> {
            if (idx[0] < lineas.length) {
                log(lineas[idx[0]++], GREEN_DIM);
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
            }
        });
        t.start();
    }

    // ── Conexión ──────────────────────────────────────────────────────────────
    private void iniciarComoServidor() {
        try {
            log("> MODO: " + modoSeleccionado + "  |  ROL: SERVIDOR  |  PUERTO: " + PUERTO, AMBER);
            log("> TU IP: " + TCP.obtenerIPReal(), AMBER);
            log("> ESPERANDO CONEXION ENTRANTE...", GREEN_DIM);

            ServerSocket ss = new ServerSocket(PUERTO);
            socket = ss.accept();
            ss.close();

            conectado = true;
            String ipAmigo = socket.getInetAddress().getHostAddress();
            log("", GREEN);
            log("▌ CONEXION ESTABLECIDA CON: " + ipAmigo, GREEN);
            log("─────────────────────────────────────────────", GREEN_DARK);
            setEstado("● ONLINE  //  " + ipAmigo + "  //  " + modoSeleccionado);
            ejecutarModo();

        } catch (IOException e) {
            log("[ERROR] " + e.getMessage(), RED_ERR);
        }
    }

    private void iniciarComoCliente(String ip) {
        try {
            log("> MODO: " + modoSeleccionado + "  |  ROL: CLIENTE  |  TARGET: " + ip + ":" + PUERTO, AMBER);
            log("> CONECTANDO...", GREEN_DIM);
            socket = new Socket(ip, PUERTO);
            conectado = true;
            log("", GREEN);
            log("▌ CONECTADO AL SERVIDOR: " + ip, GREEN);
            log("─────────────────────────────────────────────", GREEN_DARK);
            setEstado("● ONLINE  //  " + ip + "  //  " + modoSeleccionado);
            ejecutarModo();
        } catch (IOException e) {
            log("[ERROR] No se pudo conectar: " + e.getMessage(), RED_ERR);
        }
    }

    private void ejecutarModo() {
        switch (modoSeleccionado) {
            case "CHAT"    -> { cambiarAChat(); iniciarHiloEscucha(); }
            case "ENVIAR"  -> { cambiarAChat(); /* solo envía, no escucha */ }
            case "RECIBIR" -> modoSoloRecibir();
        }
    }

    private void iniciarHiloEscucha() {
        try {
            salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            new Thread(() -> {
                try {
                    String cifrado;
                    while ((cifrado = entrada.readLine()) != null) {
                        String claro = m.decodificar(cifrado, m.matriz);
                        String hora  = sdf.format(new Date());
                        log("[" + hora + "] RX ENCRYPTED  > " + cifrado, GREEN_DARK);
                        log("[" + hora + "] RX DECRYPTED  > " + (claro != null ? claro.trim() : "ERR"), GREEN);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) log("[WARN] CONEXION PERDIDA.", RED_ERR);
                }
            }).start();

        } catch (IOException e) {
            log("[ERROR] Stream: " + e.getMessage(), RED_ERR);
        }
    }

    private void modoSoloRecibir() {
        // En modo RECIBIR solo mostramos lo que llega, sin abrir panel de escritura
        new Thread(() -> {
            try {
                BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
                log("> ESPERANDO MENSAJE ENTRANTE...", GREEN_DIM);
                String cifrado = entrada.readLine();
                if (cifrado != null) {
                    String claro = m.decodificar(cifrado, m.matriz);
                    String hora  = sdf.format(new Date());
                    log("", GREEN);
                    log("[" + hora + "] MENSAJE RECIBIDO", AMBER);
                    log("[" + hora + "] ENCRYPTED  >  " + cifrado, GREEN_DARK);
                    log("[" + hora + "] DECRYPTED  >  " + (claro != null ? claro.trim() : "ERR"), GREEN);
                    log("─────────────────────────────────────────────", GREEN_DARK);
                    log("> TRANSMISION COMPLETADA. CONEXION CERRADA.", GREEN_DIM);
                }
                socket.close();
                setEstado("● OFFLINE");
            } catch (IOException e) {
                log("[ERROR] " + e.getMessage(), RED_ERR);
            }
        }).start();
    }

    // ── Envío de mensaje ──────────────────────────────────────────────────────
    private void enviarMensaje() {
        if (!conectado) return;
        if (modoSeleccionado.equals("RECIBIR")) {
            log("[WARN] Estas en modo RECIBIR. No puedes enviar.", AMBER);
            return;
        }
        String msg = inputMensaje.getText().trim();
        if (msg.isEmpty()) return;

        // Inicializar stream de salida si aún no existe (modo ENVIAR)
        if (salida == null) {
            try {
                salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            } catch (IOException e) {
                log("[ERROR] No se pudo abrir stream: " + e.getMessage(), RED_ERR);
                return;
            }
        }

        String cifrado = m.codificar(msg, m.matriz);
        if (cifrado == null) {
            log("[WARN] Solo letras A-Z, espacios o puntos.", AMBER);
            return;
        }
        salida.println(cifrado);
        String hora = sdf.format(new Date());
        log("[" + hora + "] TX PLAINTEXT  > " + msg.toUpperCase(), GREEN_DIM);
        log("[" + hora + "] TX ENCRYPTED  > " + cifrado, GREEN);
        inputMensaje.setText("");

        // En modo ENVIAR cerramos tras mandar el mensaje
        if (modoSeleccionado.equals("ENVIAR")) {
            try {
                log("─────────────────────────────────────────────", GREEN_DARK);
                log("> MENSAJE ENVIADO. CONEXION CERRADA.", GREEN_DIM);
                socket.close();
                setEstado("● OFFLINE");
            } catch (IOException ex) { /* ignorar */ }
        } else {
            if (salida.checkError()) log("[ERROR] Fallo al enviar.", RED_ERR);
        }
    }

    // ── Helpers UI ────────────────────────────────────────────────────────────
    private void log(String texto, Color color) {
        SwingUtilities.invokeLater(() -> {
            // Append con color usando HTML no funciona en JTextArea.
            // Usamos JTextArea simple; el color lo controlamos globalmente.
            // Para líneas de amigo (RX DECRYPTED) ponemos prefijo especial.
            pantalla.append(texto + "\n");
            pantalla.setCaretPosition(pantalla.getDocument().getLength());
        });
    }

    private void setEstado(String texto) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(texto);
            lblEstado.setForeground(GREEN);
        });
    }

    private void cambiarAChat() {
        SwingUtilities.invokeLater(() -> {
            CardLayout cl = (CardLayout) ((JPanel) panelConexion.getParent()).getLayout();
            cl.show(panelConexion.getParent(), "chat");
            inputMensaje.requestFocus();
        });
    }

    // ── Integración con JFrame existente ──────────────────────────────────────
    // Llama esto desde el botón en Jframe.java:
    //   TCP.iniciarChatTerminal(m);
}
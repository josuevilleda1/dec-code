import javax.swing.*; // Interfaz grafica
import javax.swing.border.*; // componentes para poder usar la libreria
import java.awt.*;// esta la usamos para los colores y llos graficos
import java.awt.event.*;// esucha el mause y el teclado
import java.io.*; // entrada y salida de informacion 
import java.net.*; // uso de sockets 
import java.text.SimpleDateFormat; // para poder darle el frmato de hora que queremos
import java.util.Date;// para tener la hora en que se mando los mensajes 

public class ChatTerminal extends JFrame {

    //  Colores 
    private static final Color negro = new Color(0,   0,   0);
    private static final Color Verde_Chinto = new Color(0,   255, 70);
    private static final Color Verde_Normal   = new Color(0,   180, 50);
    private static final Color Verde_Oscuro  = new Color(0,    60, 20);
    private static final Color Amarillo       = new Color(255, 200,  0);
    private static final Color Rojo     = new Color(255,  50, 50);
    // Fuentes
    private static final Font  MONO        = new Font("Courier New", Font.BOLD, 13);
    private static final Font  MONO_SM     = new Font("Courier New", Font.PLAIN, 12);
    private static final int   PUERTO      = 9999;

    // herramienas para la interface 
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
        getContentPane().setBackground(negro);
        setLayout(new BorderLayout(0, 0));

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(negro);
        header.setBorder(new EmptyBorder(10, 14, 6, 14));

        JLabel titulo = new JLabel("[ HILL CIPHER SECURE TERMINAL ]");
        titulo.setFont(new Font("Courier New", Font.BOLD, 16));
        titulo.setForeground(Verde_Chinto);

        lblEstado = new JLabel("● OFFLINE");
        lblEstado.setFont(MONO_SM);
        lblEstado.setForeground(Rojo);
        lblEstado.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(titulo,   BorderLayout.WEST);
        header.add(lblEstado, BorderLayout.EAST);

        // Línea separadora verde
        JPanel lineaSep = new JPanel();
        lineaSep.setBackground(Verde_Oscuro);
        lineaSep.setPreferredSize(new Dimension(820, 1));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(negro);
        topPanel.add(header,  BorderLayout.NORTH);
        topPanel.add(lineaSep, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // ── Pantalla principal ────────────────────────────────────────────────
        pantalla = new JTextArea();
        pantalla.setBackground(negro);
        pantalla.setForeground(Verde_Chinto);
        pantalla.setFont(MONO_SM);
        pantalla.setEditable(false);
        pantalla.setCaretColor(Verde_Chinto);
        pantalla.setLineWrap(true);
        pantalla.setWrapStyleWord(false);
        pantalla.setBorder(new EmptyBorder(8, 12, 8, 12));

        JScrollPane scroll = new JScrollPane(pantalla);
        scroll.setBackground(negro);
        scroll.setBorder(BorderFactory.createLineBorder(Verde_Oscuro, 1));
        scroll.getViewport().setBackground(negro);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setBackground(negro);
        scroll.getVerticalScrollBar().setForeground(Verde_Oscuro);

        add(scroll, BorderLayout.CENTER);

        // ── Panel inferior (conexión + chat) ──────────────────────────────────
        JPanel bottom = new JPanel(new CardLayout());
        bottom.setBackground(negro);

        panelConexion = construirPanelConexion();
        panelChat     = construirPanelChat();

        bottom.add(panelConexion, "conexion");
        bottom.add(panelChat,     "chat");

        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel construirPanelConexion() {
        JPanel p = new JPanel(null);
        p.setBackground(negro);
        p.setPreferredSize(new Dimension(820, 140));
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Verde_Oscuro));

        // ── FILA 1: elegir modo ───────────────────────────────────────────────
        JLabel lblModo = new JLabel("> SELECCIONA MODO:");
        lblModo.setFont(MONO);
        lblModo.setForeground(Verde_Normal);
        lblModo.setBounds(14, 10, 300, 22);
        p.add(lblModo);

        JButton btnModoChat    = hacerBoton("[ CHAT        ]", 14,  38, 190, 28);
        JButton btnModoEnviar  = hacerBoton("[ SOLO ENVIAR DATO ]", 214, 38, 190, 28);
        JButton btnModoRecibir = hacerBoton("[ SOLO RECIBIR DATO]", 414, 38, 190, 28);
        p.add(btnModoChat);
        p.add(btnModoEnviar);
        p.add(btnModoRecibir);

        // resaltar el modo activo por defecto
        btnModoChat.setForeground(Verde_Chinto);
        btnModoChat.setBackground(Verde_Oscuro);

        btnModoChat.addActionListener(e -> {
            modoSeleccionado = "CHAT";
            btnModoChat.setForeground(Verde_Chinto);    btnModoChat.setBackground(Verde_Oscuro);
            btnModoEnviar.setForeground(Verde_Normal); btnModoEnviar.setBackground(negro);
            btnModoRecibir.setForeground(Verde_Normal); btnModoRecibir.setBackground(negro);
        });
        btnModoEnviar.addActionListener(e -> {
            modoSeleccionado = "ENVIAR";
            btnModoEnviar.setForeground(Verde_Chinto);  btnModoEnviar.setBackground(Verde_Oscuro);
            btnModoChat.setForeground(Verde_Normal);   btnModoChat.setBackground(negro);
            btnModoRecibir.setForeground(Verde_Normal); btnModoRecibir.setBackground(negro);
        });
        btnModoRecibir.addActionListener(e -> {
            modoSeleccionado = "RECIBIR";
            btnModoRecibir.setForeground(Verde_Chinto); btnModoRecibir.setBackground(Verde_Oscuro);
            btnModoChat.setForeground(Verde_Normal);   btnModoChat.setBackground(negro);
            btnModoEnviar.setForeground(Verde_Normal);  btnModoEnviar.setBackground(negro);
        });

        // separador fino entre filas
        JPanel sep = new JPanel();
        sep.setBackground(Verde_Oscuro);
        sep.setBounds(14, 74, 792, 1);
        p.add(sep);

        // ── FILA 2: elegir rol ────────────────────────────────────────────────
        JLabel lblRol = new JLabel("> POCICION:");
        lblRol.setFont(MONO);
        lblRol.setForeground(Verde_Normal);
        lblRol.setBounds(14, 82, 300, 22);
        p.add(lblRol);

        btnServidor = hacerBoton("[ SERVIDOR ]", 14,  108, 190, 28);
        btnCliente  = hacerBoton("[ CLIENTE  ]", 214, 108, 190, 28);
        p.add(btnServidor);
        p.add(btnCliente);

        lblIP = new JLabel("");
        lblIP.setFont(MONO_SM);
        lblIP.setForeground(Amarillo);
        lblIP.setBounds(420, 108, 390, 28);
        p.add(lblIP);

        inputIP = new JTextField();
        inputIP.setBackground(negro);
        inputIP.setForeground(Verde_Chinto);
        inputIP.setCaretColor(Verde_Chinto);
        inputIP.setFont(MONO);
        inputIP.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Verde_Oscuro),
            new EmptyBorder(2, 6, 2, 6)));
        inputIP.setBounds(420, 108, 270, 28);
        inputIP.setVisible(false);
        p.add(inputIP);

        JButton btnConectar = hacerBoton("[ ENTRAR A SALA ]", 700, 108, 110, 28);
        btnConectar.setVisible(false);
        p.add(btnConectar);

        btnServidor.addActionListener(e -> {
            esServidor = true;
            btnServidor.setForeground(Verde_Chinto);  btnServidor.setBackground(Verde_Oscuro);
            btnCliente.setForeground(Verde_Normal); btnCliente.setBackground(negro);
            inputIP.setVisible(false);
            btnConectar.setVisible(false);
            lblIP.setText("> TU IP: " + TCP.obtenerIPReal() + "  — esperando...");
            lblIP.setVisible(true);
            new Thread(() -> iniciarComoServidor()).start();
        });

        btnCliente.addActionListener(e -> {
            esServidor = false;
            btnCliente.setForeground(Verde_Chinto);   btnCliente.setBackground(Verde_Oscuro);
            btnServidor.setForeground(Verde_Normal); btnServidor.setBackground(negro);
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
        p.setBackground(negro);
        p.setPreferredSize(new Dimension(820, 50));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Verde_Oscuro),
            new EmptyBorder(8, 12, 8, 12)));

        JLabel prompt = new JLabel("> ");
        prompt.setFont(MONO);
        prompt.setForeground(Verde_Chinto);

        inputMensaje = new JTextField();
        inputMensaje.setBackground(negro);
        inputMensaje.setForeground(Verde_Chinto);
        inputMensaje.setCaretColor(Verde_Chinto);
        inputMensaje.setFont(MONO);
        inputMensaje.setBorder(BorderFactory.createLineBorder(Verde_Oscuro));
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
        b.setForeground(Verde_Normal);
        b.setBackground(negro);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(Verde_Oscuro));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        b.setBounds(x, y, w, h);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setForeground(Verde_Chinto); b.setBorder(BorderFactory.createLineBorder(Verde_Chinto)); }
            public void mouseExited(MouseEvent e)  { b.setForeground(Verde_Normal); b.setBorder(BorderFactory.createLineBorder(Verde_Oscuro)); }
        });
        return b;
    }

    // ── Efecto boot ───────────────────────────────────────────────────────────
    private void iniciarEfectoBoot() {
        String[] lineas = {
            "...INITIALIZING...",
            "CARGANDO DESENCRIPCION DE DATOS............[OK]",
            "VERIFICANDO COMPONENTES DE LA MATRIZ.........[OK]",
            "",
            "",
            "",
            "...",

            "─────────────────────────────────────────────",
            "Decida operacion a realizar",
            ""
        };
        javax.swing.Timer t = new javax.swing.Timer(120, null);
        int[] idx = {0};
        t.addActionListener(e -> {
            if (idx[0] < lineas.length) {
                log(lineas[idx[0]++], Verde_Normal);
            } else {
                ((javax.swing.Timer) e.getSource()).stop();
            }
        });
        t.start();
    }

    // ── Conexión ──────────────────────────────────────────────────────────────
    private void iniciarComoServidor() {
        try {
            log("> MODO: " + modoSeleccionado + "  |  ROL: SERVIDOR  |  PUERTO: " + PUERTO, Amarillo);
            log("> TU IP: " + TCP.obtenerIPReal(), Amarillo);
            log("> ESPERANDO CONEXION ENTRANTE...", Verde_Normal);

            ServerSocket ss = new ServerSocket(PUERTO);
            socket = ss.accept();
            ss.close();

            conectado = true;
            String ipAmigo = socket.getInetAddress().getHostAddress();
            log("", Verde_Chinto);
            log("▌ CONEXION ESTABLECIDA CON: " + ipAmigo, Verde_Chinto);
            log("─────────────────────────────────────────────", Verde_Oscuro);
            setEstado("● ONLINE  //  " + ipAmigo + "  //  " + modoSeleccionado);
            ejecutarModo();

        } catch (IOException e) {
            log("[ERROR] " + e.getMessage(), Rojo);
        }
    }

    private void iniciarComoCliente(String ip) {
        try {
            log("> MODO: " + modoSeleccionado + "  |  ROL: CLIENTE  |  TARGET: " + ip + ":" + PUERTO, Amarillo);
            log("> CONECTANDO...", Verde_Normal);
            socket = new Socket(ip, PUERTO);
            conectado = true;
            log("", Verde_Chinto);
            log("▌ CONECTADO AL SERVIDOR: " + ip, Verde_Chinto);
            log("─────────────────────────────────────────────", Verde_Oscuro);
            setEstado("● ONLINE  //  " + ip + "  //  " + modoSeleccionado);
            ejecutarModo();
        } catch (IOException e) {
            log("[ERROR] No se pudo conectar: " + e.getMessage(), Rojo);
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
                        log("[" + hora + "] RX ENCRYPTED  > " + cifrado, Verde_Oscuro);
                        log("[" + hora + "] RX DECRYPTED  > " + (claro != null ? claro.trim() : "ERR"), Verde_Chinto);
                    }
                } catch (IOException e) {
                    if (!socket.isClosed()) log("[WARN] CONEXION PERDIDA.", Rojo);
                }
            }).start();

        } catch (IOException e) {
            log("[ERROR] Stream: " + e.getMessage(), Rojo);
        }
    }

    private void modoSoloRecibir() {
        // En modo RECIBIR solo mostramos lo que llega, sin abrir panel de escritura
        new Thread(() -> {
            try {
                BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));
                log("> ESPERANDO MENSAJE ENTRANTE...", Verde_Normal);
                String cifrado = entrada.readLine();
                if (cifrado != null) {
                    String claro = m.decodificar(cifrado, m.matriz);
                    String hora  = sdf.format(new Date());
                    log("", Verde_Chinto);
                    log("[" + hora + "] MENSAJE RECIBIDO", Amarillo);
                    log("[" + hora + "] ENCRYPTED  >  " + cifrado, Verde_Oscuro);
                    log("[" + hora + "] DECRYPTED  >  " + (claro != null ? claro.trim() : "ERR"), Verde_Chinto);
                    log("─────────────────────────────────────────────", Verde_Oscuro);
                    log("> TRANSMISION COMPLETADA. CONEXION CERRADA.", Verde_Normal);
                }
                socket.close();
                setEstado("● OFFLINE");
            } catch (IOException e) {
                log("[ERROR] " + e.getMessage(), Rojo);
            }
        }).start();
    }

    // ── Envío de mensaje ──────────────────────────────────────────────────────
    private void enviarMensaje() {
        if (!conectado) return;
        if (modoSeleccionado.equals("RECIBIR")) {
            log("[WARN] Estas en modo RECIBIR. No puedes enviar.", Amarillo);
            return;
        }
        String msg = inputMensaje.getText().trim();
        if (msg.isEmpty()) return;

        // Inicializar stream de salida si aún no existe (modo ENVIAR)
        if (salida == null) {
            try {
                salida = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            } catch (IOException e) {
                log("[ERROR] No se pudo abrir stream: " + e.getMessage(), Rojo);
                return;
            }
        }

        String cifrado = m.codificar(msg, m.matriz);
        if (cifrado == null) {
            log("[WARNING] SOLO PUEDES MANDAR LETRAS ESPACIOS Y PUNTOS .", Rojo);
            return;
        }
        salida.println(cifrado);
        String hora = sdf.format(new Date());
        log("[" + hora + "] ->  >> " + msg.toUpperCase(), Verde_Normal);
        log("[" + hora + "] -> ENCRYPTED  >> " + cifrado, Amarillo);
        inputMensaje.setText("");

        if (modoSeleccionado.equals("ENVIAR")) {
            try {
                log("─────────────────────────────────────────────", Verde_Oscuro);
                log("> MENSAJE ENVIADO. CONEXION CERRADA.", Verde_Normal);
                socket.close();
                setEstado("● OFFLINE");
            } catch (IOException ex) { /* ignorar */ }
        } else {
            if (salida.checkError()) log("[ERROR] Fallo al enviar.", Rojo);
        }
    }

    // ── Helpers UI ────────────────────────────────────────────────────────────
    private void log(String texto, Color color) {
        SwingUtilities.invokeLater(() -> {
            pantalla.append(texto + "\n");
            pantalla.setCaretPosition(pantalla.getDocument().getLength());
        });
    }

    private void setEstado(String texto) {
        SwingUtilities.invokeLater(() -> {
            lblEstado.setText(texto);
            lblEstado.setForeground(Verde_Chinto);
        });
    }

    private void cambiarAChat() {
        SwingUtilities.invokeLater(() -> {
            CardLayout cl = (CardLayout) ((JPanel) panelConexion.getParent()).getLayout();
            cl.show(panelConexion.getParent(), "chat");
            inputMensaje.requestFocus();
        });
    }
;
}
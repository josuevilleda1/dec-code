import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
public class Jframe{
    public int calculadoraModular2 (int x) {
        return (x % 29 + 29) % 29;

    }
        public static void main(String args[]){
        cuadro1();
    }

    public static void cuadro1(){
        JFrame inicio = new JFrame("Inicio");

        //Pedir matriz
        JLabel txt1 = new JLabel("BIENVENID@");
        txt1.setBounds(415, 50, 500, 60);
        txt1.setFont(new Font("Century Gothic", Font.BOLD, 50));
        inicio.add(txt1);

        JLabel txt2 = new JLabel("Ingrese matriz");
        txt2.setBounds(415, 100, 600, 30); 
        inicio.add(txt2);

        JTextField a11 = new JTextField(5);
        a11.setBounds(415, 130, 600, 30); 
        JTextField a12 = new JTextField(5);
        a12.setBounds(415, 165, 600, 30); 
        JTextField a21 = new JTextField(5);
        a21.setBounds(415, 200, 600, 30); 
        JTextField a22 = new JTextField(5);
        a22.setBounds(415, 235, 600, 30); 
        JButton crear = new JButton("CREAR");
        crear.setBounds(415, 270, 600, 30); 

crear.addActionListener(e -> {
    String a = a11.getText();
    String b = a12.getText();
    String c = a21.getText();
    String d = a22.getText();
    if(a.matches("\\d+") & b.matches("\\d+") & c.matches("\\d+") & d.matches("\\d+")){
        Matriz m = new Matriz(Integer.parseInt(a), Integer.parseInt(b),
                              Integer.parseInt(c), Integer.parseInt(d));
        cuadro2(m);
        inicio.dispose();
    } else { cuadroError(); }
});

        inicio.add(a11);
        inicio.add(a12);
        inicio.add(a21);
        inicio.add(a22);
        inicio.add(crear);

        inicio.setLayout(null);
        inicio.setSize(1200,550);
        inicio.setLocationRelativeTo(null);
        inicio.getContentPane().setBackground(new Color(245, 245,220));
        inicio.setVisible(true);
        inicio.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    public static void cuadroError(){
        JFrame error = new JFrame("ERROR");
        JLabel me = new JLabel("Argumentos no validos");
        me.setBounds(10,50,300,50);
        error.add(me);
        error.setLayout(null);
        error.setSize(200,200);
        error.setLocationRelativeTo(null);
        error.getContentPane().setBackground(new Color(255, 253,230));
        error.setVisible(true);
    }

    public static void cuadro2(Matriz m){
        m.generarMatriz();
        m.crearAlfabeto();
        m.crearAlfabetoInverso();

        try {
            int det = m.determinante(m.matriz);
            m.inversoModular(det);
        } catch (IllegalArgumentException ex) {
            cuadroError();
            return;
        }
        JFrame cuadro2 = new JFrame("Opciones de la matriz");
        //Antes de llamar a las funciones de codificar, decodificar y mostrar matriz inversa como la matriz original
        //verificar que la matriz no sea nula, si es nula, mandar a ventana error. 
        cuadro2.setLayout(null);
        cuadro2.setSize(600,490);
        cuadro2.setLocationRelativeTo(null);
        cuadro2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel titulo = new JLabel(" Menú de operaciones", SwingConstants.CENTER);
        titulo.setFont(new Font("Century Gothic", Font.BOLD, 24));
        titulo.setBounds(50, 20, 500, 40);
        cuadro2.add(titulo);

        JLabel lblInput = new JLabel("ingrese texto");
        lblInput.setBounds(50, 80, 500, 30);
        cuadro2.add(lblInput);

        JTextField inputTexto = new JTextField();
        inputTexto.setBounds(50, 110, 500, 30);
        cuadro2.add(inputTexto);

        JButton btnCodificador = new JButton("Codificador");
        btnCodificador.setBounds(50, 160, 220, 30);
        cuadro2.add(btnCodificador);

        JButton btnDecodificador = new JButton("Decodificador");
        btnDecodificador.setBounds(330, 160, 220, 30);
        cuadro2.add(btnDecodificador);

        JButton btnDeterminate = new JButton("Ver Determinante");
        btnDeterminate.setBounds(50, 210, 220, 30);
        cuadro2.add(btnDeterminate);

        JButton btnInversa = new JButton("ver la matriz inversa");
        btnInversa.setBounds(330, 210, 220, 30);
        cuadro2.add(btnInversa);

        JLabel lblResultados = new JLabel("Resultados");
        lblResultados.setBounds(50, 270, 500, 30);
        cuadro2.add(lblResultados);

        JTextField txtResultados = new JTextField();
        txtResultados.setBounds(50, 300, 500, 40);
        txtResultados.setFont(new Font("Century Gothic", Font.BOLD, 14));
        txtResultados.setEditable(false);
        cuadro2.add(txtResultados);

        JButton btnRecargar = new JButton("Recargar");
        btnRecargar.setBounds(200, 360, 200, 30);
        cuadro2.add(btnRecargar);

        JButton btnVolver = new JButton("Volver");
        btnVolver.setBounds(200, 390, 200, 30);
        cuadro2.add(btnVolver);
        JButton btnChat = new JButton("Chat entre computadoras");
        btnChat.setBounds(50, 420, 500, 30);   // ajusta Y si necesitas espacio
        cuadro2.add(btnChat);
        
        btnChat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TCP.iniciarChatTerminal(m);   // abre la terminal hacker con la matriz actual
            }
        });

        btnCodificador.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String texto = inputTexto.getText();
                String res = m.codificar(texto, m.matriz);
                if (res == null || res.isEmpty()) {
                    txtResultados.setText("Error!!!: usa letras del alfabeto");
                } else {
                    txtResultados.setText(res);
                }
            }
        });

        btnDecodificador.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String texto = inputTexto.getText();
                try {
                    String res = m.decodificar(texto, m.matriz);
                    txtResultados.setText(res);
                } catch (Exception ex) {
                    txtResultados.setText("Error al decodificar! vuelve a intetarlo");
                }
            }
        });

        btnDeterminate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int det = m.determinante(m.matriz);
                txtResultados.setText("el determinante es:" + det);
            }
        });

        btnInversa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int [][] inv = m.MatrizInversa(m.matriz);
                String matrizString = String.format("[ %d, %d ] [ %d. %d ]", inv[0][0], inv[0][1], inv[1][0], inv[1][1]);
                txtResultados.setText("Matriz Inversa es;" + matrizString);
            }
        });

        btnRecargar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputTexto.setText("");
                txtResultados.setText( "");
            }
        });

        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cuadro1();
                cuadro2.dispose();
            }
        });

        cuadro2.getContentPane().setBackground(new Color(255, 253, 230));

        cuadro2.setVisible(true);

    }
}
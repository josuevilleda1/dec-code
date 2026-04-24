import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.util.Arrays;
public class Jframe{
    public static void main(String args[]){
        cuadro1();
    }

    public static void cuadro1(){
        JFrame inicio = new JFrame("Inicio");

        //Pedir matriz
        JLabel txt1 = new JLabel("BIENVENID@");
        txt1.setBounds(415, 50, 500, 60);
        txt1.setFont(new Font("Arial", Font.BOLD, 50));
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
            //Si los datos para la matriz incluyen letras o cualquier simbolo que no sea un digito es un error.
            //Para controlarlo, se verifica antes de crear el objeto matriz. 
            if(a.matches("\\d+")& b.matches("\\d+")& c.matches("\\d+")& d.matches("\\d+")){
                Matriz m = new Matriz(Integer.parseInt(a),Integer.parseInt(b),Integer.parseInt(c),Integer.parseInt(d));
                cuadro2(m);
                }
            else{cuadroError();}
        });

        inicio.add(a11);
        inicio.add(a12);
        inicio.add(a21);
        inicio.add(a22);
        inicio.add(crear);

        inicio.setLayout(null);
        inicio.setSize(1200,550);
        inicio.setLocationRelativeTo(null);
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
        error.setVisible(true);
    }

    public static void cuadro2(Matriz m){
        JFrame cuadro2 = new JFrame("Cuadro2");
        //Antes de llamar a las funciones de codificar, decodificar y mostrar matriz inversa como la matriz original
        //verificar que la matriz no sea nula, si es nula, mandar a ventana error. 
        cuadro2.setLayout(null);
        cuadro2.setSize(200,200);
        cuadro2.setLocationRelativeTo(null);
        cuadro2.setVisible(true);
    }
}
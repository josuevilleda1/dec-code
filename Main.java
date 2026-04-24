import java.util.Arrays;
import java.util.Scanner; // Importamos la clase Scanner para leer datos

public class Main {

    public static void main(String[] args) {
        
        System.out.println("***PRUEBAS DE LAS OPERACIONES EN LA TERMINAL***");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Coloque mensaje a codificar");
        String str = scanner.nextLine();
        System.out.print("Escriba mensaje a decodificar");
        String rs2 = scanner.nextLine();
        System.out.print("a11: ");
        int a = scanner.nextInt();
        System.out.print("a12: ");
        int b = scanner.nextInt();
        System.out.print("a21: ");
        int c = scanner.nextInt();
        System.out.print("a22: ");
        int d = scanner.nextInt();

        Matriz m = new Matriz(a,b,c,d);
        int [][] matrizOriginal = m.generarMatriz();

        System.out.println("MATRIZ GENERADA [FILA1,FILA2]");
        System.out.println(Arrays.deepToString(matrizOriginal));
        System.out.println("PRUEBA DE ALFABETO GENERADO: ");
        System.out.println(m.crearAlfabeto());
        System.out.println("CREACION DE PAREJAS FORMADAS");
        System.out.println(Arrays.deepToString(m.crearParejas(str)));
        int determinante = m.determinante(matrizOriginal);
         System.out.println("DETERMINANTE GENERADO");
        System.out.println(determinante);
        System.out.println("INVERSO MODULAR GENERADO");
        System.out.println(m.inversoModular(determinante));
         System.out.println("MATRIZ INVERSA GENERADA [FILA1,FILA2]...");
        System.out.println(Arrays.deepToString(m.MatrizInversa(matrizOriginal)));
         System.out.println("MENSAJE CODIFICADO");
        System.out.println(m.codificar(str,matrizOriginal));
        System.out.println("MENSAJE DECODIFICADO");
        System.out.println(m.decodificar(rs2,matrizOriginal));


        scanner.close();
    }
}
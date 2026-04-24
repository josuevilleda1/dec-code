import java.util.HashMap; 
import java.util.Map;
import java.util.ArrayList;

public class Matriz{

    int a;
    int b;
    int c;
    int d;
    int matrizMensaje [][];
    int matriz [][];
    HashMap<Character, Integer> alfabeto;
    HashMap<Integer, Character> alfabetoInverso;

    public Matriz(int a, int b, int c, int d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        matriz = new int[2][2];
        alfabeto = new HashMap<>();
        alfabetoInverso = new HashMap<>();
    }

    public int [][] generarMatriz(){
        //Si el numero dado en la matriz es mayor que 29, usamos operacion de modulo 29.
        if(a>=29){a = a%29;}
        if(b>=29){b=b%29;}
        if(b>=29){b=b%29;}
        if(c>=29){c=c%29;}
        if(d>=29){d=d%29;}
        matriz[0][0] = a;
        matriz[0][1] = b;
        matriz[1][0] = c;
        matriz[1][1] = d;
        return matriz;
    }

    public HashMap<Character,Integer> crearAlfabeto(){
        alfabeto.put(' ',0);
        int contador = 0;
        for (char inicio = 'A'; inicio <='N'; inicio++){
            contador = contador+1;
            alfabeto.put(inicio,contador);
        }
        contador = contador+1;
        alfabeto.put('Ñ',contador);
        contador = contador+1;
        for (char inicio = 'O'; inicio <='Z'; inicio++){
            alfabeto.put(inicio,contador);
            contador = contador+1;
        }
        alfabeto.put('.',contador);
        return alfabeto;
    }

    public HashMap<Integer,Character> crearAlfabetoInverso(){
        alfabetoInverso.put(0,' ');
        int contador = 0;
        for (char inicio = 'A'; inicio <='N'; inicio++){
            contador = contador+1;
            alfabetoInverso.put(contador,inicio);
        }
        contador = contador+1;
        alfabetoInverso.put(contador, 'Ñ');
        contador = contador+1;
        for (char inicio = 'O'; inicio <='Z'; inicio++){


            alfabetoInverso.put(contador,inicio);
            contador = contador+1;
        }
        alfabetoInverso.put(contador, '.');
        return alfabetoInverso;
    }

    public int [][] crearParejas(String mensaje){
        ArrayList<Integer> aux = new ArrayList<>();
        int size = mensaje.length();
        //Si una cadena de texto tiene un size impar, lo obliga a convertirse en par
        //colocando al final un espacio, porque la matriz no puede quedar vacia.
        if (mensaje.length()%2 != 0){
            mensaje = mensaje + " ";
        }
        //Por seguridad todas las cadenas pasan por el filtro de mayusculas
        //SI una cadena es minuscula, de todas formas se convierte en mayuscula. Se controla error.
        mensaje = mensaje.toUpperCase();
        //Nos ayuda a verificar que la cuerda posee solo el alfabeto.
        boolean alfabetoIsCorrect = true;        
        try{
        for(int i=0; i<size; i = i+2){
            int num1 = 0; int num2 = 0;
            if(alfabeto.containsKey(mensaje.charAt(i))){
                num1 = alfabeto.get(mensaje.charAt(i));
            }else{
                alfabetoIsCorrect = false;
                break;
            }
            if(alfabeto.containsKey(mensaje.charAt(i+1))){
                num2 = alfabeto.get(mensaje.charAt(i+1));}
            else{
                alfabetoIsCorrect = false;
                break;
            }
            aux.add(num1); aux.add(num2);
        }

        if(alfabetoIsCorrect){
            int columna=0;
        matrizMensaje = new int[2][aux.size()/2];
        for(int z=0; z<aux.size();z=z+2){
            matrizMensaje[0][columna] = aux.get(z);
            matrizMensaje[1][columna] =aux.get(z+1);
            columna = columna+1;
        }}
        else{
            matrizMensaje = null;
        }
        
        }catch(Exception e){System.out.print("");}
        return matrizMensaje;
    }

    public int calculadoraModular (int x) {
        return (x % 29 + 29) % 29;

    }

    public int determinante (int[][] matriz ){
        int a = matriz[0][0];
        int b = matriz[0][1];
        int c = matriz[1][0];
        int d = matriz[1][1];
        
        return calculadoraModular((a * d) - (b * c));
    }
    

    public int [][] MatrizInversa(int[][] matriz){
        int det = determinante(matriz);
        int detInv = inversoModular(det);
        int[][] inv = new int[2][2];
        inv[0][0] = calculadoraModular( matriz[1][1] * detInv);
        inv[1][1] = calculadoraModular( matriz[0][0] * detInv);
        inv[0][1] = calculadoraModular(-matriz[0][1] * detInv);
        inv[1][0] = calculadoraModular(-matriz[1][0] * detInv);
        return inv;
    }

    public String codificar(String mensaje, int[][] matrizOriginal) {
        HashMap<Integer, Character> alfabetoInverso = crearAlfabetoInverso();
        try{ 
        int [][] matrizMensaje = crearParejas(mensaje); //Este te devuelve la matriz con la que operar.
        String resultado = "";

        for (int i = 0; i < matrizMensaje[0].length; i++) {
            int c1 = calculadoraModular(matrizOriginal[0][0] * matrizMensaje[0][i] + matrizOriginal[0][1] * matrizMensaje[1][i]);
            int c2 = calculadoraModular(matrizOriginal[1][0] * matrizMensaje[0][i] + matrizOriginal[1][1] * matrizMensaje[1][i]);
            resultado = resultado + alfabetoInverso.get(c1);
            resultado= resultado + alfabetoInverso.get(c2);
        }
        return resultado;
        }catch(Exception e){return null;}
    }

    public String decodificar(String mensajeCifrado, int [][] matrizOriginal) {
        int[][] inv = MatrizInversa(matrizOriginal);
        int [][] matrizMensaje = crearParejas(mensajeCifrado);
        String resultado = "";
        HashMap<Integer, Character> alfabetoInverso = crearAlfabetoInverso();

        for (int i = 0; i < matrizMensaje[0].length; i++) {
            int p1 = calculadoraModular(inv[0][0] * matrizMensaje[0][i] + inv[0][1] * matrizMensaje[1][i]);
            int p2 = calculadoraModular(inv[1][0] * matrizMensaje[0][i] + inv[1][1] * matrizMensaje[1][i]);
            resultado = resultado + alfabetoInverso.get(p1);
            resultado= resultado + alfabetoInverso.get(p2);
        }
        return resultado;
    }

    public int inversoModular(int det) {
        int detMod = calculadoraModular(det);
        for (int i = 1; i < 29; i++) {
            if ((detMod * i) % 29 == 1) {
                return i;
            }
        }
        throw new IllegalArgumentException("El determinante no tiene inverso. Esta matriz no sirve como clave.");
}

}
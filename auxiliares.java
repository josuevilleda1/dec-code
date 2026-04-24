import java.util.ArrayList;

public class auxiliares {
    private final String ALFABETO = " ABCDEFGHIJKLMNÑOPQRSTUVWXYZ.";
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


   public String codificar(String mensaje, int[][] matriz) {
        mensaje = mensaje.toUpperCase();
        
        if (mensaje.length() % 2 != 0) {
            mensaje += " "; 
        }

        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < mensaje.length(); i += 2) {
            int p1 = ALFABETO.indexOf(mensaje.charAt(i));
            int p2 = ALFABETO.indexOf(mensaje.charAt(i + 1));
            if(p1 == -1) p1 = 0;
            if(p2 == -1) p2 = 0;

            int c1 = calculadoraModular(matriz[0][0] * p1 + matriz[0][1] * p2);
            int c2 = calculadoraModular(matriz[1][0] * p1 + matriz[1][1] * p2);
            resultado.append(ALFABETO.charAt(c1));
            resultado.append(ALFABETO.charAt(c2));
        }
        return resultado.toString();
    }

    public String decodificar(String mensajeCifrado, int[][] matriz) {
        int[][] inv = MatrizInversa(matriz);
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < mensajeCifrado.length(); i += 2) {
            int c1 = ALFABETO.indexOf(mensajeCifrado.charAt(i));
            int c2 = ALFABETO.indexOf(mensajeCifrado.charAt(i + 1));

            int p1 = calculadoraModular(inv[0][0] * c1 + inv[0][1] * c2);
            int p2 = calculadoraModular(inv[1][0] * c1 + inv[1][1] * c2);

            resultado.append(ALFABETO.charAt(p1));
            resultado.append(ALFABETO.charAt(p2));
        }
        return resultado.toString();
    }

    public int inversoModular(int det) {
        int detMod = calculadoraModular(det);
        for (int i = 1; i < 29; i++) {
            if ((detMod * i) % 29 == 1) {
                return i;
            }
        }
        throw new IllegalArgumentException("El determinante no tiene inverso. Esta matriz no sirve como clave.");
}}
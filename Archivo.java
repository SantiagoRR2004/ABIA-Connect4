import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class Archivo {

    public static float[] obtenerPesos(String nombreArchivo) {
        float[] pesos = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(nombreArchivo));
            String linea = br.readLine();
            String[] valores = linea.split(",");
            pesos = new float[valores.length];
            for (int i = 0; i < valores.length; i++) {
                pesos[i] = Float.parseFloat(valores[i]);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo");
        }
        return pesos;
    }

    public static void guardarPesos(String nombreArchivo, float[] pesos) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo));
            for (int i = 0; i < pesos.length; i++) {
                bw.write(pesos[i] + ",");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al escribir el archivo");
        }
    }

}

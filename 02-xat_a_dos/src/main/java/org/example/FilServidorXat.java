package org.example;
import java.io.*;

public class FilServidorXat extends Thread {
    private ObjectInputStream in;

    public FilServidorXat(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String missatge;
            while (true) {
                missatge = (String) in.readObject();
                System.out.println("Rebut: " + missatge);

                if (missatge.equals(ServidorXat.MSG_SORTIR)) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en el fil del servidor: " + e.getMessage());
        }
    }
}
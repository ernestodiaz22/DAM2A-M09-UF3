package org.example;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connecta() throws IOException {
        socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
        System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Flux d'entrada i sortida creat.");
    }

    public void enviarMissatge(String missatge) throws IOException {
        out.writeObject(missatge);
        out.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("Client tancat.");
        } catch (IOException e) {
            System.err.println("Error en tancar el client: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner scanner = new Scanner(System.in);

        try {
            client.connecta();

            FilLectorCX filLector = new FilLectorCX(client.in);
            System.out.println("Fil de lectura iniciat");
            filLector.start();

            System.out.print("Missatge ('sortir' per tancar): ");
            String resposta = scanner.nextLine();
            System.out.println("Rebut: Escriu el teu nom:");
            System.out.print("Missatge ('sortir' per tancar): ");
            String nom = scanner.nextLine();
            client.enviarMissatge(nom);

            String missatge;
            while (true) {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = scanner.nextLine();
                client.enviarMissatge(missatge);

                if (missatge.equals(ServidorXat.MSG_SORTIR)) {
                    break;
                }
            }

            filLector.join();
        } catch (Exception e) {
            System.err.println("Error al client: " + e.getMessage());
            System.out.println("El servidor ha tancat la connexió.");
        } finally {
            scanner.close();
            client.tancarClient();
        }
    }
}
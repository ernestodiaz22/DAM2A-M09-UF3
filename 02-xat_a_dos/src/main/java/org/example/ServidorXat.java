package org.example;
import java.io.*;
import java.net.*;

public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";

    private ServerSocket serverSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Servidor aturat.");
        }
    }

    public String getNom(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        Socket clientSocket = null;

        try {
            servidor.iniciarServidor();
            clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());

            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());

            String nomClient = servidor.getNom(in);
            System.out.println("Nom rebut: " + nomClient);

            FilServidorXat fil = new FilServidorXat(in);
            System.out.println("Fil de xat creat.");
            System.out.println("Fil de " + nomClient + " iniciat");
            fil.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String missatge;

            while (true) {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = reader.readLine();
                out.writeObject(missatge);
                out.flush();

                if (missatge.equals(MSG_SORTIR)) {
                    System.out.println("Fil de xat finalitzat.");
                    break;
                }
            }

            fil.join();
        } catch (Exception e) {
            System.err.println("Error al servidor: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) clientSocket.close();
                servidor.pararServidor();
            } catch (IOException e) {
                System.err.println("Error en tancar connexions: " + e.getMessage());
            }
        }
    }
}
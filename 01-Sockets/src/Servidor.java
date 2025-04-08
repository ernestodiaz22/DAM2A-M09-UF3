import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void connecta() throws IOException {
        srvSocket = new ServerSocket(PORT);
        System.out.println("Servidor en marxa a " + HOST + ":" + PORT);
        System.out.println("Esperant connexions a " + HOST + ":" + PORT);
        clientSocket = srvSocket.accept();
        System.out.println("Client connectat: " + clientSocket.getInetAddress());
    }

    public void repDades() throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String linia;
        while ((linia = bf.readLine()) != null) {
            System.out.println("Rebut: " + linia);
        }
        bf.close();
    }

    public void tanca() throws IOException {
        clientSocket.close();
        srvSocket.close();
        System.out.println("Servidor tancat.");
    }

    public static void main(String[] args) {
        try {
            Servidor servidor = new Servidor();
            servidor.connecta();
            servidor.repDades();
            servidor.tanca();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private ServerSocket serverSocket;
    private Socket socket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        socket = serverSocket.accept();
        System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
        return socket;
    }

    public void tancarConnexio(Socket socket) throws IOException {
        if (socket != null && !socket.isClosed()) {
            System.out.println("Tancant connexió amb el client: " + socket.getRemoteSocketAddress());
            socket.close();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void enviarFitxers(Socket socket) throws IOException, ClassNotFoundException {
        // Primero el OutputStream y hacer flush
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        try {
            System.out.println("Esperant el nom del fitxer del client...");
            String nomFitxer = (String) ois.readObject();
            System.out.println("Nomfitxer rebut: " + nomFitxer);

            if (nomFitxer == null || nomFitxer.isEmpty() || nomFitxer.equalsIgnoreCase("sortir")) {
                System.out.println("Nom del fitxer buit o nul. Sortint...");
                return;
            }

            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] contingut = fitxer.getContingut();

            if (contingut != null) {
                System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                oos.writeObject(contingut);
                oos.flush();
                System.out.println("Fitxer enviat al client: " + nomFitxer);
            } else {
                System.out.println("Error llegint el fitxer del client: " + nomFitxer);
                oos.writeObject(null);
            }
        } finally {
            oos.close();
            ois.close();
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            Socket socket = servidor.connectar();
            servidor.enviarFitxers(socket);
            servidor.tancarConnexio(socket);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
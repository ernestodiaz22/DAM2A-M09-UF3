import java.io.*;
import java.net.*;
import java.util.Hashtable;
public class ServidorXat {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    private boolean sortir = false;
    private ServerSocket servidorSocket;
    private Hashtable<String, GestorClients> clients = new Hashtable<>();

    public void servidorAEscoltar() {
        try {
            servidorSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);

            while (!sortir) {
                Socket clientSocket = servidorSocket.accept();
                GestorClients gc = new GestorClients(clientSocket, this);
                gc.start();
            }
        } catch (IOException e) {
            System.out.println("Error servidor: " + e.getMessage());
        }
    }

    public void pararServidor() {
        try {
            servidorSocket.close();
        } catch (IOException e) {}
    }

    public void finalitzarXat() {
        enviarMissatgeGrup(Missatge.getMissatgeSortirTots(MSG_SORTIR));
        clients.clear();
        sortir = true;
        pararServidor();
        System.out.println("Servidor finalitzat.");
    }

    public synchronized void afegirClient(GestorClients gc) {
        clients.put(gc.getNom(), gc);
        enviarMissatgeGrup(Missatge.getMissatgeGrup("Entra: " + gc.getNom()));
    }

    public synchronized void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
            enviarMissatgeGrup(Missatge.getMissatgeGrup(nom + " ha sortit."));
        }
    }

    public synchronized void enviarMissatgeGrup(String missatge) {
        for (GestorClients gc : clients.values()) {
            gc.enviarMissatge("Servidor", missatge);
        }
    }

    public synchronized void enviarMissatgePersonal(String desti, String remitent, String missatge) {
        GestorClients receptor = clients.get(desti);
        if (receptor != null) {
            receptor.enviarMissatge(remitent, Missatge.getMissatgePersonal(desti, missatge));
        }
    }

    public static void main(String[] args) {
        new ServidorXat().servidorAEscoltar();
    }
}

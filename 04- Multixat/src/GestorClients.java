import java.io.*;
import java.net.*;

public class GestorClients extends Thread {
    private Socket client;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket client, ServidorXat servidor) {
        this.client = client;
        this.servidor = servidor;
        try {
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creant fluxos client.");
        }
    }

    public String getNom() {
        return nom;
    }

    public void enviarMissatge(String remitent, String missatge) {
        try {
            oos.writeObject(missatge);
        } catch (IOException e) {
            sortir = true;
        }
    }

    public void run() {
        try {
            while (!sortir) {
                String missatge = (String) ois.readObject();
                processaMissatge(missatge);
            }
        } catch (Exception e) {
            System.out.println("Error rebent missatge.");
        } finally {
            try {
                client.close();
            } catch (IOException e) {}
        }
    }

    private void processaMissatge(String missatge) {
        String codi = Missatge.getCodiMissatge(missatge);
        String[] parts = Missatge.getPartsMissatge(missatge);

        if (codi == null || parts == null) return;

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                this.nom = parts[1];
                servidor.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidor.eliminarClient(this.nom);
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;
            case Missatge.CODI_MSG_PERSONAL:
                servidor.enviarMissatgePersonal(parts[1], nom, parts[2]);
                break;
            case Missatge.CODI_MSG_GRUP:
                servidor.enviarMissatgeGrup(parts[1]);
                break;
            default:
                System.out.println("Codi desconegut.");
        }
    }

}

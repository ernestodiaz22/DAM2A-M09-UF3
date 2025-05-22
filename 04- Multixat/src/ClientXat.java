import java.io.*;
import java.net.*;
import java.util.Scanner;
public class ClientXat {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir = false;

    public void connecta() {
        try {
            socket = new Socket("localhost", 9999);
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Client connectat a localhost:9999");
        } catch (IOException e) {
            System.out.println("No es pot connectar.");
        }
    }

    public void enviarMissatge(String msg) {
        try {
            oos.writeObject(msg);
            System.out.println("Enviant missatge: " + msg);
        } catch (IOException e) {
            System.out.println("Error enviant.");
        }
    }

    public void tancarClient() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
        } catch (IOException e) {}
    }

    public void llegir() {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            while (!sortir) {
                String msg = (String) ois.readObject();
                String codi = Missatge.getCodiMissatge(msg);
                String[] parts = Missatge.getPartsMissatge(msg);
                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        break;
                    case Missatge.CODI_MSG_PERSONAL:
                        System.out.println("Missatge de (" + parts[1] + "): " + parts[2]);
                        break;
                    case Missatge.CODI_MSG_GRUP:
                        System.out.println("Grup: " + parts[1]);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error rebent missatge. Sortint...");
        } finally {
            tancarClient();
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public static String getLinea(Scanner sc, String missatge, boolean obligatori) {
        String entrada = "";
        do {
            System.out.print(missatge);
            entrada = sc.nextLine();
        } while (obligatori && entrada.isBlank());
        return entrada;
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        new Thread(() -> client.llegir()).start();

        Scanner sc = new Scanner(System.in);
        client.ajuda();
        boolean sortir = false;
        while (!sortir) {
            String op = getLinea(sc, "Opció: ", false);
            switch (op) {
                case "1":
                    String nom = getLinea(sc, "Introdueix el nom: ", true);
                    client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                    break;
                case "2":
                    String dest = getLinea(sc, "Destinatari:: ", true);
                    String msg = getLinea(sc, "Missatge a enviar: ", true);
                    client.enviarMissatge(Missatge.getMissatgePersonal(dest, msg));
                    break;
                case "3":
                    String msgG = getLinea(sc, "Missatge grup: ", true);
                    client.enviarMissatge(Missatge.getMissatgeGrup(msgG));
                    break;
                case "4":
                    sortir = true;
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                    break;
                case "5":
                    sortir = true;
                    client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    break;
                default:
                    client.ajuda();
            }
        }
        sc.close();
        client.tancarClient();
    }
}

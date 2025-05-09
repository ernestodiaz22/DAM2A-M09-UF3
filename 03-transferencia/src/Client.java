import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "C:\\Temp";
    private Socket socket;

    public void connectar() throws IOException {
        System.out.println("Connectant a -> localhost:9999");
        socket = new Socket("localhost", 9999);
        System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());

        // Crear el directorio si no existe
        new File(DIR_ARRIBADA).mkdirs();
    }

    public void rebreFitxers() throws IOException, ClassNotFoundException {
        // Primero el OutputStream y hacer flush
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
                String nomFitxer = scanner.nextLine();

                oos.writeObject(nomFitxer);
                oos.flush();

                if (nomFitxer.equalsIgnoreCase("sortir")) {
                    System.out.println("Sortint...");
                    break;
                }

                System.out.print("Nom del fitxer a guardar: ");
                String desti = scanner.nextLine();
                if (desti.isEmpty()) {
                    desti = DIR_ARRIBADA + "\\" + new File(nomFitxer).getName();
                }

                Object response = ois.readObject();
                if (response == null) {
                    System.out.println("El servidor no pudo encontrar el archivo");
                    continue;
                }

                byte[] contingut = (byte[]) response;
                try (FileOutputStream fos = new FileOutputStream(desti)) {
                    fos.write(contingut);
                    System.out.println("Fitxer rebut i guardat com: " + desti);
                }
            }
        } finally {
            oos.close();
            ois.close();
        }
    }

    public void tancarConnexio() throws IOException {
        if (socket != null && !socket.isClosed()) {
            System.out.println("Connexio tancada.");
            socket.close();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        try {
            client.connectar();
            client.rebreFitxers();
            client.tancarConnexio();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
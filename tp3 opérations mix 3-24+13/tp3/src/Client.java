import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String argv[]) throws Exception {
        int choixClient;
        String operandes;
        String resultatOperation;
        Scanner sc = new Scanner(System.in);
        Socket socketClient = new Socket("192.168.58.25", 7010);
        DataOutputStream sortieVersServeur = new DataOutputStream(socketClient.getOutputStream());
        BufferedReader entreeDepuisServeur =
                new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

        int quit = 1;
        while(quit != 0) {
            System.out.println("Quelle operation vous souhaitez ?");
            System.out.println("1-Addition");
            System.out.println("2-Soustraction");
            System.out.println("3-Multiplication");
            System.out.println("4-Division");
            System.out.println("5-Expression mixte (ex: 3-2/4+1*3)");
            System.out.println("0-Quitter");
            System.out.println("Entrez votre choix:");
            choixClient = sc.nextInt();
            sc.nextLine();

            if(choixClient == 0) {
                quit = 0;
                sortieVersServeur.writeBytes(0 + ";" + 0 + ";" + 0 + "\n");

                break;
            }

            if(choixClient == 5) {
                System.out.println("Entrez l'expression mixte (ex: 3-2/4+1*3):");
            } else {
                System.out.println("Entrez les operandes separees par espace:");
            }

            operandes = sc.nextLine();
            sortieVersServeur.writeBytes(choixClient + ";" + operandes + "\n");
            resultatOperation = entreeDepuisServeur.readLine();
            System.out.println("[Server] : " + resultatOperation);
            System.out.println("---------------------------------\n");
        }
        socketClient.close();
    }
}
import java.io.*;
import java.net.*;

public class Sendy {
    public static void main(String argv[]) throws Exception {
        String phrase = "école nationale des sciences appliquées de tétouan";
        String phraseModifiee;

        Socket socketClient = new Socket("localhost", 7010);

        DataOutputStream sortieVersServeur = new DataOutputStream(socketClient.getOutputStream());

        BufferedReader entreeDepuisServeur = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

        sortieVersServeur.writeBytes(phrase + '\n');

        phraseModifiee = entreeDepuisServeur.readLine();
        System.out.println("RECU DU SERVEUR: " + phraseModifiee);

        socketClient.close();
    }
}
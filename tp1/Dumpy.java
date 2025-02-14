import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Dumpy {
    public static void main(String argv[]) throws Exception{
        String phraseClient;
        String phraseMajuscule;
        ServerSocket conn = new ServerSocket(7010);
        while(true){
            Socket comm = conn.accept();
            BufferedReader entreeDepuisClient = new BufferedReader(new InputStreamReader(comm.getInputStream()));
            DataOutputStream sortieVersClient = new DataOutputStream(comm.getOutputStream());
            phraseClient = entreeDepuisClient.readLine();
            phraseMajuscule = phraseClient.toUpperCase()+ '\n';
            sortieVersClient.writeBytes(phraseMajuscule);
        }
    }
}

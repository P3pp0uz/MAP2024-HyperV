package di.uniba.map.b.adventure.socket;

import di.uniba.map.b.adventure.db.GameStatus;
import di.uniba.map.b.adventure.type.CommandOutputGui;
import di.uniba.map.b.adventure.type.CommandTypeGui;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * Classe che si occupa di gestire la comunicazione del client con il server
 */
public class Client implements ClientInterface {

    /**
     * Socket per la comunicazione
     */
    private Socket socket;
    /**
     * Oggetto per la lettura degli oggetti inviati dal server
     */
    private ObjectInputStream objectReader;
    /**
     * Oggetto per la scrittura di stringhe da inviare al server
     */
    private BufferedWriter bufferedWriter;

    /**
     * Costruttore della classe
     * @throws IOException  Eccezione lanciata in caso di errore di I/O
     */
    public Client() throws IOException {

        socket = new Socket("localhost", 5000);
        objectReader = new ObjectInputStream(socket.getInputStream());
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    /**
     * Metodo che si occupa di inviare un comando al server e di ricevere la risposta
     * @param command Comando da inviare al server
     * @return Risposta del server
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     * @throws ClassNotFoundException Eccezione lanciata in caso di classe non trovata
     */
    public CommandOutputGui executeCommand(String command) throws IOException, ClassNotFoundException {

        CommandOutputGui commandGUIOutput = null;
        Object response = null;

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        out.println(command);
        response=objectReader.readObject();
        if(response instanceof CommandOutputGui){
            commandGUIOutput=(CommandOutputGui) response;
            return commandGUIOutput;
        } else {
            System.err.println("Errore nella risposta del server");
            return new CommandOutputGui(CommandTypeGui.UNKNOWN);
        }
    }

    /**
     * Metodo che si occupa di inviare un comando di richiesta di risorse al server e di ricevere la risposta
     * @param command Comando da inviare al server
     * @return Risposta del server
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     * @throws ClassNotFoundException Eccezione lanciata in caso di classe non trovata
     */
    @SuppressWarnings("unchecked")
	public <T> T getResourcesFromServer(String command) throws IOException, ClassNotFoundException {

        Object response = null;

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        out.println(command);
        response = objectReader.readObject();
        return (T) response;
    }


    /**
     * Metodo che si occupa di inviare risorse al server e di ricevere la risposta
     * @param resource risorsa da inviare al server
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     */
    public void sendResourcesToServer(String resource) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        out.println(resource);
    }

    /**
     * Metodo che chiude la connessione con il server
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     */
    public void closeConnection() throws IOException {
        socket.close();
    }
}

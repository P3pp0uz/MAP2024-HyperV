package di.uniba.map.b.adventure.socket;

import di.uniba.map.b.adventure.Engine;
import di.uniba.map.b.adventure.type.CommandOutputGui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Classe che si occupa di gestire la comunicazione del server con il client
 */
public class Server implements ServerInterface {

    /**
     * Engine che rappresenta il motore di gioco
     */
    private Engine engine;
    /**
     * Socket per la comunicazione
     */
    private  ServerSocket serverSocket;
    /**
     * Socket per la comunicazione
     */
    private Socket clientSocket;

    /**
     * Oggetto per la scrittura di oggetti da inviare al client
     */
    private ObjectOutputStream objectOutputStream;

    /**
     * Costruttore della classe
     * @param engine Engine che rappresenta il motore di gioco
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     */
    public Server(Engine engine) throws IOException {
        this.engine = engine;

        serverSocket = new ServerSocket(5000);

        clientSocket=serverSocket.accept();
        System.out.println("Client connesso");

    }

    /**
     * Metodo che si occupa di avviare il server
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     * @throws SQLException Eccezione lanciata in caso di errore di connessione al database
     */
    public void start() throws IOException, SQLException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        this.objectOutputStream = objectOutputStream;
        CommandOutputGui commandGUI = null;
        Object resource = null;
        String commandString = bufferedReader.readLine();

        while (true) {
                    if(commandString.startsWith("username:")){
                        String username = commandString.split(":")[1];
                        engine.setUsername(username);
                    } else if(commandString.startsWith("resources:")){
                        String resourceRequest = commandString.split(":")[1];
                        resource = engine.sendResourcesToClient(resourceRequest);
                        objectOutputStream.writeObject(resource);
                    } else{
                        commandGUI=engine.executeCommand(commandString);
                        objectOutputStream.writeObject(commandGUI);
                    }
                    objectOutputStream.flush();
                    commandString = bufferedReader.readLine();
        }
    }

    /**
     * Metodo che si occupa di inviare un comando al client
     * @param commandGUIOutput Comando da inviare al client
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     */
    public void sendCommand(CommandOutputGui commandGUIOutput) throws IOException {
        System.out.println(commandGUIOutput.getType());
        objectOutputStream.writeObject(commandGUIOutput);
        objectOutputStream.flush();
    }

    /**
     * Metodo che si occupa di impostare l'engine
     * @param engine Engine da impostare
     */
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

}

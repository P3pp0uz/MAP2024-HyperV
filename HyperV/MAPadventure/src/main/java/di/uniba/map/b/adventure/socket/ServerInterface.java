package di.uniba.map.b.adventure.socket;

import di.uniba.map.b.adventure.Engine;
import di.uniba.map.b.adventure.type.CommandOutputGui;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Interfaccia che rappresenta un server che può essere esteso con dei plugin
 */
public interface ServerInterface {
    /**
     * Metodo che si occupa di inviare un comando al client
     * @param commandGUIOutput Comando da inviare al client
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     */
    void sendCommand(CommandOutputGui commandGUIOutput) throws IOException;

    /**
     * Metodo che si occupa di inviare risorse al client
     * @throws IOException Eccezione lanciata in caso di errore di I/O
     * @throws SQLException Eccezione lanciata in caso di errore di SQL
     */
    void start() throws IOException, SQLException;

    /**
     * Metodo che si occupa di impostare la connessione con l'Engine
     * @param engine Engine con cui si vuole impostare la connessione
     */
    void setEngine(Engine engine);
}

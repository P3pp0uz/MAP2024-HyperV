package di.uniba.map.b.adventure.parser;

import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.Command;

/**
 * Classe che gestisce l'ouput del parser
 */
public class ParserOutput {

    /**
     * Comando dell'utente
     */
    private Command command;

    /**
     * Oggetto
     */
    private AdvObject object;
    
    /**
     * Oggetto nell'inventario
     */
    private AdvObject invObject;

    /**
     * Password
     */
    private String password;

    /**
     * Costruttore della classe
     * @param command comando dell'utente
     * @param object oggetto
     */
    public ParserOutput(Command command, AdvObject object) {
        this.command = command;
        this.object = object;
    }

    /**
     * Costruttore della classe
     * @param command comando dell'utente
     * @param object oggetto
     * @param invObejct oggetto nell'inventario
     * @param password testo ausiliario
     */
    public ParserOutput(Command command, AdvObject object, AdvObject invObejct, String password){
        this.command = command;
        this.object = object;
        this.invObject = invObejct;
        this.password = password;
    }

    /**
     * Costruttore della classe
     * @param command comando dell'utente
     * @param object oggetto
     * @param invObejct oggetto nell'inventario
     */
    public ParserOutput(Command command, AdvObject object, AdvObject invObejct) {
        this.command = command;
        this.object = object;
        this.invObject = invObejct;
    }

    /**
     * Getter del comando
     * @return comando dell'utente
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Setter del comando
     * @param command comando dell'utente
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * Getter dell'oggetto
     * @return oggetto
     */
    public AdvObject getObject() {
        return object;
    }

    /**
     * Setter dell'oggetto
     * @param object oggetto
     */
    public void setObject(AdvObject object) {
        this.object = object;
    }

    /**
     * Getter dell'oggetto nell'inventario
     * @return oggetto nell'inventario
     */
    public AdvObject getInvObject() {
        return invObject;
    }

    /**
     * Setter dell'oggetto nell'inventario
     * @param invObject oggetto nell'inventario
     */
    public void setInvObject(AdvObject invObject) {
        this.invObject = invObject;
    }

    /**
     * Getter dell'attributo password.
     * @return password
     */
    public String getPasswordInput(){
        return password;
    }

    /**
     * Setter dell'attributo password.
     * @param password
     */
    public void setPasswordInput(String password){
        this.password = password;
    }
}


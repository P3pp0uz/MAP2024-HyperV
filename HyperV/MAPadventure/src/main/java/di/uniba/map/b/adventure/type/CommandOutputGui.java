package di.uniba.map.b.adventure.type;

import java.io.Serializable;
/**
 * Classe che rappresenta l'output di un comando
 */
public class CommandOutputGui implements Serializable {
    /**
     * Enumerativo dei possibili tipi di output di un comando
     */
    private CommandTypeGui type;
    
    /**
     * Testo dell'output
     */
    private String text;

    /**
     * Percorso della risorsa da visualizzare
     */
    private String pathResource;

    /**
     * Costruttore della classe CommandOutputGui
     * @param type tipo di output
     * @param text testo dell'output
     * @param resource percorso della risorsa da visualizzare
     */
    public CommandOutputGui(final CommandTypeGui type,final String text,final String resource) {
        this.type = type;
        this.pathResource = resource;
        this.text = text;
    }

    public CommandOutputGui(final CommandTypeGui type,final String text) {
        this.type = type;
        this.text = text;
    }

    public CommandOutputGui(final CommandTypeGui type) {
        this.type = type;
    }

    /**
     * Getter del testo dell'output
     * @return type testo dell'output
     */
    public String getText() {
        return text;
    }

    /**
     * Getter del tipo di output
     * @return type tipo di output
     */
    public CommandTypeGui getType() {
        return type;
    }

    /**
     * Getter del percorso della risorsa da visualizzare
     * @return pathResource percorso della risorsa da visualizzare
     */
    public String getResource() {
        return pathResource;
    }

    /**
     * Setter del tipo dell'output
     * @param type dell'output
     */
    public void setType(final CommandTypeGui type) {
        this.type = type;
    }

    /**
     * Setter del percorso della risorsa da visualizzare
     */
    public void setResource(final String resource) {
        this.pathResource = resource;
    }

}

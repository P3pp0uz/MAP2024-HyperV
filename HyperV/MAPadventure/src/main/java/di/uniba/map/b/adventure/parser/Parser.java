package di.uniba.map.b.adventure.parser;

import di.uniba.map.b.adventure.Utils;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.Command;
import java.util.List;
import java.util.Set;

/**
 * Classe per effettuare il parsing
 */
public class Parser {

    /**
     * Set di stringhe da rimuovere
     */
    private final Set<String> stopwords;

    /**
     * Costruttore
     * @param stopwords Set di stringhe da rimuovere
     */
    public Parser(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    /**
     * Metodo che controlla se una stringa è un comando
     * @param token Stringa da controllare
     * @param commands Lista di comandi
     * @return Indice del comando nella lista, -1 se non è un comando
     */
    private int checkCommand(String token, List<Command> commands) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getName().equals(token) || commands.get(i).getAlias().contains(token)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Metodo che controlla se la stringa è un oggetto
     * @param token Stringa da controllare
     * @param objects Lista di oggetti
     * @return Indice dell'oggetto nella lista, -1 se non è un oggetto
     */
    private int checkObject(String token, List<AdvObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getName().equals(token) || objects.get(i).getAlias().contains(token)) {
                return i;
            }
        }
        return -1;
    }

/**
 * Metodo di controllo per la password
 * @param tokensList Lista di stringhe da controllare
 * @return L'ultima parola come password, null se la lista contiene più di una parola dopo il comando
 */
private String checkPassword(List<String> tokensList) {
    if (tokensList == null || tokensList.size() != 3) {
        return null;
    }
    String lastToken = tokensList.get(2);
    if (lastToken != null && !lastToken.trim().isEmpty()) {
        return lastToken.trim(); 
    }
    return null;
}

    /**
     * Metodo che effettua il parsing del comando
     * @param command Comando di cui effetuare il parsing
     * @param commands Lista dei comandi
     * @param objects Lista degli oggetti
     * @param inventory Lista degli oggetti nell'inventario
     * @return Oggetto di tipo ParserOutput che contiene il comando, l'oggetto e l'oggetto dell'inventario
     */
    public ParserOutput parse(String command, List<Command> commands, List<AdvObject> objects, List<AdvObject> inventory) {
        List<String> tokens = Utils.parseString(command, stopwords);
        String aux_text;
        if (!tokens.isEmpty()) {
            int ic = checkCommand(tokens.get(0), commands);
            if (ic > -1) {
                if (tokens.size() > 1) {
                    int io = checkObject(tokens.get(1), objects);
                    int ioinv = -1;
                    if (io < 0 && tokens.size() > 2) {
                        io = checkObject(tokens.get(2), objects);
                    }
                    if (io < 0) {
                        ioinv = checkObject(tokens.get(1), inventory);
                        if (ioinv < 0 && tokens.size() > 2) {
                            ioinv = checkObject(tokens.get(2), inventory);
                        }
                    }
                    aux_text = checkPassword(tokens);
                    if (io > -1 && ioinv > -1) {
                        return new ParserOutput(commands.get(ic), objects.get(io), inventory.get(ioinv));
                    } else if (io > -1 && aux_text != null) {
                        return new ParserOutput(commands.get(ic), objects.get(io), null, aux_text);
                    } else if (ioinv > -1 && aux_text != null) {
                        return new ParserOutput(commands.get(ic), null, inventory.get(ioinv), aux_text);
                    } else if (io > -1) {
                        return new ParserOutput(commands.get(ic), objects.get(io), null);
                    } else if (ioinv > -1) {
                        return new ParserOutput(commands.get(ic), null, inventory.get(ioinv));
                    } else {
                        return new ParserOutput(commands.get(ic), null, null);
                    }
                } else {
                    return new ParserOutput(commands.get(ic), null);
                }
            } else {
                return new ParserOutput(null, null);
            }
        } else {
            return null;
        }
    }
}

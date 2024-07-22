package di.uniba.map.b.adventure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe che gestisce le descrizioni delle stanze
 */
public class RoomDescription {
    
    private final String[] descriptions;
    private final String[] names;

    /**
     * Costruttore della classe RoomDescription
     * @param titleFilePath Percorso del file contenente i nomi delle stanze
     * @param descFilePath Percorso del file contenente le descrizioni delle stanze
     * @throws IOException Eccezione per file non trovato
     */
    public RoomDescription(final String filePath1, final String filePath2) throws IOException {
        descriptions = getInfo(filePath2);
        names = getInfo(filePath1);
    }

    /**
     * Metodo che legge da file le informazioni (nome e descrizione) relative alle stanze
     * @param filePath Percorso del file
     * @return Array di stringhe contenente le informazioni relative alle stanze
     * @throws IOException Eccezione per file non trovato
     */
    private String[] getInfo(final String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines().toArray(String[]::new);
        }
    }

    /**
     * Getter della descrizione di una stanza
     * @param i Indice della descrizione della stanza
     * @return Descrizione della stanza
     */
    public String getDescription(final int i) {
        if (i >= 0 && i < descriptions.length) {
            return descriptions[i];
        }
        return "Nessuna descrizione disponibile";
    }

    /**
     * Getter del nome di una stanza
     * @param i Indice del nome della stanza
     * @return Nome della stanza
     */
    public String getName(final int i) {
        if (i >= 0 && i < names.length) {
            return names[i];
        }
        return "Nessun nome disponibile";
    }
}

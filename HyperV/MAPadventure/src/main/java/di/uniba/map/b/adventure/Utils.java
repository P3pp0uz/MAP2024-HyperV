package di.uniba.map.b.adventure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classe che contiene metodi di utilità
 */
public class Utils {

    /**
     * Metodo che carica un file di testo in un Set di stringhe
     * @param file File da caricare in memoria 
     * @return Set di stringhe contenente le righe del file di testo 
     * @throws IOException Eccezione lanciata in caso di errore di I/O 
     */
    public static Set<String> loadFileListInSet(File file) throws IOException {
        Set<String> set = new HashSet<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            set.add(reader.readLine().trim().toLowerCase());
        }
        reader.close();
        return set;
    }

    /**
     * Metodo che effettua il parsing di una stringa
     * @param string Stringa di cui effettuare il parsing
     * @param stopwords Set di stringhe da rimuovere
     * @return Lista di stringhe
     */
    public static List<String> parseString(String string, Set<String> stopwords) {
        List<String> tokens = new ArrayList<>();
        String[] split = string.toLowerCase().split("\\s+");
        for (String t : split) {
            if (!stopwords.contains(t)) {
                tokens.add(t);
            }
        }
        return tokens;
    }

}

package di.uniba.map.b.adventure.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che rappresenta l'inventario del giocatore
 */
public class Inventory {

    private List<AdvObject> list = new ArrayList<>();

    /**
     * Getter della lista di oggetti
     * @return list lista di oggetti
     */
    public List<AdvObject> getList() {
        return list;
    }

    /**
     * Setter della lista di oggetti
     * @param list lista di oggetti
     */
    public void setList(List<AdvObject> list) {
        this.list = list;
    }

    /**
     * Metodo che aggiunge un oggetto all'inventario
     * @param o oggetto da aggiungere
     */
    public void add(AdvObject o) {
        list.add(o);
    }

    /**
     * Metodo che rimuove un oggetto dall'inventario
     * @param o oggetto da rimuovere
     */
    public void remove(AdvObject o) {
        list.remove(o);
    }
}

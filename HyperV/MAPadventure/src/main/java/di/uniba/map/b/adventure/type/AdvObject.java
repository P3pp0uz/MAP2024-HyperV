package di.uniba.map.b.adventure.type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class AdvObject {

    private final int id;

    private String name;

    private String description;

    private Set<String> alias;

    private boolean pickupable = true;

    private boolean usable = true;

    private boolean unlockable = false;

    private String password;


    /**
     * Costruttore dell'oggetto
     * @param id
     */
    public AdvObject(int id) {
        this.id = id;
    }

    /**
     * Costruttore dell'oggetto
     * @param id
     * @param name
     */
    public AdvObject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Costruttore dell'oggetto
     * @param id
     * @param name
     * @param description
     */
    public AdvObject(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Getter del nome dell'oggetto
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Setter del nome dell'oggetto
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter della descrizione dell'oggetto
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter della descrizione dell'oggetto
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter della possibilità di prendere l'oggetto
     * @return true se l'oggetto è prendibile, false altrimenti
     */
    public boolean isPickupable() {
        return pickupable;
    }

    /**
     * Setter della possibilità di prendere l'oggetto
     * @param pickupable
     */
    public void setPickupable(boolean pickupable) {
        this.pickupable = pickupable;
    }

    /**
     * Getter della possibilità di usare l'oggetto
     * @return true se l'oggetto è usabile, false altrimenti
     */
    public boolean isUsable() {
        return usable;
    }

    /**
     * Setter della possibilità di usare l'oggetto
     * @param usable
     */
    public void setUsable(boolean usable) {
        this.usable = usable;
    }


    /**
     * Getter della possibilità di sbloccare l'oggetto
     * @return true se l'oggetto è sbloccabile, false altrimenti
     */
    public boolean isUnlockable() {
        return unlockable;
    }

    /**
     * Setter della possibilità di sbloccare l'oggetto
     * @param unlockable
     */
    public void setUnlockable(boolean unlockable) {
        this.unlockable = unlockable;
    }

    /**
     * Setter della password dell'oggetto
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getter della password dell'oggetto
     * @return
     */
    public String getPassword() {
        return password;
    }


    /**
     * Getter degli alias dell'oggetto
     * @return
     */
    public Set<String> getAlias() {
        return alias;
    }

    /**
     * Setter degli alias dell'oggetto
     * @param alias
     */
    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }

    /**
     * Setter degli alias dell'oggetto
     * @param alias
     */
    public void setAlias(String[] alias) {
        this.alias = new HashSet<>(Arrays.asList(alias));
    }

    /**
     * Getter dell'id dell'oggetto
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Metodo che converte in hascode l'id dell'oggetto
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        return hash;
    }

    /**
     * Metodo che confronta l'id dell'oggetto
     * @param obj Oggetto da confrontare
     * @return true se l'id è uguale, false altrimenti
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AdvObject other = (AdvObject) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

}

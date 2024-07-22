package di.uniba.map.b.adventure.type;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import java.awt.Image;

/**
 * Classe che rappresenta una stanza
 */
public class Room {

    private final int id;

    private String name;

    private String description;

    private boolean locked = false;
    
    private boolean visited = false;

    private Room south = null;

    private Room north = null;

    private Room east = null;

    private Room west = null;

    /**
     * Background della stanza.
     */
    private Image backgroundImage;

    /**
     * Percorso del background della stanza
     */
    private String backgroundImagePath;

    /**
     * Lista di oggetti contenuti nella stanza
     */
    private final List<AdvObject> objects=new ArrayList<>();

    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        setBackgroundImage();
    }

    /**
     * Getter dell'id della stanza
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter del nome della stanza
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter del nome della stanza
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter della descrizione della stanza
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter della descrizione della stanza
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Metodo che restituisce se la stanza è bloccata o meno
     * @return locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Metodo che setta se la stanza è bloccata o meno
     * @param locked
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Metodo che restituisce se la stanza è stata visitata o meno
     * @return visited
     */
    public boolean isVisited(){
        return this.visited;
    }
    /**
     * Metodo che setta se la stanza è stata visitata o meno
     * @param visited
     */
    public void setVisited(final boolean visited){
        this.visited = visited;
    }

    /**
     * Getter della stanza a sud
     * @return south
     */
    public Room getSouth() {
        return south;
    }

    /**
     * Setter della stanza a sud
     * @param south
     */
    public void setSouth(Room south) {
        this.south = south;
    }

    /**
     * Getter della stanza a nord
     * @return north
     */
    public Room getNorth() {
        return north;
    }

    /**
     * Setter della stanza a nord
     * @param north
     */
    public void setNorth(Room north) {
        this.north = north;
    }

    /**
     * Getter della stanza ad est
     * @return east
     */
    public Room getEast() {
        return east;
    }

    /**
     * Setter della stanza ad est
     * @param east
     */
    public void setEast(Room east) {
        this.east = east;
    }

    /**
     * Getter della stanza ad ovest
     * @return west
     */
    public Room getWest() {
        return west;
    }

    /**
     * Setter della stanza ad ovest
     * @param west
     */
    public void setWest(Room west) {
        this.west = west;
    }

    /**
     * Getter della lista di oggetti contenuti nella stanza
     * @return objects
     */
    public List<AdvObject> getObjects() {
        return objects;
    }

    /**
     * Setter della lista di oggetti contenuti nella stanza
     * @param objects
     */
public void setObjects(List<AdvObject> objects2) {
    
    this.objects.clear();
    if (objects2 != null && !objects2.isEmpty()) {
        this.objects.addAll(objects2);
    } 
}

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.id;
        return hash;
    }

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
        final Room other = (Room) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }


    /*
     * Imposta l'immagine di sfondo della stanza
     */
    private void setBackgroundImage() {
        ImageIcon backgroundImageIcon = new ImageIcon("HyperV/MAPadventure/resources/"+this.id+".png");
        backgroundImagePath= "HyperV/MAPadventure/resources/"+this.id+".png";
        Image backgroundImage = backgroundImageIcon.getImage().getScaledInstance(backgroundImageIcon.getIconWidth(), backgroundImageIcon.getIconHeight(), Image.SCALE_SMOOTH);
        this.backgroundImage = backgroundImage;
    }

    /*
     * Getter dell'immagine di sfondo della stanza
     */
    public Image getBackgroundImage(){
        return this.backgroundImage;
    }

    /*
     * Getter del percorso dell'immagine di sfondo della stanza
     */
    public String getBackgroundImagePath(){
        return this.backgroundImagePath;
    }

}

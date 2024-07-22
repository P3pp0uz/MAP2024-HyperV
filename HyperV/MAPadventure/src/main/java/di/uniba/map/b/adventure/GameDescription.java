package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Classe astratta che descrive il gioco
 */
public abstract class GameDescription {

    private final List<Room> rooms = new ArrayList<>();
    private final List<Command> commands = new ArrayList<>();
    private List<AdvObject> objects = new ArrayList<>();
    private List<AdvObject> inventory = new ArrayList<>();
    private Room currentRoom;

    /**
     * Getter delle stanze
     * @return Lista di stanze
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * Metodo che filtra le stanze in base ad un predicato
     * @param predicate predicato
     * @return lista di stanze filtrate
     */
    public List<Room> filterRoom(Predicate<Room> predicate) {
    return rooms.stream().filter(predicate).collect(Collectors.toList());
}


    /**
     * Getter della stanza corrente
     * @return Stanza corrente
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Setter della stanza corrente
     * @param currentRoom Stanza corrente
     */
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    /**
     * Getter dei comandi
     * @return Lista di comandi
     */
    public List<Command> getCommands() {
        return commands;
    }

    /** 
     * Getter della lista di oggetti nel gioco
     * @return lista di oggetti nel gioco
     */
    public List<AdvObject> getObjectsList() {
        return objects;
    }

    /** 
     * Metodo che restituisce un oggetto in base all'ID
     * @param id ID dell'oggetto
     * @return lista di oggetti nel gioco
     */
    public List<AdvObject> getAllObjects() {
        List<AdvObject> allObjects = new ArrayList<>();
        
        for (Room room : getRooms()) {
            allObjects.addAll(room.getObjects());
        }
        
        allObjects.addAll(getInventory());
        allObjects.addAll(objects);
        allObjects = allObjects.stream().distinct().collect(Collectors.toList());

        return allObjects;
    }


    /** 
     * Metodo che filtra gli oggetti in base ad un predicato
     * @param predicate predicato
     * @return lista di oggetti filtrati
     */
    public List<AdvObject> filterObjects(Predicate<AdvObject> filter) {
        List<AdvObject> allObjects = getAllObjects();
        
        List<AdvObject> filteredObjects = allObjects.stream()
            .filter(filter)
            .collect(Collectors.toList());
    
        return filteredObjects;
    }
    /**
     * Getter degli oggetti nell'inventario
     * @return Lista di oggetti nell'inventario
     */
    public List<AdvObject> getInventory() {
        return inventory;
    }

    /** 
     * Setter degli oggetti nell'inventario
     * @param inventoryObjects Lista di oggetti nell'inventario
     */
    public void setInventory(List<AdvObject> inventoryObjects)
    {
        this.inventory = inventoryObjects;
    }

    /**
     * Metodo astratto che inizializza il gioco
     * @throws Exception Eccezione lanciata in caso di errore
     */
    public abstract void init() throws Exception;

    /**
     * Metodo astratto che restituisce la mossa successiva
     * @param p Stream di output
     * @return 
     */
    public abstract String nextMove(ParserOutput p);

    /**
     * Setter del motore del gioco
     */
    public void setEngine(Engine engine) {}

    /**
     * Metodo che restituisce una stanza in base all'ID
     * @param roomId ID della stanza
     * @return Stanza
     */
    protected Room getRoomById(Integer roomId) {
        return rooms.stream()
            .filter(room -> room.getId() == roomId)
            .findFirst()
            .orElse(null);
    }


}
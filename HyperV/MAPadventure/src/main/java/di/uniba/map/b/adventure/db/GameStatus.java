package di.uniba.map.b.adventure.db;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe che rappresenta lo stato di gioco di un utente.
 * Contiene le informazioni necessarie per ripristinare lo stato di gioco di un utente.
 * Implementa l'interfaccia Serializable per poter essere salvata su file.
 */
public class GameStatus implements Serializable {

    private String username;
    private Integer lastRoomId;
    private List<Integer> inventoryIds;
    private LocalDateTime time;
    private List<Integer> unlockedRoomIds;
    private Map<Integer, List<Integer>> roomObjectIds;

    /**
     * Variabile booleana per il controllo del gioco.
     */
    private boolean isRunning = true;

    /**
     * Costruttore della classe GameStatus.
     * @param username nome utente.
     * @param lastRoomId stanza corrente in cui si trova l'utente.
     * @param inventoryIds lista di oggetti in inventario.
     * @param time tempo di salvataggio.
     */
    public GameStatus(String username, Integer lastRoomId, List<Integer> inventoryIds, LocalDateTime time) {
        this.username = username;
        this.lastRoomId = lastRoomId;
        this.inventoryIds = inventoryIds;
        this.time = time;
        this.unlockedRoomIds = new ArrayList<>();
        this.roomObjectIds = new HashMap<>();
    }

    /*
     * Getter del Username
     */
    public String getUsername() {
        return username;
    }

    /*
     * Setter del Username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /*
     * Getter dell'ultima stanza
     */
    public Integer getlastRoomId() {
        return lastRoomId;
    }

    /*
     * Setter dell'ultima stanza
     */
    public void setlastRoomId(Integer lastRoomId) {
        this.lastRoomId = lastRoomId;
    }

    /*
     * Getter degli oggetti in inventario
     */
    public List<Integer> getInventoryIds() {
        return inventoryIds;
    }

    /*
     * Setter degli oggetti in inventario
     */
    public void setInventoryIds(List<Integer> inventoryIds) {
        this.inventoryIds = inventoryIds;
    }

    /*
     * Getter del tempo
     */
    public LocalDateTime getTime() {
        return time;
    }

    /*
     * conversione tempo 
     */
    public String getFormattedTime() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    return time.format(formatter);
}

    /*
     * Setter del tempo
     */
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    /*
     * Getter delle stanze sbloccate
     */
    public List<Integer> getUnlockedRoomIds() {
        return unlockedRoomIds;
    }

    /*
     * Setter delle stanze sbloccate
     */
    public void setUnlockedRoomIds(List<Integer> unlockedRoomIds) {
        this.unlockedRoomIds = unlockedRoomIds;
    }

    /*
     * Getter degli oggetti nelle stanze
     */
    public Map<Integer, List<Integer>> getRoomObjectIds() {
        return roomObjectIds;
    }

    /*
     * Setter degli oggetti nelle stanze
     */
    public void setRoomObjectIds(Map<Integer, List<Integer>> roomObjectIds) {
        this.roomObjectIds = roomObjectIds;
    }

    /*
     * Agggiunge le stanze sbloccate
     */
    public void addUnlockedRoom(Integer roomId) {
        if (!unlockedRoomIds.contains(roomId)) {
            unlockedRoomIds.add(roomId);
        }
    }

    /*
     * Controllo se la stanza è sbloccata
     */
    public boolean isRoomUnlocked(Integer roomId) {
        return unlockedRoomIds.contains(roomId);
    }


    /*
     * Aggiunge oggetti alla stanza
     */
    public void addObjectToRoom(Integer roomId, Integer objectId) {
        roomObjectIds.computeIfAbsent(roomId, k -> new ArrayList<>()).add(objectId);
    }

    /*
     * Rimuove oggetti dalla stanza
     */
    public void removeObjectFromRoom(Integer roomId, Integer objectId) {
        if (roomObjectIds.containsKey(roomId)) {
            roomObjectIds.get(roomId).remove(objectId);
        }
    }

    /*
     * Controllo se l'oggetto è nella stanza
     */
    public List<Integer> getObjectsInRoom(Integer roomId) {
        return roomObjectIds.getOrDefault(roomId, new ArrayList<>());
    }

    /*
     * Prendo gli id degli oggetti in inventario
     */
    public String getInventoryIdsAsString() {
        return inventoryIds.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    @Override
    public String toString() {
        return "GameStatus{" +
                "username='" + username + '\'' +
                ", lastRoomId=" + lastRoomId +
                ", inventoryIds=" + inventoryIds +
                ", time=" + time +
                ", unlockedRoomIds=" + unlockedRoomIds +
                ", roomObjectIds=" + roomObjectIds +
                '}';
    }
}
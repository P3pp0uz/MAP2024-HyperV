package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.db.DBManager;
import di.uniba.map.b.adventure.db.GameStatus;
import di.uniba.map.b.adventure.games.HyperV;
import di.uniba.map.b.adventure.parser.Parser;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.socket.Server;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.CommandOutputGui;
import di.uniba.map.b.adventure.type.CommandTypeGui;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.socket.ServerInterface;
import di.uniba.map.b.adventure.type.Room;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Classe che indica il motore del gioco
 */
public class Engine {

    /**
     * Gioco in esecuzione
     */
    private final GameDescription game;
    /**
     * Parser per il gioco
     */
    private Parser parser;
    /**
     * Gestore del database
     */
    private DBManager database;
    /**
     * Server per il gioco
     */
    private ServerInterface server;
    /**
     * Username del giocatore
     */
    private String username;

    

    /**
     * Costruttore dell'engine
     * @param game gioco in esecuzione
     */
    public Engine(GameDescription game) {
        this.game = game;
            try {
                this.game.init();
                this.game.setEngine(this); 
            } catch (Exception ex) {
                System.err.println(ex);
            }
            try {
                Set<String> stopwords = Utils.loadFileListInSet(new File("./resources/stopwords"));
                parser = new Parser(stopwords);
            } catch (IOException ex) {
                System.err.println(ex);
            }
            try {
                database=new DBManager();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                server = new Server(this);
                server.start();
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
    }


    /**
     * Setter dello username del gioco in esecuzione.
     * @param username username del giocatore
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Getter dello username del gioco in esecuzione.
     * @return username del giocatore
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Metodo che carica il gioco salvato precedentemente di un utente.
     * @param username username del giocatore
     * @throws SQLException eccezione
     */
    public void loadGame(String username) throws SQLException {
        GameStatus gameStatus = database.loadGameStatus(username);
        if (gameStatus == null) {
            throw new SQLException("No saved game found for user: " + username);
        }
    
        List<Integer> idsInventory = gameStatus.getInventoryIds();
        Integer lastRoomId = gameStatus.getlastRoomId();
        
        // Clear all objects from rooms and inventory
        for (Room room : game.getRooms()) {
            room.getObjects().clear();
        }
        game.getInventory().clear();
        
        // Get all objects
        List<AdvObject> allObjects = game.getAllObjects();
        
        if (allObjects.isEmpty()) {
            System.out.println("Warning: No objects found in the game. Check object initialization.");
        }
        
        // Update inventory
        for (AdvObject obj : allObjects) {
            if (idsInventory.contains(obj.getId())) {
                game.getInventory().add(obj);
            }
        }
        
        // Set current room
        game.setCurrentRoom(game.getRoomById(lastRoomId));
    
        // Load unlocked rooms
        for (Integer roomId : gameStatus.getUnlockedRoomIds()) {
            Room room = game.getRoomById(roomId);
            if (room != null) {
                room.setLocked(false);
            }
        }
    
        // Load room objects
        Map<Integer, List<Integer>> roomObjectIds = gameStatus.getRoomObjectIds();
        for (Map.Entry<Integer, List<Integer>> entry : roomObjectIds.entrySet()) {
            Room room = game.getRoomById(entry.getKey());
            if (room != null) {
                for (Integer objectId : entry.getValue()) {
                    AdvObject obj = allObjects.stream()
                        .filter(o -> o.getId() == objectId)
                        .findFirst()
                        .orElse(null);
                    if (obj != null) {
                        room.getObjects().add(obj);
                    } else {
                        System.out.println("Warning: Object with ID " + objectId + " not found in allObjects list.");
                    }
                }
            }
        }
    
        // Verify final state
        for (Room room : game.getRooms()) {
            System.out.println("Room " + room.getId() + " objects after loading: " + room.getObjects());
        }
    }
    
    

    /**
     * Metodo che salva nel database lo stato del gioco
     * @param username username del giocatore
     */
    public void saveGame(String username) {
        List<Integer> inventoryIds = new ArrayList<>();
        for (AdvObject o : game.getInventory()) {
            inventoryIds.add(o.getId());
        }

        List<Integer> unlockedRoomIds = new ArrayList<>();
        for (Room room : game.getRooms()) {
            if (!room.isLocked()) {
                unlockedRoomIds.add(room.getId());
            }
        }

        Map<Integer, List<Integer>> roomObjectIds = new HashMap<>();
        for (Room room : game.getRooms()) {
        List<Integer> objectIds = room.getObjects().stream().map(AdvObject::getId).collect(Collectors.toList());
        if (!objectIds.isEmpty()) {
            roomObjectIds.put(room.getId(), objectIds);
        }
    }


        GameStatus gameStatus = new GameStatus(username, game.getCurrentRoom().getId(), inventoryIds,
                LocalDateTime.now());
        gameStatus.setUnlockedRoomIds(unlockedRoomIds);
        gameStatus.setRoomObjectIds(roomObjectIds);

        try {
            database.saveGameStatus(gameStatus);
            System.out.println("Game saved. Unlocked rooms: " + unlockedRoomIds + ". Room objects: " + roomObjectIds);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    

    /**
     * Metodo che fa partire l'engine
     * @return output del comando
     */
    public CommandOutputGui execute() {
        String response;
        System.out.println("================================");
        System.out.println("* HyperV *"); 
        System.out.println("================================");
        response = game.getCurrentRoom().getName();
        response = response + "\n================================================\n\n";
        response = response + game.getCurrentRoom().getDescription()+"\n";
        game.getCurrentRoom().setVisited(true);
        return new CommandOutputGui(CommandTypeGui.DISPLAY_TEXT, response);
    }

    /**
     * Metodo che esegue il comando inserito dall'utente
     * @param command comando inserito dall'utente
     * @return output del comando
     * @throws SQLException eccezione
     */
    public CommandOutputGui executeCommand(String command) throws SQLException {
        String response =command + "\n";
        CommandOutputGui commandGUIOutput;
        CommandType commType;
        ParserOutput p = parser.parse(command, game.getCommands(), game.getCurrentRoom().getObjects(), game.getInventory());
        if (p == null || p.getCommand() == null) {
            response = response + "Non capisco quello che mi vuoi dire.\n";
        } else {
            commType = p.getCommand().getType();
            response = response + game.nextMove(p);
            switch (commType) {
                case EAST:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.CHANGE_ROOM, response, game.getCurrentRoom().getBackgroundImagePath());
                    break;
                case NORD:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.CHANGE_ROOM, response, game.getCurrentRoom().getBackgroundImagePath());
                    break;
                case SOUTH:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.CHANGE_ROOM, response, game.getCurrentRoom().getBackgroundImagePath());
                    break;
                case WEST:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.CHANGE_ROOM, response, game.getCurrentRoom().getBackgroundImagePath());
                    break;
                case LOAD_GAME:
                    this.loadGame(this.getUsername());
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.LOAD_GAME, "Caricamento partita", String.valueOf(game.getCurrentRoom().getId()));
                    break;
                case HELP:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.HELP, "", null);
                    break;
                case SAVE:
                    this.saveGame(this.getUsername());
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.SAVE, "", null);
                    break;
                case WORDLE_START:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.WORDLE_START, "", null);
                    break;
                case WORDLE_GUESS:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.WORDLE_GUESS, "", null);
                    break;
                case LOSE:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.LOSE, response, null);
                    break;
                case WIN:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.WIN, response, null);
                    break;
                case END:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.END, response, null);
                    break;
                case DIE:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.DIE, response, null);
                    break;
                default:
                    commandGUIOutput = new CommandOutputGui(CommandTypeGui.DISPLAY_TEXT, response, null);
                    break;
            }
            return commandGUIOutput;
        }
        return commandGUIOutput = new CommandOutputGui(CommandTypeGui.DISPLAY_TEXT, response, null);
    }

    /**
     * Metodo che invia le risorse al client
     * @param request richiesta del client
     * @return risorse da inviare al client
     * @throws SQLException eccezione
     */
    public Object sendResourcesToClient(String request) throws SQLException {
        String response = "";
        Object resources = null;
        CommandType commType;
        ParserOutput p = parser.parse(request, game.getCommands(), game.getCurrentRoom().getObjects(), game.getInventory());
        if (p == null || p.getCommand() == null) {
            response = response + "Non capisco quello che mi vuoi dire.\n";
        } else {
            commType = p.getCommand().getType();
            switch (commType) {
                case GET_SAVES:
                    resources = this.getSaves();
                    break;
                default:
                    resources = null;
                    break;
            }
            return resources;
        }

        return resources = null;
    }

    /**
     * Metodo che carica una partita salvata
     * @return output del comando
     * @throws SQLException eccezione
     */
    public List<GameStatus> getSaves() throws SQLException {

        return database.getAllSaves();
    }

    /**
     * Main del gioco che fa partire l'engine
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {     
        try {
            new Engine(new HyperV());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

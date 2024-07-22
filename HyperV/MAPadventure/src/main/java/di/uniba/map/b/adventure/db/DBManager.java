package di.uniba.map.b.adventure.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Classe che gestisce la connessione al database.
 */
public class DBManager {
    /**
     * Connessione al db.
     */
    private Connection connection;
    boolean isRunning = true;

    /**
     * Costruttore della classe DBManager.
     * 
     * @throws SQLException eccezione lanciata in caso di errore di connessione al
     *                      database.
     */
    public DBManager() throws SQLException {
        // connessione con oggetto Properties
        Properties dbprops = new Properties();
        dbprops.setProperty("user", "utente");
        dbprops.setProperty("password", "1221");
        connection = DriverManager.getConnection("jdbc:h2:./db/db", dbprops);

        initTables();
    }

    /**
     * Metodo che inizializza le tabelle del database.
     * @throws SQLException eccezione lanciata in caso di errore di connessione al
     *                      database.
     */
    private void initTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS GAMESTATUS ("
                    + "Username VARCHAR PRIMARY KEY,"
                    + "LastRoom INTEGER NOT NULL,"
                    + "Inventory VARCHAR, "
                    + "Time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");

            // Tabella per le stanze sbloccate
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS UNLOCKEDROOMS ("
                    + "Username VARCHAR,"
                    + "RoomId INTEGER,"
                    + "PRIMARY KEY (Username, RoomId),"
                    + "FOREIGN KEY (Username) REFERENCES GAMESTATUS(Username)"
                    + ")");

            // Tabella per gli oggetti nelle stanze
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ROOMOBJECTS ("
                    + "Username VARCHAR,"
                    + "RoomId INTEGER,"
                    + "ObjectId INTEGER,"
                    + "PRIMARY KEY (Username, RoomId, ObjectId),"
                    + "FOREIGN KEY (Username) REFERENCES GAMESTATUS(Username)"
                    + ")");
        }
    }
 
    /**
     * Metodo che salva lo stato di gioco di un utente.
     * 
     * @throws SQLException eccezione lanciata in caso di errore di connessione al
     *                      database.
     */
    public void saveGameStatus(GameStatus status) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Save basic game status
            String query = "MERGE INTO GAMESTATUS (Username, LastRoom, Inventory, Time) KEY (Username) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, status.getUsername());
                pstmt.setInt(2, status.getlastRoomId());
                pstmt.setString(3, status.getInventoryIdsAsString());
                pstmt.setTimestamp(4, Timestamp.valueOf(status.getTime()));
                pstmt.executeUpdate();
            }
    
            // Save unlocked rooms
            query = "DELETE FROM UNLOCKEDROOMS WHERE Username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, status.getUsername());
                pstmt.executeUpdate();
            }
    
            query = "MERGE INTO UNLOCKEDROOMS (Username, RoomId) KEY (Username, RoomId) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                for (Integer roomId : status.getUnlockedRoomIds()) {
                    pstmt.setString(1, status.getUsername());
                    pstmt.setInt(2, roomId);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
    
            // Save room objects
            query = "DELETE FROM ROOMOBJECTS WHERE Username = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, status.getUsername());
                pstmt.executeUpdate();
            }
    
            query = "INSERT INTO ROOMOBJECTS (Username, RoomId, ObjectId) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                for (Map.Entry<Integer, List<Integer>> entry : status.getRoomObjectIds().entrySet()) {
                    Integer roomId = entry.getKey();
                    for (Integer objectId : entry.getValue()) {
                        pstmt.setString(1, status.getUsername());
                        pstmt.setInt(2, roomId);
                        pstmt.setInt(3, objectId);
                        pstmt.addBatch();
                    }
                }
                pstmt.executeBatch();
            }
    
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    

    /**
     * Metodo che carica lo stato di gioco di un utente.
     * @param username nome utente.
     * @return stato di gioco.
     * @throws SQLException eccezione lanciata in caso di errore di connessione al
     *                      database.
     */
    public GameStatus loadGameStatus(String username) throws SQLException {
        GameStatus status = null;
        List<Integer> unlockedRoomIds = new ArrayList<>();
        Map<Integer, List<Integer>> roomObjects = new HashMap<>();
    
        // Load basic game status
        String query = "SELECT * FROM GAMESTATUS WHERE Username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int lastRoomId = rs.getInt("LastRoom");
                    String inventoryStr = rs.getString("Inventory");
                    List<Integer> inventory = new ArrayList<>();
                    if (inventoryStr != null && !inventoryStr.isEmpty()) {
                        for (String id : inventoryStr.split(",")) {
                            inventory.add(Integer.parseInt(id.trim()));
                        }
                    }
                    Timestamp time = rs.getTimestamp("Time");
                    status = new GameStatus(username, lastRoomId, inventory, time.toLocalDateTime());
                }
            }
        }
    
        if (status == null) {
            return null;
        }
    
        // Load unlocked rooms
        query = "SELECT RoomId FROM UNLOCKEDROOMS WHERE Username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    unlockedRoomIds.add(rs.getInt("RoomId"));
                }
            }
        }

    query = "SELECT RoomId, ObjectId FROM ROOMOBJECTS WHERE Username = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, username);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int roomId = rs.getInt("RoomId");
                int objectId = rs.getInt("ObjectId");
                roomObjects.computeIfAbsent(roomId, k -> new ArrayList<>()).add(objectId);
            }
        }
    }
    System.out.println("Loaded room objects from DB: " + roomObjects);
        // Aggiungo le stanze sbloccate allo stato di gioco
        status.setUnlockedRoomIds(unlockedRoomIds);
        status.setRoomObjectIds(roomObjects);
        System.out.println("Loaded game status for " + username + ". Unlocked rooms: " + unlockedRoomIds + ", room objects: " + roomObjects);
        return status;
    }
    
    

    /**
     * Metodo che restituisce lo stato di gioco di un utente.
     * 
     * @param username nome utente.
     * @return stato di gioco.
     * @throws SQLException eccezione lanciata in caso di errore di connessione al database.
     */
    public GameStatus getGameStatus(final String username) throws SQLException {
        Statement getStatement = connection.createStatement();
        String lastRoomId = "";
        String username1;
        List<Integer> inventoryIds = new ArrayList<Integer>();
        String query = "SELECT * FROM GameStatus WHERE username = '" + username + "'";
        ResultSet rs = getStatement.executeQuery(query);
        if (rs.next()) {
            username1 = rs.getString("Username");
            lastRoomId = rs.getString("LastRoom");
            String inventory = rs.getString("Inventory");
            Timestamp time = rs.getTimestamp("Time");
            if (!inventory.equals("")) {
                for (String id : inventory.split(",")) {
                    inventoryIds.add(Integer.valueOf(id));
                }
            }
            return new GameStatus(username, Integer.valueOf(lastRoomId), inventoryIds,
                    time.toLocalDateTime());
        }
        return null;

    }

    /**
     * Metodo che inserisce un nuovo stato di gioco nel database
     * 
     * @param gamestatus stato di gioco
     * @return codice di stato
     * @throws SQLException eccezione lanciata in caso di errore di connessione al database.
     */
    public int insertNewGameStatus(final GameStatus gamestatus) throws SQLException {
        int statusCode = 0;
        Statement updateStatement = connection.createStatement();
        String query = "SELECT * FROM GameStatus WHERE username = '" + gamestatus.getUsername() + "'";
        ResultSet rs = updateStatement.executeQuery(query);
        if (rs.next()) {
            query = "UPDATE GameStatus SET LastRoom = '" + gamestatus.getlastRoomId().toString() + "', Inventory = '"
                    + gamestatus.getInventoryIdsAsString() + "' WHERE username = '" + gamestatus.getUsername() + "'";
            statusCode = updateStatement.executeUpdate(query);
        } else {
            query = "INSERT INTO GameStatus (Username, LastRoom, Inventory) VALUES ('" + gamestatus.getUsername()
                    + "', '" + gamestatus.getlastRoomId().toString() + "', '" + gamestatus.getInventoryIdsAsString()
                    + "');";
            statusCode = updateStatement.executeUpdate(query);
        }

        return statusCode;

    }

    /**
     * Metodo che restituisce tutti gli stati di gioco salvati
     * 
     * @return lista di stati di gioco
     */
    public List<GameStatus> getAllSaves() {
        List<GameStatus> savedGame = new ArrayList<GameStatus>();
        try {
            Statement getStatement = connection.createStatement();
            String lastRoomId = "";
            String username1;
            List<Integer> inventoryIds = new ArrayList<Integer>();
            String query = "SELECT * FROM GameStatus";
            ResultSet rs = getStatement.executeQuery(query);
            while (rs.next()) {
                username1 = rs.getString("Username");
                lastRoomId = rs.getString("LastRoom");
                String inventory = rs.getString("Inventory");
                Timestamp time = rs.getTimestamp("Time");
                if (!inventory.equals("")) {
                    for (String id : inventory.split(",")) {
                        inventoryIds.add(Integer.valueOf(id));
                    }
                }
                savedGame.add(new GameStatus(username1,
                        Integer.valueOf(lastRoomId),
                        inventoryIds,
                        time.toLocalDateTime()));
            }
            return savedGame;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

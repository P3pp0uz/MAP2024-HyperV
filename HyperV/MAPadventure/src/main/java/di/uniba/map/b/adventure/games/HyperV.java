package di.uniba.map.b.adventure.games;

import di.uniba.map.b.adventure.Engine;
import di.uniba.map.b.adventure.GameDescription;
import di.uniba.map.b.adventure.RoomDescription;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.Room;

import java.io.File; 
import java.io.IOException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * ATTENZIONE: La descrizione del gioco è fatta in modo che qualsiasi gioco
 * debba estendere la classe GameDescription. L'Engine è fatto in modo che possa
 * eseguire qualsiasi gioco che estende GameDescription, in questo modo si
 * possono creare più gioci utilizzando lo stesso Engine.
 *
 * Diverse migliorie possono essere applicate: - la descrizione del gioco
 * potrebbe essere caricate da file o da DBMS in modo da non modificare il
 * codice sorgente - l'utilizzo di file e DBMS non è semplice poiché all'interno
 * del file o del DBMS dovrebbe anche essere codificata la logica del gioco
 * (nextMove) oltre alla descrizione di stanze, oggetti, ecc...
 */
public class HyperV extends GameDescription {

    /**
     * Oggetto di tipo Engine.
     */
    private Engine engine;

    /**
     * Costruttore della classe HyperV
     * 
     * @throws IOException
     */
    public HyperV() throws IOException {
        super();
    }

    /**
     * Metodo che inizializza i comandi
     */
    private void setCommands() throws Exception {

        Command inventory = new Command(CommandType.INVENTORY, "inventario");
        inventory.setAlias(new String[] { "inventary", "catalogo", "repertorio", "zaino", "borsa", "inv" });
        getCommands().add(inventory);

        Command nord = new Command(CommandType.NORD, "nord");
        nord.setAlias(new String[] { "n", "N", "Nord", "NORD" });
        getCommands().add(nord);
        Command sud = new Command(CommandType.SOUTH, "sud");
        sud.setAlias(new String[] { "s", "S", "Sud", "SUD" });
        getCommands().add(sud);
        Command est = new Command(CommandType.EAST, "est");
        est.setAlias(new String[] { "e", "E", "Est", "EST" });
        getCommands().add(est);
        Command ovest = new Command(CommandType.WEST, "ovest");
        ovest.setAlias(new String[] { "o", "O", "Ovest", "OVEST" });
        getCommands().add(ovest);

        Command end = new Command(CommandType.END, "end");
        end.setAlias(new String[] { "end", "fine", "esci", "muori", "ammazzati", "ucciditi", "suicidati", "exit" });
        getCommands().add(end);
        Command lose = new Command(CommandType.LOSE, "Becher");
        lose.setAlias(
                new String[] { "Becher", "becher", "BECHER", "pozione blu", "blu", "Pozione Blu", "POZIONE BLU" });
        getCommands().add(lose);
        Command win = new Command(CommandType.WIN, "Provetta");
        win.setAlias(new String[] { "Provetta", "provetta", "PROVETTA", "pozione rossa", "rosso", "Pozione Rossa",
                "POZIONE ROSSA" });
        getCommands().add(win);

        Command help = new Command(CommandType.HELP, "help");
        help.setAlias(new String[] { "HELP", "aiuto", "comandi", "help", "istruzioni" });
        getCommands().add(help);
        Command look = new Command(CommandType.LOOK_AT, "osserva");
        look.setAlias(new String[] { "guarda", "vedi", "trova", "cerca", "descrivi" });
        getCommands().add(look);

        Command pickup = new Command(CommandType.PICK_UP, "raccogli");
        pickup.setAlias(new String[] { "prendi" });
        getCommands().add(pickup);
        Command use = new Command(CommandType.USE, "usa");
        use.setAlias(new String[] { "utilizza" });
        getCommands().add(use);
        Command unlock = new Command(CommandType.UNLOCK, "sblocca");
        unlock.setAlias(new String[] { "apri" });
        getCommands().add(unlock);

        Command saveGame = new Command(CommandType.SAVE, "SAVE");
        saveGame.setAlias(new String[] { "SAVE", "save" });
        getCommands().add(saveGame);
        Command getSaves = new Command(CommandType.GET_SAVES, "GETSAVES");
        getSaves.setAlias(new String[] { "GETSAVES", "getsaves" });
        getCommands().add(getSaves);
        Command load = new Command(CommandType.LOAD_GAME, "LOADGAME");
        load.setAlias(new String[] { "LOADGAME", "loadgame" });
        getCommands().add(load);
    }

    /**
     * Metodo che inizializza le stanze del gioco e definisce la mappa
     */
    private void setRooms() {
        String namesFilePath = "./resources/names.txt";
        String descriptionsFilePath = "./resources/descriptions.txt";

        try {
            RoomDescription info = new RoomDescription(namesFilePath, descriptionsFilePath);

            Room stanzaTute1 = new Room(1, info.getName(0), info.getDescription(0));
            Room stanzaTute2 = new Room(2, info.getName(1), info.getDescription(1));
            Room corridoio1 = new Room(3, info.getName(2), info.getDescription(2));
            Room stanzaLetti = new Room(4, info.getName(3), info.getDescription(3));
            Room teleSud = new Room(5, info.getName(4), info.getDescription(4));
            teleSud.setLocked(true);
            Room teleNord = new Room(6, info.getName(5), info.getDescription(5));
            Room bob = new Room(7, info.getName(6), info.getDescription(6));
            bob.setVisited(false);
            Room stanzaRobot2 = new Room(9, info.getName(8), info.getDescription(8));
            stanzaRobot2.setLocked(true);
            Room corridoio2 = new Room(10, info.getName(9), info.getDescription(9));
            Room ufficio = new Room(12, info.getName(11), info.getDescription(11));
            ufficio.setLocked(true);
            Room enigma = new Room(13, info.getName(12), info.getDescription(12));
            Room libreria = new Room(14, info.getName(14), info.getDescription(14));
            libreria.setLocked(true);
            Room labEsperimenti1 = new Room(15, info.getName(15), info.getDescription(15));
            labEsperimenti1.setLocked(true);
            Room labEsperimenti2 = new Room(16, info.getName(16), info.getDescription(16));
            Room giardino = new Room(17, info.getName(17), info.getDescription(17));
            giardino.setLocked(true);
            Room salaControllo = new Room(18, info.getName(18), info.getDescription(18));
            Room laboratorio = new Room(19, info.getName(19), info.getDescription(19));
            laboratorio.setLocked(true);

            AdvObject chiave = new AdvObject(1, "Chiave",
                    "Una chiave. Potrebbe servire per aprire la porta.");
            chiave.setAlias(new String[] { "chiave" });
            chiave.setPickupable(true);
            stanzaTute2.getObjects().add(chiave);

            AdvObject badge = new AdvObject(2, "Badge di accesso",
                    "Un badge di accesso. Potrebbe servire per aprire la porta.");
            badge.setAlias(new String[] { "tessera", "badge" });
            badge.setPickupable(true);
            bob.getObjects().add(badge);

            AdvObject laser = new AdvObject(3, "Laser",
                    "Un piccolo laser di precisione. Potrebbe essere utile per scassinare una porta.");
            laser.setPickupable(true);
            laser.setAlias(new String[] { "laser" });
            laser.setUsable(true);
            stanzaRobot2.getObjects().add(laser);

            AdvObject torcia = new AdvObject(4, "Torcia",
                    "Una torcia funzionante. Potrebbe essere utile per illuminare la stanza.");
            torcia.setAlias(new String[] { "luce", "torcia", "faretto", "lampada", });
            torcia.setPickupable(true);
            torcia.setUsable(true);
            stanzaLetti.getObjects().add(torcia);

            AdvObject portalGun = new AdvObject(6, "Portal Gun",
                    "Una pistola che crea portali. Potrebbe essere utile per spostarsi rapidamente e raggiungere certe stanze.");
            portalGun.setAlias(new String[] { "pistola", "gun", "portal gun", "portale", "portal", "portalgun" });
            enigma.getObjects().add(portalGun);

            AdvObject note = new AdvObject(7, "Pezzo di carta",
                    "Un bigliettino logoro. Sembra esserci scritto qualcosa...");
            note.setAlias(new String[] { "biglietto", "Biglietto", "pezzo di carta", "note", "nota", "carta", "pezzo",
                    "foglio" });
            note.setPickupable(true);
            libreria.getObjects().add(note);

            AdvObject becher = new AdvObject(8, "Becher",
                    "Un becher che contiene una pozione blu. Potrebbe essere utile piu' avanti.");
            becher.setAlias(
                    new String[] { "Becher", "becher", "Pozione Blu", "pozione blu" });
            becher.setPickupable(true);
            giardino.getObjects().add(becher);

            AdvObject provetta = new AdvObject(9, "Provetta",
                    "Una provetta che contiene una pozione rossa. Potrebbe essere utile piu' avanti.");
            provetta.setAlias(
                    new String[] { "Provetta", "provetta", "Pozione Rossa", "pozione rossa" });
            provetta.setPickupable(true);
            labEsperimenti2.getObjects().add(provetta);

            AdvObject wordle = new AdvObject(11, "wordle");
            wordle.setAlias(new String[] { "porta" });
            wordle.setPickupable(false);
            wordle.setUnlockable(true);
            wordle.setPassword("furto");
            enigma.getObjects().add(wordle);

            AdvObject codice = new AdvObject(12, "Porta",
                    "Una porta da sbloccare con un codice di 5 lettere, forse hai il necessario per sapere quale sia...");
            codice.setAlias(new String[] { "password", "codice", "porta" });
            codice.setPickupable(false);
            codice.setUnlockable(true);
            codice.setPassword("virus");
            salaControllo.getObjects().add(codice);

            getObjectsList().add(chiave);
            getObjectsList().add(badge);
            getObjectsList().add(torcia);
            getObjectsList().add(portalGun);
            getObjectsList().add(becher);
            getObjectsList().add(provetta);
            getObjectsList().add(note);
            getObjectsList().add(laser);
            getObjectsList().add(wordle);
            getObjectsList().add(codice);

            stanzaTute1.setEast(stanzaTute2);
            stanzaTute2.setSouth(stanzaTute1);
            stanzaTute2.setNorth(corridoio1);
            corridoio1.setEast(teleSud);
            corridoio1.setSouth(stanzaTute2);
            teleSud.setNorth(teleNord);
            teleSud.setSouth(corridoio1);
            teleNord.setEast(bob);
            teleNord.setEast(bob);
            bob.setSouth(teleNord);
            bob.setNorth(stanzaRobot2);
            stanzaRobot2.setSouth(bob);
            teleNord.setNorth(corridoio2);
            teleNord.setWest(libreria);
            teleNord.setSouth(teleSud);
            corridoio2.setEast(labEsperimenti1);
            corridoio2.setWest(ufficio);
            labEsperimenti1.setNorth(labEsperimenti2);
            labEsperimenti1.setSouth(corridoio2);
            labEsperimenti2.setEast(labEsperimenti1);
            labEsperimenti2.setNorth(stanzaLetti);
            stanzaLetti.setSouth(labEsperimenti2);
            corridoio2.setSouth(teleNord);
            ufficio.setNorth(enigma);
            ufficio.setWest(corridoio2);
            enigma.setSouth(ufficio);
            libreria.setNorth(giardino);
            libreria.setSouth(teleNord);
            giardino.setNorth(salaControllo);
            giardino.setSouth(libreria);
            salaControllo.setWest(laboratorio);
            salaControllo.setSouth(giardino);
            laboratorio.setSouth(salaControllo);

            getRooms().add(stanzaLetti);
            getRooms().add(stanzaTute1);
            getRooms().add(stanzaTute2);
            getRooms().add(corridoio1);
            getRooms().add(stanzaLetti);
            getRooms().add(teleSud);
            getRooms().add(teleNord);
            getRooms().add(bob);
            getRooms().add(stanzaRobot2);
            getRooms().add(corridoio2);
            getRooms().add(ufficio);
            getRooms().add(enigma);
            getRooms().add(libreria);
            getRooms().add(labEsperimenti1);
            getRooms().add(labEsperimenti2);
            getRooms().add(giardino);
            getRooms().add(salaControllo);
            getRooms().add(laboratorio);

            // stanza iniziale
            setCurrentRoom(stanzaTute1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo che inizializza il gioco
     */
    @Override
    public void init() throws Exception {
        setCommands();
        setRooms();
    }

    /**
     * Imposta l'engine del gioco
     * 
     * @param engine l'engine del gioco
     */
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    /**
     * Metodo per gestire la riproduzione di un file audio
     * 
     * @param soundFilePath percorso del file audio
     */
    public void playSound(String soundFilePath) {
        try {
            File soundFile = new File(soundFilePath);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(soundFile));
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo per gestire il comando inventario
     * 
     * @return out
     */
    public String inventoryCommand() {
        String out;
        if (getInventory().isEmpty() == false) {
            out = "Nel tuo inventario hai:\n";
            for (AdvObject o : getInventory()) {
                out += "\n" + o.getName() + ": " + o.getDescription();
            }
        } else {
            out = "Non hai niente nell'inventario.";

        }
        return out;
    }

    /**
     * Metodo per gestire l'osservazione delle stanze
     * 
     * @return out
     */
    public String lookAtCommand() {
        String out = "";
        if (getCurrentRoom().getObjects().size() > 0) {
            out += getCurrentRoom().getDescription() + "\nNella stanza puoi trovare:\n";
            for (AdvObject o : getCurrentRoom().getObjects()) {
                if (!getInventory().contains(o)) {
                    if (o.getName() != "porta")
                        out += o.getName() + ": " + o.getDescription() + "\n";
                }  else {
                    out = getCurrentRoom().getDescription() + "Non c'è niente da osservare.";
                }
            }

        } else {
            out = getCurrentRoom().getDescription() + "\nNon ci sono oggetti con cui interagire.";
        }
        return out;
    }

    /**
     * Metodo per gestire la raccolta degli oggetti
     * 
     * @param p output del parser
     * @return out
     */
    public String pickUpCommand(ParserOutput p) {
        String out = "";
        if (!getCurrentRoom().getObjects().isEmpty()) {
            if (p.getObject() != null) {
                if (p.getObject().isPickupable()) {
                     if (p.getObject().getId() == 6) {
                        playSound("./resources/portachiusa.wav");
                        getInventory().add(p.getObject());
                        getCurrentRoom().getObjects().remove(p.getObject());
                        getCurrentRoom().getSouth().setLocked(true);
                        out = "Hai raccolto: " + p.getObject().getDescription();
                        out += "\nSenti un rumore alle tue spalle, la porta dietro di te si è chiusa! Noti uno schermo accendersi, ti informa che si è attivato un sistema di sicurezza. Hai 5 minuti per trovare la password giusta e sbloccare la porta. Per trovare la password digita \"risolvi enigma\" e usala per aprire la porta a Sud.";
                    } else {
                        getInventory().add(p.getObject());
                        getCurrentRoom().getObjects().remove(p.getObject());
                        out = "Hai raccolto: " + p.getObject().getDescription();
                    }
                } else {
                    out = "Non puoi raccogliere questo oggetto.";
                }
            } else {
                out = "Non esiste quell'oggetto.";
            }
        } else {
            out = "Non c'è nulla da raccogliere qui.";
        }
        return out;
    }

    /**
     * Metodo per gestire l'uso degli oggetti
     * 
     * @param p output del parser
     * @return out
     */
    public String useCommand(ParserOutput p) {
        String out = "";
        if (p.getInvObject() != null && !getInventory().isEmpty()) {
            if (p.getInvObject().isUsable()) {
                int idObject = p.getInvObject().getId();
                switch (idObject) {
                    case 1:
                        if (getCurrentRoom().getId() == 3) {
                            out = "Hai aperto la porta. Ora puoi entrare nella stanza a Est.";
                            getCurrentRoom().getEast().setLocked(false);
                        } else {
                            out = "Non c'è nessuna porta da aprire.";
                        }
                        break;
                    case 2:
                        if (getCurrentRoom().getId() == 10) {
                            out = "Hai aperto la porta del laboratorio esperimenti ad Est.";
                            getCurrentRoom().getEast().setLocked(false);
                        } else {
                            out = "Non c'è nessuna porta da aprire.";
                        }
                        break;
                    case 3:
                        if (getCurrentRoom().getId() == 6) {
                            out = "Hai aperto la porta a ovest. Vedi una strana scintilla uscire dal laser, lo butti a terra, ora non funziona più.";
                            playSound("./resources/laser.wav");
                            getInventory().remove(p.getInvObject());
                            getCurrentRoom().getWest().setLocked(false);
                        } else {
                            out = "Non ha senso usarlo qui.";
                        }
                        break;
                    case 4:
                        if (getCurrentRoom().getId() == 10) {
                            out = "Hai illuminato la stanza, ora puoi entrare e vedere cosa c'è dentro.";
                            getCurrentRoom().getWest().setLocked(false);
                        } else {
                            out = "Perchè vuoi sprecare le batterie?";
                        }
                        break;
                    case 6:
                        if (getCurrentRoom().getId() == 7 || getCurrentRoom().getId() == 14) {
                            out = "Hai creato il portale. Ora puoi accedere alla stanza a nord.";
                            getCurrentRoom().getNorth().setLocked(false);
                        } else {
                            out = "Non puoi usare questo oggetto in questa stanza, non c'è un portale di uscita a cui collegarsi.";
                        }
                        break;
                    case 7:
                        out = "Il biglietto dice:\n\"Vanadio, zInco, baRio, plUtonio, oSsigeno.\"\nCosa potrebbe significare? Probabilmente puoi ricavarci una password che ti servirà più avanti.";
                        break;
                }
            } else {
                out = "Non puoi usare questo oggetto in questa in questa stanza.";
            }
        } else if (getInventory().isEmpty()) {
            out = "Non hai nulla da usare! Devi prima raccogliere qualcosa.";
        } else {
            out = "Non hai nessun oggetto da usare in questa stanza.";
        }
        return out;
    }

    /**
     * Metodo per gestire lo sblocco degli oggetti
     * 
     * @param p output del parser
     * @return out
     */
    public String unlockCommand(ParserOutput p) {
        String out = "";
        if (p.getObject() != null) {
            if (p.getObject().isUnlockable()) {
                String objectPassword = p.getObject().getPassword();
                String inputPassword = p.getPasswordInput();

                if (inputPassword == null) {
                    out = "Non hai inserito una password.";
                } else if (objectPassword.equals(inputPassword)) {
                    out = "Hai sbloccato la porta!";
                    Room currentRoom = getCurrentRoom();
                    if (currentRoom != null && currentRoom.getWest() != null) {
                        currentRoom.getWest().setLocked(false);
                    }
                    if (currentRoom != null && currentRoom.getSouth() != null) {
                        currentRoom.getSouth().setLocked(false);
                    } else {
                        out = "Errore: la stanza o la direzione non esiste.";
                    }
                } else {
                    out = "Password errata.";
                }
            } else {
                out = "Non puoi sbloccare questo oggetto.";
            }
        } else {
            out = "Non c'è niente da sbloccare qui.";
        }
        return out;
    }

    /**
     * Metodo per gestire le mosse del giocatore
     * 
     * @param p output del parser
     * @return out
     */
    @Override
    public String nextMove(ParserOutput p) { // MODIFICARE
        boolean noroom = false;
        boolean newroom = false;
        boolean lockroom = false;
        String out = "";
        CommandType command = p.getCommand().getType();
        System.out.println(command);
        switch (command) {
            case NORD:
                if (getCurrentRoom().getNorth() != null) {
                    if (!getCurrentRoom().getNorth().isLocked()) {
                        if (getCurrentRoom().getId() == 16) {
                            playSound("./resources/goku.wav");
                        } else if (getCurrentRoom().getId() == 5) {
                            playSound("./resources/goku.wav");
                        } 
                        setCurrentRoom(getCurrentRoom().getNorth());
                        newroom = true;
                    } else if (getCurrentRoom().getId() == 7) {
                        out = "Quella parte della stanza e' troppo in alto ma vedi un portale a cui potresti collegarti in qualche modo.";
                    } else {
                        lockroom = true;
                    }
                } else if (getCurrentRoom().getId() == 3) {
                    out = "La porta è bloccata e non c'è modo di aprirla.";
                } else {
                    noroom = true;
                }
                break;
            case EAST:
                if (getCurrentRoom().getEast() != null) {
                    if (!getCurrentRoom().getEast().isLocked()) {
                        if (getCurrentRoom().getId() == 6) {
                            if (!getCurrentRoom().getEast().isVisited()) {
                                setCurrentRoom(getCurrentRoom().getEast());
                                out = " -------------------------------\n";
                                out += "\t" + getCurrentRoom().getName() + "\n";
                                out += " -------------------------------\n";
                                out += getCurrentRoom().getDescription()
                                        + "Prima di riuscire a fare qualsiasi azione vieni interrotto da qualcuno...o qualcosa. \"Ciao, che strano vedere qualcuno in questo posto! Il mio nome è Bobert e sono rimasto chiuso qui da quando lo scienziato che vive qui mi ha creato. Se vuoi andare da quel lato ti serve una strana pistola, almeno di solito fanno così.\"";
                                getCurrentRoom().setVisited(true);
                                break;
                            }
                        }
                        setCurrentRoom(getCurrentRoom().getEast());
                        if (getCurrentRoom().getId() == 4) {
                            playSound("./resources/goku.wav");
                        }
                        newroom = true;

                    } else if (getCurrentRoom().getId() == 3) {
                        out = "La porta è bloccata. Potrebbe servire una chiave per aprirla.";
                    } else {
                        lockroom = true;
                    }
                } else {
                    noroom = true;
                }
                break;
            case WEST:
                if (getCurrentRoom().getWest() != null) {
                    if (!getCurrentRoom().getWest().isLocked()) {
                        if (getCurrentRoom().getId() == 6) {
                            if (!getCurrentRoom().getWest().isVisited()) {
                                setCurrentRoom(getCurrentRoom().getWest());
                                out = " -------------------------------\n";
                                out += "\t" + getCurrentRoom().getName() + "\n";
                                out += " -------------------------------\n";
                                out += getCurrentRoom().getDescription()
                                        + "\"Qualche idea?\" domandi a Bobert, con il quale ormai hai preso un po' di confidenza. \"Io so che esiste un altro piano...ma non so come ci si arrivi, ma potresti utilizzare di nuovo quella pistola e andare a Nord.\" risponde.";
                                getCurrentRoom().setVisited(true);
                                break;
                            }
                        }
                        setCurrentRoom(getCurrentRoom().getWest());
                        newroom = true;
                    } else if (getCurrentRoom().getId() == 10) {
                        if (getCurrentRoom().getWest().isLocked()) {
                            out = "La stanza è troppo buia, non riesci a vedere nulla. Potrebbe servire una qualcosa per illuminarla.";
                        }
                    } else {
                        lockroom = true;
                    }
                } else {
                    noroom = true;
                }
                break;
            case SOUTH:
                if (getCurrentRoom().getSouth() != null) {
                    if (!getCurrentRoom().getSouth().isLocked()) {
                        setCurrentRoom(getCurrentRoom().getSouth());
                        if (getCurrentRoom().getId() == 5) {
                            playSound("./resources/goku.wav");
                        }
                        newroom = true;
                    } else {
                        lockroom = true;
                    }
                } else {
                    noroom = true;
                }
                break;
            case INVENTORY:
                out = inventoryCommand();
                break;
            case LOOK_AT:
                out = lookAtCommand();
                break;
            case PICK_UP:
                out = pickUpCommand(p);
                break;
            case USE:
                out = useCommand(p);
                break;
            case UNLOCK:
                out = unlockCommand(p);
                break;
            case END:
                out = "";
                break;
            case WIN:
                out = "";
                break;
            case LOSE:
                out = "";
                break;
            case DIE:
                out = "";
                break;
            default:
                out = "Non ho capito cosa devo fare! Prova con un altro comando.";
                break;
        }
        if (noroom) {
            out = "Non puoi andare in quella direzione.";
        } else if (newroom) {
            out = " -------------------------------\n";
            out += "\t" + getCurrentRoom().getName() + "\n";
            out += " -------------------------------\n";
            out += getCurrentRoom().getDescription();
        } else if (lockroom) {
            out = "La porta è bloccata.";
        }
        return out;
    }

}
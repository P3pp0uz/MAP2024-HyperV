package di.uniba.map.b.adventure.games;

import di.uniba.map.b.adventure.db.GameStatus;
import di.uniba.map.b.adventure.socket.Client;
import di.uniba.map.b.adventure.socket.ClientInterface;
import di.uniba.map.b.adventure.type.CommandOutputGui;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import di.uniba.map.b.adventure.type.CommandTypeGui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.URL;

public class HyperVGui extends JFrame {

    private JPanel mainPanel = null;
    private JPanel startPanel = null;
    private JTextArea textArea = null;
    private JScrollPane scrollPane = null;
    private JTextField textField = null;

    /**
     * Pannello per gestire i salvataggi
     */
    private JPanel contentPanel = null;

    /**
     * Pannello per l'immagine di sfondo dell'interfaccia grafica
     */
    private JPanel backgroundPanel = null;
    private Image backgroundImage = null;
    private boolean shouldCloseGame = false;

    /**
     * Printer per la stampa del testo
     */
    private Printer printer;
    private boolean isDead = false;

    /**
     * Client per la gestione della connessione
     */
    private static ClientInterface client;

    private boolean isPlayingWordle = false;
    private JPanel wordlePanel;
    private JTextField wordleInputField;
    private JTextArea wordleOutputArea;
    private WordleGame wordleGame;
    private JDialog wordleDialog;

    public HyperVGui() {
        try {
            client = new Client();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setTitle("HyperV");
        initMainPanel();
        initStartPanel();
        setVisible(true);
    }

    /**
     * Metodo che inizializza il pannello principale
     */
    private void initMainPanel() {
        // Ottieni le dimensioni dello schermo
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        JOptionPane frame = new JOptionPane();

        // Impostazioni della finestra principale
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (shouldCloseGame) {
                    try {
                        client.closeConnection();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    System.exit(0);
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (textArea != null && !isDead) {
                    int scelta = JOptionPane.showConfirmDialog(frame, "Vuoi salvare la partita?", "Salvataggio",
                            JOptionPane.YES_NO_OPTION);
                    if (scelta == JOptionPane.YES_OPTION) {
                        try {
                            openUsernameInputDialog(e);
                        } catch (IOException | ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else if (scelta == JOptionPane.NO_OPTION) {
                        e.getWindow().dispose(); // Chiude solo la finestra
                    }
                } else if (isDead) {
                    e.getWindow().dispose(); // Chiude solo la finestra
                } else {
                    e.getWindow().dispose(); // Chiude solo la finestra
                }
            }
        });

        setSize(screenWidth, screenHeight);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Pannello principale
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);
    }

    /**
     * Metodo che apre il pannello di conferma di chiusura del gioco
     */
    private void openUsernameInputDialog(WindowEvent e)
            throws IOException, ClassNotFoundException {
        boolean validUsername = false;

        while (!validUsername) {
            JOptionPane input = new JOptionPane();
            JTextField usernameField = new JTextField();
            Object[] message = {
                    "Username:", usernameField
            };

            int option = JOptionPane.showOptionDialog(input, message, "Inserisci Username",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new Object[] { "OK", "Cancel" }, "OK");

            if (option == JOptionPane.OK_OPTION) {
                String username = usernameField.getText();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(input, "Il campo non può essere vuoto!", "Errore",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    client.executeCommand("STOPTIMER");
                    textField.setEditable(false);
                    client.sendResourcesToServer("username:" + username);
                    client.executeCommand("SAVE");
                    validUsername = true;
                    shouldCloseGame = true;
                    e.getWindow().dispose();
                }
            } else {
                validUsername = true;
                shouldCloseGame = false;
            }
        }
    }

    /**
     * Metodo che inizializza il pannello di avvio del gioco
     */
    private void initStartPanel() {
        startPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImageIcon = new ImageIcon("./resources/start.png");
                Image backgroundImage = backgroundImageIcon.getImage();
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        startPanel.setLayout(null);
        mainPanel.add(startPanel, BorderLayout.CENTER);

        JButton startButton = createCustomButton("./resources/newgame.png");
        JButton loadGameButton = createCustomButton("./resources/loadgame.png");

        startButton.addActionListener(e -> {
            try {
                startGame();
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        loadGameButton.addActionListener(e -> {
            try {
                loadGame();
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        startPanel.add(startButton);
        startPanel.add(loadGameButton);

        startPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateButtonPositions();
            }
        });

        updateButtonPositions();
    }

    /**
     * Metodo che crea un JButton personalizzato
     * 
     * @param imagePath path dell'immagine
     * @return JButton personalizzato
     */
    private JButton createCustomButton(String imagePath) {
        return new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                super.paintComponent(g);
                ImageIcon backgroundImageIcon = new ImageIcon(imagePath);
                Image backgroundImage = backgroundImageIcon.getImage();
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    /**
     * Metodo che aggiorna la posizione dei bottoni
     */
    private void updateButtonPositions() {
        int panelWidth = startPanel.getWidth();
        int panelHeight = startPanel.getHeight();

        int buttonWidth = (int) (panelWidth * 0.2);
        int buttonHeight = (int) (panelHeight * 0.13);
        int buttonX = (panelWidth - buttonWidth) / 2;

        int startButtonY = panelHeight - (2 * buttonHeight) - 100;
        startPanel.getComponent(0).setBounds(buttonX, startButtonY, buttonWidth, buttonHeight);

        int loadButtonY = startButtonY + buttonHeight + 5;
        startPanel.getComponent(1).setBounds(buttonX, loadButtonY, buttonWidth, buttonHeight);
    }

    /*
     * Metodo per impostare lo sfondo
     */
    public void setBackgroundImageFromPath(String path) {
        // Rimuovi il prefisso del percorso se presente
        String fileName = path.replaceAll("^.*?resources/", "");

        // Prova prima come risorsa
        URL resourceUrl = getClass().getResource("/resources/" + fileName);
        if (resourceUrl != null) {
            this.backgroundImage = new ImageIcon(resourceUrl).getImage();
            backgroundPanel.repaint();
        } else {
            // Se non trova come risorsa, prova come file
            File file = new File("./resources/" + fileName);
            if (file.exists()) {
                this.backgroundImage = new ImageIcon(file.getAbsolutePath()).getImage();
                backgroundPanel.repaint();
                System.out.println("Immagine caricata come file: " + file.getAbsolutePath());
            } else {
                System.err.println("Impossibile trovare l'immagine: " + path);
            }
        }
    }

    /**
     * Metodo che inizializza il pannello di sfondo
     */
    private void initBackgroundPanel(int roomId) {
        String imagePath = "./resources/" + roomId + ".png";
        ImageIcon backgroundImageIcon = new ImageIcon(imagePath);
        if (backgroundImageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            System.out.println("Immagine caricata con successo");
        } else {
            System.err.println("Errore nel caricamento dell'immagine");
        }
        backgroundImage = backgroundImageIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Carica l'immagine di sfondo
                try {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        backgroundPanel.setPreferredSize(new Dimension(getWidth() - 14, 0));
        backgroundPanel.setLayout(new BorderLayout());
        mainPanel.add(backgroundPanel, BorderLayout.EAST);
    }

    /**
     * Metodo che inizializza il pannello di sfondo nel caso del caricamento di una
     * partita salvata
     */
    private void initLoadGameBackgroundPanel() {
        ImageIcon backgroundImageIcon = new ImageIcon("./resources/start.png");
        backgroundImage = backgroundImageIcon.getImage().getScaledInstance(backgroundImageIcon.getIconWidth(),
                backgroundImageIcon.getIconHeight(), Image.SCALE_SMOOTH);
        // Creazione del pannello per l'immagine sopra l'inputPanel e a destra del
        // sidePanel
        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Carica l'immagine di sfondo
                try {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mainPanel.add(backgroundPanel, BorderLayout.CENTER);
    }

    /**
     * Metodo che inizializza il pannello di output
     */
    private void initOutputArea() throws IOException, ClassNotFoundException {
        // Crea un pannello personalizzato per lo sfondo colorato
        JPanel backgroundColorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                Color background = new Color(100, 100, 100, 100);
                g2.setColor(background);
                int height = getHeight() - 497;
                g2.fillRect(0, 497, getWidth(), height);
                g2.dispose();
            }
        };
        backgroundColorPanel.setOpaque(false);
        backgroundColorPanel.setLayout(null);

        // Crea la JTextArea
        textArea = new JTextArea();
        printer = new Printer(textArea, 5);
        String firstDescription = "Sei finalmente riuscito ad entrare nel laboratorio di Dexter e puoi proseguire la tua ricerca dell'HyperV, un pericoloso e potente virus. Risolvi gli enigmi e trova la via giusta per raggiungere il tuo obiettivo. La mappa in alto a sinistra puo' aiutarti ad orientarti, il triangolo rosso indica dove ti trovi ed e' orientato verso Nord.\n Buona fortuna!\n\nDigita HELP per visualizzare i comandi disponibili\n\n";
        performCommand(new CommandOutputGui(CommandTypeGui.DISPLAY_TEXT, firstDescription));
        textArea.setFont(new Font("Consolas", Font.PLAIN, 18));
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setForeground(Color.WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Crea la JScrollPane per avvolgere la JTextArea
        scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Aggiunge lo scrollPane al pannello di sfondo
        backgroundColorPanel.add(scrollPane);

        // Aggiunge il pannello di sfondo al pannello principale
        backgroundPanel.add(backgroundColorPanel, BorderLayout.CENTER);

        // Aggiunge un ComponentListener per mantenere le dimensioni e la posizione
        // corrette
        backgroundPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int panelHeight = backgroundPanel.getHeight();
                int greyRectHeight = panelHeight - 537; // 497 (top) + 77 (bottom)
                backgroundColorPanel.setPreferredSize(new Dimension(backgroundPanel.getWidth(), panelHeight));
                scrollPane.setBounds(0, 497, backgroundPanel.getWidth(), greyRectHeight);
                backgroundPanel.revalidate();
                backgroundPanel.repaint();
            }
        });
    }

    /**
     * Metodo che inizializza il pannello di output nel caso del caricamento di una
     * partita salvata
     */
    private void initOutputLoadedGamesArea() {

        Color backgroundColor = new Color(0, 0, 0, 150); // Colore di sfondo con opacità ridotta (valori RGB: 0, 0, 0,
                                                         // opacità)

        contentPanel = new JPanel(); // Pannello principale che conterrà i pannelli delle righe
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Layout per allineare verticalmente gli
                                                                               // elementi
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);

        scrollPane = new JScrollPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(backgroundColor);
                g2.fillRect(0, 0, getWidth(), getHeight()); // Riempie l'area con il colore di sfondo
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        scrollPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
        scrollPane.setOpaque(false); // Rende lo sfondo trasparente
        scrollPane.getViewport().setOpaque(false); // Rende lo sfondo del viewport trasparente
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.NORTH);
    }

    /**
     * Metodo che inizializza il pannello di input
     */
    private void initInputArea() {
        Color background = new Color(0, 20, 70, 150); // Colore di sfondo con opacità ridotta (valori RGB: 0, 0, 0,
                                                      // opacità)
        textField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(background);
                g2.fillRect(0, 0, getWidth(), getHeight()); // Riempie l'area con il colore di sfondo
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        textField.setOpaque(false); // Rendi lo sfondo trasparente
        textField.setPreferredSize(new Dimension(getWidth() - 150, 40));
        textField.setForeground(Color.WHITE); // Colore del testo
        textField.setFont(new Font("Consolas", Font.PLAIN, 18)); // Font del testo
        backgroundPanel.add(textField, BorderLayout.SOUTH);

        textField.addActionListener(e -> {
            CommandOutputGui responseToGUI;
            Printer printer = new Printer(textArea, 10);
            printer.setDelay(10);
            String inputText = textField.getText();
            inputText = inputText.trim().toLowerCase();
            if (inputText.equals("risolvi enigma")) {
                try {
                    openWordlePanel();
                } catch (ClassNotFoundException | IOException e1) {
                    throw new RuntimeException(e1);
                }
            } else {
                try {
                    responseToGUI = client.executeCommand(inputText); // Esegui il comando inserito nella JTextField
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    performCommand(responseToGUI); // Stampa la risposta carattere per carattere nella JTextArea
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
            textField.setText(""); // Resetta il contenuto della JTextField
            scrollPane.setVisible(true); // Mostra la JScrollPane
            textArea.setCaretPosition(textArea.getDocument().getLength()); // Scrolla la JTextArea fino alla fine del
                                                                           // testo
        });

        // Imposta la JTextArea per lo scorrimento automatico
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

    }

    // Apertura del pannello di wordle
    private void openWordlePanel() throws ClassNotFoundException, IOException {
        wordlePanel = new JPanel(new BorderLayout());
        wordlePanel.setPreferredSize(new Dimension(700, 600));
        textField.setEditable(false);

        JLabel timerLabel = new JLabel("Tempo rimanente: 500s");
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        wordlePanel.add(timerLabel, BorderLayout.NORTH);

        wordleOutputArea = new JTextArea();
        wordleOutputArea.setEditable(false);
        wordleOutputArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        wordleOutputArea.setBackground(Color.BLACK);
        wordleOutputArea.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(wordleOutputArea);
        wordlePanel.add(scrollPane, BorderLayout.CENTER);

        wordleInputField = new JTextField();
        wordleInputField.setPreferredSize(new Dimension(600, 50));
        wordleInputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        wordleInputField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        wordleInputField.setBackground(Color.darkGray);
        wordleInputField.setForeground(Color.WHITE);
        wordleInputField.addActionListener(e -> {
            try {
                processWordleInput();
            } catch (ClassNotFoundException | IOException e1) {
                throw new RuntimeException(e1);
            }
        });
        wordlePanel.add(wordleInputField, BorderLayout.SOUTH);

        wordleGame = new WordleGame(wordleOutputArea);
        wordleGame.setOnGameEndCallback(() -> {
            try {
                closeWordlePanel();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(wordleInputField, BorderLayout.CENTER);
        wordlePanel.add(inputPanel, BorderLayout.SOUTH);

        CommandOutputGui initialOutput = wordleGame.play("");
        wordleOutputArea.append(initialOutput.getText());

        wordleDialog = new JDialog(this, "Pannello di controllo", false);
        wordleDialog.setContentPane(wordlePanel);
        wordleDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        wordleDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    closeWordlePanel();
                } catch (ClassNotFoundException | IOException e1) {
                    throw new RuntimeException(e1);
                }
            }
        });
        wordleDialog.setResizable(false);
        wordleDialog.pack();
        wordleDialog.setLocationRelativeTo(this);
        wordleDialog.setVisible(true);

        wordleGame.startTimer(timerLabel);
    }

    // Chiusura del pannello di wordle
    private void closeWordlePanel() throws ClassNotFoundException, IOException {
        wordleGame.stopTimer();
        textField.setEditable(true);
        wordleDialog.dispose();
    }

    // Processa l'input del gioco Wordle
    private void processWordleInput() throws ClassNotFoundException, IOException {
        String guess = wordleInputField.getText().trim().toLowerCase();
        CommandOutputGui result = wordleGame.play(guess);
        wordleOutputArea.append(result.getText() + "\n");

        // Controlla il tipo di risultato e aggiorna l'interfaccia utente di conseguenza
        if (result.getType() == CommandTypeGui.WORDLE_GUESS) {
            wordleInputField.setEditable(false);
            textField.setEditable(true);
            Timer timer = new Timer(5000, e -> {
                try {
                    closeWordlePanel();
                } catch (ClassNotFoundException | IOException e1) {
                    throw new RuntimeException(e1);
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else if (result.getType() == CommandTypeGui.END) {
            if (result.getText().contains("Il tempo è scaduto!")) {
                wordleOutputArea.append("E' scattato l'allarme, ti hanno scoperto\n");
            }
            Timer chiuso = new Timer(3500, e -> {
                try {
                    closeWordlePanel();
                } catch (ClassNotFoundException | IOException e1) {
                    throw new RuntimeException(e1);
                }
            });
            chiuso.setRepeats(false);
            chiuso.start();
            wordleInputField.setEditable(false);
            textField.setEditable(true);
        }
        wordleInputField.setText("");
    }

    /**
     * Metodo che esegue il comando
     * 
     * @param command Comando da eseguire
     */
    public void performCommand(CommandOutputGui command)
            throws IOException, ClassNotFoundException {
        switch (command.getType()) {
            case LOAD_GAME:
                startLoadedGame(Integer.parseInt(command.getResource()));
                break;
            case CHANGE_ROOM:
                String imagePath = command.getResource();
                this.setBackgroundImageFromPath(imagePath);
                appendAreaText(command.getText());
                break;
            case DISPLAY_TEXT:
                appendAreaText(command.getText());
                break;
            case HELP:
                appendAreaText(helpCommand());
                break;
            case WORDLE_START:
                startWordleGame();
                break;
            case WORDLE_GUESS:
                handleWordleGuess(command.getText());
                break;
            case END:
                endCommand(command.getText());
                break;
            case LOSE:
                loseCommand(command.getText());
                break;
            case WIN:
                winCommand(command.getText());
                break;
            default:
                System.out.println("Comando non riconosciuto");
                break;
        }
    }

    /**
     * Metodo che inizia il gioco Wordle
     */
    private void startWordleGame() {
        wordleGame = new WordleGame(textArea);
        isPlayingWordle = true;
        appendAreaText("Wordle game started! Guess a 5-letter word.");
    }

    /**
     * Metodo che gestisce il tentativo di indovinare la parola nel gioco Wordle
     * 
     * @param guess tentativo di indovinare la parola
     */
    private void handleWordleGuess(String guess) {
        if (isPlayingWordle) {
            CommandOutputGui result = wordleGame.play(guess);
            appendAreaText(result.getText());
            if (result.getType() == CommandTypeGui.END) {
                isPlayingWordle = false;
                appendAreaText("Hai indovinato la parola, ora puoi usarla per aprire la porta.");
            }
        }
    }

    /**
     * Metodo che scrive il testo nella text area
     * 
     * @param text testo da scrivere
     */
    public void appendAreaText(String text) {
        printer.printText(text);
    }

    /**
     * Metodo che stampa a video il messaggio di help
     * 
     * @return messaggio di help
     */
    public String helpCommand() { // MODIFICARE
        return ("Sei alla ricerca del potente virus HyperV, esplora il laboratorio per portare a termine la tua missione.\n"
                +
                "Per portare a termire la tua missione devi essere in grado di osservare attentamente i dintorni ed essere il piu' rapido possibile,"
                +
                "cercando di non tralasciare nulla indietro...\n" +
                "\n" +
                "Per spostarti usa:\n" +
                "\n" +
                "- NORD, SUD, EST, OVEST oppure \n- N, S, E, O\n"
                + "La mappa in alto a sinistra puo' aiutarti ad orientarti, il triangolo rosso indica dove ti trovi ed e' orientato verso Nord."
                +
                "\n" +
                ". Se vuoi rileggere la descrizione della stanza in cui ti trovi digita:\n" +
                "\n" +
                "- OSSERVA\n" +
                "\n" +
                "Comandi fondamentali per interagire con gli oggetti:\n" +
                "\n" +
                "- PRENDI oggetto: raccoglie un oggetto presente nella stanza e lo aggiunge nel tuo inventario\n" +
                "- USA oggetto: utilizza un oggetto\n" +
                "- SBLOCCA oggetto \"password\": sblocca un oggetto attraverso una password\n" +
                "\n" +
                "Altri comandi che potrebbero esserti d'aiuto:\n" +
                "\n" +
                "- INV elenca gli oggetti nel tuo inventario\n" +
                "- HELP presenta lo scopo del gioco e i comandi disponibili.\n");
    }

    /**
     * Metodo che esegue il comando di sconfitta
     * 
     * @param command comando da eseguire
     */
    public void loseCommand(String command) throws IOException, ClassNotFoundException {
        textField.setText("");
        textField.setEditable(false);
        textArea.setText("\nMischiando i due composti hai causato una grande esplosione. \n" + "\n" + " GAME OVER");
        isDead = true;
    }

    /**
     * Metodo che esegue il comando di vittoria
     * 
     * @param command comando da eseguire
     */
    public void winCommand(String command) throws IOException, ClassNotFoundException {
        isDead = true;
        textField.setText("");
        textField.setEditable(false);
        textArea.setText("\nHai creato un nuovo composto chimico in grado di controllare le menti di tutti. \n" + " \n"
                + " HAI VINTO!");
        textArea.setEditable(false);
    }

    /**
     * Metodo che esegue il comando di fine partita
     * 
     * @param command comando da eseguire
     */
    public void endCommand(String command) throws IOException, ClassNotFoundException {
        isDead = true;
        textField.setText("");
        textField.setEditable(false);
        textArea.setText("\nVedi arrivare un robot nella tua direzione e ti colpisce, cadi a terra e muori. \n" +
                "Bobert ti dice addio. \n \n GAME OVER");
        textArea.setEditable(false);
    }

    /**
     * Metodo che fa partire il gioco inizializzando tutte le componenti
     */
    private void startGame() throws IOException, ClassNotFoundException {
        mainPanel.remove(startPanel);
        initBackgroundPanel(1);
        initOutputArea();
        initInputArea();
        revalidate();
    }

    /**
     * Metodo che fa partire il gioco salvato inizializzando tutte le componenti
     */
    private void startLoadedGame(int id)
            throws IOException, ClassNotFoundException {
        initBackgroundPanel(id);
        initOutputArea();
        initInputArea();
        revalidate();
    }

    /**
     * Metodo che carica le partite salvate
     */
    private void loadGame()
            throws SQLException, IOException, ClassNotFoundException {
        mainPanel.remove(startPanel);
        initLoadGameBackgroundPanel();
        initOutputLoadedGamesArea();
        showSavedGames();
        revalidate();
    }

    /**
     * Metodo che mostra le partite salvate
     */
    private void showSavedGames()
            throws IOException, ClassNotFoundException {

        List<GameStatus> savedGames = (List<GameStatus>) client.getResourcesFromServer("resources:GETSAVES");

        Color backgroundColor = new Color(0, 20, 70, 150); // Colore di sfondo con opacità ridotta
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, savedGames.size() * 50));
        scrollPane.setViewportView(new SavedGame(savedGames, mainPanel, contentPanel) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(backgroundColor);
                g2.fillRect(0, 0, getWidth(), getHeight()); // Riempie l'area con il colore di sfondo
                super.paintComponent(g2);
                g2.dispose();
            }
        });

        // Aggiorna la visualizzazione della scroll pane
        scrollPane.revalidate();
        scrollPane.repaint();
    }

    /**
     * Pannello che mostra le partite salvate
     */
    public class SavedGame extends JPanel {
        /**
         * Costruttore del pannello
         * 
         * @param savedGames   lista delle partite salvate
         * @param mainPanel    pannello principale
         * @param contentPanel pannello di contenuto
         */
        public SavedGame(List<GameStatus> savedGames, JPanel mainPanel, JPanel contentPanel) {
            setLayout(new GridLayout(savedGames.size(), 1)); // Imposta il layout con una riga per ogni partita salvata
            this.setOpaque(false);
            this.setPreferredSize(new Dimension(this.getPreferredSize().width, savedGames.size() * 50)); // Imposta la
                                                                                                         // dimensione
                                                                                                         // del pannello
            for (GameStatus game : savedGames) {
                Color background = new Color(0, 0, 0, 0);
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Imposta il layout con allineamento
                                                                            // sinistro
                panel.setOpaque(false); // Imposta lo sfondo trasparente
                panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 50)); // Imposta la dimensione del
                                                                                           // pannello
                String rowString = game.getUsername() + " - " + game.getlastRoomId() + " - " + game.getFormattedTime();
                JLabel rowLabel = new JLabel(rowString) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setColor(background);
                        g2.fillRect(0, 0, getWidth(), getHeight()); // Riempie l'area con il colore di sfondo
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                rowLabel.setOpaque(false);
                rowLabel.setFont(new Font("Consolas", Font.PLAIN, 18));
                rowLabel.setForeground(Color.WHITE);
                panel.add(rowLabel);

                // Aggiunge un listener per il click del mouse e per il passaggio sopra con il
                // mouse
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { // Quando passi sopra con il mouse
                        panel.setOpaque(true);
                        Color hoverColor = new Color(70, 70, 70, 255);
                        panel.setBackground(hoverColor); // Imposta il colore di sfondo quando passi sopra con il mouse
                        panel.repaint(); // Forza l'aggiornamento grafico del pannello
                    }

                    @Override
                    public void mouseExited(MouseEvent e) { // Quando esci con il mouse
                        panel.setOpaque(false); // Ripristina l'opacità del pannello a false
                        panel.setBackground(new Color(0, 0, 0, 0)); // Ripristina il colore di sfondo trasparente
                        panel.repaint(); // Forza l'aggiornamento grafico del pannello
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) { // Quando clicchi sul pannello
                        mainPanel.remove(contentPanel);
                        mainPanel.revalidate();
                        mainPanel.repaint();
                        try {
                            client.sendResourcesToServer("username:" + game.getUsername());
                            CommandOutputGui response = client.executeCommand("LOADGAME"); // Carica la partita
                            performCommand(response); // Esegue il comando

                        } catch (IOException | ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                add(panel); // Aggiunge il pannello alla lista
            }
        }
    }

    /**
     * Classe per la stampa del testo con un effetto di scrittura
     */
    public static class Printer {

        private final JTextArea textArea;
        private int delay;
        private StringBuilder buffer;

        /**
         * Costruttore della classe
         * 
         * @param textArea textArea
         * @param delay    delay
         */
        public Printer(JTextArea textArea, int delay) {
            this.textArea = textArea;
            this.delay = delay;
            this.buffer = new StringBuilder();
        }

        public void skipPrinting() {
            if (buffer.length() > 0) {
                textArea.append(buffer.toString());
                buffer.setLength(0);
            }
        }

        /**
         * Metodo che imposta il delay
         * 
         * @param delay delay
         */
        public void setDelay(int delay) {
            this.delay = delay;
        }

        /**
         * Metodo che stampa il testo con un effetto di scrittura
         * 
         * @param inputText
         */
        public void printText(String inputText) {
            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                /**
                 * Metodo che separa il testo in caratteri e li stampa uno alla volta
                 * 
                 * @return
                 * @throws Exception
                 */
                @Override
                protected Void doInBackground() throws Exception {
                    String[] chars = inputText.split("");
                    for (String c : chars) {
                        publish(c);
                        Thread.sleep(delay);
                    }
                    return null;
                }

                /**
                 * Metodo che aggiunge il testo alla JTextArea
                 * 
                 * @param chunks intermediate results to process
                 *
                 */
                @Override
                protected void process(java.util.List<String> chunks) {
                    for (String c : chunks) {
                        textArea.append(c); // Aggiungi il testo alla JTextArea, aggiungendo un a capo
                    }
                }
            };

            worker.execute();
            textArea.append("\n"); // Aggiungi un a capo alla fine del testo
        }

    }

    /**
     * Metodo main
     * 
     * @param args argomenti
     */
    public static void main(String[] args) {
        HyperVGui gui = new HyperVGui();
    }
}

package di.uniba.map.b.adventure.games;

import di.uniba.map.b.adventure.type.CommandOutputGui;
import di.uniba.map.b.adventure.type.CommandTypeGui;

import javax.swing.*;


public class WordleGame {
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 10;
    private String targetWord;
    private int currentAttempt;
    private JTextArea outputArea;
    private boolean gameStarted = false;
    private volatile int timeRemaining;
    private volatile boolean isRunning;
    private Thread timerThread;
    private int timerSpeed = 1000;
    private Runnable onGameEndCallback;

    // Costruttore della classe WordleGame
    public WordleGame(JTextArea outputArea) {
        this.outputArea = outputArea;
        targetWord = "furto";
        currentAttempt = 0;
        this.timeRemaining = 300; // Impostiamo il timer iniziale a 300 secondi
        this.isRunning = true;
    }

    // Metodo per iniziare il gioco
    public CommandOutputGui play(String guess) {
        StringBuilder output = new StringBuilder();

        if (!gameStarted) {
            output.append("Ricerca password...\n");
            output.append("Indovina la parola di ").append(WORD_LENGTH).append(" lettere. Hai ").append(MAX_ATTEMPTS).append(" tentativi.\nin caso di lettera corretta ma nel posto sbagliato avrai '?'\n");
            gameStarted = true;
            return new CommandOutputGui(CommandTypeGui.DISPLAY_TEXT, output.toString());
        }

        if (timeRemaining <= 0) {
            return new CommandOutputGui(CommandTypeGui.END, "Il tempo è scaduto!");
        }

        if (guess.length() != WORD_LENGTH) {
            return new CommandOutputGui(CommandTypeGui.DISPLAY_TEXT, "La parola deve essere di " + WORD_LENGTH + " lettere. Riprova.");
        }

        currentAttempt++;
        String result = makeGuess(guess);
        output.append("Tentativo ").append(currentAttempt).append("/").append(MAX_ATTEMPTS).append(": ").append(guess).append("\n");
        output.append(result).append("\n");

        if (!guess.equals(targetWord)) {
            timerSpeed = Math.max(timerSpeed - 150, 100);
        }

        if (guess.equals(targetWord) || currentAttempt >= MAX_ATTEMPTS) {
            if (guess.equals(targetWord)) {
                output.append("Hai indovinato la parola! Ora puoi usarla per sbloccare la porta.\n");
                return new CommandOutputGui(CommandTypeGui.WORDLE_GUESS, output.toString());
            } else {
                output.append("Spiacente, hai esaurito i tentativi.");
                return new CommandOutputGui(CommandTypeGui.END, output.toString());
            }
        } else if (timeRemaining <= 0) {
            output.append("Il tempo è scaduto!");
            return new CommandOutputGui(CommandTypeGui.END, output.toString());
        }

        return new CommandOutputGui(CommandTypeGui.DISPLAY_TEXT, output.toString());
    }

    // Metodo per stampare il feedback
    private String makeGuess(String guess) {
        return "Feedback: " + getFeedback(targetWord, guess);
    }

    // Metodo per generare il feedback
    private String getFeedback(String targetWord, String guess) {
        char[] feedback = new char[WORD_LENGTH];
        boolean[] targetUsed = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == targetWord.charAt(i)) {
                feedback[i] = guess.charAt(i);
                targetUsed[i] = true;
            } else {
                feedback[i] = '_';
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (feedback[i] == '_') {
                for (int j = 0; j < WORD_LENGTH; j++) {
                    if (!targetUsed[j] && guess.charAt(i) == targetWord.charAt(j)) {
                        feedback[i] = '?';
                        targetUsed[j] = true;
                        break;
                    }
                }
            }
        }

        return new String(feedback);
    }

    // Metodo per iniziare il timer
    public void startTimer(JLabel timerLabel) {
        timerThread = new Thread(() -> {
            while (isRunning && timeRemaining > 0) {
                try {
                    Thread.sleep(timerSpeed);
                    timeRemaining--;
                    SwingUtilities.invokeLater(() -> timerLabel.setText("Tempo rimanente: " + timeRemaining + "s"));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            if (timeRemaining <= 0) {
                SwingUtilities.invokeLater(() -> endGame("Il tempo è scaduto!"));
                
            }
        });
        timerThread.start();
    }

    // Metodo per impostare il callback alla fine del gioco
        public void setOnGameEndCallback(Runnable callback) {
        this.onGameEndCallback = callback;
    }

    // Metodo per terminare il gioco
    private void endGame(String message) {
        isRunning = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
        StringBuilder output = new StringBuilder();
        output.append(message);
        output.append("\nHai esaurito il tempo.");
        outputArea.append(output.toString());

        CommandOutputGui endOutput = new CommandOutputGui(CommandTypeGui.END, output.toString());

        if (onGameEndCallback != null) {
            Timer timer = new Timer(3000, e -> SwingUtilities.invokeLater(onGameEndCallback));
            timer.setRepeats(false);
            timer.start();
        }
    }

    // Metodo per fermare il timer
    public void stopTimer() {
        isRunning = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
    }
}

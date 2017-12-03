package assignment1.server.net;

import assignment1.server.model.HangmanGame;
import assignment1.shared.Commands;
import assignment1.shared.OutputHandler;
import java.io.IOException;
import java.net.Socket;

/**
 * Object that handles the communication with the client and the game.
 */
class GameHandler implements Runnable {

    private HangmanGame game = new HangmanGame();
    private ClientSocket clientSocket;
    private OutputHandler output;
    boolean running = false;

    /**
     * Creates a new Game handler.
     * @param socket The client socket.
     * @param output The Handler that controls output on the server.
     */
    GameHandler(Socket socket, OutputHandler output) {
        this.clientSocket = new ClientSocket(socket);
        this.output = output;
    }

    @Override
    public void run() {
        running = true;
        output.sendMessage("Client connected");
        clientSocket.sendMessage(
                "Welcome to the Hangman game server. The following commands are available:\n"
                + Commands.PLAY + " - Starts a new hangman game\n"
                + Commands.GET_SCORE + " - Gets your current score in the hangman game\n"
                + "All other input counts as guesses in the hangman game.\n"
                + "If a single characters is entered you're asking if the word contains this letter.\n"
                + "If a word is entered you're asking if the word is this word."
        );
        while (running) {
            try {
                String response = clientSocket.recieveMessage().toLowerCase();
                switch (response) {
                    case Commands.DISCONNECT:
                        disconnect();
                        break;
                    case Commands.PLAY:
                        clientSocket.sendMessage(game.newRound());
                        break;
                    case Commands.GET_SCORE:
                        clientSocket.sendMessage("Current score: " + game.getScore());
                        break;
                    default:
                        if (game.isPlaying()) {
                            clientSocket.sendMessage(game.performGuess(response));
                        } else {
                            clientSocket.sendMessage("No active game found. Type " + Commands.PLAY + " to play!");
                        }
                        break;
                }
            } catch (IOException ex) {
                disconnect();
            }
        }
    }

    private void disconnect() {
        output.sendMessage("Client disconnected");
        clientSocket.close();
        running = false;
    }
}

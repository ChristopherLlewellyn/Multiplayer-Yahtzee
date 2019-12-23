import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private final int port;
    private int numPlayers = 0;
    private int playerLimit;
    private ServerSocket serverSocket = null;
    private boolean listening = true;
    private ArrayList<PlayerThread> playerList = new ArrayList<>();

    public boolean waiting;
    public ArrayList<Integer> playerScores = new ArrayList<>();
    public ArrayList<String> playerScoresheets = new ArrayList<>();

    public Server(int port, int numPlayers) {
        this.port = port;
        this.playerLimit = numPlayers;
    }

    // Getters
    public List<PlayerThread> getPlayerList() {
        return playerList;
    }

    // Setters
    public void setPlayerScore(int playerNumber, int score) {
        this.playerScores.set(playerNumber - 1, score);
    }

    public void setPlayerScoresheet(int playerNumber, String scoresheet) {
        this.playerScoresheets.set(playerNumber - 1, scoresheet);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            System.out.println("Waiting for " + playerLimit + " players to connect.");

            while (listening) {
                // create a new player thread when a client connects
                numPlayers++;
                PlayerThread player = new PlayerThread(this, serverSocket.accept(), numPlayers);
                playerList.add(player);

                // create space in arrays to record the player's score and scoresheet
                playerScores.add(0);
                playerScoresheets.add("");

                player.start();
                System.out.println("Player " + numPlayers + " connected.");
                player.send("You are player " + player.playerNumber);

                // check if we've reached the player limit
                if (numPlayers == playerLimit) {
                    listening = false;
                }
            } // finished waiting for players to connect
            System.out.println("All players connected.");

            // Start the game
            System.out.println("Starting game.");
            broadcast("Starting game.");
            for (int round = 1; round < 14; round++) {
                System.out.println("Starting round " + round);
                broadcast("Round " + round + ".");

                // Broadcast everybody's score
                for (PlayerThread player : playerList) {
                    broadcast("Player " + player.playerNumber + "'s score is " + player.score);
                }

                // Notify player it is their turn
                for (PlayerThread player : playerList) {
                    System.out.println("Player " + player.playerNumber + "'s turn.");
                    broadcast("Player " + player.playerNumber + "'s turn.");
                    player.send("Your turn.");

                    // Wait for them to take their turn
                    waiting = true;
                    while (waiting) {
                        Thread.sleep(1000);
                    }
                }
            }

            // Check who won
            int highestScore = 0;
            PlayerThread winningPlayer = playerList.get(0);
            int winningPlayerNumber = 0;

            for (int i = 0; i < playerScores.size(); i++) {
                if (playerScores.get(i) > highestScore) {
                    highestScore = playerScores.get(i);
                    winningPlayer = playerList.get(i);
                }
            }
            winningPlayerNumber = winningPlayer.playerNumber;

            // Notify all players and end the game
            String winnerMessage =
                    "Player "
                            + winningPlayerNumber
                            + " is the winner with "
                            + highestScore
                            + " points!";

            System.out.print(winnerMessage);
            broadcast(winnerMessage);

            System.out.print("Game over.");
            broadcast("Game over.");

        } catch (IOException | InterruptedException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }

    // Send a message to all players
    public void broadcast(String message) {
        for (PlayerThread player : playerList) {
            player.send(message);
        }
    }
}

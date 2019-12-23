import java.io.*;
import java.net.*;
import java.util.List;

public class PlayerThread extends Thread {
    private final Server server;
    private final Socket socket;
    private PrintWriter outputStream;
    public int playerNumber;
    public int score = 0;

    public PlayerThread(Server server, Socket socket, int playerNumber) throws IOException {
        this.server = server;
        this.socket = socket;
        this.playerNumber = playerNumber;
        this.outputStream = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            handleSocket();
        } catch(InterruptedException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    // Handle incoming messages from the client
    private void handleSocket() throws IOException, InterruptedException, ClassNotFoundException {
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object[] inputObject;

        while ((inputObject = (Object[]) objectInputStream.readObject()) != null) {
            process(inputObject);
        }

        socket.close();

    }

    // Send a message to the client
    public void send(String message) {
        outputStream.println(message);
    }

    // Send a message to all clients besides this one
    private void broadcast(String message) {
        List<PlayerThread> playerList = server.getPlayerList();
        for (PlayerThread player : playerList) {
            if (player.playerNumber != this.playerNumber) {
                player.send(message);
            }
        }
    }

    /* Process result of client's turn
    0th element of the input is the player's score (Integer)
    1st element of the input is the player's score record (int[][])
     */
    private void process(Object[] input) {
        // Get result from input
        int[][] scoreRecordArray = (int[][]) input[1];
        score = (Integer) input[0];
        // Build a scoresheet string using the scoreRecord
        String scoresheet = buildScoresheet(scoreRecordArray);

        // Send score and scoresheet to server
        server.setPlayerScore(this.playerNumber, score);
        server.setPlayerScoresheet(this.playerNumber, scoresheet);

        // Broadcast the result of the turn
        broadcast("Player " + playerNumber + "'s score is now " + score);
        broadcast("Player " + playerNumber + "'s scoresheet:");
        broadcast(scoresheet);

        // Notify the server we have finished our turn
        this.server.waiting = false;
    }

    // Build scoresheet string that is properly formatted to be sent to other players
    private String buildScoresheet(int[][] currentScoreRecord) {
        //Scoring Y FH LS SS 4K 3K On Tw Th Fo Fi Si C
        String[] options = {"Yahtzee", "Full-House", "Long-Straight", "Short-Straight", "Quad", "Triple", "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Chance"};
        String scoresheet = "";
        // Build scoresheet
        for (int i=0; i<13; i++) {
            scoresheet += (options[i] + " scoring " + currentScoreRecord[i][1] + " points\n");
        }
        return scoresheet;
    }

}

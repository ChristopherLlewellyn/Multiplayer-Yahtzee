
import java.io.*;
import java.net.*;
import java.util.*;

/* Taken from The Java Tutorial (Campione and Walrath)
/* Further modifications made to accommodate lab and home running
          Simon Taylor October 2011 */

public class Client {
    public static void main(String[] args) throws IOException {

        // Set up the socket, in and out variables

        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        ObjectOutputStream objectOut = null;


        // Change XXXX to the name of the computer that your kkserver is running
        // Change port 4444 to another number so you dont run into someone elses!
        // The code then connects the input and output


        try {
            socket = new Socket("localhost", 4444);
            out = new PrintWriter(socket.getOutputStream(), true);
            objectOut = new ObjectOutputStream((socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: localhost ");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: 4444.");
            System.exit(1);
        }

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromServer;

        System.out.println("Initialised client and IO connections");


        // Connection established and waiting for players

        // Create local instance of game
        Yahtzee yahtzee = new Yahtzee();

        System.out.println("Welcome to Yahtzee Multiplayer!");

        // Wait for messages from server, do something based on the message
        while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            if (fromServer.equals("Game over."))
                break;
            if (fromServer.equals("Your turn.")) {
                yahtzee.takeTurn();
                Object[] result = {yahtzee.getCurrentScore(), yahtzee.getCurrentScoreRecord()};
                objectOut.writeObject(result);
                objectOut.reset();
            }
        }
        // Tidy up
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
}
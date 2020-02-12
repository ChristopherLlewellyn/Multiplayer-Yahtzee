# Multiplayer-Yahtzee
Command line based multi-player game of Yahtzee, developed using Java's sockets API. See src folder for source code.

Allows people on the same network to play Yahtzee together through a command line interface. The number of players can be configured in the Server class. 

This implementation uses a multi-threaded server to coordinate the player's turns, communicate events and maintain the game state. The client application allows a player to connect to the game via the server and allow the player to take their turn (when it is their turn to do so) and display the outcomes of other player's turns.

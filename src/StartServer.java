public class StartServer {
    public static void main(String[] args) {
        Server server = new Server(4444, 3);
        server.start();
    }
}

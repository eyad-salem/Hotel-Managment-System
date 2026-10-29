import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    public static final int PORT = 9090;
    private static ServerSocket serverSocket;
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static boolean running = false;

    public static void start() {
        if (running) return;
        running = true;
        Thread serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("[ChatServer] Started on port " + PORT);
                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        ClientHandler handler = new ClientHandler(clientSocket);
                        clients.add(handler);
                        new Thread(handler).start();
                        System.out.println("[ChatServer] New client connected: " + clientSocket.getInetAddress());
                    } catch (IOException e) {
                        if (running) System.out.println("[ChatServer] Accept error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.out.println("[ChatServer] Could not start: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public static void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public static void broadcastAll(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    public static int getClientCount() {
        return clients.size();
    }

    public static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName = "Unknown";

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                clientName = in.readLine();
                System.out.println("[ChatServer] Client identified as: " + clientName);
                broadcastAll("[SERVER] " + clientName + " has joined the chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    String formatted = clientName + ": " + message;
                    System.out.println("[ChatServer] " + formatted);
                    broadcastAll(formatted);
                }
            } catch (IOException e) {
                System.out.println("[ChatServer] Client disconnected: " + clientName);
            } finally {
                try { socket.close(); } catch (IOException ignored) {}
                removeClient(this);
                broadcastAll("[SERVER] " + clientName + " has left the chat.");
            }
        }

        public void sendMessage(String message) {
            if (out != null) out.println(message);
        }

        public String getClientName() { return clientName; }
    }
}

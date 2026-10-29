import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Consumer<String> onMessage;
    private boolean connected = false;

    public ChatClient(String username, Consumer<String> onMessage) {
        this.username = username;
        this.onMessage = onMessage;
    }

    public boolean connect() {
        try {
            socket = new Socket("localhost", ChatServer.PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(username);
            connected = true;

            Thread receiverThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        final String msg = message;
                        Platform.runLater(() -> onMessage.accept(msg));
                    }
                } catch (IOException e) {
                    if (connected) {
                        Platform.runLater(() -> onMessage.accept("[Disconnected from chat server]"));
                    }
                }
            });
            receiverThread.setDaemon(true);
            receiverThread.start();
            return true;
        } catch (IOException e) {
            System.out.println("[ChatClient] Could not connect: " + e.getMessage());
            return false;
        }
    }

    public void sendMessage(String message) {
        if (connected && out != null) {
            out.println(message);
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }

    public boolean isConnected() { return connected; }
    public String getUsername() { return username; }
}

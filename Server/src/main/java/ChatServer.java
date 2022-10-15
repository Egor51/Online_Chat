
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {

    static Logger logger;

    public static void main(String[] args) {

        logger = Logger.getInstance();
        new ChatServer();
    }

    private static ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server running...");
        logger.log("Server running " + LocalDateTime.now());
        try (ServerSocket serverSocket = new ServerSocket(5050)) {
            while (true) {
                try {

                    new TCPConnection(this, serverSocket.accept());

                } catch (IOException e) {
                    System.out.println("TCPConnection exception " + e);


                }
            }
        } catch (IOException e) {
            logger.log("Server stop");
        }
    }


    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAll(Client.fieldNickName.getText() + "connection" + tcpConnection);
        logger.log(Client.fieldNickName.getText() + ": " + "connection " + LocalDateTime.now());
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAll(Client.fieldNickName.getText() + " disconnection: " + tcpConnection);
        logger.log(Client.fieldNickName.getText() + " disconnection: " + LocalDateTime.now());


    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAll(value);

    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);

    }

    private void sendToAll(String value) {
        System.out.println(value);
        for (TCPConnection connection : connections) {
            if (connection != null) {
                connection.sendString(value);
            }
        }


    }
}
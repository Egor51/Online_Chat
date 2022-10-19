
import java.io.IOException;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {

    static Logger logger;

    private  String name = ClientConsole.setNickName();


    public static void main(String[] args) {

        logger = Logger.getInstance();
        new ChatServer();
    }

    private static ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server running...");
        logger.log("Server running " + LocalDateTime.now());

        try (ServerSocket serverSocket = new ServerSocket(TCPConnection.setPort())) {
            while (true) {
                try {

                    new TCPConnection(this, serverSocket.accept());

                } catch (IOException e) {
                    System.out.println("TCPConnection exception " + e);


                }
            }
        } catch (IOException e) {
            logger.log("TCPConnection exception:" + e +": "+ LocalDateTime.now());
        }
    }


    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAll(name + ": " + " connection");
        logger.log(name + ": " + " connection " + LocalDateTime.now()+ tcpConnection);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
      //  sendToAll(" disconnection: " + tcpConnection);
        sendToAll(name + ": " + " disconnection");
        logger.log(name + " disconnection: " + ": " + LocalDateTime.now() + tcpConnection);


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





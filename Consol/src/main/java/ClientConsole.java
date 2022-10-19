
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ClientConsole implements TCPConnectionListener {


    private Scanner write =new Scanner(System.in);;
    private final Logger logger = Logger.getInstance();
    private final String IP_ADDR = "127.0.0.1";
    private final int PORT = TCPConnection.setPort();
    TCPConnection connection;
    private static String nickName;



    public static void main(String[] args) {

        Scanner name = new Scanner(System.in);
        System.out.println("Enter your nickname");
        nickName = name.next();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new ClientConsole();
            }
        });
        thread.start();

    }

    public ClientConsole(){
    try {

        connection = new TCPConnection(this,IP_ADDR,PORT);
        System.out.println("Hello " + nickName + " you can send a message to the chat" );
        System.out.println("to exit the chat, enter < exit >");
        connection.sendString(nickName + ": " + "is now in the chat");

        while (true){

            String msg = write.nextLine();
            if(msg.equals("exit")) {
                connection.disconnect();

            } else{

                connection.sendString("message from " + nickName +  ": " + msg);
                logger.log(nickName +":" + "send massage"+"[ " + msg + " ]");

            }
        }
    } catch (IOException e) {

        logger.log("Client console exception: " + e + ": " +  LocalDateTime.now());

    }
}
    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMes("Connection ready ...");
        logger.log("Connection ready: " + LocalDateTime.now() + "|" + tcpConnection);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMes("Connection close ...");
        logger.log("Connection close: " + LocalDateTime.now() + "|" +tcpConnection);
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMes(value);

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {

        printMes("Connection exception: " + e);
        logger.log("Client console exception: " + e + ": " +  LocalDateTime.now());
    }
    private synchronized void printMes(String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(msg);
            }
        }).start();
    }

    public static String setNickName(){
        return nickName;
    }
}


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;


public class Client extends JFrame implements ActionListener,TCPConnectionListener {




    private static String IP_ADDR = "127.0.0.1";
    private static final int PORT = TCPConnection.setPort();
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    static Logger logger;

    static String name = "Input yore name";

    public static void main(String[] args) {

        logger = Logger.getInstance();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    static final JTextField fieldNickName = new JTextField("USER");
    private final JTextField fieldInput = new JTextField();
    private TCPConnection connection;



    private Client (){

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(true);
        log.setEditable(false);
        log.setLineWrap(true);
        fieldInput.addActionListener(this);
        add(log, BorderLayout.CENTER);
        add(fieldInput,BorderLayout.SOUTH);
        add(fieldNickName,BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnection(this,IP_ADDR,PORT);
        } catch (IOException e) {
            printMes("Connection exception: " + e);
            logger.log("Connection exception: " + e + ": " + LocalDateTime.now());

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    String msg = fieldInput.getText();
    if (msg.equals(""))return;
    fieldInput.setText(null);

    connection.sendString(fieldNickName.getText() + " :"  + msg);
    logger.log(fieldNickName.getText()+ ": " + " send massage" + "'" +msg+ "' - "+ LocalDateTime.now());




    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMes("Connection ready ...");
        logger.log("Connection ready: " + LocalDateTime.now());
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMes("Connection close ...");
        logger.log("Connection close: " + LocalDateTime.now());
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
    printMes(value);

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {

        printMes("Connection exception: " + e);
        logger.log("Exception: " + e + ": " +  LocalDateTime.now());
    }
    private synchronized void printMes(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}

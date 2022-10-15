import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TCPConnection {
    private final Socket socket;
    private final Thread thread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener eventListener;
 Logger logger ;


    public TCPConnection(TCPConnectionListener eventListener,String ipAddress,int port) throws IOException {
        this(eventListener, new Socket(ipAddress,port));
    }

    public TCPConnection(final TCPConnectionListener eventListener, Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                logger = Logger.getInstance();

                try {
                    eventListener.onConnectionReady(TCPConnection.this);

                    while (!thread.isInterrupted()) {
                    eventListener.onReceiveString(TCPConnection.this,in.readLine());

                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this,e);

                }finally {
                    eventListener.onDisconnect(TCPConnection.this);



                }
            }
        });
        thread.start();

    }
    synchronized void  sendString(String value){
        try {
            out.write(value+"\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
            disconnect();
        }
    }
    synchronized void disconnect(){
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);

        }
    }
    @Override
    public String toString(){
        return "Connection" + socket.getInetAddress() + " : " + socket.getPort();
    }
}

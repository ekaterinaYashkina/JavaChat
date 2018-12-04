import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Connection  {

    private final static Logger logger = LogManager.getLogger("networkLog");
    private final Socket socket;
    private final Thread readingStream; //thread for reading from the server
    private final ConnectionHandler listener;
    private final BufferedWriter writer;

    public Socket getSocket(){
        System.out.println(this.socket);
        return socket;
    }


    public Connection(ConnectionHandler listener, InetAddress ip, int port) throws IOException {
        this(listener, new Socket(ip, port));
    }

    public Connection(ConnectionHandler listener, String ip, int port) throws IOException {
        this(listener, new Socket(ip, port));
    }

    public Connection(ConnectionHandler listener, Socket socket) throws IOException {
        this.socket = socket;
        this.listener = listener;
        writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        readingStream = new Thread(new ConnectionThread(listener, this, this.socket.getInputStream()));

        readingStream.start();
    }


    //method for sending message to server
    public synchronized void sendString(String msg){
        try {
            writer.write(msg+"\r\n");
            writer.flush();
        } catch (IOException e) {
            listener.onException(Connection.this, e);
        }
    }

    //disconnecting from server and closing all resources
    public synchronized void disconnect(){
        readingStream.interrupt();
        logger.info("Connection " +this.toString()+" is stopping...");
        try {
            socket.close();
            logger.info("Socket "+socket.toString()+"is closed");
        }catch (IOException e){
            listener.onException(Connection.this, e);
        }
    }


    public String toString(){
        return socket.getPort()+":"+socket.getInetAddress();
    }
}
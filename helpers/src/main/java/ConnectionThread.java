import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

//Thread for reading from server or client (depends on connection)
public class ConnectionThread implements Runnable {

    private final static Logger logger = LogManager.getLogger("networkLog");
    private final ConnectionHandler handler;
    private final Connection connection;
    private InputStream ios;

    public ConnectionThread(ConnectionHandler handler, Connection connection, InputStream reader) {
        this.handler = handler;
        this.connection = connection;
        this.ios = reader;
    }

    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(ios))) {
            handler.onConnectionReady(connection);
            logger.info("Connection on "+this.toString() +"established");
            while (!Thread.currentThread().isInterrupted()) {
                String msg = reader.readLine();
                handler.onReceiveString(connection, msg);
            }
        } catch (IOException e) {
            handler.onException(connection, e);
        } finally {
//            handler.
//            connection.setStatus(0);
            logger.info("Thread with connection "+connection.toString()+" has been interrupted.");
            handler.onDisconnect(connection);
        }
    }
}
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements ConnectionHandler {
    private final static Logger logger = LogManager.getLogger("serverLog");
    private final static ResourceManager manager = new ResourceManager();

    public static void main(String[] args) {

        if (args.length == 1){
            manager.setDirectory(args[0]);
        }

        new Server();
    }

    //Thread-access special storage
    private CopyOnWriteArrayList<Connection> connections = new CopyOnWriteArrayList<>();
    private Server(){
        try{
            manager.loadClass();
        }
        catch (ClassNotFoundException e){
            logger.warn("No needed classes in JAR");
        }
        try (ServerSocket socket = new ServerSocket(9000)){
            logger.info("Running.....");
            while(true){
                try{
                    new Connection(this, socket.accept());
                }catch(IOException e){
                    logger.error(e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    //When a user is connected
    public synchronized void onConnectionReady(Connection connection) {
        connections.add(connection);
        logger.info("New connection "+connection+"has been established");
        logger.info("Amount of connections: {}", connections.size());
        broadcast("New user has joined the chat.", connection);
    }


    //process input and send result or calculate or close the resources based on it
    public void onReceiveString(Connection connection, String value) {
        String withoutNickname = value.substring(value.indexOf(":")+2);
        System.out.println(withoutNickname);
        if (withoutNickname.startsWith("/")) {
            String command;
            String[] params = null;
            if (withoutNickname.contains(" ")){
                command = withoutNickname.substring(0, withoutNickname.indexOf(" "));
                System.out.println(command);
                params = withoutNickname.substring(withoutNickname.indexOf(" ")+1).split(" ");
                System.out.println(params.length);}
            else command = withoutNickname;
            CommandProducer result = manager.caluculate(command);
            if (result == null) {
                connection.sendString("No such command");
                return;
            }
            try{
                Calculator calc = new Calculator(result, params, connection);
                Thread calculation = new Thread(calc);
                calculation.start();}
            catch (IllegalArgumentException e) {
                logger.warn(e);
                connection.sendString(e.getMessage());

            }


        }
        else if (withoutNickname.equals("exit")){
            connection.disconnect();
        }
        else broadcast(value, connection);
    }


    //Close connection passed
    public synchronized void onDisconnect(Connection connection) {
        connections.remove(connection);
        logger.info("Connection "+connection+" is closing...");
        logger.info("Amount of connections: {}", connections.size());
        broadcast("User left the chat", connection);
    }


    public synchronized void onException(Connection connection, Exception e) {
        logger.error("Exception occurred", e);
    }

    //Send message everybody except the one that actually sent the message
    private synchronized void broadcast(String msg, Connection listn){

        final int size = connections.size();
        logger.info("Broadcasting a new message....");

        for (int i = 0; i<size; i++) {
            if (connections.get(i)!=listn) connections.get(i).sendString(msg);
        }
        logger.info("Message broadcasted!");
    }
}


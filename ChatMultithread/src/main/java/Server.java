import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Server{

    private final static Logger logger = LogManager.getLogger("serverLog");
    //List of available clients (actually their handlers)
    private HashMap<Integer, ClientHandler1> clientHandlers;
    private HashSet<String> uniqueNames;

    //Port of communication
    private final int PORT = 9000;

    private boolean isRunning;

    public Server(){
        clientHandlers = new HashMap<>();
        uniqueNames = new HashSet<>();
    }

    /**
     * Starts server work - accept new clients while they come
     */
    public void startServer(){

        isRunning = true;

        int id = 0;

        try {
            ServerSocket serSocket = new ServerSocket(PORT);

            logger.info("Established connection on port"+PORT+", socket info: "+serSocket.toString());
            System.out.println("Established connection on port"+PORT);
            logger.info("Wait for clients...");
            System.out.println("Wait for clients...");

            while(isRunning){
                Socket socket = serSocket.accept();
                logger.info("New client is here!");
                System.out.println("New client is here!");
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                ClientHandler1 handler = new ClientHandler1(socket, dis, dos, id);

                clientHandlers.put(id,handler);
                handler.start();
                logger.info("Client with id "+id+" added");
                id++;
                System.out.println("Client added!");

            }

            try {
                serSocket.close();
                for(Map.Entry<Integer, ClientHandler1> client: clientHandlers.entrySet()) {
                    ClientHandler1 tc = client.getValue();
                    tc.dis.close();
                    tc.dos.close();
                    tc.socket.close();
                }
            }
            catch(Exception e) {
                logger.error("Exception closing the server and clients: " + e);
                System.out.println("Exception closing the server and clients");
            }



        }
        catch (IOException exc){
            exc.printStackTrace();
        }
    }

    private synchronized boolean logoutUser(ClientHandler1 handler){
        if (clientHandlers.containsKey(handler.id))
        {
            String nameLeave = clientHandlers.get(handler.id).name;
            uniqueNames.remove(nameLeave);
            clientHandlers.remove(handler.id);

            broadcast(nameLeave+" has left the chatroom!", handler.id);
            logger.info("Client with id "+handler.id+" send request to logout.");

        return true;
        }
        else return false;
    }

    private synchronized boolean initializeUser(ClientHandler1 handler){
        try{
            handler.dos.writeUTF("Please, print your username. If you want to stay anonymous, click Enter with blank line");
            String username;
            while (true){
                username = handler.dis.readUTF();
                if ((!uniqueNames.contains(username))){
                    if (!username.equals("")){
                    handler.setUserName(username);
                    uniqueNames.add(username);}
                    handler.dos.writeUTF("Hello, "+handler.name+"!");
                    logger.info("Client with id "+handler.id+" is initialized");
                    broadcast(handler.name+" has joined the chatroom!", handler.id);

                    break;
                }
                else {
                    handler.dos.writeUTF("A user with provided name already exists. Try another one.");
                    logger.info("Client with name "+username+" is already in the chatroom. Attempting to initialize again...");
                }
            }

            return true;
        }catch (IOException e){
            logger.error("Exception while sending initialization message: "+e);
            return false;
        }
    }


    //TODO - use returned value for retry sending
    private boolean broadcast (String message, int sender){
        try{
            logger.info("Broadcasting message from client "+sender+"...");
        for (Map.Entry<Integer, ClientHandler1> mc : clientHandlers.entrySet())
        {
            if (!mc.getKey().equals(sender))
                mc.getValue().dos.writeUTF(message);

        }
            logger.info("Successfully broadcasted message from "+sender);
            return true;
        }
        catch (IOException e) {
            logger.error("Exception while broadcasting message from "+sender);
            return false;
        }
    }

    private class ClientHandler1 extends Thread {

        final Socket socket;
        final DataInputStream dis;
        final DataOutputStream dos;
        private String name = "Anonymous";
        private int id;
        boolean isloggedin = false;

        public void setUserName(String name){
            this.name = name;
        }

        public ClientHandler1(Socket s, DataInputStream dis, DataOutputStream dos, int id){
            this.dis = dis;
            this.dos = dos;
            this.socket = s;
            this.id = id;
        }

        @Override
        public void run() {
            while (!isloggedin){
                logger.info("Trying to initialize client "+this.id+"....");
                isloggedin = initializeUser(this);
            }
            String received;
            while (true)
            {
                try
                {
                    // receive the string
                    received = dis.readUTF();

                    if(received.equals("logout")){
                        logger.info("A client "+this.id+" wants to logout....");
                        System.out.println(this.name+" has logged out");
                        break;
                    }

                    broadcast(this.name+": "+received, this.id);
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
            try
            {
                // closing resources
                //boolean loggedOut = false;
                boolean loggedOut = logoutUser(this);
                if (!loggedOut) logger.error("The client with id "+this.id+" has been already deleted!");
                this.socket.close();
                this.dis.close();
                this.dos.close();
                logger.info("Terminated client "+this.id+" process");

            }catch(IOException e){
                logger.error("Exception while releasing resources: "+e);
            }
        }
    }


}

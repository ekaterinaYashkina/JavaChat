import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Server{

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

            System.out.println("Established connection on port"+PORT);
            System.out.println("Wait for clients...");

            while(isRunning){
                Socket socket = serSocket.accept();
                System.out.println("New client is here!");
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                ClientHandler1 handler = new ClientHandler1(socket, dis, dos, id);

                clientHandlers.put(id,handler);
                handler.start();
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
                System.out.println("Exception closing the server and clients: " + e);
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
                    broadcast(handler.name+" has joined the chatroom!", handler.id);
                    break;
                }
                else handler.dos.writeUTF("A user with provided name already exists. Try another one.");
            }

            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }


    //TODO - use returned value for retry sending
    private boolean broadcast (String message, int sender){
        try{
        for (Map.Entry<Integer, ClientHandler1> mc : clientHandlers.entrySet())
        {
            if (!mc.getKey().equals(sender))
                mc.getValue().dos.writeUTF(message);

        }
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
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
                boolean loggedOut = false;
                while (!loggedOut){
                loggedOut = logoutUser(this);}
                this.socket.close();
                this.dis.close();
                this.dos.close();
                Thread.currentThread().interrupt();

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }


}

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server{

    //List of available clients (actually thier handlers)
    private HashMap<Integer, ClientHandler1> clientHandlers;

    //Port of communication
    private final int PORT = 9000;

    private boolean isRunning;

    public Server(){
        clientHandlers = new HashMap<>();
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
                id++;
                System.out.println("Client added!");

                Thread t = new Thread(handler);
                t.start();

            }

            try {
                serSocket.close();
                for(Map.Entry<Integer, ClientHandler1> client: clientHandlers.entrySet()) {
                    ClientHandler1 tc = client.getValue();
                    try {
                        // close all data streams and socket
                        tc.dis.close();
                        tc.dos.close();
                        tc.socket.close();
                    }
                    catch(IOException ioE) {
                    }
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

    private synchronized boolean logoutUser(int id){
        if (clientHandlers.containsKey(id))
        {clientHandlers.remove(id);
        return true;
        }
        else return false;
    }

    private class ClientHandler1 implements Runnable {

        final Socket socket;
        final DataInputStream dis;
        final DataOutputStream dos;
        private String name;
        private int id;
        boolean isloggedin;

        public ClientHandler1(Socket s, DataInputStream dis, DataOutputStream dos, int id){
            this.dis = dis;
            this.dos = dos;
            this.socket = s;
            //this.isloggedin=true;
            initializeUser();
            this.id = id;
        }

        private void initializeUser(){
            try{
            this.name = dis.readUTF();}
            catch(IOException e){
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            String received;
            while (true)
            {
                try
                {
                    // receive the string
                    received = dis.readUTF();

                    System.out.println(received);

                    if(received.equals("logout")){
                        dos.writeUTF(this.name+"has logged out");
                        System.out.println(this.name+"has logged out");
                        break;
                    }

                    //endMessage(this.name+": "+received);
                    for (Map.Entry<Integer, ClientHandler1> mc : clientHandlers.entrySet())
                    {
                        if (!mc.getKey().equals(this.id))
                            mc.getValue().dos.writeUTF(this.name+": "+received);
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
            try
            {
                // closing resources
                logoutUser(this.id);
                this.socket.close();
                this.dis.close();
                this.dos.close();

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }


}

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server{

    //List of available clients (actually thier handlers)
    private HashMap<String, ClientHandler> clientHandlers;

    //Port of communication
    private final int PORT = 9000;

    public Server(){
        clientHandlers = new HashMap<>();
    }

    /**
     * Starts server work - accept new clients while they come
     */
    public void startServer(){
        try (ServerSocket serSocket = new ServerSocket(PORT)){
            System.out.println("Established connection on port"+PORT);
            System.out.println("Wait for clients...");

            while(true){
                Socket socket = serSocket.accept();
                System.out.println("New client is here!");
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                ClientHandler handler = new ClientHandler(socket, dis, dos);

                Thread t = new Thread(handler);

                clientHandlers.put(handler.getName(),handler);
                System.out.println("Client added!");
                t.start();

            }



        }
        catch (IOException exc){
            exc.printStackTrace();
        }
    }
}
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {

    private Socket socket;
    final DataInputStream dis;
    final DataOutputStream dos;
    private String name;
    boolean isloggedin;

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos){
        this.dis = dis;
        this.dos = dos;
        this.socket = s;
        this.isloggedin=true;
        initializeUser();
    }

    private void initializeUser(){

        while(true){
            try {
                dos.writeUTF("Please, write your name to join chat");
                String username = dis.readUTF();
                if (!username.equals("") ) {
                    this.name = username;
                    break;
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String getName(){
        return this.name;
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
                    this.isloggedin=false;
                    this.socket.close();
                    break;
                }

//                // break the string into message and recipient part
//                StringTokenizer st = new StringTokenizer(received, "#");
//                String MsgToSend = st.nextToken();
//                String recipient = st.nextToken();

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (Map.Entry<String, ClientHandler> mc : Server.clientHandlers.entrySet())
                {
                    if (!mc.getKey().equals(this.name))
                    mc.getValue().dos.writeUTF(this.name+": "+received);
                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.io.*;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;
public class ClientTest {
    private Client client;
    private static ServerSocket serverSocket;

    @BeforeAll
    static void initServer(){
        try{
            serverSocket = new ServerSocket(9000);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    void testWithLocalhostConnection(){

        client = new Client(1, "localhost", 9000);
//        client.initConnection();
        assertDoesNotThrow(
                ()->{
                    client.initConnection();
                    //do whatever you want to do here
                    //ex : objectName.thisMethodShoulThrowNullPointerExceptionForNullParameter(null);
                }
        );
        assertEquals(client.getConnection().getSocket().getInetAddress().getHostName(), "localhost");
        assertEquals(client.getConnection().getSocket().getPort(), 9000);


    }

    @Test
    void testWithCustomConnection(){
        client = new Client(1, "local", 9000);
        PrintStream oldOut = System.out;

        // Create a ByteArrayOutputStream so that we can get the output
        // from the call to print
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Change System.out to point out to our stream
        System.setOut(new PrintStream(baos));
        client.initConnection();
        // Reset the System.out
        System.setOut(oldOut);

        // Our baos has the content from the print statement
        String output = new String(baos.toByteArray());

        // Add some assertions out output
        assertTrue(output.contains("Wrong hostname: local"));
    }

    @Test
    void testUserInpuExit(){
        client = new Client(1, "localhost", 9000);
        client.initConnection();

        try{
        InputAsker asker = Mockito.mock(InputAsker.class);
        Mockito.when(asker.getInput("nickname")).thenReturn("nick");
        Mockito.when(asker.getInput("input")).thenReturn("exit");
        client.startApplication(asker);
        assertEquals(client.getStatus(), "disconnected");}
        catch (IOException e){
            e.printStackTrace();
        }

    }

    @Test
    void testOnConnectionReady(){
        client = new Client(1, "localhost", 9000);

        //PrintStream oldOut = System.out;

        Connection conn = Mockito.mock(Connection.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        System.setOut(new PrintStream(baos));

        client.onConnectionReady(conn);

        String output = new String(baos.toByteArray());

        assertTrue(output.contains("Connection Ready"));
    }


    @Test
    void testOnReceiveString(){
        client = new Client(1, "localhost", 9000);
        Connection conn = Mockito.mock(Connection.class);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        System.setOut(new PrintStream(baos));

        client.onReceiveString(conn, "hi");

        String output = new String(baos.toByteArray());

        assertTrue(output.contains("hi"));

    }

    @Test
    void testOnDisconnect(){
        client = new Client(1, "localhost", 9000);
        Connection conn = Mockito.mock(Connection.class);
        conn.disconnect();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        System.setOut(new PrintStream(baos));

        client.onDisconnect(conn);

        String output = new String(baos.toByteArray());

        assertTrue(output.contains("Connection close"));
        assertEquals(client.getStatus(), "disconnected");

    }


    }
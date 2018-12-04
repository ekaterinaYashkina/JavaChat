import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionTest {

    private static Connection conn;
    private static ConnectionHandler handler;
    private static ServerSocket server;

    @BeforeAll
    static void init(){

        handler = Mockito.mock(ConnectionHandler.class);
        try {
            server = new ServerSocket(9000);
            conn = new Connection(handler, "localhost", 9000);
        }catch (IOException e){
            e.printStackTrace();
        }
        }



    @Test
    void worksOnSocket() {

        assertEquals(conn.getSocket().getPort(), 9000);
        assertEquals(conn.getSocket().isClosed(), false);
    }

    @Test
    void stringRepr() {
        assertEquals(conn.toString(), "9000:localhost/127.0.0.1");
    }

    @Test
    void disconnect() {
        conn.disconnect();
        assertEquals(conn.getSocket().isClosed(), true);
    }
}

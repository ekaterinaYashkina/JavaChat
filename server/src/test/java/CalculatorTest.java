import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.ServerSocket;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    private static ServerSocket server;
    private static ConnectionHandler handler;
    private static Connection connection;

    @BeforeAll
    static void initServer(){
        try {
            server = new ServerSocket(9000);
            handler = Mockito.mock(ConnectionHandler.class);
            connection = new Connection(handler, "localhost", 9000);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Test
    void init(){
        CommandProducer com = Mockito.mock(CommandProducer.class);
        Mockito.when(com.amountParams()).thenReturn(2);
        String [] params = {"1", "2"};
        assertDoesNotThrow(() -> {Calculator calc = new Calculator(com, params, connection);});
    }
}
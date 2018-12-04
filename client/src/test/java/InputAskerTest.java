import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class InputAskerTest {

    private static InputAsker asker;

    @BeforeAll
    static void initAsker(){
        asker = new InputAsker();
        assertEquals(asker.getStatus(), "connected");
    }

    @Test
    void setDisconnected() {
        asker.setDisconnected();
        assertEquals(asker.getStatus(), "disconnected");
    }

}
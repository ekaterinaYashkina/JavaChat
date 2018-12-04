import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ResourceManagerTest {

    private static ResourceManager manager;

    @BeforeAll
    static void initManager(){
        manager = new ResourceManager();
    }

    @org.junit.jupiter.api.Test
    void setDirectory() {

        manager.setDirectory("/Documents/newMyDir");
        assertEquals(manager.getDirectory(), "/Documents/newMyDir");

    }

    @org.junit.jupiter.api.Test
    void calculate() {
        CommandProducer prod = Mockito.mock(CommandProducer.class);
        manager.append(prod, "Add");
         assertEquals(manager.caluculate("Add"), prod);

    }


    @org.junit.jupiter.api.Test
    void loadClassNoDir() {


        manager.setDirectory("/Documents/newMyDir");
        assertThrows(NullPointerException.class, ()->{
            manager.loadClass();
            //do whatever you want to do here
            //ex : objectName.thisMethodShoulThrowNullPointerExceptionForNullParameter(null);
        });

    }
}
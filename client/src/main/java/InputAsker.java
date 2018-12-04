import java.io.IOException;
import java.util.Scanner;

//Wrapper for polling for message from server
public class InputAsker {

    private final Scanner scanner;
    private String status = "disconnected";

    public String getStatus(){
        return status;
    }

    public void setDisconnected(){
        this.status = "disconnected";
        scanner.close();
    }

    public InputAsker(){
        scanner = new Scanner(System.in);
        this.status = "connected";
    }

    public String getInput(String ask) throws IOException{
        if (status.equals("disconnected")) throw new IOException("I/O stream was disconnected");
        return scanner.nextLine();
    }
}

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class Client implements ConnectionHandler {

    private final static Logger logger = LogManager.getLogger("clientLog");
    private static int port = 9000;
    private static String ip = "localhost";

    //0 - default, 1 - custom
    private static int flag = 0;
    //private static InetAddress ip;
    private String nickname = "Anonymous";
    private Connection connection;

    public static void main(String[] args) throws Exception {
        if (args.length == 2){
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        else if (args.length == 1){
            logger.error("Wrong amount of arguments provided: expected 0 or 2, got 1");
            System.out.println("Wrong amount of arguments provided: expected 0 or 2, got 1");
            return;
        }
        new Client();
    }

    private Client(){

        try {
            if (flag == 0) connection = new Connection(this, ip, port);
            else{
            InetAddress ipAdd = InetAddress.getByName("localhost");
            connection = new Connection(this, ipAdd, port);}
            logger.info("Connected to server");
            startApplication();
        } catch (IOException e) {
            logger.error(e);
        }

    }

    private void startApplication(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Please provide your nickname, or press enter to stay anonymous.");
        String name = sc.nextLine();
        if (!name.equals("")) nickname = name;
        System.out.println("Hello, "+nickname);
        logger.info("Nickname set");
        String message = "";

        while (true){
            message = sc.nextLine();
            if (message.equals("exit")){
                logger.info("User {} requested exit. Preparing ...", this.toString());
                connection.sendString(message);
                connection.disconnect();
                logger.info("Successfully disconnected!");
                break;
            }
            connection.sendString(nickname+": "+message);
            logger.info("User {} sent message", this.toString());
        }
    }


    public void onConnectionReady(Connection connection) {
        logger.info("Connection {} with server has been established", connection);

        printMessage("Connection Ready");
    }


    public void onReceiveString(Connection connection, String value, int status) {
        printMessage(value);
    }


    public void onDisconnect(Connection connection) {
        logger.info("Connection {} id closed", connection);
        printMessage("Connection close");
    }


    public void onException(Connection connection, Exception e) {
        logger.error("Exception on connection {} ", connection, e);
    }

    private void printMessage(String msg){
        System.out.println(msg);
    }
}
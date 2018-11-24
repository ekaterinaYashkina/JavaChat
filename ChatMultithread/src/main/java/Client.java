import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.*;
import java.util.Scanner;

    public class Client {

        private final static Logger logger = LogManager.getLogger("clientLog");
        final private int ServerPort = 9000;
        private DataInputStream dis;
        private Socket socket;
        private DataOutputStream dos;
        private boolean isloggedin;

        private Scanner scan = new Scanner(System.in);

        public Client() {

            try {
                // getting localhost ip
                InetAddress ip = InetAddress.getByName("localhost");

                // establish the connection
                socket = new Socket(ip, ServerPort);

                // obtaining input and out streams
                this.dis = new DataInputStream(socket.getInputStream());
                this.dos = new DataOutputStream(socket.getOutputStream());
                logger.info("Connection with server has been established!");
            } catch (UnknownHostException e) {
                logger.error("Can not find ip for provided host: localhost");
                System.out.println("No such host");
            } catch (IOException e) {
                logger.error("Exception obtaining data streams from socket: "+socket+", "+e);
            }
        }

        public void start() {
            isloggedin = true;
            sendMessage.start();
            readMessage.start();
            logger.info("Registered on server");
        }


        protected void disconnect() {
            try {
                if (dis != null) dis.close();
            } catch (Exception e) {
                logger.error("Exception while closing DataInputStream: "+e);
            }
            try {
                if (dos != null) dos.close();
            } catch (Exception e) {
                logger.error("Exception while closing DataOutputStream: "+e);
            }
            try {
                if (socket != null) socket.close();
            } catch (Exception e) {
                logger.error("Exception while Socket: "+e);
            }
            }



        private Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isloggedin) {
                    // read the message to deliver.
                    String msg = scan.nextLine();

                    try {
                        System.out.print("> ");
                        // write on the output stream
                        dos.writeUTF(msg);
                        logger.info("Message sent!");
                        if (msg.equals("logout")) {
                            logger.info("Trying to disconnect ...");
                            isloggedin = false;
                        }
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }

                // close resource
                scan.close();
                // client completed its job. disconnect client.
                disconnect();
            }
        });

        // readMessage thread
        private Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (isloggedin) {
                    try {
                        // read the message form the input datastream
                        String msg = dis.readUTF();
                        logger.info("Message arrived!");
                        // print the message
                        System.out.println("\n"+msg);
                        System.out.print("> ");
                    } catch (IOException e) {
                        //TODO: better way?
                        logger.info("Closing the connection, "+e);
                        System.out.println("Server has closed the connection: " + e);
                        break;
                    }
                }
            }
        });


        public static void main(String[] args) {

            // create the Client object
            Client client = new Client();
            // try to connect to the server and return if not connected
            client.start();

            System.out.println("\nHello! Welcome to the chatroom.");
        }
    }


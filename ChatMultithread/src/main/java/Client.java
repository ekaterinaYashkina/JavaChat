import java.io.*;
import java.net.*;
import java.util.Scanner;

    public class Client {
        final static int ServerPort = 9000;
        private DataInputStream dis;
        private Socket socket;
        private DataOutputStream dos;
        private String username;

        Scanner scan = new Scanner(System.in);

        public Client(String username) {

            this.username = username;

            try {
                //Scanner scn = new Scanner(System.in);

                // getting localhost ip
                InetAddress ip = InetAddress.getByName("localhost");

                // establish the connection
                socket = new Socket(ip, ServerPort);

                // obtaining input and out streams
                this.dis = new DataInputStream(socket.getInputStream());
                this.dos = new DataOutputStream(socket.getOutputStream());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void start() {
            sendMessage.start();
            readMessage.start();
//            new ServerHandler().start();

            try {
                dos.writeUTF(this.username);
            } catch (IOException eIO) {
                System.out.println("Exception doing login : " + eIO);
//                disconnect();
//                return false;
            }
        }


        protected void disconnect() {
            try {
                if (dis != null) dis.close();
            } catch (Exception e) {
            }
            try {
                if (dos != null) dos.close();
            } catch (Exception e) {
            }
            try {
                if (socket != null) socket.close();
            } catch (Exception e) {
            }
        }


        Thread sendMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // read the message to deliver.
                    String msg = scan.nextLine();

                    try {
                        // write on the output stream
                        dos.writeUTF(msg);
                        if (msg.equals("logout")) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // close resource
                scan.close();
                // client completed its job. disconnect client.
                disconnect();
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    //System.out.println("jjj");
                    try {
                        // read the message form the input datastream
                        String msg = dis.readUTF();
                        // print the message
                        System.out.println(msg);
                        System.out.print("> ");
                    } catch (IOException e) {
                        System.out.println("Server has closed the connection: " + e);
                        break;
                    }
                }
            }
        });


        public static void main(String[] args) {
            String userName = "Anonymous";
            Scanner scan = new Scanner(System.in);

            System.out.println("Enter the username: ");
            userName = scan.nextLine();

            // create the Client object
            Client client = new Client(userName);
            // try to connect to the server and return if not connected
            client.start();

            System.out.println("\nHello.! Welcome to the chatroom.");
            System.out.print(">");
        }
    }


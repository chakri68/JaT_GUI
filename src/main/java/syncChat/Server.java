package syncChat;

import utils.ColorPrinter;
import utils.Command;
import utils.CommandHandler;
import static java.util.Map.entry;

import java.net.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Server implements Runnable {

    CommandHandler cHandler;
    ArrayList<ClientHandler> clients;
    ServerSocket server;
    int portNumber;
    boolean exit;
    ExecutorService pool;
    String commandInitStr = "\\";
    // ArrayList<String> randomNames = ["giant giraffe", "curious cuttlefish"];

    /**
     * The constructor for Server class
     * 
     * @param portNumber The port to start the server in
     */
    public Server(int portNumber) {
        clients = new ArrayList<>();
        this.portNumber = portNumber;
        exit = false;
    }

    public void initCommandHandler() {
        cHandler = new CommandHandler(this, Map.<String, Command>ofEntries(
                entry("u", new Command<String>((s, inpList) -> {
                    // Change username
                    // Check the username first
                    if (s.validNickname(inpList[0])) {

                        String pvUsername = s.username;
                        s.username = inpList[0];
                        return ColorPrinter.printf("$YELLOW{" + pvUsername + "}"
                                + " has changed his/her username to $PURPLE{" + s.username + "}");
                    } else {
                        s.sendMessage(ColorPrinter.println(inpList[0] + " is not a valid username!", "RED"));
                        return null;
                    }
                })),
                entry("f", new Command<>((s, inpList) -> s.username + ": " + ColorPrinter.printf(String.join(" ", inpList)))),
                entry("q", new Command<Void>((s, inpList) -> {
                    s.endCodeWord = String.join(" ", inpList);
                    s.sendMessage("Your new exit word is " + s.endCodeWord);
                    return null;
                }))));
    }

    @Override
    public void run() {
        System.out.println("The server is listening at " + portNumber);
        try {
            server = new ServerSocket(portNumber);
            initCommandHandler();
            pool = Executors.newCachedThreadPool();
            while (!exit) {
                Socket connection = server.accept();
                ClientHandler client = new ClientHandler(connection);
                clients.add(client);
                pool.execute(client);
            }
        } catch (IOException e) {
            // TODO: handle IOException
            e.printStackTrace();
            exit();
        }
    }

    /**
     * Calls `sendMessage` of all ClientHandlers in the clients array list with the
     * message param as input
     * 
     * @param message The message to broadcast
     */
    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            if (client != null) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * Handles the client connections
     */
    public class ClientHandler implements Runnable {
        Socket client;
        BufferedReader in; // Get stream from the socket
        PrintWriter out; // Send the messages to the socket
        String endCodeWord = "OVER AND OUT";
        String username;

        /**
         * Constructor of the ClientHandler class
         * 
         * @param client The socket (connection) to create the handler for
         */
        public ClientHandler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);
                out.println(ColorPrinter.printf(
                        "$YELLOW_BRIGHT{This chat system is like group chat, you and other users get to send messages to each other.} \nSend $PURPLE_BOLD_BRIGHT{\""
                                + endCodeWord
                                + "\"} to end the session anytime!\n$CYAN_BOLD_BRIGHT{Please enter your username:} "));
                String inpName = in.readLine();
                while (!validNickname(inpName)) {
                    out.println(ColorPrinter.printf("$CYAN_BOLD_BRIGHT{Please enter your username: }"));
                    inpName = in.readLine();
                }
                username = inpName;
                System.out.println(ColorPrinter.printf("$YELLOW_BRIGHT{" + username + "}") + " has connected");
                broadcast(ColorPrinter.printf("$YELLOW_BRIGHT{" + username + "}") + " has joined the chat!");
                // The main loop
                String message;
                while ((message = in.readLine()) != null) {
                    boolean isCommand;
                    if (commandInitStr.equals("\\")) {
                        // Match using regex
                        Pattern pattern = Pattern.compile("^\\\\(?!\\\\).+");
                        Matcher match = pattern.matcher(message);
                        isCommand = match.results().count() == 1;
                    } else {
                        // Match using prefix
                        isCommand = message.startsWith(commandInitStr);
                    }
                    if (isCommand) {
                        // A command!
                        handleCommands(message);
                        continue;
                    } else if (message.equals(endCodeWord)) {
                        // The client left
                        exit();
                    }
                    broadcast(ColorPrinter.printf("$YELLOW_BRIGHT{" + username + "}") + ": " + message);
                }
            } catch (IOException e) {
                // TODO handle IOException
                e.printStackTrace();
                exit();
            }
        }

        /**
         * Method to handle commands sent by the client
         * 
         * @param message The command string
         */
        private void handleCommands(String message) {
            // handle using cHandler
            Object ret = cHandler.handle(this, message);
            if (ret != null) {
                broadcast(ret.toString());
            }
        }

        /**
         * Checks if the userName is valid
         * 
         * @param userName The username to be checked
         * @return true if the username is valid else false
         */
        public boolean validNickname(String userName) {
            // Handle usernames
            // Doesn't start with '\' and not ""
            if (userName == null || userName.startsWith("\\")) {
                return false;
            }

            // Unique
            for (ClientHandler client : clients) {
                if (!client.equals(this) && client.username.equals(userName)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Method to close all the resources and sockets of ClientHandler safely and
         * return from the main loop
         */
        public void exit() {
            broadcast(username + " left the chat");
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
                // TODO: Remove the ClientHandle instance from the array
                clients.remove(this);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("SOME ERROR DISCONNECTING THE USER");
                e.printStackTrace();
            }
        }

        /**
         * To send message through the socket
         * 
         * @param message The message string
         */
        public void sendMessage(String message) {
            out.println(message);
        }
    }

    /**
     * Close all resources and finally the server
     */
    public void exit() {
        exit = true;
        try {
            if (!server.isClosed()) {
                server.close();
            }
        } catch (Exception e) {
            // can't handle
            e.printStackTrace();
        }
        // Close all clients
        for (ClientHandler client : clients) {
            client.exit();
        }
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }

        Server server = new Server(Integer.parseInt(args[0]));
        server.run();
    }
}

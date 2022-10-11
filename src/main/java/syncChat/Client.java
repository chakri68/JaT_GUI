package syncChat;

import com.jat.jat_gui.ChatSceneController;
import javafx.application.Platform;

import java.net.*;
import java.io.*;

public class Client implements Runnable {
    String hostName;
    int portNumber;
    ChatSceneController csc;
    Socket client;
    BufferedReader in;
    PrintWriter out;
    boolean exit;

    /**
     * @param hostName   The hostname
     * @param portNumber The port number to connect to
     */
    public Client(String hostName, int portNumber, ChatSceneController csc) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.csc = csc;
        this.exit = false;
    }

    public void run() {
        try {
            client = new Socket(hostName, portNumber);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            String incomingMessage;
            while ((incomingMessage = in.readLine()) != null) {
                String finalIncomingMessage = incomingMessage;
                Platform.runLater(
                        () -> csc.addNewMessage(finalIncomingMessage)
                );
            }
            exit();
        } catch (IOException e) {
            exit();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Close the resources and socket
     */
    public void exit() {
        out.println("has disconnected");
        exit = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
            // To exit immediately
            System.exit(0);
        } catch (IOException e) {
            // TODO: handle exception
            // ignore
        }
    }
}

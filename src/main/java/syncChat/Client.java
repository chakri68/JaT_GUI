package syncChat;

import com.jat.jat_gui.ChatSceneController;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;

import java.net.*;
import java.io.*;
import java.util.Optional;

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
            System.out.println(hostName +" "+ portNumber);
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
            Platform.runLater(
                    () -> {
                        Optional<ButtonType> result = csc.showError("Error", "Something went wrong while connecting to the host " + hostName + ":" + portNumber);
                        csc.goToScene("home");
//                        System.exit(0);
                    }
            );
            if (client != null && client.isConnected()) {
                exit();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Close the resources and socket
     */
    public void exit() {
        if (client == null || in == null || out == null) {
            System.exit(0);
            return;
        }
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

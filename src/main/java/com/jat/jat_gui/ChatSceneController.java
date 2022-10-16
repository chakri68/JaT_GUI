package com.jat.jat_gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import syncChat.Client;

public class ChatSceneController {
    String hostName;
    int portNumber;
    Client client;
    @FXML
    TextField chatInp;
    @FXML
    ListView<String> chatList;
    @FXML
    Button sendBtn;

    public ChatSceneController() {
    }

    public ChatSceneController(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    @FXML
    public void initialize() {
        System.out.println("IN INITIALIZE");
        this.client = new Client(hostName, portNumber, this);
        Thread t = new Thread(this.client);
        t.start();
        sendBtn.setOnAction((event) -> handleSendBtn());
    }

    @FXML
    public void handleSendBtn() {
        client.sendMessage(chatInp.getCharacters().toString());
        chatInp.clear();
    }

    public void addNewMessage(String message) {
        chatList.getItems().add(message);
    }

    public void shutdown() {
        this.client.exit();
    }
}

package com.jat.jat_gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import syncChat.Client;

import java.io.IOException;
import java.util.Optional;

public class ChatSceneController {
    App app;
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

    public ChatSceneController(String hostName, int portNumber, App app) {
        this.app = app;
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

    public Optional<ButtonType> showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        return alert.showAndWait();
    }

    public void goToScene(String sceneName) {
        try {
            this.app.changeSceneTo(sceneName);
        } catch (IOException e) {
            showError("Error", "Something bad happened");
            e.printStackTrace();
        }
    }
}

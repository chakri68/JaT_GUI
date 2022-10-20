package com.jat.jat_gui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class EnterSceneController {
    @FXML
    TextField hostname;
    @FXML
    TextField portnumber;
    @FXML
    Button connectBtn;
    @FXML
    public void onConnect(){

        String hostName = hostname.getCharacters().toString();
        String portNumber = portnumber.getCharacters().toString();
        System.out.println("Hostname: " + hostName + ", port number: " + portNumber);
        ChatSceneController csc = new ChatSceneController(this.hostname.getCharacters().toString(), Integer.parseInt(this.portnumber.getCharacters().toString()), App.getApplicationInstance());
        App.getApplicationInstance().rootStage.setOnCloseRequest(e -> {
            csc.shutdown();
            Platform.exit();
            System.exit(0);
        });
        try {
            App.getApplicationInstance().changeSceneTo("chat", csc);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }


}

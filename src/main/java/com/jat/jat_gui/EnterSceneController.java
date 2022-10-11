package com.jat.jat_gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

    }
}

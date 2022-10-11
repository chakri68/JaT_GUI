package com.jat.jat_gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("EnterScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Java Chat GUI");
        stage.setScene(scene);
        stage.show();
        EnterSceneController esc = fxmlLoader.getController();
        esc.connectBtn.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("ChatScene.fxml"));
            ChatSceneController csc = new ChatSceneController(esc.hostname.getCharacters().toString(), Integer.parseInt(esc.portnumber.getCharacters().toString()));
            loader.setController(csc);
            stage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });
            try {
                stage.setScene(new Scene(loader.load()));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public static void main(String[] args) {
        launch();
    }
}
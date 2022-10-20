package com.jat.jat_gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.ScenePage;

import java.io.IOException;
import java.util.Map;

import static java.util.Map.entry;

public class App extends Application {
    private static App applicationInstance;

    public static App getApplicationInstance() {
        return applicationInstance;
    }

    @Override
    public void init() {
        applicationInstance = this;
    }
    Map<String, String> loaders;
    Stage rootStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.initLoaders();
        rootStage = stage;
        stage.setTitle("Java Chat GUI");
        FXMLLoader home = new FXMLLoader(App.class.getResource(this.loaders.get("home")));
        stage.setScene(new Scene(home.load()));
        stage.show();
        EnterSceneController esc = home.getController();
        esc.connectBtn.setOnAction(event -> {
            ChatSceneController csc = new ChatSceneController(esc.hostname.getCharacters().toString(), Integer.parseInt(esc.portnumber.getCharacters().toString()), this);
            stage.setOnCloseRequest(e -> {
                csc.shutdown();
                Platform.exit();
                System.exit(0);
            });
            try {
                changeSceneTo("chat", csc);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

    }

    void initLoaders() {

        this.loaders = Map.ofEntries(
                entry("home", "EnterScene.fxml"),
                entry("chat", "ChatScene.fxml")
        );
    }

    public void changeSceneTo(String loaderName) throws IOException {
        this.rootStage.setScene(new Scene(new ScenePage<>(new FXMLLoader(App.class.getResource(loaders.get(loaderName)))).getRoot()));
        this.rootStage.show();
    }
    public void changeSceneTo(String loaderName, Object controller) throws IOException, ClassNotFoundException {
        this.rootStage.setScene(new Scene(new ScenePage<>(new FXMLLoader(App.class.getResource((loaders.get(loaderName)))), controller).getRoot()));
        this.rootStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ScenePage<C> {
    public FXMLLoader getLoader() {
        return loader;
    }

    private final FXMLLoader loader;
    private C controller;
    private Parent node;
    public ScenePage(FXMLLoader loader) throws IOException {
        this.loader = loader;
        this.setNode();
    }
    public ScenePage(FXMLLoader loader, C controller) throws IOException {
        this.loader = loader;
        this.controller = controller;
        this.setNode();
    }

    public Parent getRoot(){
        return this.node;
    }
    private void setNode() throws IOException {
        if (this.controller != null) {
            this.loader.setController(controller);
        }
        this.node = this.loader.load();
    }
}

module com.jat.jat_gui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.jat.jat_gui to javafx.fxml;
    exports com.jat.jat_gui;
}
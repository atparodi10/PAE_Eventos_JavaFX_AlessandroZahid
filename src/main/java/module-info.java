module ni.edu.uam.pae_eventos_javafx {

    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens ni.edu.uam.pae_eventos_javafx
            to javafx.fxml;

    opens ni.edu.uam.pae_eventos_javafx.controllers
            to javafx.fxml;

    opens ni.edu.uam.pae_eventos_javafx.model
            to javafx.base;

    exports ni.edu.uam.pae_eventos_javafx;
}
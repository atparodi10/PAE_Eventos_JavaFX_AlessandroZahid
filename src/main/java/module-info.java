module ni.edu.uam.pae_eventos_javafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.pae_eventos_javafx to javafx.fxml;
    exports ni.edu.uam.pae_eventos_javafx;
}
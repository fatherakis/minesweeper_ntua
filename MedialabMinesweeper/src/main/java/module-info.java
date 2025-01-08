module com.minesweeper18870.medialabminesweeper {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires com.almasb.fxgl.all;

    opens com.minesweeper18870.medialabminesweeper to javafx.fxml;
    exports com.minesweeper18870.medialabminesweeper;
}
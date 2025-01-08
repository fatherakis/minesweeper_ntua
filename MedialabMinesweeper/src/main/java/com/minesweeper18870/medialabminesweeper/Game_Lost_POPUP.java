package com.minesweeper18870.medialabminesweeper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Game_Lost_POPUP {

    private Stage stage;

    /*
        Public method to show the popup
     */
    public void display(String message){
        Stage popup = new Stage();
        popup.initModality(Modality.WINDOW_MODAL);
        stage = popup;
        Scene scene = new Scene(initPane(message),400, 200);
        popup.setTitle("GAME LOST");
        popup.setScene(scene);
        popup.show();
        popup.setOnCloseRequest(e -> {
            popup.close();
        });
    }
    /*
        Popup layout. Very  simple 2 text labels + 1 close button
     */
    private VBox initPane(String message){
        VBox popup_pane = new VBox();
        popup_pane.setStyle("-fx-background-color: grey;");

        Label mes = new Label(message);
        mes.setStyle("-fx-background-color: grey;");
        mes.setPadding(new Insets(10,10,10,10));
        mes.setAlignment(Pos.CENTER);
        mes.setFont(Font.font(24));
        mes.setTextFill(Color.BLACK);

        Label mes2 = new Label("Game Over!");
        mes2.setStyle("-fx-background-color: grey;");
        mes2.setPadding(new Insets(10,10,10,10));
        mes2.setAlignment(Pos.CENTER);
        mes2.setFont(Font.font(28));
        mes2.setTextFill(Color.DARKRED);

        Button button = new Button();
        button.setTranslateY(10);
        button.setPadding(new Insets(10,10,10,10));
        button.setText("Close");
        button.setAlignment(Pos.CENTER);
        button.setStyle("-fx-background-color: darkgrey;");
        button.setOnAction(e -> stage.close());

        popup_pane.getChildren().addAll(mes,mes2,button);
        popup_pane.setAlignment(Pos.CENTER);

        return popup_pane;
    }
}

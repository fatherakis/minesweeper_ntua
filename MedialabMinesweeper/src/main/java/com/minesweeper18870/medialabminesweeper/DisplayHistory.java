package com.minesweeper18870.medialabminesweeper;

import com.almasb.fxgl.core.collection.Array;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.Objects;

public class DisplayHistory {

    /*
        Public method to show popup window
     */
    public void display(){
        Stage popup = new Stage();
        popup.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(initPane(),560, 250);
        popup.setTitle("GAME HISTORY SUMMARY");
        popup.setScene(scene);
        popup.setResizable(false);
        popup.show();
        popup.setOnCloseRequest(e -> {
            popup.close();
        });
    }

    /*
        Popup Layout: "Bubble" Based Tiles each with game info colored based on win or loss.
     */
    private VBox initPane () {
        VBox summaryWindow = new VBox();
        summaryWindow.setStyle("-fx-background-color: grey;");

        Array<String> games = load_games();

        summaryWindow.setSpacing(10);
        summaryWindow.setPadding(new Insets(5,5,5,5));
        if (!games.isEmpty()) {
            for (int i = 0; i < games.size(); i++) {
                String[] str = games.get(i).split(" ");
                HBox Tile = new HBox();

                Label TotalMines = new Label("Mines: " + str[0]);
                Label MoveCount = new Label("Moves Made: " + str[2]);
                Label TotalTime = new Label("Time Available: " + str[1]);
                Label gameState = new Label("Winner: " + str[3]);

                TotalMines.setTextFill(Color.BLACK);
                TotalMines.setAlignment(Pos.CENTER);
                TotalMines.setPadding(new Insets(5,5,5,5));
                int fontSize = 16;
                TotalMines.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
                MoveCount.setTextFill(Color.BLACK);
                MoveCount.setAlignment(Pos.CENTER);
                MoveCount.setPadding(new Insets(5,5,5,5));
                MoveCount.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
                TotalTime.setTextFill(Color.BLACK);
                TotalTime.setAlignment(Pos.CENTER);
                TotalTime.setPadding(new Insets(5,5,5,5));
                TotalTime.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
                gameState.setTextFill(Color.BLACK);
                gameState.setAlignment(Pos.CENTER);
                gameState.setPadding(new Insets(5,5,5,5));
                gameState.setFont(Font.font("Arial", FontWeight.BOLD, fontSize));
                Tile.getChildren().addAll(TotalMines,MoveCount,TotalTime,gameState);
                Tile.setAlignment(Pos.CENTER);
                Tile.setPadding(new Insets(5,5,5,5));
                if(Objects.equals(str[3], "Computer"))
                    Tile.setStyle(
                            "-fx-background-color: darkred;"
                            + "-fx-border-radius: 10 10 10 10;"
                            + "-fx-background-radius: 10 10 10 10;"
                    );
                else
                    Tile.setStyle(
                        "-fx-background-color: green;"
                        + "-fx-border-radius: 10 10 10 10;"
                        + "-fx-background-radius: 10 10 10 10;"
                    );
                summaryWindow.getChildren().add(Tile);
            }
        }
        return summaryWindow;
    }

    /*
        Game History loader. game_history.txt is read and pushed line by line in an Array
                             to get the information of each round.
     */
    private Array<String> load_games(){
        Array<String> textRead = new Array<>();
        File history = new File("medialab/game_history.txt");
        try {
            BufferedReader lineParser = new BufferedReader(new FileReader(history));
            String line;
            while ((line = lineParser.readLine()) != null){
                textRead.add(line);
            }
            lineParser.close();
        } catch (FileNotFoundException e) {
            return textRead;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return textRead;
    }
}

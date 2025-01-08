package com.minesweeper18870.medialabminesweeper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CreatePopup {
    private boolean ok = false;
    private int lvl,mine,timer,super_mine;
    private File scenario_file;
    private String id;
    private Stage stage;
    private List<SavedListener> listeners = new ArrayList<SavedListener>();

    /*
        Accessible by all method to create the popup.
     */
    public void display(){
        Stage popup = new Stage();
        popup.initModality(Modality.WINDOW_MODAL);
        stage = popup;
        Scene scene = new Scene(initPane(),500, 300);
        popup.setTitle("NEW GAME");
        popup.setScene(scene);
        popup.show();
        popup.setOnCloseRequest(e -> {
            popup.close();
        });
    }

    /*
        Popup layout with check & save action on click
     */
    private VBox initPane(){
        VBox popup_pane = new VBox();
        popup_pane.setStyle("-fx-background-color: grey;");

        HBox Scenario_ID = new HBox();

        Label Scenario = new Label("SCENARIO-ID: ");
        Scenario.setStyle("-fx-background-color: grey;");
        Scenario.setPadding(new Insets(10,10,10,10));
        Scenario.setAlignment(Pos.CENTER);
        Scenario.setFont(Font.font(18));
        Scenario.setTextFill(Color.BLACK);

        TextField ID = new TextField("Enter the game ID");
        ID.setPrefWidth(250);
        ID.setOnMouseClicked(e -> ID.setText(""));
        ID.setAlignment(Pos.CENTER);
        Scenario_ID.getChildren().addAll(Scenario,ID);
        Scenario_ID.setAlignment(Pos.CENTER);

        HBox Level_NUM = new HBox();

        Label Level = new Label("LEVEL: ");
        Level.setStyle("-fx-background-color: grey;");
        Level.setPadding(new Insets(10,10,10,10));
        Level.setAlignment(Pos.CENTER);
        Level.setFont(Font.font(18));
        Level.setTextFill(Color.BLACK);

        TextField NUM = new TextField("1: Level 1 (9x9), 2: Level 2 (16x16)");
        NUM.setPrefWidth(250);
        NUM.setAlignment(Pos.CENTER);
        NUM.setOnMouseClicked(e -> NUM.setText(""));
        Level_NUM.getChildren().addAll(Level,NUM);
        Level_NUM.setAlignment(Pos.CENTER);

        HBox Mines_Text = new HBox();

        Label Mines = new Label("MINES: ");
        Mines.setStyle("-fx-background-color: grey;");
        Mines.setPadding(new Insets(10,10,10,10));
        Mines.setAlignment(Pos.CENTER);
        Mines.setFont(Font.font(18));
        Mines.setTextFill(Color.BLACK);

        TextField Text = new TextField("(9-11) for level 1, (35-45) for level 2");
        Text.setPrefWidth(250);
        Text.setAlignment(Pos.CENTER);
        Text.setOnMouseClicked(e -> Text.setText(""));
        Mines_Text.getChildren().addAll(Mines,Text);
        Mines_Text.setAlignment(Pos.CENTER);

        HBox Timer_time = new HBox();

        Label Timer = new Label("TIME LIMIT: ");
        Timer.setStyle("-fx-background-color: grey;");
        Timer.setPadding(new Insets(10,10,10,10));
        Timer.setAlignment(Pos.CENTER);
        Timer.setFont(Font.font(18));
        Timer.setTextFill(Color.BLACK);

        TextField time = new TextField("(120-180) for level 1, (240-360) for level 2");
        time.setPrefWidth(250);
        time.setAlignment(Pos.CENTER);
        time.setOnMouseClicked(e -> time.setText(""));
        if (Objects.equals(time.getText(), "")) time.setText("(120-180) for level 1, (240-360) for level 2");
        Timer_time.getChildren().addAll(Timer,time);
        Timer_time.setAlignment(Pos.CENTER);

        HBox SUPERMINE_bool = new HBox();

        Label SUPERMINE = new Label("SUPER MINE: ");
        SUPERMINE.setStyle("-fx-background-color: grey;");
        SUPERMINE.setPadding(new Insets(10,10,10,10));
        SUPERMINE.setAlignment(Pos.CENTER);
        SUPERMINE.setFont(Font.font(18));
        SUPERMINE.setTextFill(Color.BLACK);

        ComboBox<String> bool = new ComboBox<String>();
        bool.getItems().add("Yes");
        bool.getItems().add("No");
        bool.setStyle("-fx-alignment: CENTER;");
        SUPERMINE_bool.getChildren().addAll(SUPERMINE,bool);
        SUPERMINE_bool.setAlignment(Pos.CENTER);


        Button button = new Button();
        button.setTranslateY(10);
        button.setPadding(new Insets(10,10,10,10));
        button.setText("Create New Game");
        button.setAlignment(Pos.CENTER);
        button.setStyle("-fx-background-color: darkgrey;");
        button.setOnAction(e -> {
            try {
                checkThresholds(ID.getText(), NUM.getText(),Text.getText(),time.getText(), String.valueOf((Objects.equals(bool.getValue(), "Yes"))? 1 : 0));
                saveFile();
                if(ok) {
                    stage.close();
                }
            }catch ( InvalidValueException | IOException er){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(er.getClass().getSimpleName());
                alert.setContentText(er.getMessage());
                alert.showAndWait();
            }
        });

        popup_pane.getChildren().addAll(Scenario_ID,Level_NUM,Mines_Text,Timer_time,SUPERMINE_bool,button);
        popup_pane.setAlignment(Pos.CENTER);

        return popup_pane;
    }

    /*
        Threshold check on button press. Throws exceptions based on the errors.
        **InvalidDescription** isn't used since layout and values are predefined.
     */
    private void checkThresholds(String id, String lvl, String mine, String timer, String super_mine) throws InvalidValueException {
        try{
            this.lvl = Integer.parseInt(lvl);
            this.mine = Integer.parseInt(mine);
            this.timer = Integer.parseInt(timer);
            this.super_mine = Integer.parseInt(super_mine);
        }catch (NumberFormatException e){
            throw new InvalidValueException("Please only use numbers in the fields");
        }
        this.id = id;
        if (this.lvl == 1){
            if (this.mine < 9 || this.mine > 11){
                throw new InvalidValueException("Level 1: Allowed mines are 9 to 11. Please choose accordingly!");
            }
            if (this.timer < 120 || this.timer > 180){
                throw new InvalidValueException("Level 1: Allowed time limit is 120 - 180 seconds!");
            }
            if (this.super_mine == 1)
                throw new InvalidValueException("Level 1: No Super-Mine allowed!");
        } else if(this.lvl == 2){
            if (this.mine < 35 || this.mine > 45){
                throw new InvalidValueException("Level 2: Allowed mines are 35 to 45. Please choose accordingly!");
            }
            if (this.timer < 240 || this.timer > 360){
                throw new InvalidValueException("Level 2: Allowed time limit is 240 - 360 seconds!");
            }
        } else {
            throw new InvalidValueException("Please choose Level 1 or 2!");
        }
        if (this.super_mine != 1 && this.super_mine != 0){
            throw new InvalidValueException("Please choose whether to have Super Mine");
        }
        ok = true;
    }
    /*
        Listener to notify when a file is done saving
     */
    public void addListener(SavedListener interestedClass){
        listeners.add(interestedClass);
    }

    /*
        File creation with values in the specified format to later be read with no errors
        Notify Listeners list when done.
     */
    private void saveFile() throws InvalidValueException,IOException {
        try {
            scenario_file = new File("medialab/" + id + ".txt");
            if (!scenario_file.createNewFile()) {
                throw new InvalidValueException("Game_ID already exists. Choose another one!");
            }
        } catch (IOException e){
            System.out.println("Error on saving file.");
            e.printStackTrace();
        }
        try {
            FileWriter scenario_writer = new FileWriter("medialab/" + id + ".txt");
            scenario_writer.write(String.valueOf(lvl));
            scenario_writer.write(System.lineSeparator());
            scenario_writer.write(String.valueOf(mine));
            scenario_writer.write(System.lineSeparator());
            scenario_writer.write(String.valueOf(timer));
            scenario_writer.write(System.lineSeparator());
            scenario_writer.write(String.valueOf(super_mine));
            scenario_writer.close();
            for (SavedListener s: listeners) s.fileSaved();
        } catch (IOException e){
            System.out.println("Error writing in file.");
            e.printStackTrace();
        }
    }

    /*
        Retrieves the File that saved.
        *** IMPORTANT:  Returns null unless notified from saveFile() ***
     */
    public File getFile(){
        return scenario_file;
    }
}

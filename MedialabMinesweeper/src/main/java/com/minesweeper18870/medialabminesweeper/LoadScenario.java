package com.minesweeper18870.medialabminesweeper;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class LoadScenario {
    /*
        JavaFX file selector on default directory medialab
     */
    private File loadScenario(){
        Stage popup = new Stage();
        popup.initModality(Modality.WINDOW_MODAL);

        FileChooser scenarioFileSelector = new FileChooser();
        scenarioFileSelector.setInitialDirectory(new File("medialab/"));
        scenarioFileSelector.setTitle("Choose Scenario File");
        scenarioFileSelector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scenario File", "*.txt"));

        File scenario_file = scenarioFileSelector.showOpenDialog(popup);
        popup.close();
        return scenario_file;
    }

    /*
        Reads file and checks values throwing exceptions with the corresponding message
     */
    private ArrayList<Integer> LoadVerifyArray(File Predefined) throws IOException,InvalidValueException,InvalidDescriptionException {
        ArrayList<Integer> values = new ArrayList<>();
            if (Predefined == null){
                return null;
            }
            FileReader file_read = new FileReader(Predefined);
            BufferedReader line_fetch = new BufferedReader(file_read);
            String line;
            int lines_read = 0;
            int var_conv = 0;
            while ((line = line_fetch.readLine()) != null) {
                try {
                    var_conv = Integer.parseInt(line);
                    values.add(var_conv);
                    lines_read++;
                } catch (NumberFormatException e) {
                    throw new InvalidValueException("Please check your values. Only numbers allowed.");
                }
            }
            if (lines_read != 4){
                throw new InvalidDescriptionException("You provided " + lines_read + " lines when 4 are required!");
            }
            //Checking Values
            if (values.get(0) != 1 && values.get(0) != 2){
                throw new InvalidValueException("Please choose level 1 or level 2 in your configuration!");
            } else {
                if (values.get(0) == 1) { values.add(4,9); } else { values.add(4,16); }
            }
            switch (values.get(0)) {
                case 1 -> {
                    if (values.get(1) > 11 || values.get(1) < 9) {
                        throw new InvalidValueException("Allowed mines are between 9 and 11. Please choose accordingly!");
                    }
                    if (values.get(2) > 180 || values.get(2) < 120) {
                        throw new InvalidValueException("Allowed available timer is from 120 to 180. Please choose accordingly!");
                    }
                    if (values.get(3) == 1) throw new InvalidValueException("Level 1: No Super-Mine allowed!");
                }
                case 2 -> {
                    if (values.get(1) > 45 || values.get(1) < 35) {
                        throw new InvalidValueException("Allowed mines are between 35 and 45. Please choose accordingly!");
                    }
                    if (values.get(2) > 360 || values.get(2) < 240) {
                        throw new InvalidValueException("Allowed available timer is from 240 to 360. Please choose accordingly!");
                    }
                }
                default -> {
                }
            }
            line_fetch.close();
        return values;
    }

    /*
        Loads game with user chosen file. Shows alert popup based on exception
     */
    public ArrayList<Integer> LoadGame(){
        try{
            return LoadVerifyArray(loadScenario());
        } catch (InvalidValueException | InvalidDescriptionException | IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(e.getClass().getSimpleName());
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return null;
    }

    /*
        Overloaded method: Loads game with a given file. Used for initial create&load game.
     */
    public ArrayList<Integer> LoadGame(File predefined){
        try{
            return LoadVerifyArray(predefined);
        } catch (InvalidValueException | InvalidDescriptionException | IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(e.getClass().getSimpleName());
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return null;
    }
}

package com.minesweeper18870.medialabminesweeper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Medialab_Minesweeper extends Application implements SavedListener,SuperMineTrigger {
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 768; //Window Sizes
    private CreatePopup gameSetup; //Create Game popup
    private Timer time_check = new Timer(); //Game timer
    private Stage stage_class; //Stage coming from Application. Used for the game after create/load
    private int grid_size,mines,time,current_time,superMine_y,superMine_x;
    private int state; //0 Computer wins  1 Human wins 2 Ongoing
    private int click_count,new_difficulty,new_grid_size,new_mines,new_time = 0;
    private boolean super_mine,new_super_mine,load = false;
    private boolean once = false; // Game variables
    private BorderPane grid; //Main window layout
    private Pane minesweeper; //Game layout
    private Label markedMinesLabel, remainingTimeLabel; //Updatable labels
    private boolean[][] mine_states; //Mine positions
    private Tile[][] tile_grid;      //Block elements

    @Override
    /*
        Triggered by create game popup when a file is created
        ignored unless its on window initialization
     */
    public void fileSaved(){
        if (load){
            LoadScenario gameLoad = new LoadScenario();
            ArrayList<Integer> res = gameLoad.LoadGame(gameSetup.getFile());
            if (res != null) {
                new_difficulty = res.get(0);
                new_grid_size = res.get(4);
                new_mines = res.get(1);
                new_time = res.get(2);
                new_super_mine = (res.get(3) == 1);
            }
            StartGame(stage_class);
        }
    }

    /*
        Triggered by right-click on a SuperMine.
        Ignored unless left-clicks < 5
     */
    @Override
    public void triggerEvent(){
        if (click_count < 5 && !once) {
            once = true;
            SuperMineReveal();
        }
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.stage_class = stage;
        InitializeWindow(stage);
    }

    /*
        First run layout consisting of create and load game
     */
    private VBox startingPane(Stage stage){
        VBox main_window = new VBox();
        Button create_game = new Button("Create New Game");
        Button load_game = new Button("Load Game");

        create_game.setOnAction(e -> {
            load = true;
            gameSetup = new CreatePopup();
            gameSetup.addListener(this);
            gameSetup.display();
        });
        create_game.setAlignment(Pos.CENTER);
        create_game.setPadding(new Insets(10,10,10,10));

        load_game.setOnAction(e -> {
            LoadScenario gameLoad = new LoadScenario();
            ArrayList<Integer> res = gameLoad.LoadGame();
            if(res != null){
                new_difficulty = res.get(0);
                new_grid_size = res.get(4);
                new_mines = res.get(1);
                new_time = res.get(2);
                new_super_mine = (res.get(3) == 1);
            }
            StartGame(stage);
        });
        load_game.setAlignment(Pos.CENTER);
        load_game.setPadding(new Insets(10,10,10,10));

        main_window.getChildren().addAll(create_game,load_game);
        main_window.setAlignment(Pos.CENTER);
        main_window.setSpacing(10);
        return main_window;
    }

    /*
        First run initialization Scene called from Application start
     */
    private void InitializeWindow(Stage stage){
        Scene scene = new Scene(startingPane(stage),WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("MediaLab Minesweeper");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e -> {
            stage.close();
            Platform.exit();
        });
    }

    /*
        Start Button function => Move loaded variables and initialize the scene
     */
    private void StartGame(Stage stage){
        if (new_difficulty != 0) {
            time_check.cancel();
            stage.close();
            grid_size = new_grid_size;
            mines = new_mines;
            time = new_time;
            super_mine = new_super_mine;
            initializeScene(stage);
            Scene scene = new Scene(grid,WINDOW_WIDTH, WINDOW_HEIGHT);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setTitle("MediaLab Minesweeper");
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(e -> {
                stage.close();
                Platform.exit();
            });
        }
    }

    /*
        Main Game layout:
            Top has menuBar + Buttons
            Mid has the game

        The check and timer are initialized and handled from here
     */
    private void initializeScene(Stage stage) {
        load = false;
        grid = new BorderPane();

        // Create the menu bar
        MenuBar menuBar = new MenuBar();

        // Create the "Application" menu
        Menu applicationMenu = new Menu("Application");
        MenuItem createMenuItem = new MenuItem("Create");
        MenuItem loadMenuItem = new MenuItem("Load");
        MenuItem startMenuItem = new MenuItem("Start");
        MenuItem exitMenuItem = new MenuItem("Exit");

        createMenuItem.setOnAction(event -> {
            CreatePopup gameSetup = new CreatePopup();
            gameSetup.display();
        });
        loadMenuItem.setOnAction(event -> {
            LoadScenario gameLoad = new LoadScenario();
            ArrayList<Integer> res = gameLoad.LoadGame();
            if(res != null){
                new_difficulty = res.get(0);
                new_grid_size = res.get(4);
                new_mines = res.get(1);
                new_time = res.get(2);
                new_super_mine = (res.get(3) == 1);
            }
        });
        startMenuItem.setOnAction(event -> {
            StartGame(stage);
        });
        exitMenuItem.setOnAction(event -> {
            time_check.cancel();
            stage.close();
            Platform.exit();
        });

        applicationMenu.getItems().addAll(createMenuItem, loadMenuItem, startMenuItem, new SeparatorMenuItem(), exitMenuItem);

        // Create the "Details" menu
        Menu detailsMenu = new Menu("Details");
        MenuItem roundsMenuItem = new MenuItem("Rounds");
        MenuItem solutionMenuItem = new MenuItem("Solution");

        roundsMenuItem.setOnAction(event -> {
            displayHistory();
        });
        solutionMenuItem.setOnAction(event -> {
            solveGame(minesweeper);
        });

        detailsMenu.getItems().addAll(roundsMenuItem, solutionMenuItem);

        // Add the "Application" and "Details" menus to the menu bar
        menuBar.getMenus().addAll(applicationMenu, detailsMenu);

        //Create top section
        HBox topSection = new HBox();
        topSection.setPadding(new Insets(10, 10, 10, 10));
        topSection.setSpacing(250);
        topSection.setAlignment(Pos.CENTER);
        topSection.setStyle("-fx-background-color: #808080;"); /* Use retro-minesweeper gray */

        Label totalMinesLabel = new Label("Total Mines: " + mines);
        totalMinesLabel.setTextFill(Color.WHITE);
        int current_mines = 0;
        markedMinesLabel = new Label("Marked Mines: " + current_mines);
        markedMinesLabel.setTextFill(Color.LIGHTGREEN);
        current_time = time;
        remainingTimeLabel = new Label("Remaining Time: " + current_time);
        remainingTimeLabel.setTextFill(Color.DARKRED);


        topSection.getChildren().addAll(totalMinesLabel, markedMinesLabel, remainingTimeLabel);
        topSection.getChildren().add(menuBar);

        HBox separator = new HBox();
        separator.setAlignment(Pos.CENTER);

        Line line = new Line(0,0,WINDOW_WIDTH,0);
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);

        separator.getChildren().addAll(line);

        BorderPane topPane = new BorderPane();
        topPane.setTop(menuBar);
        topPane.setCenter(topSection);
        topPane.setBottom(line);

        // Create the bottom section

        BorderPane minesweeperGrid = new BorderPane();
        minesweeperGrid.setPadding(new Insets(10, 10, 10, 10));
        minesweeperGrid.setStyle("-fx-background-color: #808080;");
        minesweeper = minesweeper_constructor();
        state = 2;
        click_count = 0;

        once = false;

        minesweeperGrid.getChildren().add(minesweeper);
        minesweeper.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) click_count++;
            checkState(minesweeper);
        });

        //new_game = new minesweeper(grid_size,mines,time,super_mine,time_check,markedMinesLabel);
        grid.setTop(topPane);
        grid.setCenter(minesweeperGrid);
        time_check = new Timer();
        time_check.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (current_time != 0) {
                        remainingTimeLabel.setText("Remaining Time: " + --current_time);
                    } else {
                        game_lost(minesweeper,"Your time is up! Be faster next time.");
                        this.cancel();
                    }
                });
            }
        }, 0, 1000);

    }

    /*
        Constructor of the minesweeper grid
            > Generates the mines
            > Creates Grid x Grid tiles and assigns the number for each tile
            > Gets positioned in the center of the main window layout.
     */
    private Pane minesweeper_constructor(){
        Pane minesweeper = new Pane();
        Generate_mines();
        tile_grid = new Tile[grid_size][grid_size];
        for (int y = 0; y < grid_size; y++){
            for (int x = 0; x < grid_size; x++){
                Tile block;
                if (x == superMine_x && y == superMine_y && super_mine) {
                    block = new Tile(x,y,mine_states[x][y],true);
                } else { block = new Tile(x,y,mine_states[x][y],false); }
                block.addListener(this);
                tile_grid[x][y] = block;
                minesweeper.getChildren().add(block);
            }
        }
        fetchNumbers();
        minesweeper.setLayoutX(WINDOW_WIDTH/2.0 - grid_size*20);
        minesweeper.setLayoutY(WINDOW_HEIGHT/2.0 - 30 - grid_size*20);
        return minesweeper;
    }

    /*
        Checks the whole grid for a pressed mine and counts marked ones.
        Also handles the won or lost popup and disabling the tiles when markedMines = TotalMines
     */
    private void checkState(Pane gamePane) {
        int non_mines_open = 0;
        int current_mines = 0;
        for (int i = 0; i < tile_grid.length; i++){
            for (int j = 0; j < tile_grid.length; j++){
                if (tile_grid[i][j].get_open()){
                    if (mine_states[i][j]){
                        game_lost(gamePane,"Uh oh! You stepped on a mine!");
                    }else {
                        non_mines_open++;
                    }
                }else if (tile_grid[i][j].get_mark()) {
                    current_mines++;
                    markedMinesLabel.setText("Marked Mines: " + current_mines);
                }
                if(current_mines < mines){
                    tile_grid[i][j].set_markAllowed();
                }
            }
        }
        if(current_mines >= mines){
            for (Tile[] t: tile_grid) {for (Tile r: t) {r.unset_markAllowed();}}
        }
        if (non_mines_open == grid_size * grid_size - mines){
            game_won(gamePane);
        }
    }

    /*
        Recursive mine generation for a total of ${mines} mines
     */
    private void Generate_mines() {
        boolean[] mine_pos = new boolean[grid_size*grid_size];
        boolean[] supermineLoc = new boolean[grid_size*grid_size];
        int num_mines = mines;
        Arrays.fill(mine_pos,0,grid_size*grid_size,false);
        Arrays.fill(supermineLoc,0,grid_size*grid_size,false);
        int superminePos = (super_mine)? (int) (Math.random() * mines) : 0;
        Generate_mines_recur(mine_pos,supermineLoc,num_mines,superminePos);
        translate_mines(mine_pos,supermineLoc);
    }
    private void Generate_mines_recur(boolean[] mine_pos,boolean[] SuperMine, int num_mines, int supermine) {
        for (int i = 0; i < grid_size * grid_size; i++){
            if(!mine_pos[i]) {
                boolean isMine = Math.random() * 0.6 > 0.5;
                if (num_mines == 0) isMine = false;
                if (isMine && num_mines == supermine) { SuperMine[i] = true; num_mines--;}
                else if (isMine) num_mines--;
                mine_pos[i] = isMine;
            }
        }
        if (num_mines > 0) Generate_mines_recur(mine_pos,SuperMine,num_mines,supermine);
    }
    /*
        Translates 1D mines array to 2D mine_states array and outputs them to medialab/mines.txt
     */
    private void translate_mines(boolean[] mine_pos,boolean[] SuperMine) {
        try{
            FileWriter mines = new FileWriter("medialab/mines.txt",false);
            List<String> list = new ArrayList<>();
            mine_states = new boolean[grid_size][grid_size];
            for (int i = 0; i < grid_size*grid_size; i++){
                if (mine_pos[i]) {
                    list.add( i / grid_size + "," + i % grid_size + "," + ((SuperMine[i])? 1 : 0) );
                }
                if (SuperMine[i]) {
                    superMine_y = i / grid_size;
                    superMine_x = i % grid_size;
                }
                mine_states[i%grid_size][i/grid_size] = mine_pos[i];
            }
            list = list.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
            for (String str: list) mines.write(str + System.lineSeparator());
            mines.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /*
        Counts neighboring mines of each tile.
     */
    private void fetchNumbers(){
        for (int x = 0; x < grid_size; x++){
            for (int y = 0; y < grid_size; y++){
                Tile temp = tile_grid[x][y];
                countBombs(temp,x,y);
            }
        }
    }
    /*
        Counts and sets the color of a tile based on the mines surrounding it.
     */
    private void countBombs(Tile temp, int x, int y) {
        int bombs = 0;
        if(!temp.get_bomb()){
            bombs = (int) temp.fetchNeighboringTiles(tile_grid,x,y).stream().filter(Tile::get_bomb).count();
            if (bombs > 0){
                temp.set_Text(String.valueOf(bombs));
                if (bombs == 1){
                    temp.set_TextFill(Color.BLUE);
                } else if (bombs == 2) {
                    temp.set_TextFill(Color.DARKGREEN);
                } else if (bombs == 3){
                    temp.set_TextFill(Color.RED);
                } else if (bombs == 4) {
                    temp.set_TextFill(Color.PURPLE);
                } else if (bombs == 5) {
                    temp.set_TextFill(Color.DARKRED);
                } else if (bombs == 6) {
                    temp.set_TextFill(Color.MEDIUMTURQUOISE);
                } else if (bombs == 8) {
                    temp.set_TextFill(Color.GREY);
                }
            }
        }
    }
    /*
        Game won by Human POPUP call (revealed all the non-mine tiles)
     */
    private void game_won(Pane gameBoard){
        time_check.cancel();
        Game_Won_POPUP popup = new Game_Won_POPUP();
        popup.display("Congratulations! You avoided all the mines!");
        gameBoard.setDisable(true);
        state = 1;
        savetoHistory();
    }
    /*
        Game lost by human POPUP call (pressed on mine or time run out)
     */
    private void game_lost(Pane gameBoard, String message){
        time_check.cancel();
        Game_Lost_POPUP popup = new Game_Lost_POPUP();
        popup.display(message);
        gameBoard.setDisable(true);
        state = 0;
        savetoHistory();
    }
    /*
        Supermine Mechanic called from the Interface, forcing open the tiles
            without recursion on empty tiles
     */
    private void SuperMineReveal(){
        for (int i = 0; i < grid_size; i++){
            tile_grid[i][superMine_y].supermineMechanic();
            tile_grid[superMine_x][i].supermineMechanic();
        }
    }
    /*
        When a game is lost or won it gets pushed into medialab/game_history.txt
     */
    private void savetoHistory() {
        ArrayList<String> games = new ArrayList<>();
        File history = new File("medialab/game_history.txt");
        try {
            BufferedReader lineParser = new BufferedReader(new FileReader(history));
            String line;
            while ((line = lineParser.readLine()) != null){
                games.add(line);
            }
            lineParser.close();
        } catch (FileNotFoundException e) {
            try {
                history.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            String game_summary = mines + " " + time + " " + click_count + " ";
            if (state == 0){
                game_summary = game_summary + "Computer";
            } else game_summary = game_summary + "Human";

            games.add(0,game_summary);
            FileWriter writeHistory = new FileWriter(history,false);

            for (int i = 0; i < Math.min(games.size(),5); i++){
                writeHistory.write(games.get(i) + System.lineSeparator());
            }

            writeHistory.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*
        Called from details->solution button => marks game as lost and solves it.
     */
    private void solveGame(Pane gameBoard) {
        if(state == 2) {
            state = 0;
            savetoHistory();
            for (Tile[] row : tile_grid) {
                for (Tile tile : row) {
                    if (tile.get_bomb() && !tile.get_mark()) {
                        tile.markAsMine();
                    } else tile.reveal();
                }
            }
            time_check.cancel();
            gameBoard.setDisable(true);
            state = 0;
        }
    }
    /*
        Previous games POPUP  <- accessed from details->rounds
     */
    private void displayHistory(){
        DisplayHistory summary = new DisplayHistory();
        summary.display();
    }

    public static void main(String[] args) {
        launch();
    }
}
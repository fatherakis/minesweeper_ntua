package com.minesweeper18870.medialabminesweeper;

import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;


public class Tile extends StackPane {
    private final int Tile_Size = 40;
    private final boolean bomb, supermine;
    private boolean markAllowed = true;
    private boolean open,marked = false;
    private List<Tile> neighbors = new ArrayList<>();
    private final Rectangle border = new Rectangle(Tile_Size-2,Tile_Size-2);
    private final Text text = new Text();
    private final List<SuperMineTrigger> listeners = new ArrayList<SuperMineTrigger>();

    /**
     *  Tile constructor of each tile that will be painted on screen.
     *  The given inputs are saved in its class and the rectangle is styled.
     *  Mouse clicks are also checked where left click opens the tile
     *  and right click marks the tile and notifies the listeners if the tile is a supermine.
     * @param x Sets the row position in the minesweeper grid.
     * @param y Sets the column position in the minesweeper grid.
     * @param bomb Is a boolean that creates a Bomb block.
     * @param supermine Is another boolean that sets the bomb block as a supermine.
     */
    public Tile(int x ,int y, boolean bomb, boolean supermine){
        this.bomb = bomb;
        this.supermine = supermine;

        border.setStroke(Color.LIGHTGREY);
        border.setFill(Color.DARKGREY);
        text.setFont(Font.font(36));
        text.setText(bomb ? "X": "");
        text.setVisible(false);
        getChildren().addAll(border,text);

        setTranslateX(x * Tile_Size);
        setTranslateY(y * Tile_Size);
        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                reveal();
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                markAsMine();
                if (this.supermine) {
                    for (SuperMineTrigger s: listeners) s.triggerEvent();
                }
            }
        });

    }

    /**
     * addListener adds the interestedClass in a list of all the interested classes
     * to be notified when the SuperMine is triggered using the SuperMineTrigger Interface.
     * @param interestedClass Adds the interested class of the SuperMineTrigger interface in a list
     *                        to later be notified.
     */
    public void addListener(SuperMineTrigger interestedClass){
        listeners.add(interestedClass);
    }

    /**
     * This method opens the tile. Unless the tile is already in an opened state,
     * it is set as opened and its style is changed to be shown as such.
     * If the tile is a bomb then a mine image is loaded from the medialab folder
     * and its set on the block to signify the bomb "behind" the hidden block.
     * If it's not a bomb then the number of surrounding mines is shown (text).
     *      in case has no mines around it, it reveals all neighbouring blocks.
     */
    public void reveal() {
        if (open) return;
        open = true;
        border.setFill(Color.DIMGREY);
        if(bomb){
            String path = System.getProperty("user.dir");
            Image img_mine = new Image("file://" + path + "/medialab/mine.png");
            border.setFill(new ImagePattern(img_mine));
            text.setText(".");
            return;
        }

        text.setVisible(true);

        if(text.getText().isEmpty()) {
            neighbors.forEach(Tile::reveal);
        }
    }

    /**
     * When the SuperMine is triggered, all corresponding blocks run this mechanic.
     * If the block is not open then, if it's a bomb and unmarked, it gets marked.
     * if the unopened block is not a bomb then it is simply revealed and set as open.
     * Lastly since it's an interaction from the SuperMine, it gets disabled so no user input
     *                                                     will be able to change its state.
     */
    public void supermineMechanic(){
        if(!open) {
            if (bomb) {
                if(!marked)
                    markAsMine();
            } else {
                open = true;
                border.setFill(Color.DIMGREY);
                text.setVisible(true);
            }
        }
        this.setDisable(true);
    }

    /**
     * This method simply disables the ability for a block to get marked.
     * Its intended use is when the total marked blocks are as many as the marked mines.
     */
    public void unset_markAllowed() {
        markAllowed = false;
    }

    /**
     * This method simply enables the ability for a block to get marked.
     * It is intended to be used when the marking has been previously disabled but now it is allowed.
     */
    public void set_markAllowed() {
        markAllowed = true;
    }

    /**
     * Accesses the Tile's bomb variable.
     * @return True or False if the tile is a bomb
     */
    public boolean get_bomb(){
        return bomb;
    }

    /**
     * Accesses the Tile's marked variable.
     * @return True or False if the tile is marked
     */
    public boolean get_mark(){
        return marked;
    }

    /**
     * Accesses the Tile's open variable.
     * @return True or False if the tile is opened
     */
    public boolean get_open(){
        return open;
    }

    /**
     * Sets the tile's text as the given message.
     * It is used to change the tile's number based on the neighbouring mines
     * @param message The number that will be set as the Tile text.
     */
    public void set_Text(String message){
        text.setText(message);
    }

    /**
     * Sets the tile's color as the given color.
     * This exists just to make the game feel closer to the original version
     * where each number changes color based on its importance (e.g. 1 is blue and 5 is dark purple)
     * @param color The Color that will be set as the Tile text color.
     */
    public void set_TextFill(Color color){
        text.setFill(color);
    }

    /**
     * This method is called to mark the block as a mine.
     * Unless the block is opened, if its already marked it gets unmarked thus being used as a toggle.
     * If it isn't marked then the flag image is loaded from the medialab folder and set in the tile.
     */
    public void markAsMine(){
        if(!open) {
            if (marked) {
                border.setFill(Color.DARKGREY);
                marked = false;
            } else {
                if (markAllowed) {
                    String path = System.getProperty("user.dir");
                    Image img_mine = new Image("file://" + path + "/medialab/flag.png");
                    border.setFill(new ImagePattern(img_mine));
                    marked = true;
                }
            }
        }
    }

    /**
     * This method creates a list with all the *existing* neighboring blocks of the block that's called.
     * @param grid The 2D Tile array containing all the tiles of the block.
     * @param x The row position of the tile in the array provided
     * @param y The column position of the tile in the array provided
     * @return a List of Tiles containing all the neighboring blocks.
     */
    public List<Tile> fetchNeighboringTiles(Tile[][] grid,int x, int y){
        List<Tile> neighbor = new ArrayList<>();
            /*
              -1,-1 0,-1, 1,-1
              -1,0   X    1,0
              -1,1  0,1   1,1
             */
        int max = grid.length;
        if (x - 1 >= 0 ){
            if (y - 1 >= 0) {
                neighbor.add(grid[x-1][y-1]);
            }
            neighbor.add(grid[x-1][y]);
            if (y + 1 < max) {
                neighbor.add(grid[x-1][y+1]);
            }
        }
        if (x + 1 < max) {
            if (y - 1 >= 0) {
                neighbor.add(grid[x+1][y-1]);
            }
            neighbor.add(grid[x+1][y]);
            if (y + 1 < grid.length) {
                neighbor.add(grid[x+1][y+1]);
            }
        }
        if (y - 1 >= 0) neighbor.add(grid[x][y-1]);
        if (y + 1 < max) neighbor.add(grid[x][y+1]);
        neighbors = neighbor;
        return neighbor;
    }

}

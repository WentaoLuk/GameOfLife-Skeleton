package gameoflife.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * The main class for running the Game of Life.<br>
 * This class includes most of the GUI components.
 *
 * @author Shariar (Shawn) Emami, Wentao Lu
 * @version May 17, 2021
 */
public class GameOfLife extends Application {

    private static final int CELL_WIDTH = 10;
    private static final int CELL_HEIGHT = 10;
    private static final int CELL_COUNT_ROW = 60;
    private static final int CELL_COUNT_COL = 60;

    private static final String CELL_DEAD_STYLE_ID = "cell";
    private static final String CELL_ALIVE_STYLE_ID = "cell_selected";
    private static final String BUTTON_EDIT_ICON_STYLE_ID = "button_edit";
    private static final String BUTTON_ERASE_ICON_STYLE_ID = "button_erase";
    private static final String BUTTON_RESET_ICON_STYLE_ID = "button_reset";
    private static final String BUTTON_INFO_ICON_STYLE_ID = "button_info";
//    private static final String CREDIT_TEXT_PATH = "credit.txt";


    private enum Tool {
        PEN, ERASER
    }

    private static final String TITLE = "Conway's Game Of Life - Skeleton";

    private GridPane grid;
    private String[] backupGridStyleArray;
    private int pressedGirdUnitIndex;

    private BorderPane root;
    private ToolBar menuBar;
    private ToolBar statusBar;
    private ToggleGroup selectedTool;

    private Alert infoDialog;
    private Label generationCount;

    private int generation;

    private Label[][] cells;

    public void init() throws Exception {

        cells = new Label[CELL_COUNT_ROW][CELL_COUNT_COL];

        menuBar = new ToolBar();

        grid = new GridPane();

        //set up the backup grid for later use.
        statusBar = new ToolBar();

        selectedTool = new ToggleGroup();

        //call the method createToolBar, createGridContent, and createStatusBar.
        createToolBar();

        createGridContent(CELL_COUNT_ROW, CELL_COUNT_COL);

        createStatusBar();

        backupGridStyleArray = new String[CELL_COUNT_ROW * CELL_COUNT_COL];

        for (int i = 0; i < grid.getChildren().stream().count(); i++) {
            backupGridStyleArray[i] = grid.getChildren().get(i).getId();
        }
        // initialize the index as -1 and verify it later when the undo function is triggered.
        pressedGirdUnitIndex = -1;

        root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(grid);
        root.setBottom(statusBar);

        generation = 0;
        KeyCodeCombination returnComb = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);

        root.setOnKeyPressed(e -> {
//            If user pressed space, if will go to the next round.
            if (e.getCode() == KeyCode.SPACE) {
                generationCount.setText(++generation + "");
            }
            if (returnComb.match(e)) {
                undoProcess();
            }
        });
        //In the lambda if the key pressed is a space, increment the value of
        //generation and set it on generationCount label.
        //The lambda for events always takes one argument.
        //This argument can be of many types like MouseEvent, KeyEvent, and ActionEvent.
        //You can find the type by looking at the documentation of setOnKeyPressed.
        //To see what key is pressed you can use the method getCode on the lambda argument.
        //This will return the code for the key pressed.
        //Now compare the code to one of the static values in the class KeyCode.
        //In this case it will be space.
    }

    public void undoProcess() {
        for (int i = 0; i < grid.getChildren().stream().count(); i++) {
            grid.getChildren().get(i).setId(backupGridStyleArray[i]);
        }
        //This step is to make the initially pressed unit reverse to the opposite style.
        if (pressedGirdUnitIndex != -1) {
            grid.getChildren().get(pressedGirdUnitIndex).setId(
                    grid.getChildren().get(pressedGirdUnitIndex).getId() == CELL_ALIVE_STYLE_ID ?
                            CELL_DEAD_STYLE_ID : CELL_ALIVE_STYLE_ID
            );
        }
    }

    public void start(Stage primaryStage) throws Exception {
        // Alert must be created inside of start method as it needs to be created on JAVAFX Thread.
        infoDialog = new Alert(AlertType.INFORMATION);
        //read the special JavaFX CSS file.
        infoDialog.setContentText("Icons created by freepik.\n" +
                "https://www.flaticon.com/authors/freepik ");
        // scene holds all JavaFX components that need to be displayed in Stage.

        Scene scene = new Scene(root);

        scene.getStylesheets().add("root.css");

        primaryStage.setScene(scene);

        primaryStage.setTitle(TITLE);
        primaryStage.setResizable(true);
        // when escape key is pressed close the application.
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {

            if (!event.isConsumed() && KeyCode.ESCAPE == event.getCode()) {
                primaryStage.hide();
            }
        });

        // display the JavaFX application.
        primaryStage.show();
        //since grid is the node with the primary key listener on it, request focus on it.

        grid.requestFocus();

    }

    /**
     * Helper method to create the ToolBar at the top. This will hold the options.
     */
    private void createToolBar() {

        //These two buttons will be functional for editing or erasing the content.
        ToggleButton penToolButton = new ToggleButton("Edit");

        penToolButton = createButton(penToolButton.getClass(), BUTTON_EDIT_ICON_STYLE_ID, false, Tool.PEN, null);

        ToggleButton eraseToolButton = new ToggleButton("Erase");

        eraseToolButton = createButton(eraseToolButton.getClass(), BUTTON_ERASE_ICON_STYLE_ID, false, Tool.ERASER, null);

        //To allow selectedTool to keep track of pressed button we add the desired buttons to it.
        penToolButton.setToggleGroup(selectedTool);
        eraseToolButton.setToggleGroup(selectedTool);

        Button restCanvasButton = new Button();

        restCanvasButton = createButton(restCanvasButton.getClass(), BUTTON_RESET_ICON_STYLE_ID,
                false, null, (ActionEvent e) -> {
                    for (Node unit : grid.getChildren()) {
                        unit.setId(CELL_DEAD_STYLE_ID);
                    }
                });

        KeyCodeCombination returnComb = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        Button undoButton = new Button("Undo(Ctrl+Z)");

        undoButton.setMinHeight(32);
//        undoButton.setOnKeyPressed(event -> {
//                    if (returnComb.match(event)) {
//                        undoProcess();
//                    }
//                }
//        );
        undoButton.setOnAction(event -> {
            undoProcess();
        });


        //the Pane object will be used as a filler.
        Pane fillerPane = new Pane();

        //this is to allow the Pane object to grow as much as needed to fill the width space.
        HBox.setHgrow(fillerPane, Priority.ALWAYS);

        Button popDialogButton = new Button();
        popDialogButton = createButton(popDialogButton.getClass(), BUTTON_INFO_ICON_STYLE_ID, false, null, (ActionEvent e) -> {
            infoDialog.showAndWait();
        });

        Separator separator = new Separator();
        menuBar.getItems().addAll(penToolButton, eraseToolButton, separator, restCanvasButton, fillerPane, popDialogButton);

    }

    /**
     * This is a helper method to create different types of buttons.<br>
     * In JavaFX buttons inherit from the class ButtonBase. Meaning if a generic
     * method return the ButtonBase class we can create any button in the same method.<br>
     * <br>
     * Then why use generic? Why not just return the return type ButtonBase?<br>
     * Using generic we can have the return type of the desired type, meaning we wont have
     * to cast it to get access to special methods. It is more convenient.
     *
     * @param <T>       - generic type of method which inherits from ButtonBase.
     * @param clazz     - The class type of the object we are creating. This is used to find the constructor with reflection.
     * @param id        - CSS id used for the button.
     * @param focusable - is this button focusable.
     * @param userDta   - special user data to store in the object. We will use this for ToggleButton to store the Tool type.
     * @param action    - the lambda for setOnAction event.
     * @return a fully initialized button.
     */
    private <T extends ButtonBase> T createButton(Class<T> clazz, String id, boolean focusable, Object userDta,
                                                  EventHandler<ActionEvent> action) {
        //The code below is using the reflection library to access the default constructor of
        //the generic ButtonBase class. Using that constructor create a new instance of the object.
        T button = null;
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            button = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException(e);
        }
        //set up the button.
        button.setFocusTraversable(focusable);
        button.setUserData(userDta);
        button.setId(id);
        button.setOnAction(action);
        return button;
    }

    /**
     * This is helper method to create the labels in the main grid.
     *
     * @param rows - the number of rows in the grid.
     * @param cols - the number of columns in the the grid.
     */
    private void createGridContent(int rows, int cols) {

        //use the method createLabel to create a new label.
        //then assign that label to the cells array.
        //Finally using the method add(Node child, int columnIndex, int rowIndex)
        //of grid add the label to the grid.
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Label l = createLabel(row, col);
                cells[row][col] = l;
                grid.add(l, col, row);
            }
        }

        // Whenever the mouse is released, the program will take a "screenshot" of the current pattern as backup.
        //However, this will not correctly save the grid unit on which the user started drawing. So I used
        //pressedGirdUnitIndex to take the index and reverse the unit's css style when the undo function is triggered.
        grid.setOnMousePressed(event -> {
            //loop to save
            for (int i = 0; i < grid.getChildren().stream().count(); i++) {
                backupGridStyleArray[i] = grid.getChildren().get(i).getId();
            }
        });
    }

    /**
     * This is a utility method for creating a Label.
     *
     * @param row - the row at which the label is placed.
     * @param col - the column at which the label is placed.
     * @return a fully initialized label object.
     */
    private Label createLabel(int row, int col) {
        //call and set up the label
        Label label = new Label();
        label.setMaxSize(CELL_WIDTH, CELL_HEIGHT);
        label.setMinSize(CELL_WIDTH, CELL_HEIGHT);
        label.setId(CELL_DEAD_STYLE_ID);

        //once mouse is pressed, it start drawing/erasing.
        label.setOnMousePressed(event -> {
            labelMouseAction(event, label, row, col);
            //This will set the index into the button index clicked.
            if (selectedTool.getSelectedToggle() != null) {
                pressedGirdUnitIndex = row * CELL_COUNT_COL + col;//heyyy
            }
        });
        //1. First set up full drag as a basic listener
        label.setOnDragDetected(event -> {
            label.startFullDrag();
        });
        //2. set up each drag's reaction on the label.
        label.setOnMouseDragEntered(e -> {
            labelMouseAction(e, label, row, col);
        });

        return label;
    }

    /**
     * this method is used to determine the action of the mouse on a given label.
     *
     * @param event
     * @param l     - the effected label
     * @param row   - the row at which the label is placed.
     * @param col   - the column at which the label is placed.
     */
    private void labelMouseAction(MouseEvent event, Label l, int row, int col) {
        //an action can only occur of a button from the selectedTool is selected.
        //depending on what tool is selected change the id of label
        if (selectedTool.getSelectedToggle() == null) {
            return;
        }
        if (selectedTool.getSelectedToggle().getUserData() == Tool.PEN) {
            if (event.getButton() == MouseButton.PRIMARY) {
                l.setId(CELL_ALIVE_STYLE_ID);
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                l.setId(CELL_DEAD_STYLE_ID);
            }
        } else if (selectedTool.getSelectedToggle().getUserData() == Tool.ERASER) {
            l.setId(CELL_DEAD_STYLE_ID);
        }
    }

    /**
     * create and initialize all the objects that are needed for the ToolBar at the bottom of GUI.
     */
    private void createStatusBar() {
        Label generationText = new Label("Generation: ");
        generationCount = new Label("0");
        Pane fillerPane = new Pane();
        //this is to allow the Pane object to grow as much as needed to fill the width space.
        HBox.setHgrow(fillerPane, Priority.ALWAYS);
        Label noticeLabel = new Label("Press and Hold Space");

        //use the statusBar object to store the 4 object
        //to access the list of children in a ToolBar use the method getItems.
        //getItems method will return a list. use addAll on it to add all the Nodes.
        statusBar.getItems().addAll(generationText, generationCount, fillerPane, noticeLabel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

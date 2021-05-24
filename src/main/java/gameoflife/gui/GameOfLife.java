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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * The main class for running the Game of Life.<br>
 * This class includes most of the GUI components.
 *
 * @author Shariar (Shawn) Emami
 * @version May 17, 2021
 */
//TODO Inherit from Application class. The main JavaFX class must inherit from Application.
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
    private static final String CREDIT_TEXT_PATH = "credit.txt";


    private enum Tool {
        PEN, ERASER
    }

    public ToggleGroup getSelectedTool() {
        return selectedTool;
    }

    public void setSelectedTool(ToggleGroup selectedTool) {
        this.selectedTool = selectedTool;
    }

    private static final String TITLE = "Conway's Game Of Life - Skeleton";

    private GridPane grid;
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

        menuBar = new ToolBar(
                new Button("test button")
        );
        grid = new GridPane();
//        grid.setPadding(new Insets(10, 10, 10, 10));

        statusBar = new ToolBar();

        selectedTool = new ToggleGroup();

        //call the method createToolBar, createGridContent, and createStatusBar.
        createToolBar();

        createGridContent(CELL_COUNT_ROW, CELL_COUNT_COL);

        createStatusBar();


        root = new BorderPane();

        root.setTop(menuBar);

        root.setCenter(grid);

        root.setBottom(statusBar);

        //TODO call setOnKeyPressed on grid and pass a lambda to it.

        generation = 0;

        grid.setOnKeyPressed(e -> {
//            If user pressed space, if will go to the next round.
            if (e.getCode() == KeyCode.SPACE) {
                generationCount.setText(++generation + "");
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

    public void start(Stage primaryStage) throws Exception {
        // Alert must be created inside of start method as it needs to be created on JAVAFX Thread.
        infoDialog = new Alert(AlertType.INFORMATION);
        //read the special JavaFX CSS file.
        infoDialog.setContentText(Files.readString(Paths.get(CREDIT_TEXT_PATH)));
        // scene holds all JavaFX components that need to be displayed in Stage.

        StackPane layout = new StackPane();

        Scene scene = new Scene(root);

        scene.getStylesheets().add("root.css");

        primaryStage.setScene(scene);

        primaryStage.setTitle(TITLE);
        primaryStage.setResizable(true);
        // when escape key is pressed close the application.
        primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {

            if (!event.isConsumed() && KeyCode.ESCAPE == event.getCode()) {
//                primaryStage.hide();
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
        //TODO use the method createButton to create two ToggleButton. --Done
        //First button will have id of BUTTON_EDDIT_ICON_STYLE_ID, not focusable, for the Pen Tool, and no event handler.
        //second button will have id of BUTTON_ERASE_ICON_STYLE_ID, not focusable, for the ERASER Tool, and no event handler.
        //The reason neither button has event listener is ToggleGroup we initialized in init method.
        //selectedTool object which is of type ToggleGroup keeps track of which button is pressed.


        ToggleButton penToolButton = new ToggleButton("Edit");

        penToolButton = createButton(penToolButton.getClass(), BUTTON_EDIT_ICON_STYLE_ID, false, Tool.PEN, null);


        ToggleButton eraseToolButton = new ToggleButton("Erase");

        eraseToolButton = createButton(eraseToolButton.getClass(), BUTTON_ERASE_ICON_STYLE_ID, false, Tool.ERASER, null);

        //TODO To allow selectedTool to keep track of pressed button we need to add the --Done
        //desired buttons to it.
        //call setToggleGroup on each of the buttons created above and pass to them selectedTool.

        penToolButton.setToggleGroup(selectedTool);
        eraseToolButton.setToggleGroup(selectedTool);

        //TODO use the method createButton to create a Button. This button is for resting the canvas.
        //This button will use BUTTON_RESET_ICON_STYLE_ID for CSS Id, not focusable, and no user data.

        //For the lambda we need to call setId on all the labels in grid.
        //To get the labels in the grid use the method getChildren on grid.
        //This will return a list. Now loop through the list can call setId on each label and pass to it CELL_DEAD_STYLE_ID.


        Button restCanvasButton = new Button();

        restCanvasButton = createButton(restCanvasButton.getClass(), BUTTON_RESET_ICON_STYLE_ID,
                false, null, (ActionEvent e) -> {
                    for (Node unit: grid.getChildren()){
                        unit.setId(CELL_DEAD_STYLE_ID);
                    }
        });


        //TODO create a new Pane object, this object will be used as a filler.--Done
        Pane fillerPane = new Pane();

        //TODO call the static method setHgrow from HBox and pass to it the pane object and Priority.ALWAYS.--Done
        //this is to allow the Pane object to grow as much as needed to fill the width space.

        HBox.setHgrow(fillerPane, Priority.ALWAYS);

        //TODO use the createButton method to make another Button.--Done
        //This button will use BUTTON_INFO_ICON_STYLE_ID as CSS Id, not focusable, and no user data.
        //As for the lambda we want it pop open the information dialog.
        //in the lambda simply call the method showAndWait on infoDialog.

        Button popDialogButton = new Button();


        popDialogButton = createButton(popDialogButton.getClass(), BUTTON_INFO_ICON_STYLE_ID, false, null, (ActionEvent e) -> {
            infoDialog.showAndWait();
        });

        //TODO use the menuBar object to store the 5 object we created here.--Done
        //to access the list of children in a ToolBar use the method getItems.
        //getItems method will return a list. use addAll on it to add all the Nodes.

        Separator separator = new Separator();
        menuBar.getItems().addAll(penToolButton, separator, eraseToolButton, restCanvasButton, fillerPane, popDialogButton);

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

        //TODO call the method setFocusTraversable on the created button and pass to it focusable.
        button.setFocusTraversable(focusable);
        //TODO call the method setUserData on the created button and pass to it userDta.
        button.setUserData(userDta);
        //TODO call the method setId on the created button and pass to it id.
        button.setId(id);
        //TODO call the method setOnAction on the created button and pass to it action.
        button.setOnAction(action);
        //TODO finally return the created button.
        return button;
    }

    /**
     * This is helper method to create the labels in the main grid.
     *
     * @param rows - the number of rows in the grid.
     * @param cols - the number of columns in the the grid.
     */
    private void createGridContent(int rows, int cols) {
        //TODO you need to have nested loops here as we are dealing with a
        //2 dimensional array.
        //TODO for the body of the loop:
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
    }

    /**
     * This is a utility method for creating a Label.
     *
     * @param row - the row at which the label is placed.
     * @param col - the column at which the label is placed.
     * @return a fully initialized label object.
     */
    private Label createLabel(int row, int col) {
        //TODO create a new label
        Label label = new Label();
        //TODO call the methods setMaxSize, setMinSize, and setPrefSize on the label.
        //for each pass the arguments CELL_WIDTH and CELL_HEIGHT.
        //this is to lock down the size of the labels.
        label.setMaxSize(CELL_WIDTH, CELL_HEIGHT);
        label.setMinSize(CELL_WIDTH, CELL_HEIGHT);

        //TODO call setId on label and pass to it CELL_DEAD_STYLE_ID.
        //this is to let JAVAFX know what CSS style it should attached to this Node.
        label.setId(CELL_DEAD_STYLE_ID);

        //TODO we want the label to be execute some code every time the mouse is
        //pressed and/or dragged on it.
        //TODO call the to two methods setOnMouseDragEntered and setOnMousePressed
        //on the label and pass to it a lambda which calls the method labelMouseAction.


        label.setOnMousePressed(event -> {
            labelMouseAction(event, label, row, col);
        });

        //1. First set up full drag
        label.setOnDragDetected(event -> {
            label.startFullDrag();
        });
        //2. set up each drag's reaction on the label.
        label.setOnMouseDragEntered(event -> {
            labelMouseAction(event, label, row, col);
        });
        //TODO the issue at this point is if we try to drag the mouse over the labels
        //we wont get a continues drawing, only the initial label will change.
        //This is simply how JavaFX works, what we need to tell it is if we detect
        //a mouse drag allow the event to be also passed to other labels.
        //This is by calling setOnDragDetected on the label. Then pass a lambda to it
        //and in side of the lambda call the method startFullDrag on the label.





        //TODO finally return the created label.
        return label;
    }

    /**
     * this method is used to determine the action of the mouse on a given label.
     *
     * @param event
     * @param l   - the effected label
     * @param row - the row at which the label is placed.
     * @param col - the column at which the label is placed.
     */
    private void labelMouseAction(MouseEvent event, Label l, int row, int col) {
        //TODO an action can only occur of a button from the selectedTool is selected.
        //use the method getSelectedToggle in selectedTool to see if any button is selected.
        //if nothing is selected return.
        if (selectedTool.getSelectedToggle() == null) {
            return;
        } else {
            //The following will recognize what button the user has clicked.
            //if the pen button is pressed the left key will draw and right click will erase.
            if (selectedTool.getSelectedToggle().getUserData() == Tool.PEN) {
                if (event.getButton() == MouseButton.PRIMARY){
                l.setId(CELL_ALIVE_STYLE_ID);
                }
                if (event.getButton() == MouseButton.SECONDARY){
                    l.setId(CELL_DEAD_STYLE_ID);
                }
            }
            if (selectedTool.getSelectedToggle().getUserData() == Tool.ERASER) {
                l.setId(CELL_DEAD_STYLE_ID);
            }
        }

        //TODO depending on what tool is selected change the id of label
        //When button were created we used a class called ToggleButton.
        //This class allows us to store some information in it to be accessed later.
        //to get this data we can use the method getSelectedToggle in selectedTool.
        //This method will return a Toggle object which as a method called getUserData():Object.
        //Since this method returns an Object we have to cast to it to the Type we need.
        //We know in advance that we Stored the enum type Tool within it.
        //Now that we have our Tool type, you can use a switch or if conditions to determine what tool is selected.
        //Depending on the tool call the setId method on the label.
        //If it is Tool.ERASER pass to serId the value CELL_DEAD_STYLE_ID.
        //If it is Tool.PEN pass to serId the value CELL_ALIVE_STYLE_ID.
    }

    /**
     * create and initialize all the objects that are needed for the ToolBar at the bottom of GUI.
     */
    private void createStatusBar() {
        //TODO create a new label to show "Generation: "
        Label generationText = new Label("Generation: ");
        //TODO initialize generationCount label and set its initial value to "0"
        generationCount = new Label("0");
        //TODO create a new Pane object, this object will be used as a filler
        Pane fillerPane = new Pane();
        //TODO call the static method setHgrow from HBox and pass to it the pane object and Priority.ALWAYS.
        //this is to allow the Pane object to grow as much as needed to fill the width space.
        HBox.setHgrow(fillerPane, Priority.ALWAYS);
        //TODO create a new label and set the text as "Press and Hold Space".
        Label noticeLabel = new Label("Press and Hold Space");
        //TODO use the statusBar object to store the 4 object we created here.
        //to access the list of children in a ToolBar use the method getItems.
        //getItems method will return a list. use addAll on it to add all the Nodes.

        statusBar.getItems().addAll(generationText, generationCount, fillerPane, noticeLabel);
    }

    public static void main(String[] args) {
        //TODO to start a JavaFX application we must call the static method
        //launch( args) in the main method. This method is from the Application Class.
        launch(args);
    }
}

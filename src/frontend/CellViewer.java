package frontend;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import frontend.gridshapes.ShapeCell;
import frontend.gridshapes.ShapeFactory;
import backend.patches.Patch;
import backend.simulations.EcoSimulation;
import backend.simulations.FireSimulation;
import backend.simulations.GoLSimulation;
import backend.simulations.SegSimulation;
import backend.simulations.Simulation;
import backend.simulations.SugarSimulation;
import backend.xml.InitialCell;
import backend.xml.InitialGameParameters;
import backend.xml.XMLParsing;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CellViewer {
	private static final int SCALE_HEIGHT = 40;
	private static final int TIME_PER_FRAME = 1000;
	private static final int DIALOG_HEIGHT = 100;
	private static final int DIALOG_WIDTH = 500;
	private static final String VERY_FAST = "Faster";
	private static final String FAST = "Fast";
	private static final String NORMAL = "Normal";
	private static final String SLOW = "Slow";
	private static final String VERY_SLOW = "Slower";

	/**
	 * XML parsing output stored in this object.
	 */
	private InitialGameParameters myGameParams;
	/**
	 * Object that handles the XML parsing.
	 */
	private XMLParsing myXMLParser;
	/**
	 * CellWorld which is basically the Model class, that communicates with
	 * the view, CellViewer.
	 */
	private CellWorld myCellWorld;
	/**
	 * Boolean that stores whether the grid has been set, used for
	 * correctly displaying the grid in the Timeline.
	 */
	private boolean myGridSet = false;
	/**
	 * Boolean that handles stepping through animations and registering
	 * each click as a single step.
	 */
	private boolean myStepClicked = false;
	/**
	 * Lowest speed of the animation.
	 */
	private static final double LOWEST_SPEED = 0.5;

	/**
	 * Whether file is currently selected used to display patches &
	 * cells only after created.
	 */
	private boolean myFileSelected = false;
	/**
	 * Boolean that stores whether the file parsed has a valid format.
	 */
	private boolean myFileValid = true;
	/**
	 * Button last clicked to handle processing "stepping into" simulation
	 * frames
	 */
	private Button myLastClicked = null;

	/**
	 * Array of patches & cells that are viewed during the simulation.
	 */
	private ShapeCell[][] myViewingGrid;

	/**
	 * Array of patch objects (holding a cell) mapped 1:1 to a position
	 * in the viewing grid.
	 */
	private Patch[][] myGrid;

	/**
	 * Currently loaded file used in XML parsing.
	 */
	private File myFile;

	/**
	 * Stores the individual objects as children.
	 */

	private Group myGridGroup;
	/**
	 * Holds layout of the whole GUI.
	 */
	private BorderPane myBorderPane;

	/**
	 * Buttons used for user interaction.
	 */
	private Button myReset;
	private Button myStart;
	private Button myStop;
	private Button myStep;

	/**
	 * Stores the previously loaded XML file to avoid reloading the
	 */
	private File myPreviousFile;

	/**
	 * Superclass object that get instantiated with the appropriate
	 * simulation retrieved from the XML file.
	 */
	private Simulation myCellSimulation;

	/**
	 * Used in reflection to match simulation mode to appropriate Simulation
	 * subclass. This changes if the simulation and corresponding cell
	 * classes move to a different package
	 */
	private static final String SIMULATION_PACKAGE = "backend.simulations";
	/**
	 * Used in matching simulation mode (testing equality of class names)
	 * with appropriate Simulation subclass.
	 */
	private static final String CLASS_SUFFIX = "simulation";

	/**
	 * Add new simulation to the below array for extensibility.
	 */
	private Simulation[] mySimulations = {new EcoSimulation(),
			new FireSimulation(), new GoLSimulation(),
			new SegSimulation(), new SugarSimulation()};

	/**
	 * 
	 */
	private Timeline myAnimation = new Timeline();
	/**
	 * 
	 */
	private Paint[] myCellColors;
	private final FileChooser fileChooser = new FileChooser();
	private final Button openButton = new Button("...");
	private final ComboBox<String> mySpeedSelected = new ComboBox<String>();
	private int myHeight;
	private int myWidth;

	private ShapeFactory myShapeFactory;

	/**
	 * Creates CellViewer object which acquires the animation timeline for
	 * start/stop/reset interaction and CellWorld object to get the initial
	 * state of the grid.
	 */
	public CellViewer(Timeline animation, CellWorld cellWorld) {
		myAnimation = animation;
		myCellWorld = cellWorld;
	}

	/**
	 * Set the height of the simulation window.
	 */
	private void setHeight(int height) {
		myHeight = height;
	}

	private void setWidth(int width) {
		myWidth = width;
	}

	/**
	 * This method returns the GUI scene with buttons, file chooser, and
	 * simulation grid.
	 */
	public Scene init(Stage stage, int width, int height) {
		setHeight(height);
		setWidth(width);
		myBorderPane = new BorderPane();
		myXMLParser = new XMLParsing();

		Scene scene = new Scene(myBorderPane, width, height);
		addFileSelector(stage);
		addButtons();
		disableButtons(true);
		setButtonsOnAction();
		return scene;
	}

	/**
	 * Generates simulation with reflection by matching simulation name and
	 * corresponding class
	 */
	private void generateSimulation() {
		for (int i = 0; i < mySimulations.length; i++) {
			if (mySimulations[i]
					.getClass()
					.getName()
					.toLowerCase()
					.equals(SIMULATION_PACKAGE + "."
							+ myGameParams.simulationMode.toLowerCase()
							+ CLASS_SUFFIX)) {
				myCellSimulation = mySimulations[i];
				return;
			}
		}
	}

	/**
	 * Enables/disables buttons based on boolean passed in. Ensures
	 * buttons are not active until necessary.
	 * @param disable
	 * 		whether the buttons are deactivated such as prior to 
	 * 		the user selecting a valid XML file.
	 */
	private void disableButtons(boolean disable) {
		myReset.setDisable(disable);
		myStart.setDisable(disable);
		myStop.setDisable(disable);
		myStep.setDisable(disable);
	}

	/**
	 * Initial call to add starting position and states of cells
	 * on viewing grid.
	 */
	private void addCellsToDisplay() {
		disableButtons(false);
		myGridGroup = new Group();
		myCellColors = myCellSimulation.myCellColors;
		myBorderPane.setCenter(myGridGroup);
		double sideLength = Math.min(
				(myHeight / (myGameParams.gridXSize * 2.2)),
				(myWidth / (myGameParams.gridYSize * 1.5)));
		double centerToVertices = (Math.sqrt(3) * (sideLength / 2));
		myShapeFactory = new ShapeFactory(
				myGameParams.unitShape.toUpperCase(),
				myGameParams.gridXSize, myGameParams.gridYSize,
				sideLength, centerToVertices);
		myViewingGrid = myShapeFactory.getShapes();
		for (int row = 0; row < myGameParams.gridXSize; row++) {
			for (int col = 0; col < myGameParams.gridYSize; col++) {
				Patch patch = myGrid[row][col];
				myGridGroup.getChildren().add(
						myViewingGrid[row][col].getShape());
				setCellOnMouseListen(myViewingGrid[row][col], patch);
				myViewingGrid[row][col].getShape().setFill(patch.getColor());
				myViewingGrid[row][col].getShape().setStroke(Color.WHITE);
			}
		}
	}

	/**
	 * Sets the viewing cell on a mouse listener with a 1:1 mapping between the
	 * backend Patch/Cell object and frontend Cell Viewer object.
	 * 
	 */
	private void setCellOnMouseListen(ShapeCell viewCell, Patch patch) {
		viewCell.myShape.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent ke) {
				viewCell.myShape.setFill(myCellColors[patch.changedState()]);
			}
		});
	}

	/**
	 * Adds file selector button to choose XML file with simulation
	 * parameters.
	 */
	private void addFileSelector(final Stage stage) {
		HBox hbox = new HBox();
		Text text = new Text("Load an XML file to begin the simulation:  ");
		openButton.setScaleX(0.8);
		openButton.setScaleY(0.8);
		openButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {

				fileChooser.setTitle("Open XML File");
				fileChooser.getExtensionFilters().addAll(
						new ExtensionFilter("XML Files", "*.xml"));
				myFile = fileChooser.showOpenDialog(stage);
				if (myFile != null) {
					parseXML();
					checkFileValid(stage);
					if(myFileValid)
						resetGrid();
				} else {
					fileNotSelected(stage);
				}
			}
		});
		hbox.getChildren().addAll(text, openButton);
		myBorderPane.setTop(hbox);

	}

	/**
	 * Error-checking code that sees if a file is valid, and appropriately 
	 * display pop-up message
	 */
	private void checkFileValid(Stage stage) {
		if (myGameParams.simulationMode == null)
			popUpNotification(stage, "Simulation mode not specified!");
		if (myGameParams.unitShape == null) {
			myGameParams.unitShape = "SQUARE";
		}
		if (myGameParams.edgeType == null) {
			myGameParams.edgeType = "FINITE";
		}
		if (myGameParams.gridXSize < 1 || myGameParams.gridYSize < 1) {
			popUpNotification(stage, "Grid size needs to be positive!");
		}
		if (myGameParams.thresholdValue < 0) {
			popUpNotification(stage, "Threshold parameter invalid!");
		}
		
		if (cellOutOfBounds(myGameParams.initialCells)) {
			popUpNotification(stage, "Cell out of bounds!");
		}
	}

/**
 *  Determines whether cell/patch is out of bounds of specified indices.
 * @param initialCells
 * 		the list of initial cells read in from the XML file
 * @return boolean indicating if all cells are within specified bounds or not.
 */
	private boolean cellOutOfBounds(final List<InitialCell> initialCells) {
		for (InitialCell ic : initialCells) {
			if (ic.myX < 0 || ic.myX > myGameParams.gridXSize
					|| ic.myY < 0
					|| ic.myY > myGameParams.gridYSize) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Displays popUpNotification on the stage when there is no previously
	 * loaded XML file.
	 * @param stage
	 * 		stage to display the message to user
	 * @param message
	 * 		message displayed to the user
	 */
	private void popUpNotification(final Stage stage, final String message) {
		/**
		 * Setting separate stage to show pop-up window (missing XML
		 * file) when there is no previously loaded file.
		 */
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(stage);
		VBox textBox = new VBox();
		textBox.getChildren().add(new Text(message));
		Scene dialogScene = new Scene(textBox, DIALOG_WIDTH,
				DIALOG_HEIGHT);
		dialog.setScene(dialogScene);
		dialog.show();
		myFileValid = false;
	}


	/**
	 * Handles user notification (pop-up window) when XML file not selected.
	 * @param stage
	 * 		stage to show dialog box
	 */
	private void fileNotSelected(final Stage stage) {

		if (myPreviousFile != null) {
			myFile = myPreviousFile;
		} else {
			/**
			 * Setting separate stage to show pop-up window (missing
			 * XML file) when there is no previously loaded file.
			 */
			Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(stage);
			VBox textBox = new VBox();
			textBox.getChildren()
			.add(new Text(
					"You haven't selected an XML file.\nPlease select one."));
			Scene dialogScene = new Scene(textBox, DIALOG_WIDTH, 
					DIALOG_HEIGHT);
			dialog.setScene(dialogScene);
			dialog.show();
		}
	}

	/**
	 * Parses XML file selected into simulation parameters
	 */
	private void parseXML() {
		try {
			myGameParams = myXMLParser.parseInitialCellsFromFile(myFile);
			myFileSelected = true;
			myFileValid = true;
			myPreviousFile = myFile;

		} catch (ParserConfigurationException | SAXException | IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Returns layout with vertically aligned buttons.
	 */
	private VBox createButtonsVBox() {
		VBox vbox = new VBox();
		vbox.setSpacing(myHeight / SCALE_HEIGHT);
		vbox.setPadding(new Insets(myHeight / 4, 0, 0, 2));

		vbox.getChildren().add(myReset);
		vbox.getChildren().add(myStart);
		vbox.getChildren().add(myStop);
		vbox.getChildren().add(myStep);
		vbox.getChildren().add(new Text("Speed"));
		vbox.getChildren().add(mySpeedSelected);

		return vbox;
	}

	/**
	 * Add buttons placed on left side of window.
	 */
	private void addButtons() {
		mySpeedSelected.getItems().addAll(VERY_SLOW, SLOW, NORMAL, FAST,
				VERY_FAST);
		mySpeedSelected.setValue(NORMAL);

		myReset = new Button("Reset");
		myStart = new Button("Start/Resume");
		myStop = new Button("Stop/Pause");
		myStep = new Button("Step");

		VBox vbox = createButtonsVBox();
		myBorderPane.setLeft(vbox);

	}

	/**
	 * Sets event handlers for all buttons and action on clicked.
	 */
	private void setButtonsOnAction() {
		myStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				myAnimation.play();
				myLastClicked = myStart;

			}

		});

		myStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				myAnimation.pause();
				myLastClicked = myStop;
			}

		});

		myReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				resetGrid();
			}

		});

		myStep.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				myAnimation.play();
				myStepClicked = true;
				myLastClicked = myStep;
			}
		});
	}

	/**
	 * Sets the original grid of patch and Cell objects.
	 */
	private void setGrid() {
		myGrid = myCellSimulation.initialize(
				myGameParams.simulationMode,
				myGameParams.unitShape, myGameParams.edgeType,
				myGameParams.gridXSize, myGameParams.gridYSize,
				myGameParams.thresholdValue,
				myGameParams.initialCells);
	}

	/**
	 * Reset viewing grid to original states.
	 */
	private void resetGrid() {
		myAnimation.stop();
		generateSimulation();
		setGrid();
		addCellsToDisplay();
		myCellWorld.startAnimation();
		myAnimation.pause();
	}

	/**
	 * Returns the frame for the cell society automata.
	 */
	private EventHandler<ActionEvent> myOneFrame = new EventHandler<ActionEvent>() {
		@Override
		/**
		 * Update routine for each frame of the simulation
		 */
		public void handle(final ActionEvent evt) {
			checkFileSelectedAndSetFlags();
			if ((myLastClicked.equals(myStep))) {
				if (myStepClicked) {
					updateGrid();
					updateDisplay();
					myStepClicked = false;
					myAnimation.pause();
				}
			} else {
				updateGrid();
				updateDisplay();
			}
			checkSpeedSelection();
		}
	};

	/**
	 * Creates animation frame for simulation.
	 * @return KeyFrame
	 * 		used in the animation
	 */
	public KeyFrame start() {
		return new KeyFrame(Duration.millis(TIME_PER_FRAME), myOneFrame);
	}

	/**
	 * Checks if XML file has been selected and accordingly sets
	 * flags that sets the boolean enabling the grid to be shown.
	 */
	private void checkFileSelectedAndSetFlags() {

		if ((myFile != null) && (myFileSelected)) {
			myGridSet = true;
			myFileSelected = false;
		}
	}

	/**
	 * Update states of simulation patches & cells.
	 */
	private void updateGrid() {
		if (myGridSet) {
			myCellSimulation.updateGrid();
		}
	}

	/**
	 * Display updated states of simulation patches & cells.
	 */
	private void updateDisplay() {
		if (myGridSet) {
			for (int i = 0; i < myGrid.length; i++) {
				for (int j = 0; j < myGrid[0].length; j++) {
					Patch patch = myGrid[i][j];
					myViewingGrid[i][j].myShape.setFill(
							patch.getColor());
				}
			}
		}
	}

	/**
	 * Update animation speed based on selection.
	 */
	private void checkSpeedSelection() {
		ArrayList<String> possibleSpeeds = new ArrayList<String>();
		Collections.addAll(possibleSpeeds, VERY_SLOW, SLOW, NORMAL,
				FAST, VERY_FAST);
		for (int i = 0; i < possibleSpeeds.size() - 1; i++) {
			if (mySpeedSelected.getValue().equals(
					possibleSpeeds.get(i))) {
				myAnimation.setRate(LOWEST_SPEED * (i + 1));
				return;
			}
		}
	}
}
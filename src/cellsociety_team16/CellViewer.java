package cellsociety_team16;

import java.io.File;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CellViewer {
	private static final String VERY_FAST = "Faster";
	private static final String FAST = "Fast";
	private static final String NORMAL = "Normal";
	private static final String SLOW = "Slow";
	private static final String VERY_SLOW = "Slower";


	private Button myRestart =  new Button("Restart");
	private Button myStart= new Button("Start/Resume");
	private Button myStop = new Button("Stop/Pause");
	private Button myStep = new Button ("Step");
	


	// Stores File object .XML
	private File myFile;
	// Stores the overall Grid (consisting of individual cells) 
	private GridPane myGridPane;
	//Border Pane holds the scene graph
	private BorderPane myBorderPane;
	private static final int ROW_SIZE = 12;
	private static final int COLUMN_SIZE = 12;
	private Timeline myAnimation = new Timeline();
	private static final String [] POSSIBLE_COLORS = {"yellow", "green", "blue", "black", "orange", "white"};
	private static final Random myRandom = new Random();
	private final FileChooser fileChooser = new FileChooser();
	private final Button openButton = new Button("...");
	private final ComboBox<String> speedOptions = new ComboBox<String>();


	public CellViewer(Timeline animation) {
		myAnimation = animation;
	}

	public Scene init(Stage stage, int width, int height) {
		myBorderPane = new BorderPane();
		myGridPane = new GridPane();
		//Border Pane holds the scene graph		
		Scene scene = new Scene(myBorderPane, width, height);
		addFileSelector(stage);
		addButtons();
		setButtonsOnAction();
		addIndividualCells();
		addGridConstraints();
		return scene;
	}

	private void addIndividualCells() {

		myBorderPane.setCenter(myGridPane);

		for (int row = 0; row < ROW_SIZE ; row++) {
			for (int col = 0; col < COLUMN_SIZE ; col ++) {
				GridPane square = new GridPane();
				String color ;
				if ((row + col) % 2 == 0) {
					color = "green";
				} else {
					color = "white";
				}
				square.setStyle("-fx-background-color: "+ color +";");
				myGridPane.add(square, col, row);
			}
		}		
	}

	private void addGridConstraints() {
		for (int i = 0; i < COLUMN_SIZE; i++) {
			myGridPane.getColumnConstraints().add(new ColumnConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, HPos.CENTER, true));

		}

		for (int i = 0; i < ROW_SIZE; i++) {
			myGridPane.getRowConstraints().add(new RowConstraints(5, Control.USE_COMPUTED_SIZE, Double.POSITIVE_INFINITY, Priority.ALWAYS, VPos.CENTER, true));
		}

		// Adding padding so there are borders between perimeter cells and window edges
		myGridPane.setPadding(new Insets(0, 10, 10, 0));
	}



	private void addFileSelector(Stage stage) {
		HBox hbox = new HBox();
		Text text = new Text("Load an XML file to begin the simulation:  ");
		openButton.setScaleX(0.8);
		openButton.setScaleY(0.8);
		openButton.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {

						fileChooser.setTitle("Open XML File");
						fileChooser.getExtensionFilters().addAll(
								new ExtensionFilter("XML Files", "*.xml"));
						myFile = fileChooser.showOpenDialog(stage);
						if (myFile == null) {
							System.out.println("No file selected");
							Stage dialog = new Stage();
							dialog.initModality(Modality.APPLICATION_MODAL);
							dialog.initOwner(stage);
							VBox textBox = new VBox();
							textBox.getChildren().add(new Text("You haven't selected an XML file.\nPlease select one."));
							Scene dialogScene = new Scene(textBox, 500, 100);
							dialog.setScene(dialogScene);
							dialog.show();

						}
					}
				});
		hbox.getChildren().addAll(text, openButton);
		myBorderPane.setTop(hbox);

	}


	private Node getNodeFromGridPane(int col, int row) {
		for (Node node : myGridPane.getChildren()) {
			if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
				return node;
			}
		}
		return null;
	}


	private void addButtons() {
		VBox vbox = new VBox();
		vbox.setSpacing(30);
		vbox.setPadding(new Insets(500, 0, 0, 2));




		Text speed = new Text("Speed");

		speedOptions.getItems().addAll(
				VERY_SLOW, SLOW, NORMAL, FAST, VERY_FAST);

		// By default set this to normal speed
		speedOptions.setValue(NORMAL);


		// Adding buttons to vertical box. This could have been cleaner with an array of Buttons but from
		// a readability standpoint, this is probably better
		vbox.getChildren().add(myRestart);
		vbox.getChildren().add(myStart);
		vbox.getChildren().add(myStop);
		vbox.getChildren().add(myStep);


		vbox.getChildren().add(speed);
		vbox.getChildren().add(speedOptions);

		myBorderPane.setLeft(vbox);

	}

	private void setButtonsOnAction() {
		myStart.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						myAnimation.play();

					}

				});

		myStop.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						myAnimation.pause();
					}

				});

		/*myRestart.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						myAnimation.playFromStart();
					}

				});
		*/
		myStep.setOnAction(
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(final ActionEvent e) {
						
						
					}

				});	
	}

	private EventHandler<ActionEvent> oneFrame = new EventHandler<ActionEvent>() {
		@Override
		public void handle(ActionEvent evt) {
			updateDisplay();
			checkSpeedSelection();
			checkStepClicked();
		}
	};

	public KeyFrame start () {
		return new KeyFrame(Duration.millis(1000), oneFrame);
	}

	// Currently updating display by picking a random color;
	private void updateDisplay () {

		for (int i = 0; i < ROW_SIZE; i++) {
			for (int j = 0; j < COLUMN_SIZE; j++) {
				Node node = getNodeFromGridPane(i, j);
				node.setStyle("-fx-background-color: "+ POSSIBLE_COLORS[myRandom.nextInt(POSSIBLE_COLORS.length)] +";");
			}
		}

	}		

	private void checkStepClicked() {





	}

	private void checkSpeedSelection() {
		if (speedOptions.getValue().equals(VERY_SLOW)) {
			myAnimation.setRate(0.25);
		} else {
			if ((speedOptions.getValue().equals(SLOW))) {
				myAnimation.setRate(0.5);
			} else { 
				if ((speedOptions.getValue().equals(NORMAL))) {
					myAnimation.setRate(1.0);

				} else {
					if ((speedOptions.getValue().equals(FAST))) {
						myAnimation.setRate(2.0);
					} else {
						myAnimation.setRate(4.0);
					}
				}
			}

		}
	}
}
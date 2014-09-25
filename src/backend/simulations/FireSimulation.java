package backend.simulations;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import backend.cells.FireCell;
import backend.xml.InitialCell;

public class FireSimulation extends Simulation {
	@Override
	protected void makeNewCell(int i, int j, double thresholdValue) {
		myGrid[i][j] = new FireCell(i, j, true, 0, thresholdValue); 
	}

	@Override
	protected void setInitialState(ArrayList<InitialCell> initialState) {
		for (InitialCell c : initialState) {
			((FireCell) myGrid[c.myX][c.myY]).setState(c.myState);
		}
	}

	@Override
	public void updateGrid() {
		for (int i = 0; i < myGrid.length; i++) {
			for (int j = 0; j < myGrid[0].length; j++) {
				((FireCell) myGrid[i][j]).update();
			}
		}
		for (int i = 0; i < myGrid.length; i++) {
			for (int j = 0; j < myGrid[0].length; j++) {
				((FireCell) myGrid[i][j]).updateFire();
			}
		}
		for (int i = 0; i < myGrid.length; i++) {
			for (int j = 0; j < myGrid[0].length; j++) {
				if (myGrid[i][j].getState() == 2) {
				}
			}
		}

		for (int i = 0; i < myGrid.length; i++) {
			for (int j = 0; j < myGrid[0].length; j++) {
				myGrid[i][j].reset();

			}
		}
	}

	@Override
	protected void initializeColor() {
		myColors = new Color[3];
		myColors[0] = Color.WHITE;
		myColors[1] = Color.GREEN;
		myColors[2] = Color.RED;
		
	}
}

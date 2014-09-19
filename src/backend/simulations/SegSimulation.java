package backend.simulations;
import backend.cells.Cell;
import backend.cells.SegCell;
import backend.xml.InitialCell;

import java.util.ArrayList;


public class SegSimulation extends Simulation {

	@Override
	protected void makeNewCell(int i, int j, double thresholdValue) {
		myGrid[i][j] = new SegCell(i, j, true, 0, this, thresholdValue);
	}

	@Override
	protected void setNeighbors(int i, int j) {
		if (i > 0)
			myGrid[i][j].addNeighbor(myGrid[i - 1][j]);
		if (j > 0)
			myGrid[i][j].addNeighbor(myGrid[i][j - 1]);
		if (i < myGrid.length - 1)
			myGrid[i][j].addNeighbor(myGrid[i + 1][j]);
		if (j < myGrid[0].length - 1)
			myGrid[i][j].addNeighbor(myGrid[i][j + 1]);
		if (i > 0 && j > 0)
			myGrid[i][j].addNeighbor(myGrid[i - 1][j - 1]);
		if (i > 0 && j < myGrid[0].length - 1)
			myGrid[i][j].addNeighbor(myGrid[i - 1][j + 1]);
		if (i < myGrid.length - 1 && j > 0)
			myGrid[i][j].addNeighbor(myGrid[i + 1][j - 1]);
		if (i < myGrid.length - 1 && j < myGrid[0].length - 1)
			myGrid[i][j].addNeighbor(myGrid[i + 1][j + 1]);
	}

	@Override
	protected void setInitialState(ArrayList<InitialCell> initialState) {
		for (InitialCell c : initialState) {
			((SegCell) myGrid[c.myX][c.myY]).setState(c.myState);
		}
	}

	@Override
	public void updateGrid() {
		for (int i = 0; i < myGrid.length; i++) {
			for (int j = 0; j < myGrid[0].length; j++) {
				((SegCell) myGrid[i][j]).update();
			}
		}

		for (int i = 0; i < myGrid.length; i++) {
			for (int j = 0; j < myGrid[0].length; j++) {
				myGrid[i][j].reset();
			}
		}
	}
	
	public Cell selectRandomEmptyCell()
	{
		ArrayList<Cell> emptyCells = findEmptyCells();
		
		if(emptyCells.isEmpty())
			return null;
		else
		{
			int randomIndex = (int) Math.floor(Math.random() * emptyCells.size());
			return emptyCells.get(randomIndex);
		}
	}
	
	private ArrayList<Cell> findEmptyCells()
	{
		ArrayList<Cell> emptyCells = new ArrayList<Cell>();
		for(int i = 0; i < myGrid.length; i++)
		{
			for(int j = 0; j < myGrid[i].length; j++)
			{
				Cell currentCell = myGrid[i][j];
				if(currentCell.getState() == 0)
				{
					emptyCells.add(currentCell);
				}
			}
		}
		return emptyCells;
	}
	
	@Override
	protected void initializeColor() {
		myColors = new String[3];
		myColors[0] = "white";
		myColors[1] = "black";
		myColors[2] = "red";
		
	}
}

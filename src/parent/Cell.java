package parent;

import java.util.ArrayList;

public abstract class Cell {
	protected ArrayList<Cell> myNeighbor;
	protected int[] myCoordinates;
	protected boolean myUpdate;

	public void addNeighbor(Cell neighbor){
		myNeighbor.add(neighbor);
	}
	public abstract void update();
}
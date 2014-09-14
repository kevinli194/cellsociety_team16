package parent;

import java.util.ArrayList;

public class FireCellManager extends CellManager {

	@Override
	protected void makeNewCell(int i, int j) {
		// TODO Auto-generated method stub
		myGrid[i][j] = new FireCell(i, j, true, 0, 0); // last parameter should be the threshold
	}

	@Override
	protected void setNeighbors(int i, int j) {
		// TODO Auto-generated method stub
		if (i > 0) {
			myGrid[i][j].addNeighbor(myGrid[i - 1][j]);
		}
		if (j > 0) {
			myGrid[i][j].addNeighbor(myGrid[i][j - 1]);
		}
		if (i < myGrid.length - 1) {
			myGrid[i][j].addNeighbor(myGrid[i + 1][j]);
		}
		if (j < myGrid[0].length - 1) {
			myGrid[i][j].addNeighbor(myGrid[i][j + 1]);
		}

	}

	@Override
	protected void setInitialState(ArrayList<InitialCell> initialState) {
		// TODO Auto-generated method stub
		for(InitialCell c: initialState){
			((FireCell) myGrid[c.myX][c.myY]).setState(c.myState);
		}
	}



}
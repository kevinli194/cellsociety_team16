package backend.neighborsetters;

import backend.patches.Patch;

public abstract class NeighborSetter {
	/**
	 * The setNeighbors method sets the neighbor for a given grid, depending on
	 * the parameters for boundaryType and gridShape.
	 * 
	 * @param grid
	 *            Grid to assign neighborhoods for
	 * @param boundaryType
	 *            Type of boundary to be used: can be toroidal or finite. More
	 *            boundary types can be implemented
	 * @param gridShape
	 *            Shape of the patches in the grid: can be hexagonal,
	 *            rectangular, or triangular. More boundary types can be
	 *            implemented
	 */
	public void setNeighbors(Patch[][] grid, String boundaryType,
			String gridShape) {
		if (boundaryType.equals("FINITE")) {
			if (gridShape == "SQUARE") {
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[0].length; j++) {
						recAndTriBounded(grid, i, j);
					}
				}
			}
			if (gridShape == "TRIANGLE") {
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[0].length; j++) {
						recAndTriBounded(grid, i, j);
					}
				}
			}
			if (gridShape.equals("HEXAGON")) {
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[0].length; j++) {
						hexBounded(grid, i, j);
					}
				}
			}

		}
		if (boundaryType.equals("TOROIDAL")) {
			if (gridShape.equals("SQUARE")) {
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[0].length; j++) {
						recAndTriToroidal(grid, i, j);
					}
				}
			}
			if (gridShape.equals("TRIANGLE")) {
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[0].length; j++) {
						recAndTriToroidal(grid, i, j);
					}
				}
			}
			if (gridShape.equals("HEXAGON")) {
				for (int i = 0; i < grid.length; i++) {
					for (int j = 0; j < grid[0].length; j++) {
						hexToroidal(grid, i, j);
					}
				}
			}

		}

	}

	/**
	 * Method for determining neighborhoods for a patch given that the shape of
	 * the patch is rectangular/triangular and toroidal. Can be overridden by
	 * subclasses depending on simulation.
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neigbhors for
	 * @param j
	 *            coordinates of specific patch to assign neigbhors for
	 */
	protected void recAndTriToroidal(Patch[][] grid, int i, int j) {
		recAndTriBounded(grid, i, j);
		addCardinalEdges(grid, i, j, 1);

	}

	/**
	 * Method for determining neighborhoods for a patch given that the shape of
	 * the patch is hexagonal and toroidal. Can be overridden by subclasses
	 * depending on simulation.
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neighbors for
	 * @param j
	 *            coordinates of specific patch to assign neighbors for
	 */

	protected void hexToroidal(Patch[][] grid, int i, int j) {
		hexBounded(grid, i, j);
		addHexEdges(grid, i, j);

	}

	/**
	 * Method for determining neighborhoods for a patch given that the shape of
	 * the patch is rectangular/triangular and bounded. Can be overridden by
	 * subclasses depending on simulation.
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neighbors for
	 * @param j
	 *            coordinates of specific patch to assign neighbors for
	 */

	protected void recAndTriBounded(Patch[][] grid, int i, int j) {
		addCardinalNeighbors(grid, i, j, 1);
	}

	/**
	 * Method for determining neighborhoods for a patch given that the shape of
	 * the patch is hexagonal and finite. Can be overridden by subclasses
	 * depending on simulation.
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neighbors for
	 * @param j
	 *            coordinates of specific patch to assign neighbors for
	 */

	protected void hexBounded(Patch[][] grid, int i, int j) {
		addCardinalNeighbors(grid, i, j, 1);
		addHexDiags(grid, i, j);

	}

	/**
	 * Method for adding neighbors to the north, west, south, and east of the
	 * current patch.
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neighbors for
	 * @param j
	 *            coordinates of specific patch to assign neighbors for
	 * @param num
	 *            how far the patch can be to still be considered a cardinal
	 *            neighbor. for example, if num is 2, then cardinal neighbors
	 *            constitute as all patches within 2 patches above, below,
	 *            right, and left of the current patch
	 */

	protected void addCardinalNeighbors(Patch[][] grid, int i, int j, int num) {
		int count = 1;
		while (count <= num) {
			if (i > count - 1)
				grid[i][j].addNeighbor(grid[i - count][j]);
			if (j > count - 1)
				grid[i][j].addNeighbor(grid[i][j - count]);
			if (i < grid.length - count)
				grid[i][j].addNeighbor(grid[i + count][j]);
			if (j < grid[0].length - count)
				grid[i][j].addNeighbor(grid[i][j + count]);
			count++;
		}

	}

	/**
	 * Method for adding neighbors to the northeast, southwest, northwest, and
	 * southeast of the current patch.
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neighbors for
	 * @param j
	 *            coordinates of specific patch to assign neighbors for
	 */

	protected void addDiagonalNeighbors(Patch[][] grid, int i, int j) {
		if (i > 0 && j > 0)
			grid[i][j].addNeighbor(grid[i - 1][j - 1]);
		if (i > 0 && j < grid[0].length - 1)
			grid[i][j].addNeighbor(grid[i - 1][j + 1]);
		if (i < grid.length - 1 && j > 0)
			grid[i][j].addNeighbor(grid[i + 1][j - 1]);
		if (i < grid.length - 1 && j < grid[0].length - 1)
			grid[i][j].addNeighbor(grid[i + 1][j + 1]);
	}

	/**
	 * Method for wrapping around the neighbors if its on the edge.
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neighbors for
	 * @param j
	 *            coordinates of specific patch to assign neighbors for
	 * @param num
	 *            how far the patch can be to still be considered a cardinal
	 *            neighbor. for example, if num is 2, then cardinal neighbors
	 *            constitute as all patches within 2 patches above, below,
	 *            right, and left of the current patch
	 */
	protected void addCardinalEdges(Patch[][] grid, int i, int j, int num) {
		int count = 1;
		while (num > 0) {
			if (i <= num - 1)
				grid[i][j].addNeighbor(grid[grid.length - count][j]);
			if (j <= num - 1)
				grid[i][j].addNeighbor(grid[i][grid.length - count]);
			if (i >= grid.length - num)
				grid[i][j].addNeighbor(grid[count - 1][j]);
			if (j >= grid[0].length - num)
				grid[i][j].addNeighbor(grid[i][count - 1]);
			count++;
			num--;
		}
	}

	/**
	 * Method for adding the diagonal neighbors near the edges (wrap around).
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neighbors for
	 * @param j
	 *            coordinates of specific patch to assign neighbors for
	 */

	protected void addDiagonalEdges(Patch[][] grid, int i, int j) {
		if (i == 0) {
			if (j != 0)
				grid[i][j].addNeighbor(grid[grid.length - 1][j - 1]);
			if (j != grid[0].length - 1)
				grid[i][j].addNeighbor(grid[grid.length - 1][j + 1]);
		}
		if (i == grid.length - 1) {
			if (j != 0)
				grid[i][j].addNeighbor(grid[0][j - 1]);
			if (j != grid[0].length - 1)
				grid[i][j].addNeighbor(grid[0][j + 1]);
		}
		if (j == 0) {
			if (i != 0)
				grid[i][j].addNeighbor(grid[i - 1][grid[0].length - 1]);
			if (i != grid.length - 1)
				grid[i][j].addNeighbor(grid[i + 1][grid[0].length - 1]);
		}
		if (j == grid[0].length - 1) {
			if (i != 0)
				grid[i][j].addNeighbor(grid[i - 1][0]);
			if (i != grid.length - 1)
				grid[i][j].addNeighbor(grid[i + 1][0]);
		}

	}

	/**
	 * Method for adding the two diagonals of the hexagon.
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neighbors for
	 * @param j
	 *            coordinates of specific patch to assign neighbors for
	 */

	protected void addHexDiags(Patch[][] grid, int i, int j) {
		if (i % 2 == 0) {
			if (i > 0 && j > 0)
				grid[i][j].addNeighbor(grid[i - 1][j - 1]);
			if (i < grid.length - 1 && j > 0)
				grid[i][j].addNeighbor(grid[i + 1][j - 1]);
		}
		if (i % 2 == 1) {
			if (i > 0 && j < grid[0].length - 1)
				grid[i][j].addNeighbor(grid[i - 1][j + 1]);

			if (i < grid.length - 1 && j < grid[0].length - 1)
				grid[i][j].addNeighbor(grid[i + 1][j + 1]);
		}
	}

	/**
	 * Method for adding the edges (wrap around) for a hexagon shape grid.
	 * 
	 * @param grid
	 *            Grid to find neighbors in
	 * @param i
	 *            coordinates of specific patch to assign neighbors for
	 * @param j
	 *            coordinates of specific patch to assign neighbors for
	 */

	protected void addHexEdges(Patch[][] grid, int i, int j) {
		if (i == 0) {
			grid[i][j].addNeighbor(grid[grid.length - 1][j]);
			if (j != 0)
				grid[i][j].addNeighbor(grid[grid.length - 1][j - 1]);
		}
		if (i == grid.length - 1) {
			grid[i][j].addNeighbor(grid[0][j]);
			if (j != grid[0].length - 1)
				grid[i][j].addNeighbor(grid[0][j + 1]);
		}
		if (j == 0)
			grid[i][j].addNeighbor(grid[i][grid[0].length - 1]);
		if (j == grid[0].length - 1)
			grid[i][j].addNeighbor(grid[i][0]);

	}

}

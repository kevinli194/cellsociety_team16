package backend.neighborsetters;

import backend.patches.Patch;

public class SegNeighborSetter extends NeighborSetter {
	@Override
	public void recAndTriBounded(Patch[][] grid, int i, int j) {
		addCardinalNeighbors(grid, i, j, 1);
		addDiagonalNeighbors(grid, i, j);
	}

	@Override
	public void recAndTriToroidal(Patch[][] grid, int i, int j) {
		recAndTriBounded(grid, i, j);
		addCardinalEdges(grid, i, j, 1);
		addDiagonalEdges(grid, i, j);

	}
}

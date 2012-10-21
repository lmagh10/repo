package gma.sdp.freeuni.tictactoe.logic;

import gma.sdp.freeuni.tictactoe.model.*;

public interface Player {

	void makeMove(ReadOnlyBoard board, PlayerColor color, MoveListener moveListener);
	
	boolean undoLastMove(ReadOnlyBoard board, CellWrapped lastMove, PlayerColor color, MoveListener moveListener);
}
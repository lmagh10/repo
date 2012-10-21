package gma.sdp.freeuni.tictactoe.logic;

//import gma.sdp.freeuni.tictactoe.model.*;

public interface MoveListener {

	void makeMove(CellWrapped cell);
	
	void undoMove(CellWrapped cell);

}
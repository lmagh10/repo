package gma.sdp.freeuni.tictactoe.logic;

import gma.sdp.freeuni.tictactoe.model.*;

public class Game {

	private final Player xPlayer;
	private final Player oPlayer;
	private final Board board;
	private final Referee referee;
	private final Presenter presenter;
	private final ReadOnlyBoard readOnly;
	
	public Game(Board board, Player xPlayer, Player oPlayer, Referee referee,
			Presenter presenter) {
		this.board = board;
		readOnly = new ReadOnlyBoard(board);
		this.xPlayer = xPlayer;
		this.oPlayer = oPlayer;
		this.referee = referee;
		this.presenter = presenter;
	}

	public void play() {
		GameStatus gameStatus;
		PlayerColor currentColor = PlayerColor.X;
		
		MoveListener xMoveListener = new MoveListener() {
			public void makeMove(CellWrapped cell) {
				board.makeMoveX(cell.getCell());
				presenter.show(readOnly, referee.getGameStatus(readOnly));
			}
			
			public void undoMove(CellWrapped cell){
				board.makeMoveEmpty(cell.getCell());
				presenter.show(readOnly, referee.getGameStatus(readOnly));
			}
		};

		MoveListener oMoveListener = new MoveListener() {
			public void makeMove(CellWrapped cell) {
				board.makeMoveO(cell.getCell());
				presenter.show(readOnly, referee.getGameStatus(readOnly));
			}
			
			public void undoMove(CellWrapped cell){
				board.makeMoveEmpty(cell.getCell());
				presenter.show(readOnly, referee.getGameStatus(readOnly));
			}
		};

		do {
			gameStatus = referee.getGameStatus(readOnly);
			presenter.show(readOnly, gameStatus);
			if (gameStatus != GameStatus.INPROGRESS) {
				break;
			}

			switch (currentColor) {
			case X:
				xPlayer.makeMove(readOnly, currentColor, xMoveListener);
				break;
			case O:
				oPlayer.makeMove(readOnly, currentColor, oMoveListener);
				break;
			}

			currentColor = swapColor(currentColor);

		} while (true);
	}

	private PlayerColor swapColor(PlayerColor playerColor) {
		switch (playerColor) {
		case X:
			return PlayerColor.O;
		case O:
			return PlayerColor.X;
		}
		throw new IllegalArgumentException("There is no color for this value.");
	}
}
package gma.sdp.freeuni.tictactoe.logic;

import java.util.Scanner;
import java.io.PrintStream;

public class SmartPlayer implements Player{
	public SmartPlayer(int n, int k, int computer_index, PrintStream stream){
		N = n;
		K = k;
		out = stream;
		COMPUTER_INDEX = computer_index;
		COMP_CHAR = COMPUTER_INDEX==0? 'X': 'O';
		PLAYER_CHAR = COMP_CHAR=='O'? 'X': 'O';
		computeMaxDepth();
		state = new char[N][N];
	}
	
	@Override
	public void makeMove(ReadOnlyBoard board, PlayerColor color,
			MoveListener moveListener) {
		// TODO Auto-generated method stub
		convertIntoCharArray(board);
		Move bestmove = getBestMove(state);
		printWhoIsOnTurn(color);
		CellWrapped nextMove = new CellWrapped(bestmove.x, bestmove.y);
		moveListener.makeMove(nextMove);
	}
	
	private void printWhoIsOnTurn(PlayerColor playerColor) {
		switch (playerColor) {
		case X:
			out.println("Player X is on turn");
			break;
		case O:
			out.println("Player O is on turn");
			break;
		}
	}
	
	private void convertIntoCharArray(ReadOnlyBoard board){
		for(int i=0; i<N; i++){
			for(int j=0; j<N; j++){
				CellValueWrapped val = board.getValueAt(new CellWrapped(i, j));
				char cur = (val==CellValueWrapped.X? 'X': val==CellValueWrapped.O? 'O': '-');
				state[i][j] = cur;
			}
		}
	}

	private void computeMaxDepth(){
		long OPERATIONS_LIMIT = 400000000;
		long res = 1;
		int pow = 0;
		while(res<OPERATIONS_LIMIT){
			if(res*(N*N)<=OPERATIONS_LIMIT){
				res *= (N*N);
				pow++;
			}
			else break;
		}
		MAX_DEPTH = pow-2;
	}
	
	
	
	private Move getBestMove(char[][] board){
		Move mv = score_dep(board, COMPUTER_INDEX, MAX_DEPTH, null);
		if(mv==null){
			for(int i=0; i<N; i++) for(int j=0; j<N; j++) if(board[i][j]=='-') return new Move(i, j, 0);
		}
		if(mv.x>=0) return mv;
		for(int i=0; i<N; i++){
			for(int j=0; j<N; j++){
				if(board[i][j]!='-') continue;
				board[i][j] = COMP_CHAR;
				if(isFinished()){
					board[i][j] = '-';
					return new Move(i, j, 0);
				}
				board[i][j] = '-';
			}
		}
		return null;
	}
	
	private long score_board(char[][] board, int turn){
		if(canWinInAMove(board, turn)) return turn==COMPUTER_INDEX? INF: -INF;
		int[] X = new int[K], Y = new int[K];
		for(int i=1; i<K; i++){
			X[i] = countAppearances(i, board, COMP_CHAR);
			Y[i] = countAppearances(i, board, PLAYER_CHAR);
		}
		long res = 0;
		for(int i=1; i<K; i++){
			res += P[i]*(X[i]-Y[i]);
		}
		return res;
	}
	
	private Move score_dep(char[][] board, int turn, int depth, Move M){
		if(depth==0) return new Move(M.x, M.y, score_board(board, turn));
		if(canWinInAMove(board, turn)) return new Move(-1, -1, turn==COMPUTER_INDEX? INF: -INF);
		char ch = turn==0? 'X': 'O';
		long res = turn==COMPUTER_INDEX? -INF: INF;
		Move MV = null;
		Move mv;
		for(int i=0; i<N; i++){
			for(int j=0; j<N; j++){
				if(board[i][j]!='-') continue;
				board[i][j] = ch;
				mv = score_dep(board, (turn^1), depth-1, new Move(i, j, 0));
				board[i][j] = '-';
				if(mv==null) continue;
				if(turn==COMPUTER_INDEX){
					if(res<=mv.SCORE){
						res = mv.SCORE;
						MV = new Move(i, j, mv.SCORE);
					}
				}
				else{
					if(res>=mv.SCORE){
						res = mv.SCORE;
						MV = new Move(i, j, mv.SCORE);
					}
				}
			}
		}
		return MV;
	}
	
	private int countAppearances(int num, char[][] board, char ch){
		int res = 0;
		for(int i=0; i<N; i++){
			for(int j=0; j<N; j++){
				if(board[i][j]!=ch) continue;
				if(isSameInDirection(i, j, 0, 1, num)) res += countClear(num, board, i, j, 0, 1);
				if(isSameInDirection(i, j, 1, 0, num)) res += countClear(num, board, i, j, 1, 0);
				if(isSameInDirection(i, j, 1, 1, num)) res += countClear(num, board, i, j, 1, 1);
				if(isSameInDirection(i, j, 1, -1, num)) res += countClear(num, board, i, j, 1, -1);
			}
		}
		return res;
	}
	
	
	
	private int countClear(int num, char[][] board, int x, int y, int dx, int dy){
		int res = 0;
		int x1 = x-dx, y1 = y-dy, x2 = x+num*dx, y2 = y+num*dy;
		if(isInBounds(x1, y1) && board[x1][y1]=='-') res++;
		if(isInBounds(x2, y2) && board[x2][y2]=='-') res++;
		return res;
	}
		
	private boolean canWinInAMove(char[][] board, int turn){
		char ch = turn==0? 'X': 'O';
		int[][] X = new int[N][N], O = new int[N][N];
		X[0][0] = (board[0][0]=='X'? 1: 0);
		O[0][0] = (board[0][0]=='O'? 1: 0);
		for(int i=1; i<N; i++){
			X[i][0] = (X[i-1][0] + (board[i][0]=='X'? 1: 0));
			X[0][i] = (X[0][i-1] + (board[0][i]=='X'? 1: 0));
			O[i][0] = (O[i-1][0] + (board[i][0]=='O'? 1: 0));
			O[0][i] = (O[0][i-1] + (board[0][i]=='O'? 1: 0));
		}
		for(int i=1; i<N; i++){
			for(int j=1; j<N; j++){
				X[i][j] = X[i-1][j] + X[i][j-1] - X[i-1][j-1] + (board[i][j]=='X'? 1: 0);
				O[i][j] = O[i-1][j] + O[i][j-1] - O[i-1][j-1] + (board[i][j]=='O'? 1: 0);
			}
		}
		if(ch=='X'){
			for(int i=0; i<N; i++){
				for(int j=0; j<N; j++){
					if(j+K<=N && getSum(X, i, j, true)==K-1 && getSum(O, i, j, true)==0){
						return true;
					}
					if(i+K<=N && getSum(X, i, j, false)==K-1 && getSum(O, i, j, false)==0){
						return true;
					}
				}
			}
		}
		else{
			for(int i=0; i<N; i++){
				for(int j=0; j<N; j++){
					if(j+K<=N && getSum(X, i, j, true)==0 && getSum(O, i, j, true)==K-1){
						return true;
					}
					if(i+K<=N && getSum(X, i, j, false)==0 && getSum(O, i, j, false)==K-1){
						return true;
					}
				}
			}
		}
		int[][] DX1 = new int[N][N], DX2 = new int[N][N], DY1 = new int[N][N], DY2 = new int[N][N];
		for(int i=0; i<N; i++){
			DX1[i][0] = board[i][0]=='X'? 1: 0;
			DX2[i][0] = board[i][0]=='X'? 1: 0;
			DY1[i][0] = board[i][0]=='O'? 1: 0;
			DY2[i][0] = board[i][0]=='O'? 1: 0;
			DX1[0][i] = board[0][i]=='X'? 1: 0;
			DX2[N-1][i] = board[N-1][i]=='X'? 1: 0;
			DY1[0][i] = board[0][i]=='O'? 1: 0;
			DY2[N-1][i] = board[N-1][i]=='O'? 1: 0;
		}
		for(int i=1; i<N; i++){
			for(int j=1; j<N; j++){
				DX1[i][j] = DX1[i-1][j-1] + (board[i][j]=='X'? 1: 0);
				DY1[i][j] = DY1[i-1][j-1] + (board[i][j]=='O'? 1: 0);
			}
		}
		for(int i=N-2; i>=0; i--){
			for(int j=1; j<N; j++){
				DX2[i][j] = DX2[i+1][j-1] + (board[i][j]=='X'? 1: 0);
				DY2[i][j] = DY2[i+1][j-1] + (board[i][j]=='O'? 1: 0);
			}
		}
		if(ch=='X'){
			for(int i=0; i<N; i++){
				for(int j=0; j<N; j++){
					if(i+K<=N && j+K<=N && diagonalSum(DX1, i, j, true)==K-1 && diagonalSum(DY1, i, j, true)==0){
						return true;
					}
					if(i>=K-1 && j+K<=N && diagonalSum(DX2, i, j, false)==K-1 && diagonalSum(DY2, i, j, false)==0){
						return true;
					}
				}
			}
		}
		else{
			for(int i=0; i<N; i++){
				for(int j=0; j<N; j++){
					if(i+K<=N && j+K<=N && diagonalSum(DX1, i, j, true)==0 && diagonalSum(DY1, i, j, true)==K-1){
						return true;
					}
					if(i>=K-1 && j+K<=N && diagonalSum(DX2, i, j, false)==0 && diagonalSum(DY2, i, j, false)==K-1){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private int diagonalSum(int[][] D, int x, int y, boolean down){
		if(down){
			return D[x+K-1][y+K-1] - ((x>0 && y>0)? D[x-1][y-1]: 0);
		}
		return D[x-(K-1)][y+(K-1)] - ((x<N-1 && y>0)? D[x+1][y-1]: 0);
	}
	
	private int getSum(int[][] X, int x, int y, boolean horizontal){
		if(horizontal){
			return (X[x][y+K-1] - (y>0? X[x][y-1]: 0)) - (x>0? (X[x-1][y+K-1] - (y>0? X[x-1][y-1]: 0)) :0);
		}
		return (X[x+K-1][y] - (x>0? X[x-1][y]: 0)) - (y==0? 0: (X[x+K-1][y-1] - (x>0? X[x-1][y-1]: 0)));
	}
		
	private boolean isFinished(){
		return full(state) || isWonByAnyPlayer();
	}
	
	private boolean isWonByAnyPlayer(){
		for(int i=0; i<N; i++){
			for(int j=0; j<N; j++){
				if(isSameInDirection(i, j, 0, 1, K)) return true;
				if(isSameInDirection(i, j, 1, 0, K)) return true;
				if(isSameInDirection(i, j, 1, 1, K)) return true;
				if(isSameInDirection(i, j, 1, -1, K)) return true;
			}
		}
		return false;
	}
	
	private boolean isSameInDirection(int x, int y, int dx, int dy, int num){
		char fir = state[x][y];
		if(fir=='-') return false;
		for(int i=0; i<num; i++){
			int curX = x+dx*i, curY = y+dy*i;
			if(!isInBounds(curX, curY) || state[curX][curY]!=fir) return false;
		}
		return true;
	}
	
	private boolean isInBounds(int x, int y){
		return x>=0 && y>=0 && x<N && y<N;
	}
	
	private boolean full(char[][] board){
		for(int i=0; i<N; i++) for(int j=0; j<N; j++) if(board[i][j]=='-') return false;
		return true;
	}

	private char COMP_CHAR, PLAYER_CHAR;
	
	private int COMPUTER_INDEX;
	
	private int K;
	
	private int N;
	
	private char[][] state;
		
	public static final long[] P = new long[]{1L, (long)1000, (long)1000000, (long)1000000000, Long.parseLong("1000000000000")};
	
	private int MAX_DEPTH = 2;
	
	private static final long INF = Long.parseLong("10000000000000000");
	
	private PrintStream out;

	@Override
	public boolean undoLastMove(ReadOnlyBoard board, CellWrapped lastMove, PlayerColor color,
			MoveListener moveListener) {
		// TODO Auto-generated method stub
		return false; // STUB!
	}
	
}

class Move{
	public int x, y;
	public long SCORE;
	public Move(int a, int b, long S){ x=a; y=b; SCORE = S;}
}


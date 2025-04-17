package org.example.model;

import java.util.LinkedList;
import java.util.List;

public class King extends Piece {
    private final Board board;
    private boolean wasMoved;

    public King(int color, Square initSq, String img_file,Board board) {
        super(color, initSq, img_file);
        this.board = board;
    }

    @Override
    public boolean move(Square fin) {
        if(fin == board.getSquareArray()[getColor()*7][6] && idempotency) {
            board.getSquareArray()[getColor()*7][7].getOccupyingPiece().move(board.getSquareArray()[getColor()*7][5]);
            board.getSquareArray()[getColor()*7][5].setDisplay(true);
        }
        if(fin == board.getSquareArray()[getColor()*7][2] && idempotency) {
            board.getSquareArray()[getColor()*7][0].getOccupyingPiece().move(board.getSquareArray()[getColor()*7][3]);
            board.getSquareArray()[getColor()*7][3].setDisplay(true);
        }
        wasMoved = true;
        return super.move(fin);
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
LinkedList<Square> legalMoves = new LinkedList<Square>();
        
        Square[][] board = b.getSquareArray();
        
        int x = this.getSquare().getXNum();
        int y = this.getSquare().getYNum();
        
        for (int i = 1; i > -2; i--) {
            for (int k = 1; k > -2; k--) {
                if(!(i == 0 && k == 0)) {
                    try {
                        if(!board[y + k][x + i].isOccupied() || 
                                board[y + k][x + i].getOccupyingPiece().getColor() 
                                != this.getColor()) {
                            legalMoves.add(board[y + k][x + i]);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        continue;
                    }
                }
            }
        }
        if(this.board.cmd != null){
            if(!this.board.getSquareArray()[getColor()*7][5].isOccupied()&&
                    !this.board.getSquareArray()[getColor()*7][6].isOccupied()) {
                if (board[getColor()*7][7].isOccupied() &&
                        board[getColor()*7][7].getOccupyingPiece() instanceof Rook &&
                        !((Rook) board[getColor()*7][7].getOccupyingPiece()).wasMoved &&
                        !wasMoved &&
                        x == 4
                       ) {
                    if(getColor() == 0 &&
                            this.board.cmd.wMoves.get(board[0][6]).isEmpty() &&
                            this.board.cmd.wMoves.get(board[0][5]).isEmpty()){
                        legalMoves.add(board[y][x + 2]);
                    }
                    else if(getColor() == 1 &&
                            this.board.cmd.bMoves.get(board[7][6]).isEmpty() &&
                            this.board.cmd.bMoves.get(board[7][5]).isEmpty()){
                        legalMoves.add(board[y][x + 2]);
                    }
                }
            }
            if(!this.board.getSquareArray()[getColor()*7][1].isOccupied()&&
                !this.board.getSquareArray()[getColor()*7][2].isOccupied()&&
                !this.board.getSquareArray()[getColor()*7][3].isOccupied()){
                if(board[getColor()*7][0].isOccupied() &&
                        board[getColor()*7][0].getOccupyingPiece() instanceof Rook &&
                        !((Rook)board[getColor()*7][0].getOccupyingPiece()).wasMoved &&
                        !wasMoved &&
                        x == 4){
                    if(getColor() == 0 &&
                            this.board.cmd.wMoves.get(board[0][1]).isEmpty() &&
                            this.board.cmd.wMoves.get(board[0][2]).isEmpty() &&
                            this.board.cmd.wMoves.get(board[0][3]).isEmpty())
                        legalMoves.add(board[y][x-2]);
                    else if(getColor() == 1 &&
                            this.board.cmd.bMoves.get(board[0][1]).isEmpty() &&
                            this.board.cmd.bMoves.get(board[0][2]).isEmpty() &&
                            this.board.cmd.bMoves.get(board[0][3]).isEmpty())
                        legalMoves.add(board[y][x-2]);

                }
            }
        }


        
        return legalMoves;
    }

}

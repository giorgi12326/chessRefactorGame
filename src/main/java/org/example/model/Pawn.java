package org.example.model;

import java.util.List;
import java.util.LinkedList;

import static org.example.model.Board.RESOURCES_BQUEEN_PNG;
import static org.example.model.Board.RESOURCES_WQUEEN_PNG;

public class Pawn extends Piece {
    private boolean wasMoved;
    public int wasMovedLastTurn;
    public int enPassant;
    public Board board;


    public Pawn(int color, Square initSq, String img_file,Board board) {
        super(color, initSq, img_file);
        wasMovedLastTurn = 1;
        this.board = board;
    }

    @Override
    public boolean move(Square fin) {
        boolean b = super.move(fin);

        wasMoved = true;
        wasMovedLastTurn--;
        if(idempotency && enPassant != 0) {
            board.getSquareArray()[fin.getYNum() + 2*getColor()-1][fin.getXNum()].removePieceOnThis();
        }
        if(idempotency){
            if((getColor() == 0 && fin.getYNum() == 7) ||
                (getColor() == 1 && fin.getYNum() == 0)){
                Square square = getSquare();
                square.removePieceOnThis();
                Queen p = new Queen(getColor(), square, getColor() == 0 ? RESOURCES_BQUEEN_PNG :
                        RESOURCES_WQUEEN_PNG);
                square.put(p);
                if(getColor() ==0)
                    board.Bpieces.add(p);
                else{
                    board.Wpieces.add(p);
                }

            }
        }
        enPassant = 0;
        return b;
    }

    @Override
    public List<Square> getLegalMoves(Board b) {
        LinkedList<Square> legalMoves = new LinkedList<Square>();

        Square[][] board = b.getSquareArray();

        int x = this.getSquare().getXNum();
        int y = this.getSquare().getYNum();
        int c = this.getColor();

        if (c == 0) {
            if (!wasMoved) {
                if (!board[y + 2][x].isOccupied()&&
                        !board[y + 1][x].isOccupied()) {
                    legalMoves.add(board[y + 2][x]);
                }
            }

            if (y + 1 < 8) {
                if (!board[y + 1][x].isOccupied()) {
                    legalMoves.add(board[y + 1][x]);
                }
            }

            if (x + 1 < 8 && y + 1 < 8) {
                if (board[y + 1][x + 1].isOccupied() &&
                    board[y + 1][x + 1].getOccupyingPiece().getColor() == 1
                    ) {
                    legalMoves.add(board[y + 1][x + 1]);
                }
                if (board[y][x + 1].isOccupied() &&
                        board[y][x + 1].getOccupyingPiece() instanceof Pawn &&
                        board[y][x + 1].getOccupyingPiece().getColor() != getColor() &&
                        ((Pawn) board[y][x + 1].getOccupyingPiece()).wasMovedLastTurn == 0 &&
                        y == 4) {
                    legalMoves.add(board[y + 1][x + 1]);
                }
            }

            if (x - 1 >= 0 && y + 1 < 8) {
                if (board[y + 1][x - 1].isOccupied()&&
                    board[y + 1][x - 1].getOccupyingPiece().getColor() == 1) {
                    legalMoves.add(board[y + 1][x - 1]);
                }
                if (board[y][x - 1].isOccupied() &&
                        board[y][x - 1].getOccupyingPiece() instanceof Pawn &&
                        board[y][x - 1].getOccupyingPiece().getColor() != getColor() &&
                        ((Pawn) board[y][x - 1].getOccupyingPiece()).wasMovedLastTurn == 0 &&

                        y == 4) {
                    legalMoves.add(board[y + 1][x - 1]);
                }
            }
        }

        if (c == 1) {
            if (!wasMoved) {
                if (!board[y - 2][x].isOccupied() &&
                        !board[y - 1][x].isOccupied()) {
                    legalMoves.add(board[y - 2][x]);
                }
            }

            if (y - 1 >= 0) {
                if (!board[y - 1][x].isOccupied()) {
                    legalMoves.add(board[y - 1][x]);
                }
            }

            if (x + 1 < 8 && y - 1 >= 0) {
                if (board[y - 1][x + 1].isOccupied()&&
                        board[y - 1][x + 1].getOccupyingPiece().getColor() == 0) {
                    legalMoves.add(board[y - 1][x + 1]);
                }
                if (board[y][x + 1].isOccupied() &&
                        board[y][x + 1].getOccupyingPiece() instanceof Pawn &&
                        board[y][x + 1].getOccupyingPiece().getColor() != getColor() &&
                        ((Pawn) board[y][x + 1].getOccupyingPiece()).wasMovedLastTurn == 0 &&
                        y == 3) {
                    legalMoves.add(board[y - 1][x + 1]);
                }
            }

            if (x - 1 >= 0 && y - 1 >= 0) {
                if (board[y - 1][x - 1].isOccupied()&&
                    board[y - 1][x - 1].getOccupyingPiece().getColor() == 0) {
                    legalMoves.add(board[y - 1][x - 1]);
                }
                if (board[y][x - 1].isOccupied() &&
                        board[y][x - 1].getOccupyingPiece() instanceof Pawn &&
                        board[y][x - 1].getOccupyingPiece().getColor() != getColor() &&
                        ((Pawn) board[y][x - 1].getOccupyingPiece()).wasMovedLastTurn == 0 &&
                        y == 3) {
                    legalMoves.add(board[y - 1][x - 1]);
                }
            }
        }


        return legalMoves;
    }
}

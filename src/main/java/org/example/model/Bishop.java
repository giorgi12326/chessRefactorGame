package org.example.model;

import java.util.List;

public class Bishop extends Piece {

    public Bishop(int color, Square initSq, String img_file) {
        super(color, initSq, img_file);
    }
    
    @Override
    public List<Square> getLegalMoves(Board b) {
        Square[][] board = b.getSquareArray();
        int x = this.getSquare().getXNum();
        int y = this.getSquare().getYNum();
        
        return getDiagonalOccupations(board, x, y);
    }
}

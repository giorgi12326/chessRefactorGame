package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.example.Board.RESOURCES_BKING_PNG;
import static org.example.Board.RESOURCES_WQUEEN_PNG;

class CheckmateDetectorTest {
    private Board board;
    @BeforeEach
    void setUp() {
        board = new Board(null);
        board.getSquareArray()[1][0].put(new Queen(1,board.getSquareArray()[1][0],RESOURCES_WQUEEN_PNG));
        King blackKing = new King(0, board.getSquareArray()[1][1], RESOURCES_BKING_PNG);
        board.getSquareArray()[1][1].put(blackKing);
        King whiteKing = new King(1, board.getSquareArray()[7][7], RESOURCES_BKING_PNG);
        board.getSquareArray()[7][7].put(whiteKing);
        LinkedList<Piece> wPieces = new LinkedList<>();
        LinkedList<Piece> bPieces = new LinkedList<>();
        wPieces.add(board.getSquareArray()[1][0].getOccupyingPiece());
        bPieces.add(board.getSquareArray()[1][1].getOccupyingPiece());


    }

    @Test
    void testMove() {
        // Initial setup: move white pawn from (1, 0) to (2, 0)

    }
}

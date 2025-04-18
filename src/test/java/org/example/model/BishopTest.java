package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.example.model.Board.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BishopTest {
    Board board;
    private King blackKing;
    private King whiteKing;
    private CheckmateDetector checkmateDetector;
    private Bishop bishop;


    @BeforeEach
    public void setup(){
        board = new Board(null,null);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.getSquareArray()[i][j].liftUpThePiece();
            }
        }

        bishop = new Bishop(1, board.getSquareArray()[2][2], RESOURCES_WQUEEN_PNG);
        board.getSquareArray()[2][2].put(bishop);

        whiteKing = new King(1, board.getSquareArray()[7][7], RESOURCES_WKING_PNG,board);
        board.getSquareArray()[7][7].put(whiteKing);

        blackKing = new King(0, board.getSquareArray()[0][3], RESOURCES_BKING_PNG,board);
        board.getSquareArray()[0][3].put(blackKing);



        LinkedList<Piece> wPieces = new LinkedList<>();
        LinkedList<Piece> bPieces = new LinkedList<>();
        bPieces.add(board.getSquareArray()[0][3].getOccupyingPiece());
        wPieces.add(board.getSquareArray()[7][7].getOccupyingPiece());
        wPieces.add(board.getSquareArray()[2][2].getOccupyingPiece());

        checkmateDetector = new CheckmateDetector(board,wPieces,bPieces,whiteKing,blackKing);
    }
    @Test
    public void testLegalMoves(){
        List<Square> legalMoves = bishop.getLegalMoves(board);
        List<Square> squares = List.of(
                board.getSquareArray()[0][0],
                board.getSquareArray()[1][1],
                board.getSquareArray()[3][3],
                board.getSquareArray()[4][4],
                board.getSquareArray()[5][5],
                board.getSquareArray()[6][6],
                board.getSquareArray()[0][4],
                board.getSquareArray()[1][3],
                board.getSquareArray()[3][1],
                board.getSquareArray()[4][0]
        );
        assertTrue(squares.containsAll(legalMoves));
        assertTrue(legalMoves.containsAll(squares));
    }
}

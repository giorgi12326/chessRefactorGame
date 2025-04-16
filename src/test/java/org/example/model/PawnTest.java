package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.example.model.Board.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PawnTest {
    Board board;
    private Pawn wPawn;
    private Pawn bPawn;
    private King blackKing;
    private King whiteKing;
    private CheckmateDetector checkmateDetector;

    @BeforeEach
    public void setup(){


        board = new Board(null);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.getSquareArray()[i][j].liftUpThePiece();
            }
        }

        wPawn = new Pawn(1, board.getSquareArray()[6][1], RESOURCES_WQUEEN_PNG);
        board.getSquareArray()[6][1].put(wPawn);

        whiteKing = new King(1, board.getSquareArray()[7][7], RESOURCES_WKING_PNG);
        board.getSquareArray()[7][7].put(whiteKing);

        bPawn = new Pawn(0, board.getSquareArray()[1][2], RESOURCES_WQUEEN_PNG);
        board.getSquareArray()[1][2].put(bPawn);

        blackKing = new King(0, board.getSquareArray()[1][7], RESOURCES_BKING_PNG);
        board.getSquareArray()[1][7].put(blackKing);

        LinkedList<Piece> wPieces = new LinkedList<>();
        LinkedList<Piece> bPieces = new LinkedList<>();
        bPieces.add(board.getSquareArray()[1][2].getOccupyingPiece());
        bPieces.add(board.getSquareArray()[1][7].getOccupyingPiece());
        wPieces.add(board.getSquareArray()[7][7].getOccupyingPiece());
        wPieces.add(board.getSquareArray()[6][1].getOccupyingPiece());

        checkmateDetector = new CheckmateDetector(board,wPieces,bPieces,whiteKing,blackKing);
    }
    @Test
    public void testMove(){
        assertEquals(2,wPawn.getLegalMoves(board).size());
        wPawn.move(board.getSquareArray()[5][1]);
        assertEquals(1,wPawn.getLegalMoves(board).size());
        wPawn.move(board.getSquareArray()[3][1]);
        bPawn.move(board.getSquareArray()[3][2]);
        System.out.println(board);
        System.out.println(wPawn.getLegalMoves(board));
        assertTrue(wPawn.getLegalMoves(board).contains(board.getSquareArray()[2][2]));
        wPawn.move(board.getSquareArray()[2][2]);
        assertEquals(1,wPawn.getLegalMoves(board).size());


    }
}

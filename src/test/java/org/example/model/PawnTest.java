package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.example.model.Board.*;
import static org.junit.jupiter.api.Assertions.*;

public class PawnTest {
    Board board;
    private Pawn wPawn;
    private Pawn bPawn;
    private King blackKing;
    private King whiteKing;
    private CheckmateDetector checkmateDetector;
    private LinkedList<Piece> bPieces;

    @BeforeEach
    public void setup(){


        board = new Board(null);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.getSquareArray()[i][j].liftUpThePiece();
            }
        }

        wPawn = new Pawn(1, board.getSquareArray()[6][1], RESOURCES_WQUEEN_PNG,board);
        board.getSquareArray()[6][1].put(wPawn);

        whiteKing = new King(1, board.getSquareArray()[7][7], RESOURCES_WKING_PNG,board);
        board.getSquareArray()[7][7].put(whiteKing);

        bPawn = new Pawn(0, board.getSquareArray()[1][2], RESOURCES_WQUEEN_PNG,board);
        board.getSquareArray()[1][2].put(bPawn);

        blackKing = new King(0, board.getSquareArray()[1][7], RESOURCES_BKING_PNG,board);
        board.getSquareArray()[1][7].put(blackKing);

        LinkedList<Piece> wPieces = new LinkedList<>();
        bPieces = new LinkedList<>();
        bPieces.add(board.getSquareArray()[1][2].getOccupyingPiece());
        bPieces.add(board.getSquareArray()[1][7].getOccupyingPiece());
        wPieces.add(board.getSquareArray()[7][7].getOccupyingPiece());
        wPieces.add(board.getSquareArray()[6][1].getOccupyingPiece());

        checkmateDetector = new CheckmateDetector(board,wPieces,bPieces,whiteKing,blackKing);
    }
    @Test
    public void enPassant(){
        assertEquals(2,wPawn.getLegalMoves(board).size());
        wPawn.move(board.getSquareArray()[5][1]);
        assertEquals(1,wPawn.getLegalMoves(board).size());
        wPawn.move(board.getSquareArray()[3][1]);
        bPawn.move(board.getSquareArray()[3][2]);
        assertEquals(2,wPawn.getLegalMoves(board).size());
        assertTrue(wPawn.getLegalMoves(board).contains(board.getSquareArray()[2][2]));
        wPawn.move(board.getSquareArray()[2][2]);
        System.out.println(board);
        assertEquals(1,wPawn.getLegalMoves(board).size());
        assertFalse(board.getSquareArray()[3][2].isOccupied());
    }
    @Test
    public void canCaptureOwnPawn(){
        createPawnOnSquare(1,5,2);
        assertEquals(2,wPawn.getLegalMoves(board).size());
    }
    @Test
    public void canJumpOver(){
        createPawnOnSquare(1,5,1);
        assertEquals(0,wPawn.getLegalMoves(board).size());
    }
    @Test
    public void canPawnBlock(){
        blackKing.move(board.getSquareArray()[0][0]);
        Queen queenOnSquare = createQueenOnSquare(1, 6, 6);
        assertTrue(checkmateDetector.canBlock(List.of(queenOnSquare),checkmateDetector.bMoves,blackKing));
        createPawnOnSquare(0,1,3);
        bPawn.move(board.getSquareArray()[3][2]);
        assertTrue(checkmateDetector.canBlock(List.of(queenOnSquare),checkmateDetector.bMoves,blackKing));

    }

    private Pawn createPawnOnSquare(int color,int h, int w){
        Pawn p = new Pawn(color, board.getSquareArray()[h][w], RESOURCES_WPAWN_PNG,board);
        board.getSquareArray()[h][w].put(p);
        if(color == 0)
            checkmateDetector.bPieces.add(p);
        else
            checkmateDetector.wPieces.add(p);
        return p;

    }
    private Queen createQueenOnSquare(int color,int h, int w){
        Queen p = new Queen(color, board.getSquareArray()[h][w], RESOURCES_WQUEEN_PNG);
        board.getSquareArray()[h][w].put(p);
        if(color == 0)
            checkmateDetector.bPieces.add(p);
        else
            checkmateDetector.wPieces.add(p);
        return p;

    }
}

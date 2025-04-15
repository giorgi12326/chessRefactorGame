package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static org.example.Board.*;
import static org.junit.jupiter.api.Assertions.*;

class CheckmateDetectorTest {
    private Board board;
    CheckmateDetector checkmateDetector;
    King whiteKing;
    King blackKing;
    Queen queen;

    @BeforeEach
    void setUp() {
        board = new Board(null);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.getSquareArray()[i][j].liftUpThePiece();
            }

        }
        queen = new Queen(1, board.getSquareArray()[1][0], RESOURCES_WQUEEN_PNG);
        board.getSquareArray()[1][0].put(queen);

        blackKing = new King(0, board.getSquareArray()[1][2], RESOURCES_BKING_PNG);
        board.getSquareArray()[1][2].put(blackKing);

        whiteKing = new King(1, board.getSquareArray()[7][7], RESOURCES_WKING_PNG);
        board.getSquareArray()[7][7].put(whiteKing);
        LinkedList<Piece> wPieces = new LinkedList<>();
        LinkedList<Piece> bPieces = new LinkedList<>();
        wPieces.add(board.getSquareArray()[1][0].getOccupyingPiece());
        wPieces.add(board.getSquareArray()[7][7].getOccupyingPiece());
        bPieces.add(board.getSquareArray()[1][2].getOccupyingPiece());

        checkmateDetector = new CheckmateDetector(board,wPieces,bPieces,whiteKing,blackKing);
    }
    @Test
    public void move(){
        assertEquals(3, Arrays.stream(board.getSquareArray()).flatMap(Arrays::stream).filter(x -> x.isOccupied()).count());
        blackKing.move(board.getSquareArray()[1][2]);
        assertEquals(blackKing.getSquare(),board.getSquareArray()[1][2]);
        assertNull(board.getSquareArray()[1][1].getOccupyingPiece());
        assertEquals(3, Arrays.stream(board.getSquareArray()).flatMap(Arrays::stream).filter(x -> x.isOccupied()).count());
    }
    @Test
    public void blackKingChecked(){
        assertTrue(checkmateDetector.blackInCheck());
        blackKing.move(board.getSquareArray()[1][3]);
        assertTrue(checkmateDetector.blackInCheck());
        blackKing.move(board.getSquareArray()[2][2]);
        assertFalse(checkmateDetector.blackInCheck());
    }

    @Test
    void testMoveWithoutCapture() {
        assertFalse(checkmateDetector.testMove(blackKing,board.getSquareArray()[1][3]));
        assertFalse(checkmateDetector.testMove(blackKing,board.getSquareArray()[1][1]));
        assertFalse(checkmateDetector.testMove(blackKing,board.getSquareArray()[1][7]));
        assertTrue(checkmateDetector.testMove(blackKing,board.getSquareArray()[2][2]));
        assertTrue(checkmateDetector.testMove(blackKing,board.getSquareArray()[0][2]));
        System.out.println(board);

        Queen p = new Queen(1, board.getSquareArray()[7][3], RESOURCES_WQUEEN_PNG);
        board.getSquareArray()[7][3].put(p);
        checkmateDetector.wPieces.add(p);
        blackKing.move(board.getSquareArray()[0][2]);
        checkmateDetector.update();
        assertFalse(checkmateDetector.testMove(blackKing,board.getSquareArray()[0][3]));
        checkmateDetector.testMove(p,board.getSquareArray()[6][3]);
        checkmateDetector.testMove(blackKing,board.getSquareArray()[0][4]);
        assertFalse(checkmateDetector.blackInCheck());
    }
    @Test
    public void testMoveWithCapture(){
        Knight knight = new Knight(0, board.getSquareArray()[3][0], RESOURCES_BKING_PNG);
        board.getSquareArray()[3][0].put(knight);
        checkmateDetector.bPieces.add(knight);
        checkmateDetector.testMove(queen,knight.getSquare());

        assertEquals(2,checkmateDetector.wPieces.size());
        assertEquals(queen,board.getSquareArray()[1][0].getOccupyingPiece());
        assertEquals(board.getSquareArray()[1][0],queen.getSquare());

        assertEquals(2,checkmateDetector.bPieces.size());
        assertEquals(knight,board.getSquareArray()[3][0].getOccupyingPiece());
        assertEquals(board.getSquareArray()[3][0],knight.getSquare());
    }
    @Test
    public void canEvade(){
        blackKing.move(board.getSquareArray()[0][2]);
        whiteKing.move(board.getSquareArray()[0][4]);
        assertFalse(checkmateDetector.canEvade(checkmateDetector.wMoves,blackKing));

        Queen p = new Queen(1, board.getSquareArray()[7][3], RESOURCES_WQUEEN_PNG);
        board.getSquareArray()[7][3].put(p);
        checkmateDetector.wPieces.add(p);
        checkmateDetector.update();
        assertFalse(checkmateDetector.canEvade(checkmateDetector.wMoves,blackKing));

    }
    @Test
    public void kingLegalMoves(){
        assertEquals(8,blackKing.getLegalMoves(board).size());
    }

}

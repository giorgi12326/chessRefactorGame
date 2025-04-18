package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.example.model.Board.*;
import static org.junit.jupiter.api.Assertions.*;

public class PieceTest {
    Board board;
    private King blackKing;
    private King whiteKing;
    private CheckmateDetector checkmateDetector;
    private Piece piece;


    @BeforeEach
    public void setup(){
        board = new Board(null,null);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.getSquareArray()[i][j].liftUpThePiece();
            }
        }

        piece = new Queen(1, board.getSquareArray()[2][2], RESOURCES_WQUEEN_PNG);
        board.getSquareArray()[2][2].put(piece);

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
    public void testLegalLinerOccupations(){
        assertArrayEquals(new int[]{0, 7, 0, 7}, piece.getLinearOccupations(board.getSquareArray(), 2, 2));
        createQueenOnSquare(1,2,5);
        assertArrayEquals(new int[]{0,7,0,4},piece.getLinearOccupations(board.getSquareArray(), 2, 2));
        createQueenOnSquare(1,6,2);
        assertArrayEquals(new int[]{0,5,0,4},piece.getLinearOccupations(board.getSquareArray(), 2, 2));
        createQueenOnSquare(1,2,1);
        assertArrayEquals(new int[]{0,5,2,4},piece.getLinearOccupations(board.getSquareArray(), 2, 2));
        createQueenOnSquare(1,0,2);
        assertArrayEquals(new int[]{1,5,2,4},piece.getLinearOccupations(board.getSquareArray(), 2, 2));


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

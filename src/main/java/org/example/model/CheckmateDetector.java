package org.example.model;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;


/**
 * Component of the Chess game that detects checkmates in the game.
 * 
 * @author Jussi Lundstedt
 *
 */
public class CheckmateDetector {
    private final Board board;
     final LinkedList<Piece> wPieces;
     final LinkedList<Piece> bPieces;
    private final LinkedList<Square> movableSquares;
    private final LinkedList<Square> squares;
    private final King bk;
    private final King wk;
     final HashMap<Square,List<Piece>> wMoves;// list of pieces that can move to Square
     final HashMap<Square,List<Piece>> bMoves;
    
    /**
     * Constructs a new instance of CheckmateDetector on a given board. By
     * convention should be called when the board is in its initial state.
     * 
     * @param board The board which the detector monitors
     * @param wPieces White pieces on the board.
     * @param bPieces Black pieces on the board.
     * @param wk Piece object representing the white king
     * @param bk Piece object representing the black king
     */
    public CheckmateDetector(Board board, LinkedList<Piece> wPieces,
                             LinkedList<Piece> bPieces, King wk, King bk) {
        this.board = board;
        this.wPieces = wPieces;
        this.bPieces = bPieces;
        this.bk = bk;
        this.wk = wk;
        
        // Initialize other fields
        squares = new LinkedList<Square>();
        movableSquares = new LinkedList<Square>();
        wMoves = new HashMap<Square,List<Piece>>();
        bMoves = new HashMap<Square,List<Piece>>();
        
        Square[][] brd = board.getSquareArray();
        
        // add all squares to squares list and as hashmap keys
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                squares.add(brd[x][y]);
                wMoves.put(brd[x][y], new LinkedList<Piece>());//switched
                bMoves.put(brd[x][y], new LinkedList<Piece>());
            }
        }
        
        // update situation
        update();
    }
    
    /**
     * Updates the object with the current situation of the game.
     * clears hashmaps and movableSquares and adds in new hashmap values.
     */
    public void update() {
        // Iterators through pieces
        Iterator<Piece> wIter = wPieces.iterator();
        Iterator<Piece> bIter = bPieces.iterator();

        // empty moves and movable squares at each update
        for (List<Piece> pieces : wMoves.values()) {
            pieces.removeAll(pieces);
        }
        
        for (List<Piece> pieces : bMoves.values()) {
            pieces.removeAll(pieces);
        }
        movableSquares.removeAll(movableSquares);

        // Add each move white and black can make to map
        while (wIter.hasNext()) {
            Piece p = wIter.next();
            if (p.getSquare() == null) {
                wIter.remove();
                continue;
            }

            List<Square> mvs = p.getLegalMoves(board);
            Iterator<Square> iter = mvs.iterator();
            while (iter.hasNext()) {
                List<Piece> pieces = wMoves.get(iter.next());
                pieces.add(p);
            }
        }

        while (bIter.hasNext()) {
            Piece p = bIter.next();
            if (p.getSquare() == null) {
                bIter.remove();
                continue;
            }
            List<Square> mvs = p.getLegalMoves(board);
            Iterator<Square> iter = mvs.iterator();
            while (iter.hasNext()) {
                List<Piece> pieces = bMoves.get(iter.next());
                pieces.add(p);
            }
        }
    }
    
    /**
     * Checks if the black king is threatened
     * @return boolean representing whether the black king is in check.
     */
    public boolean blackInCheck() {
        update();
        Square sq = bk.getSquare();
        if (wMoves.get(sq).isEmpty()) {
            movableSquares.addAll(squares);
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Checks if the white king is threatened
     * @return boolean representing whether the white king is in check.
     */
    public boolean whiteInCheck() {
        update();
        Square sq = wk.getSquare();
        if (bMoves.get(sq).isEmpty()) {
            movableSquares.addAll(squares);
            return false;
        } else return true;
    }
    
    /**
     * Checks whether black is in checkmate.
     * @return boolean representing if black player is checkmated.
     */
    public boolean blackCheckMated() {
        boolean checkmate = true;
        // Check if black is in check
        if (!this.blackInCheck()) return false;
        
        // If yes, check if king can evade even with capture
        if (canEvade(wMoves, bk)) checkmate = false;
        
        // If no, check if threat itself can be captured
        List<Piece> threats = wMoves.get(bk.getSquare());
        if (canCapture(bMoves, threats, bk)) checkmate = false;
        
        // If no, check if threat can be blocked
        if (canBlock(threats, bMoves, bk)) checkmate = false;
        
        // If no possible ways of removing check, checkmate occurred
        return checkmate;
    }
    
    /**
     * Checks whether white is in checkmate.
     * @return boolean representing if white player is checkmated.
     */
    public boolean whiteCheckMated() {
        boolean checkmate = true;
        // Check if white is in check
        if (!this.whiteInCheck()) return false;
        
        // If yes, check if king can evade
        if (canEvade(bMoves, wk)) checkmate = false;
        
        // If no, check if threat can be captured
        List<Piece> threats = bMoves.get(wk.getSquare());
        if (canCapture(wMoves, threats, wk)) checkmate = false;
        
        // If no, check if threat can be blocked
        if (canBlock(threats, wMoves, wk)) checkmate = false;
        
        // If no possible ways of removing check, checkmate occurred
        return checkmate;
    }
    
    /*
     * Helper method to determine if the king can evade the check.
     * Gives a false positive if the king can capture the checking piece.
     */
    protected boolean canEvade(Map<Square,List<Piece>> tMoves, King tKing) {
        boolean evade = false;
        List<Square> kingsMoves = tKing.getLegalMoves(board);
        System.out.println("call1");

        // If king is not threatened at some square, it can evade
        for (Square sq : kingsMoves) {
            if (!testMove(tKing, sq))
                continue;
            if (tMoves.get(sq).isEmpty()) {
                movableSquares.add(sq);
                evade = true;
            }
        }
        return evade;
    }
    
    /*
     * Helper method to determine if the threatening piece can be captured.
     * //
     * movableSquares added for each of the piece that can save from check with capture
     * but old code added only up to 2/TODO
     */
    public boolean canCapture(Map<Square,List<Piece>> poss,
                               List<Piece> threats, King k) {
        boolean capture = false;
        for (Piece threat:threats) {
            for (Piece defendingPiece:poss.get(threat.getSquare())) {
                if(testMove(defendingPiece,threat.getSquare())) {
                    movableSquares.add(threat.getSquare());
                    capture = true;
                }
            }
        }
        
        return capture;
    }
    
    /*
     * Helper method to determine if check can be blocked by a piece.
     */
    protected boolean canBlock(List<Piece> threats,
                             Map <Square,List<Piece>> blockPieces, King k) {
        boolean blockable = false;

        if (threats.size() == 1) {
            Square threatSquare = threats.getFirst().getSquare();
            Square kingSquare = k.getSquare();
            Square[][] boardArray = board.getSquareArray();
            if (kingSquare.getXNum() == threatSquare.getXNum()) {
                int max = Math.max(kingSquare.getYNum(), threatSquare.getYNum());
                int min = Math.min(kingSquare.getYNum(), threatSquare.getYNum());

                for (int i = min + 1; i < max; i++) {
                    List<Piece> blks = blockPieces.get(boardArray[i][kingSquare.getXNum()]);
                    ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<>(blks);

                    if (!blockers.isEmpty()) {
                        movableSquares.add(boardArray[i][kingSquare.getXNum()]);
                        for (Piece p : blockers) {
                            if (testMove(p,boardArray[i][kingSquare.getXNum()])) {
                                System.out.println(p);
                                blockable = true;
                            }
                        }
                    }
                }
            }

            if (kingSquare.getYNum() == threatSquare.getYNum()) {
                int max = Math.max(kingSquare.getXNum(), threatSquare.getXNum());
                int min = Math.min(kingSquare.getXNum(), threatSquare.getXNum());

                for (int i = min + 1; i < max; i++) {
                    List<Piece> blks =
                            blockPieces.get(boardArray[kingSquare.getYNum()][i]);
                    ConcurrentLinkedDeque<Piece> blockers =
                            new ConcurrentLinkedDeque<Piece>();
                    blockers.addAll(blks);

                    if (!blockers.isEmpty()) {

                        movableSquares.add(boardArray[kingSquare.getYNum()][i]);

                        for (Piece p : blockers) {
                            if (testMove(p, boardArray[kingSquare.getYNum()][i])) {
                                blockable = true;
                            }
                        }

                    }
                }
            }
            if (Math.abs(kingSquare.getXNum() - threatSquare.getXNum()) ==
                    Math.abs(kingSquare.getYNum() - threatSquare.getYNum())) {

                int kX = kingSquare.getXNum();
                int kY = kingSquare.getYNum();
                int tX = threatSquare.getXNum();
                int tY = threatSquare.getYNum();

                int xDir = (kX > tX) ? 1 : -1;
                int yDir = (kY > tY) ? 1 : -1;

                int steps = Math.abs(kX - tX);

                for (int i = 1; i < steps; i++) {
                    int x = tX + i * xDir;
                    int y = tY + i * yDir;

                    List<Piece> blks = blockPieces.get(boardArray[y][x]);
                    ConcurrentLinkedDeque<Piece> blockers = new ConcurrentLinkedDeque<>(blks);


                    if (!blockers.isEmpty()) {
                        movableSquares.add(boardArray[y][x]);
                        for (Piece p : blockers) {
                            if (testMove(p, boardArray[y][x])) {
                                blockable = true;
                            }
                        }
                    }
                }
            }

        }

        return blockable;
    }

    /**
     * Method to get a list of allowable squares that the player can move.
     * Defaults to all squares, but limits available squares if player is in
     * check.
     * @param whiteMove boolean representing whether it's white player's turn (if yes,
     * true)
     * @return List of squares that the player can move into.
     */
    public List<Square> getAllowableSquares(boolean whiteMove) {
//        movableSquares.removeAll(movableSquares);
        if (whiteInCheck() && !whiteMove) {
            whiteCheckMated();
        } else if (blackInCheck() && whiteMove) {
            blackCheckMated();
        }
        return movableSquares;
    }
    
    /**
     * Tests a move a player is about to make to prevent making an illegal move
     * that puts the player in check.
     * @param movingPiece Piece moved
     * @param destinationSquare Square to which movingPiece is about to move
     * @return false if move would cause a check
     */
    public boolean testMove(Piece movingPiece, Square destinationSquare) {
        boolean movetest = true;
        movingPiece.idempotency = false;

        if(movingPiece instanceof Pawn) {
            ((Pawn) movingPiece).enPassant = 0;
        }

        Piece destinationPiece = destinationSquare.getOccupyingPiece();
        Square initSquare = movingPiece.getSquare();

        movingPiece.move(destinationSquare);
        update();

        if (movingPiece.getColor() == 0 && blackInCheck()) movetest = false;
        else if (movingPiece.getColor() == 1 && whiteInCheck()) movetest = false;

        movingPiece.move(initSquare);

        if (destinationPiece != null) {
            putPieceOnBoardAndAddToList(destinationPiece,destinationSquare);
        }
        update();

        if(movingPiece instanceof Pawn){
            ((Pawn) movingPiece).wasMovedLastTurn+=2;
        }
        movingPiece.idempotency = true;


        movableSquares.addAll(squares);
        return movetest;
    }

    private void putPieceOnBoardAndAddToList(Piece destinationPiece, Square destinationSquare) {
        destinationSquare.put(destinationPiece);
        destinationPiece.move(destinationSquare);

        if(destinationPiece.getColor() == 1)
            wPieces.add(destinationPiece);
        else
            bPieces.add(destinationPiece);
    }

}

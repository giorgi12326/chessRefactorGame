package org.example.model;

import org.example.controller.Controller;
import org.example.view.GameWindow;
import org.example.view.View;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Board {
    private static final String RESOURCES_WBISHOP_PNG = "/wbishop.png";
    private static final String RESOURCES_BBISHOP_PNG = "/bbishop.png";
    private static final String RESOURCES_WKNIGHT_PNG = "/wknight.png";
    private static final String RESOURCES_BKNIGHT_PNG = "/bknight.png";
    private static final String RESOURCES_WROOK_PNG = "/wrook.png";
    private static final String RESOURCES_BROOK_PNG = "/brook.png";
    static final String RESOURCES_WKING_PNG = "/wking.png";
    static final String RESOURCES_BKING_PNG = "/bking.png";
    public static final String RESOURCES_BQUEEN_PNG = "/bqueen.png";
    public static final String RESOURCES_WQUEEN_PNG = "/wqueen.png";
    static final String RESOURCES_WPAWN_PNG = "/wpawn.png";
    private static final String RESOURCES_BPAWN_PNG = "/bpawn.png";

    private final String PGN;
    private final Square[][] board;
    private final GameWindow gameWindow;
    public final LinkedList<Piece> Bpieces;
    public final LinkedList<Piece> Wpieces;
    private boolean whiteTurn;
    private Piece currPiece;
    public int currX;
    public int currY;
    public CheckmateDetector cmd;
    public Controller controller;
    public View view;
    List<PGNParser.PGNMove> moveList;
    public String whiteName;
    public String blackName;

    private final List<String> moveHistory = new ArrayList<>();
    private String whitePlayerName;
    private String blackPlayerName;
    private String result = "*";

    @Override
    public String toString() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getOccupyingPiece() instanceof King) System.out.print("K ");
                if (board[i][j].getOccupyingPiece() instanceof Pawn) System.out.print("P ");
                if (board[i][j].getOccupyingPiece() instanceof Knight) System.out.print("Z ");
                if (board[i][j].getOccupyingPiece() instanceof Queen) System.out.print("Q ");
                if (board[i][j].getOccupyingPiece() == null) System.out.print("0 ");
            }
            System.out.println();
        }
        return "Board{}";
    }

    public Board(GameWindow gameWindow, String PGN) {
        this.gameWindow = gameWindow;
        this.PGN = PGN;
        if (PGN == null && gameWindow != null) {
            this.whitePlayerName = gameWindow.whiteName;
            this.blackPlayerName = gameWindow.blackName;
        }
        boolean shouldTryToCheckParsed = true;
        if (PGN != null) {
            try {
                moveList = PGNParser.parseInList(PGNParser.parsePGN(PGN).getFirst());
                System.out.println(moveList);
            } catch (NoSuchElementException e) {
                moveList = new ArrayList<>();
                System.err.println("please enter valid pgn format");
                shouldTryToCheckParsed = false;
            } catch (InvalidPropertiesFormatException e) {
                shouldTryToCheckParsed = false;
                System.err.println("please enter valid pgn format");
            }
            whiteName = PGNParser.whitePlayer;
            blackName = PGNParser.blackPlayer;
        }
        board = new Square[8][8];
        Bpieces = new LinkedList<>();
        Wpieces = new LinkedList<>();
        if (shouldTryToCheckParsed) {
            initializePieces();
            whiteTurn = true;
            controller = new Controller(this);
            view = new View(this);
        }
    }

    private void initializePieces() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int xMod = x % 2;
                int yMod = y % 2;
                if ((xMod == 0 && yMod == 0) || (xMod == 1 && yMod == 1)) {
                    board[x][y] = new Square(this, 1, y, x);
                } else {
                    board[x][y] = new Square(this, 0, y, x);
                }
            }
        }
        for (int x = 0; x < 8; x++) {
            board[1][x].put(new Pawn(0, board[1][x], RESOURCES_BPAWN_PNG, this));
            board[6][x].put(new Pawn(1, board[6][x], RESOURCES_WPAWN_PNG, this));
        }
        board[7][3].put(new Queen(1, board[7][3], RESOURCES_WQUEEN_PNG));
        board[0][3].put(new Queen(0, board[0][3], RESOURCES_BQUEEN_PNG));
        King bkObj = new King(0, board[0][4], RESOURCES_BKING_PNG, this);
        King wkObj = new King(1, board[7][4], RESOURCES_WKING_PNG, this);
        board[0][4].put(bkObj);
        board[7][4].put(wkObj);
        board[0][0].put(new Rook(0, board[0][0], RESOURCES_BROOK_PNG));
        board[0][7].put(new Rook(0, board[0][7], RESOURCES_BROOK_PNG));
        board[7][0].put(new Rook(1, board[7][0], RESOURCES_WROOK_PNG));
        board[7][7].put(new Rook(1, board[7][7], RESOURCES_WROOK_PNG));
        board[0][1].put(new Knight(0, board[0][1], RESOURCES_BKNIGHT_PNG));
        board[0][6].put(new Knight(0, board[0][6], RESOURCES_BKNIGHT_PNG));
        board[7][1].put(new Knight(1, board[7][1], RESOURCES_WKNIGHT_PNG));
        board[7][6].put(new Knight(1, board[7][6], RESOURCES_WKNIGHT_PNG));
        board[0][2].put(new Bishop(0, board[0][2], RESOURCES_BBISHOP_PNG));
        board[0][5].put(new Bishop(0, board[0][5], RESOURCES_BBISHOP_PNG));
        board[7][2].put(new Bishop(1, board[7][2], RESOURCES_WBISHOP_PNG));
        board[7][5].put(new Bishop(1, board[7][5], RESOURCES_WBISHOP_PNG));
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 8; x++) {
                Bpieces.add(board[y][x].getOccupyingPiece());
                Wpieces.add(board[7 - y][x].getOccupyingPiece());
            }
        }
        cmd = new CheckmateDetector(this, Wpieces, Bpieces, (King) board[7][4].getOccupyingPiece(), (King) board[0][4].getOccupyingPiece());
        if (gameWindow == null && moveList != null) {
            while (!moveList.isEmpty() && moveList.getFirst().to != null) {
                reactToMousePress(null);
            }
            System.out.println("valid");
        }
    }

    public Square[][] getSquareArray() {
        return this.board;
    }

    public boolean getTurn() {
        return whiteTurn;
    }

    public void setCurrPiece(Piece p) {
        this.currPiece = p;
    }

    public Piece getCurrPiece() {
        return this.currPiece;
    }

    public void reactToMousePress(MouseEvent e) {
        if (PGN == null) {
            currX = e.getX() - 24;
            currY = e.getY() - 24;
            Square sq = (Square) view.getComponentAt(new Point(e.getX(), e.getY()));
            if (sq == null) return;
            if (currPiece == null && sq.isOccupied()) {
                Piece p = sq.getOccupyingPiece();
                if (p.getColor() == 0 && whiteTurn) return;
                if (p.getColor() == 1 && !whiteTurn) return;
                currPiece = p;
                sq.setDisplay(false);
            }
            view.repaint();
        } else {
            if (moveList.isEmpty()) {
                System.out.println("VALID");
                gameWindow.incorrectPgnMessage("Error, No More Moves Left");
                return;
            }
            PGNParser.PGNMove nextMove = moveList.removeFirst();
            if (nextMove == null) {
                gameWindow.incorrectPgnMessage("Error, No es Left");
                return;
            }
            int color = nextMove.isWhite ? 1 : 0;
            if (nextMove.isCastleKingSide) {
                if (getSquareArray()[color * 7][4].isOccupied() && getSquareArray()[color * 7][4].getOccupyingPiece() instanceof King && getSquareArray()[color * 7][4].getOccupyingPiece().getColor() == color)
                    getSquareArray()[color * 7][4].getOccupyingPiece().move(getSquareArray()[color * 7][6]);
                return;
            } else if (nextMove.isCastleQueenSide) {
                if (getSquareArray()[color * 7][4].isOccupied() && getSquareArray()[color * 7][4].getOccupyingPiece() instanceof King && getSquareArray()[color * 7][4].getOccupyingPiece().getColor() == color) {
                    getSquareArray()[color * 7][4].getOccupyingPiece().move(getSquareArray()[color * 7][2]);
                }
                return;
            }
            List<Piece> list;
            if (nextMove.piece == null) {
                System.out.println("BALID?");
            }
            if (nextMove.isWhite) {
                list = cmd.wMoves.get(getSquareArray()[nextMove.to[0]][nextMove.to[1]]).stream().filter(t -> nextMove.piece.isInstance(t)).toList();
            } else {
                list = cmd.bMoves.get(getSquareArray()[nextMove.to[0]][nextMove.to[1]]).stream().filter(t -> nextMove.piece.isInstance(t)).toList();
            }
            int size = list.size();
            if (size == 0) {
                gameWindow.incorrectPgnMessage("Error, That piece cant move to specified Spot");
            } else if (size == 1) {
                captureLogic(nextMove);
                list.get(0).move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
            } else {
                String disambiguation = nextMove.disambiguation;
                if (disambiguation.isEmpty()) {
                    gameWindow.incorrectPgnMessage("cant resolve ambiguity");
                } else if (disambiguation.length() == 1) {
                    char c = disambiguation.charAt(0);
                    if (c >= 'a' && c <= 'h') {
                        List<Piece> list1 = list.stream().filter(t -> t.getSquare().getXNum() == c - 'a').toList();
                        if (list1.isEmpty()) {
                            gameWindow.incorrectPgnMessage("Error, cant resolve ambiguity on " + c);
                        } else {
                            captureLogic(nextMove);
                            list1.get(0).move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
                        }
                    } else if (c >= '1' && c <= '8') {
                        List<Piece> list1 = list.stream().filter(t -> t.getSquare().getYNum() == 7 - (c - '1')).toList();
                        if (list1.isEmpty())
                            gameWindow.incorrectPgnMessage("Error, cant resolve ambiguity on " + c);
                        else {
                            captureLogic(nextMove);
                            list1.get(0).move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
                        }
                    }
                } else {
                    List<Piece> list1 = list.stream()
                            .filter(t -> t.getSquare().getXNum() == disambiguation.charAt(0) - 'a')
                            .filter(t -> t.getSquare().getYNum() == 7 - (disambiguation.charAt(1) - '1'))
                            .toList();
                    if (list1.isEmpty())
                        System.out.println("couldnt find ambigious col move!");
                    else {
                        captureLogic(nextMove);
                        list1.get(0).move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
                    }
                }
            }
            cmd.update();
        }
    }

    private void captureLogic(PGNParser.PGNMove nextMove) {
        if ((nextMove.isCapture && !getSquareArray()[nextMove.to[0]][nextMove.to[1]].isOccupied()) || (!nextMove.isCapture && getSquareArray()[nextMove.to[0]][nextMove.to[1]].isOccupied()))
            throw new InputMismatchException("capturing when not specified");
    }
    public void reactToMouseDragged(MouseEvent e) {
        currX = e.getX() - 24;
        currY = e.getY() - 24;
        view.repaint();
    }

    public void reactToKeyPress(KeyEvent e) {
    }
    public void reactToMouseReleased(MouseEvent e) {
        if (currPiece != null) {
            Square sq = (Square) view.getComponentAt(new Point(e.getX(), e.getY()));
            if (sq == null) {
                currPiece.getSquare().setDisplay(true);
                currPiece = null;
                view.repaint();
                return;
            }
            if (currPiece.getColor() == 0 && whiteTurn) return;
            if (currPiece.getColor() == 1 && !whiteTurn) return;
            List<Square> legalMoves = currPiece.getLegalMoves(this);
            if (legalMoves.contains(sq) && cmd.testMove(currPiece, sq)) {
                sq.setDisplay(true);
                Square fromSquare = currPiece.getSquare();
                boolean wasCapture;
                if (currPiece instanceof Pawn) {
                    if (!sq.isOccupied() && Math.abs(sq.getXNum() - fromSquare.getXNum()) == 1) {
                        wasCapture = true;
                    } else {
                        wasCapture = sq.isOccupied();
                    }
                } else {
                    wasCapture = sq.isOccupied();
                }
                if (currPiece instanceof Pawn) {
                    int destY = sq.getYNum();
                    boolean promotionRank = (currPiece.getColor() == 1 && destY == 0) || (currPiece.getColor() == 0 && destY == 7);
                    if (promotionRank) {
                        String[] options = {"Queen", "Rook", "Bishop", "Knight"};
                        String choice = (String) JOptionPane.showInputDialog(null, "Choose promotion piece:", "Pawn Promotion", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                        currPiece.move(sq);
                        if (choice != null) {
                            Piece newPiece = null;
                            int color = currPiece.getColor();
                            Square promotionSquare = sq;
                            if (color == 1) Wpieces.remove(currPiece);
                            else Bpieces.remove(currPiece);
                            switch (choice) {
                                case "Rook":
                                    newPiece = new Rook(color, promotionSquare, (color == 1 ? RESOURCES_WROOK_PNG : RESOURCES_BROOK_PNG));
                                    break;
                                case "Bishop":
                                    newPiece = new Bishop(color, promotionSquare, (color == 1 ? RESOURCES_WBISHOP_PNG : RESOURCES_BBISHOP_PNG));
                                    break;
                                case "Knight":
                                    newPiece = new Knight(color, promotionSquare, (color == 1 ? RESOURCES_WKNIGHT_PNG : RESOURCES_BKNIGHT_PNG));
                                    break;
                                case "Queen":
                                default:
                                    newPiece = new Queen(color, promotionSquare, (color == 1 ? RESOURCES_WQUEEN_PNG : RESOURCES_WQUEEN_PNG));
                                    break;
                            }
                            promotionSquare.put(newPiece);
                            if (color == 1) Wpieces.add(newPiece);
                            else Bpieces.add(newPiece);
                            currPiece = newPiece;
                        }
                    } else {
                        currPiece.move(sq);
                    }
                } else {
                    currPiece.move(sq);
                }
                cmd.update();
                String algebraic = generateAlgebraicMove(currPiece, fromSquare, sq, wasCapture);
                moveHistory.add(algebraic);
                if (whiteTurn && cmd.blackCheckMated()) {
                    result = "1-0";
                    currPiece = null;
                    view.repaint();
                    view.removeMouseListener(controller);
                    view.removeMouseMotionListener(controller);
                    gameWindow.checkmateOccurred(0);
                } else if (!whiteTurn && cmd.whiteCheckMated()) {
                    result = "0-1";
                    currPiece = null;
                    view.repaint();
                    view.removeMouseListener(controller);
                    view.removeMouseMotionListener(controller);
                    gameWindow.checkmateOccurred(1);
                } else {
                    currPiece = null;
                    whiteTurn = !whiteTurn;
                }
            } else {
                currPiece.getSquare().setDisplay(true);
                currPiece = null;
            }
        }
        view.repaint();
    }

    private String generateAlgebraicMove(Piece movingPiece, Square from, Square to, boolean isCapture) {
        if (movingPiece instanceof King) {
            int fileDiff = to.getXNum() - from.getXNum();
            if (Math.abs(fileDiff) == 2) {
                return (fileDiff == 2) ? "O-O" : "O-O-O";
            }
        }
        StringBuilder sb = new StringBuilder();
        char pieceLetter = 0;
        if (!(movingPiece instanceof Pawn)) {
            if (movingPiece instanceof Knight) pieceLetter = 'N';
            else if (movingPiece instanceof Bishop) pieceLetter = 'B';
            else if (movingPiece instanceof Rook) pieceLetter = 'R';
            else if (movingPiece instanceof Queen) pieceLetter = 'Q';
            else if (movingPiece instanceof King) pieceLetter = 'K';
            sb.append(pieceLetter);
        }
        if (!(movingPiece instanceof Pawn)) {
            List<Piece> sameTypePieces = new ArrayList<>();
            LinkedList<Piece> pieceList = (movingPiece.getColor() == 1) ? Wpieces : Bpieces;
            for (Piece p : pieceList) {
                if (p == movingPiece) continue;
                if (p.getClass().equals(movingPiece.getClass())) {
                    List<Square> legal = p.getLegalMoves(this);
                    if (legal.contains(to)) {
                        sameTypePieces.add(p);
                    }
                }
            }
            if (!sameTypePieces.isEmpty()) {
                boolean needFile = true, needRank = true;
                char fromFile = (char) ('a' + from.getXNum());
                char fromRank = (char) ('1' + (7 - from.getYNum()));
                for (Piece other : sameTypePieces) {
                    if (other.getSquare().getXNum() == from.getXNum()) {
                        needFile = false;
                    }
                    if ((7 - other.getSquare().getYNum()) == (7 - from.getYNum())) {
                        needRank = false;
                    }
                }
                if (needFile) {
                    sb.append(fromFile);
                } else if (needRank) {
                    sb.append(fromRank);
                } else {
                    sb.append(fromFile).append(fromRank);
                }
            }
        }
        if (isCapture) {
            if (movingPiece instanceof Pawn && sb.length() == 0) {
                char fromFile = (char) ('a' + from.getXNum());
                sb.append(fromFile);
            }
            sb.append('x');
        }
        char destFile = (char) ('a' + to.getXNum());
        char destRank = (char) ('1' + (7 - to.getYNum()));
        sb.append(destFile).append(destRank);
        if (movingPiece instanceof Pawn) {
            int rankIdx = to.getYNum();
            if ((movingPiece.getColor() == 1 && rankIdx == 0) || (movingPiece.getColor() == 0 && rankIdx == 7)) {
                sb.append("=Q");
            }
        }
        boolean givesCheck = false;
        boolean givesCheckmate = false;
        if (movingPiece.getColor() == 1) {
            if (cmd.blackCheckMated()) {
                givesCheckmate = true;
            } else if (cmd.blackInCheck()) {
                givesCheck = true;
            }
        } else {
            if (cmd.whiteCheckMated()) {
                givesCheckmate = true;
            } else if (cmd.whiteInCheck()) {
                givesCheck = true;
            }
        }
        if (givesCheckmate) {
            sb.append('#');
        } else if (givesCheck) {
            sb.append('+');
        }
        return sb.toString();
    }

    public String getPGNMovetext() {
        StringBuilder sb = new StringBuilder();
        int moveNum = 1;
        for (int i = 0; i < moveHistory.size(); i += 2) {
            sb.append(moveNum).append(". ");
            sb.append(moveHistory.get(i));
            if (i + 1 < moveHistory.size()) {
                sb.append(" ").append(moveHistory.get(i + 1));
            }
            if (i + 2 < moveHistory.size()) {
                sb.append(" ");
            }
            moveNum++;
        }
        return sb.toString().trim();
    }

    public String getFullPGN() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Event \"?\"]\n");
        sb.append("[Site \"?\"]\n");
        sb.append("[Date \"").append(java.time.LocalDate.now()).append("\"]\n");
        sb.append("[Round \"?\"]\n");
        sb.append("[White \"").append(whitePlayerName != null ? whitePlayerName : "?").append("\"]\n");
        sb.append("[Black \"").append(blackPlayerName != null ? blackPlayerName : "?").append("\"]\n");
        sb.append("[Result \"").append(result).append("\"]\n");
        sb.append("\n");
        sb.append(getPGNMovetext());
        sb.append(" ").append(result);
        return sb.toString();
    }
}

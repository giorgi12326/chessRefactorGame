package org.example.model;

import org.example.controller.Controller;
import org.example.dtos.SquareDto;
import org.example.view.GameWindow;
import org.example.view.View;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.InvalidPropertiesFormatException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@SuppressWarnings("serial")
public class Board implements Serializable {
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
                if (board[i][j].getOccupyingPiece() instanceof King)    System.out.print("K ");
                if (board[i][j].getOccupyingPiece() instanceof Pawn)    System.out.print("P ");
                if (board[i][j].getOccupyingPiece() instanceof Knight)  System.out.print("Z ");
                if (board[i][j].getOccupyingPiece() instanceof Queen)   System.out.print("Q ");
                if (board[i][j].getOccupyingPiece() == null)            System.out.print("0 ");
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
            } catch (NoSuchElementException | InvalidPropertiesFormatException e) {
                moveList = new ArrayList<>();
                shouldTryToCheckParsed = false;
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
        for (int x = 0; x < 8; x++)
            for (int y = 0; y < 8; y++)
                board[x][y] = new Square(this, ((x + y) % 2 == 0 ? 1 : 0), y, x);

        for (int x = 0; x < 8; x++) {
            board[1][x].put(new Pawn(0, board[1][x], RESOURCES_BPAWN_PNG, this));
            board[6][x].put(new Pawn(1, board[6][x], RESOURCES_WPAWN_PNG, this));
        }
        board[7][3].put(new Queen(1, board[7][3], RESOURCES_WQUEEN_PNG));
        board[0][3].put(new Queen(0, board[0][3], RESOURCES_BQUEEN_PNG));
        King bk = new King(0, board[0][4], RESOURCES_BKING_PNG, this);
        King wk = new King(1, board[7][4], RESOURCES_WKING_PNG, this);
        board[0][4].put(bk);
        board[7][4].put(wk);
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

        for (int y = 0; y < 2; y++)
            for (int x = 0; x < 8; x++) {
                Bpieces.add(board[y][x].getOccupyingPiece());
                Wpieces.add(board[7 - y][x].getOccupyingPiece());
            }

        cmd = new CheckmateDetector(this, Wpieces, Bpieces, wk, bk);
        if (gameWindow == null && moveList != null)
            while (!moveList.isEmpty() && moveList.getFirst().to != null)
                reactToMousePress(null);
    }

    public Square[][] getSquareArray() { return board; }
    public boolean getTurn()         { return whiteTurn; }
    public void setCurrPiece(Piece p){ this.currPiece = p; }
    public Piece getCurrPiece()      { return currPiece; }

    public void reactToMousePress(MouseEvent e) {
        if (PGN == null) {
            currX = e.getX() - 24;
            currY = e.getY() - 24;
            Square sq = (Square) view.getComponentAt(new Point(e.getX(), e.getY()));
            if (sq.isOccupied()) {
                currPiece = sq.getOccupyingPiece();
                if ((currPiece.getColor() == 0 && whiteTurn) || (currPiece.getColor() == 1 && !whiteTurn)) return;
                sq.setDisplay(false);
            }
            view.repaint();
        } else {
            elsePart();
        }
    }

    public void reactToMousePressDto(SquareDto e) {
        if (PGN == null) {
            Square sq = board[e.getX()][e.getY()];
            if (sq.isOccupied()) {
                currPiece = sq.getOccupyingPiece();
                if ((currPiece.getColor() == 0 && whiteTurn) || (currPiece.getColor() == 1 && !whiteTurn)) return;
                sq.setDisplay(false);
            }
            view.repaint();
        } else {
            elsePart();
        }
    }

    public void reactToMouseDragged(MouseEvent e) {
        currX = e.getX() - 24;
        currY = e.getY() - 24;
        view.repaint();
    }

    public void reactToMouseReleased(MouseEvent e) {
        Square sq = (Square) view.getComponentAt(new Point(e.getX(), e.getY()));
        if (releasePart(sq)) return;
        view.repaint();
    }

    public void reactToMouseReleasedDto(SquareDto e) {
        Square sq = board[e.getX()][e.getY()];
        if (releasePart(sq)) return;
        view.repaint();
    }

    private boolean releasePart(Square sq) {
        if ((currPiece.getColor() == 0 && whiteTurn) || (currPiece.getColor() == 1 && !whiteTurn)) {
            currPiece.getSquare().setDisplay(true);
            currPiece = null;
            return true;
        }
        List<Square> legalMoves = currPiece.getLegalMoves(this);
        if (legalMoves.contains(sq) && cmd.testMove(currPiece, sq)) {
            boolean wasCapture = sq.isOccupied();
            Square from = currPiece.getSquare();
            sq.setDisplay(true);
            currPiece.move(sq);
            Game.didMoveWentThough = true;
            cmd.update();
            moveHistory.add(generateAlgebraicMove(currPiece, from, sq, wasCapture));
            if (endCheck()) return true;
            currPiece = null;
            whiteTurn = !whiteTurn;
            return true;
        }
        currPiece.getSquare().setDisplay(true);
        currPiece = null;
        return false;
    }

    private void elsePart() {
        if (moveList.isEmpty()) {
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
            if (board[color * 7][4].getOccupyingPiece() instanceof King &&
                    board[color * 7][4].getOccupyingPiece().getColor() == color)
                board[color * 7][4].getOccupyingPiece().move(board[color * 7][6]);
            return;
        } else if (nextMove.isCastleQueenSide) {
            if (board[color * 7][4].getOccupyingPiece() instanceof King &&
                    board[color * 7][4].getOccupyingPiece().getColor() == color)
                board[color * 7][4].getOccupyingPiece().move(board[color * 7][2]);
            return;
        }
        List<Piece> list = nextMove.isWhite
                ? cmd.wMoves.get(board[nextMove.to[0]][nextMove.to[1]])
                .stream().filter(t -> nextMove.piece.isInstance(t)).toList()
                : cmd.bMoves.get(board[nextMove.to[0]][nextMove.to[1]])
                .stream().filter(t -> nextMove.piece.isInstance(t)).toList();

        if (list.isEmpty()) {
            gameWindow.incorrectPgnMessage("Error, That piece cant move to specified Spot");
        } else if (list.size() == 1) {
            captureLogic(nextMove);
            list.get(0).move(board[nextMove.to[0]][nextMove.to[1]]);
        } else {
            String dis = nextMove.disambiguation;
            if (dis.isEmpty()) {
                gameWindow.incorrectPgnMessage("cant resolve ambiguity");
            } else if (dis.length() == 1) {
                char c = dis.charAt(0);
                List<Piece> list1 = c >= 'a' && c <= 'h'
                        ? list.stream().filter(t -> t.getSquare().getXNum() == c - 'a').toList()
                        : list.stream().filter(t -> t.getSquare().getYNum() == 7 - (c - '1')).toList();
                if (list1.isEmpty()) {
                    gameWindow.incorrectPgnMessage("Error, cant resolve ambiguity on " + c);
                } else {
                    captureLogic(nextMove);
                    list1.get(0).move(board[nextMove.to[0]][nextMove.to[1]]);
                }
            } else {
                List<Piece> list1 = list.stream()
                        .filter(t -> t.getSquare().getXNum() == dis.charAt(0) - 'a')
                        .filter(t -> t.getSquare().getYNum() == 7 - (dis.charAt(1) - '1'))
                        .toList();
                if (list1.isEmpty()) {
                    System.out.println("couldnt find ambiguous move!");
                } else {
                    captureLogic(nextMove);
                    list1.get(0).move(board[nextMove.to[0]][nextMove.to[1]]);
                }
            }
        }
        cmd.update();
    }

    private void captureLogic(PGNParser.PGNMove nextMove) {
        if ((nextMove.isCapture && !board[nextMove.to[0]][nextMove.to[1]].isOccupied()) ||
                (!nextMove.isCapture && board[nextMove.to[0]][nextMove.to[1]].isOccupied()))
            throw new InputMismatchException("capturing when not specified");
    }

    private boolean endCheck() {
        if (whiteTurn && cmd.blackCheckMated()) {
            result = "1-0";
            gameWindow.checkmateOccurred(0);
            return true;
        }
        if (!whiteTurn && cmd.whiteCheckMated()) {
            result = "0-1";
            gameWindow.checkmateOccurred(1);
            return true;
        }
        return false;
    }

    private String generateAlgebraicMove(Piece movingPiece, Square from, Square to, boolean isCapture) {
        if (movingPiece instanceof King) {
            int diff = to.getXNum() - from.getXNum();
            if (Math.abs(diff) == 2) return diff == 2 ? "O-O" : "O-O-O";
        }
        StringBuilder sb = new StringBuilder();
        if (!(movingPiece instanceof Pawn)) {
            char c = movingPiece instanceof Knight ? 'N'
                    : movingPiece instanceof Bishop ? 'B'
                    : movingPiece instanceof Rook   ? 'R'
                    : movingPiece instanceof Queen  ? 'Q' : 'K';
            sb.append(c);
            List<Piece> same = new ArrayList<>();
            for (Piece p : (movingPiece.getColor() == 1 ? Wpieces : Bpieces))
                if (p != movingPiece && p.getClass() == movingPiece.getClass() && p.getLegalMoves(this).contains(to))
                    same.add(p);
            if (!same.isEmpty()) {
                char f = (char) ('a' + from.getXNum());
                char r = (char) ('1' + (7 - from.getYNum()));
                boolean needFile = true, needRank = true;
                for (Piece p : same) {
                    if (p.getSquare().getXNum() == from.getXNum()) needFile = false;
                    if ((7 - p.getSquare().getYNum()) == (7 - from.getYNum())) needRank = false;
                }
                if (needFile) sb.append(f);
                else if (needRank) sb.append(r);
                else sb.append(f).append(r);
            }
        }
        if (isCapture) {
            if (movingPiece instanceof Pawn && sb.isEmpty()) sb.append((char) ('a' + from.getXNum()));
            sb.append('x');
        }
        sb.append((char) ('a' + to.getXNum()))
                .append((char) ('1' + (7 - to.getYNum())));
        if (movingPiece instanceof Pawn) {
            int ry = to.getYNum();
            if ((movingPiece.getColor() == 1 && ry == 0) || (movingPiece.getColor() == 0 && ry == 7))
                sb.append("=Q");
        }
        boolean check = movingPiece.getColor() == 1 ? cmd.blackInCheck() : cmd.whiteInCheck();
        boolean mate  = movingPiece.getColor() == 1 ? cmd.blackCheckMated() : cmd.whiteCheckMated();
        if (mate) sb.append('#');
        else if (check) sb.append('+');
        return sb.toString();
    }

    public String getPGNMovetext() {
        StringBuilder sb = new StringBuilder();
        int num = 1;
        for (int i = 0; i < moveHistory.size(); i += 2) {
            sb.append(num++).append(". ").append(moveHistory.get(i));
            if (i + 1 < moveHistory.size()) sb.append(" ").append(moveHistory.get(i + 1));
            if (i + 2 < moveHistory.size()) sb.append(" ");
        }
        return sb.toString().trim();
    }

    public String getFullPGN() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Event \"?\"]\n")
                .append("[Site \"?\"]\n")
                .append("[Date \"").append(LocalDate.now()).append("\"]\n")
                .append("[Round \"?\"]\n")
                .append("[White \"").append(whitePlayerName != null ? whitePlayerName : "?").append("\"]\n")
                .append("[Black \"").append(blackPlayerName != null ? blackPlayerName : "?").append("\"]\n")
                .append("[Result \"").append(result).append("\"]\n\n")
                .append(getPGNMovetext()).append(" ").append(result);
        return sb.toString();
    }
}

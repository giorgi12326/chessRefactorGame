package org.example.model;

import org.example.controller.Controller;
import org.example.dtos.PGNMove;
import org.example.dtos.SquareDto;
import org.example.view.GameWindow;
import org.example.view.View;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.*;

@SuppressWarnings("serial")
public class Board implements Serializable {
	// Resource location constants for piece images
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

    // Logical and graphical representations of board
	private final Square[][] board;
    private final GameWindow gameWindow;
    // List of pieces and whether they are movable
    public final LinkedList<Piece> Bpieces;
    public final LinkedList<Piece> Wpieces;

    private boolean whiteTurn;

    private Piece currPiece;
    public int currX;
    public int currY;
    
    public CheckmateDetector cmd;
    public  Controller controller;
    public  View view;

    List<PGNMove> moveList;
    public String whiteName;
    public String blackName;
    public static String castleString;

    boolean isValid = true;



    @Override
    public String toString() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j].getOccupyingPiece() instanceof King)
                    System.out.print( "K ");
                if(board[i][j].getOccupyingPiece() instanceof Pawn)
                    System.out.print( "P ");
                if(board[i][j].getOccupyingPiece() instanceof Knight)
                    System.out.print( "K ");
                if(board[i][j].getOccupyingPiece() instanceof Bishop)
                    System.out.print( "B ");
                if(board[i][j].getOccupyingPiece() instanceof Queen)
                    System.out.print( "Q ");
                if(board[i][j].getOccupyingPiece() instanceof Rook)
                    System.out.print( "R ");
                if(board[i][j].getOccupyingPiece() ==null)
                    System.out.print("0 ");
            }
            System.out.println();
        }
        return "Board{" +
                '}';
    }

    public Board(GameWindow gameWindow,String PGN) {
        this.gameWindow = gameWindow;
        this.PGN = PGN;
        boolean shouldTryToCheckParsed = true;
        if(PGN != null) {
            try {
                moveList = PGNParser.parseInList(PGNParser.parsePGN(PGN).get(0));
                System.out.println(moveList);
            }
            catch(NoSuchElementException e){
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

        if(shouldTryToCheckParsed) {

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
                    getSquareArray()[x][y] = new Square(this, 1, y, x);//TODO
                } else {
                    getSquareArray()[x][y] = new Square(this, 0, y, x);
                }
            }
        }
        for (int x = 0; x < 8; x++) {
            board[1][x].put(new Pawn(0, board[1][x], RESOURCES_BPAWN_PNG,this));
            board[6][x].put(new Pawn(1, board[6][x], RESOURCES_WPAWN_PNG,this));
        }

        board[7][3].put(new Queen(1, board[7][3], RESOURCES_WQUEEN_PNG));
        board[0][3].put(new Queen(0, board[0][3], RESOURCES_BQUEEN_PNG));

        King bk = new King(0, board[0][4], RESOURCES_BKING_PNG,this);
        King wk = new King(1, board[7][4], RESOURCES_WKING_PNG,this);
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


        for(int y = 0; y < 2; y++) {
            for (int x = 0; x < 8; x++) {
                Bpieces.add(board[y][x].getOccupyingPiece());
                Wpieces.add(board[7-y][x].getOccupyingPiece());
            }
        }
        
        cmd = new CheckmateDetector(this, Wpieces, Bpieces, wk, bk);
        if(gameWindow == null && moveList != null) {

            while (!moveList.isEmpty() &&
                    moveList.get(0).to != null) {
                if(!elsePart2())
                    isValid = false;

            }
            if(isValid)
            System.out.println("valid");
            else System.out.println("NOVALID");
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
        if(PGN == null) {
            currX = e.getX() - 24;
            currY = e.getY() - 24;

            Square sq = (Square) view.getComponentAt(new Point(e.getX(), e.getY()));

            System.out.println(sq.getXNum() + " " + sq.getYNum());
            if (sq.isOccupied()) {
                currPiece = sq.getOccupyingPiece();
                if (currPiece.getColor() == 0 && whiteTurn)
                    return;
                if (currPiece.getColor() == 1 && !whiteTurn)
                    return;
                sq.setDisplay(false);
            }
            view.repaint();
            System.out.println(currPiece);
        }
        else{
            elsePart2();
        }
    }

    public SquareDto[] elsePart(PGNMove nextMove) {
        if (nextMove == null) {
            return null;
        }

        int color = nextMove.isWhite ? 1 : 0;

        if (nextMove.isCastleKingSide) {
            if (getSquareArray()[color * 7][4].isOccupied() &&
                    getSquareArray()[color * 7][4].getOccupyingPiece() instanceof King &&
                    getSquareArray()[color * 7][4].getOccupyingPiece().getColor() == color)
                getSquareArray()[color * 7][4].getOccupyingPiece().move(getSquareArray()[color * 7][6]);
            cmd.update();
            castleString = "O-O";
            return new SquareDto[]{new SquareDto(4,color*7), new SquareDto(6,color*7)};

        } else if (nextMove.isCastleQueenSide) {
            if (getSquareArray()[color * 7][4].isOccupied() &&
                    getSquareArray()[color * 7][4].getOccupyingPiece() instanceof King &&
                    getSquareArray()[color * 7][4].getOccupyingPiece().getColor() == color) {
                getSquareArray()[color * 7][4].getOccupyingPiece().move(getSquareArray()[color * 7][2]);
            }
            cmd.update();
            castleString = "O-O-O";
            return new SquareDto[]{new SquareDto(4,color*7), new SquareDto(2,color*7)};
        }
        List<Piece> list;
        if (nextMove.isWhite) {
            list = cmd.wMoves.get(getSquareArray()[nextMove.to[0]][nextMove.to[1]]).stream().filter(t -> PGNParser.parsePiece(nextMove.piece).isInstance(t)).toList();
        } else {
            list = cmd.bMoves.get(getSquareArray()[nextMove.to[0]][nextMove.to[1]]).stream().filter(t -> PGNParser.parsePiece(nextMove.piece).isInstance(t)).toList();
        }
        System.out.println(this);
        int size = list.size();
        if (size == 0) {
            System.out.println("NOT VALID");
            cmd.update();
            return null;
        } else if (size == 1) {
            captureLogic(nextMove);
            Piece first = list.get(0);
            SquareDto[] squareDtos = {new SquareDto(first.getSquare().getXNum(), first.getSquare().getYNum()), new SquareDto(nextMove.to[1], nextMove.to[0])};
            first.move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
            cmd.update();
            return squareDtos;
        } else {
            String disambiguation = nextMove.disambiguation;
            if (disambiguation.isEmpty()) {
                System.out.println("NOT VALID");
                return null;
            } else if (disambiguation.length() == 1) {
                char c = disambiguation.charAt(0);
                if (c >= 'a' && c <= 'h') {

                    List<Piece> list1 = list.stream().filter(t -> t.getSquare().getXNum() == c - 'a').toList();
                    if (list1.isEmpty()) {
                        System.out.println("NOT VALID");
                        return null;

                    } else {
                        System.out.println("1231231231ffgf");
                        captureLogic(nextMove);
                        Piece first = list1.get(0);
                        SquareDto[] squareDtos = {new SquareDto(first.getSquare().getXNum(), first.getSquare().getYNum()), new SquareDto(nextMove.to[1], nextMove.to[0])};
                        first.move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
                        cmd.update();
                        return squareDtos;
                    }
                } else if (c >= '1' && c <= '8') {
                    List<Piece> list1 = list.stream().filter(t -> t.getSquare().getYNum() == 7 - (c - '1')).toList();
                    System.out.println(list1 + " asldkjasldkasjldk");
                    if (list1.isEmpty()) {
                        System.out.println("NOT VALID");
                        return null;
                    }
                    else {
                        captureLogic(nextMove);
                        Piece first = list1.get(0);
                        SquareDto[] squareDtos = {new SquareDto(first.getSquare().getXNum(), first.getSquare().getYNum()), new SquareDto(nextMove.to[1], nextMove.to[0])};
                        first.move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
                        cmd.update();
                        return squareDtos;
                    }
                }
            } else {
                List<Piece> list1 = list.stream()
                        .filter(t -> t.getSquare().getXNum() == disambiguation.charAt(0) - 'a')
                        .filter(t -> t.getSquare().getYNum() == 7 - (disambiguation.charAt(1) - '1'))
                        .toList();
                if (list1.isEmpty()) {
                    System.out.println("NOT VALID");

                    return null;                    }
                else {
                    captureLogic(nextMove);
                    Piece first = list1.get(0);
                    SquareDto[] squareDtos = {new SquareDto(first.getSquare().getXNum(), first.getSquare().getYNum()), new SquareDto(nextMove.to[1], nextMove.to[0])};
                    first.move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
                    cmd.update();
                    return squareDtos;
                }

            }
        }
        cmd.update();

        return null;
    }
    public boolean elsePart2() {

        while(!moveList.isEmpty()) {

            PGNMove nextMove = moveList.remove(0);
            if (nextMove == null) {
                break;

            }

            int color = nextMove.isWhite ? 1 : 0;

            if (nextMove.isCastleKingSide) {
                if (getSquareArray()[color * 7][4].isOccupied() &&
                        getSquareArray()[color * 7][4].getOccupyingPiece() instanceof King &&
                        getSquareArray()[color * 7][4].getOccupyingPiece().getColor() == color)
                    getSquareArray()[color * 7][4].getOccupyingPiece().move(getSquareArray()[color * 7][6]);
                continue;

            } else if (nextMove.isCastleQueenSide) {
                if (getSquareArray()[color * 7][4].isOccupied() &&
                        getSquareArray()[color * 7][4].getOccupyingPiece() instanceof King &&
                        getSquareArray()[color * 7][4].getOccupyingPiece().getColor() == color) {
                    getSquareArray()[color * 7][4].getOccupyingPiece().move(getSquareArray()[color * 7][2]);
                }
                continue;
            }
            List<Piece> list;

            if (nextMove.isWhite) {
                list = cmd.wMoves.get(getSquareArray()[nextMove.to[0]][nextMove.to[1]]).stream().filter(t -> PGNParser.parsePiece(nextMove.piece).isInstance(t)).toList();
            } else {
                list = cmd.bMoves.get(getSquareArray()[nextMove.to[0]][nextMove.to[1]]).stream().filter(t -> PGNParser.parsePiece(nextMove.piece).isInstance(t)).toList();
            }
            int size = list.size();
            if (size == 0) {
                System.out.println("NOT VALID");
                return false;
            } else if (size == 1) {
                try {
                    captureLogic(nextMove);
                }
                catch (Exception e){
                    return false;
                }                list.get(0).move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
            } else {
                String disambiguation = nextMove.disambiguation;
                if (disambiguation.isEmpty()) {
                    System.out.println("NOT VALID");

                    return false;
                } else if (disambiguation.length() == 1) {
                    char c = disambiguation.charAt(0);
                    if (c >= 'a' && c <= 'h') {

                        List<Piece> list1 = list.stream().filter(t -> t.getSquare().getXNum() == c - 'a').toList();
                        if (list1.isEmpty()) {
                            System.out.println("NOT VALID");

                            return false;

                        } else {
                            try {
                                captureLogic(nextMove);
                            }
                            catch (Exception e){
                                return false;
                            }                            list1.get(0).move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
                        }
                    } else if (c >= '1' && c <= '8') {
                        List<Piece> list1 = list.stream().filter(t -> t.getSquare().getYNum() == 7 - (c - '1')).toList();
                        if (list1.isEmpty()) {
                            System.out.println("NOT VALID");

                            return false;
                        }
                        else {
                            try {
                                captureLogic(nextMove);
                            }
                            catch (Exception e){
                                return false;
                            }
                            list1.get(0).move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
                        }
                    }
                } else {
                    List<Piece> list1 = list.stream()
                            .filter(t -> t.getSquare().getXNum() == disambiguation.charAt(0) - 'a')
                            .filter(t -> t.getSquare().getYNum() == 7 - (disambiguation.charAt(1) - '1'))
                            .toList();
                    if (list1.isEmpty()) {
                        System.out.println("NOT VALID");
                        return false;
                    }
                    else {
                        try {
                            captureLogic(nextMove);
                        }
                        catch (Exception e){
                            return false;
                        }
                        list1.get(0).move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);
                    }

                }
            }
            cmd.update();
        }
        return true;

    }

    private void captureLogic(PGNMove nextMove) {
        if((nextMove.isCapture && !getSquareArray()[nextMove.to[0]][nextMove.to[1]].isOccupied())||
        (!nextMove.isCapture && getSquareArray()[nextMove.to[0]][nextMove.to[1]].isOccupied()))
            throw new InputMismatchException("capturing when not specified");
    }

    public void reactToMouseReleased(MouseEvent e) {
        System.out.println(currPiece + " currpciec");
        if (currPiece != null) {

            Square sq = (Square) view.getComponentAt(new Point(e.getX(), e.getY()));
            System.out.println(sq.getXNum() + " " + sq.getYNum());

            if (releasePart(sq)) return;
        }

//        drawAttackSpots();

        view.repaint();
    }
    public void reactToMouseReleasedDto(SquareDto e) {
        if (currPiece != null) {

            Square sq = board[e.getX()][e.getY()];

            boolean b = releasePart(sq);
            System.out.println("isvalid" + b);
            if (b) return;
        }

//        drawAttackSpots();

        view.repaint();
    }

    private boolean releasePart(Square sq) {
        if (currPiece.getColor() == 0 && whiteTurn)
            return true;
        if (currPiece.getColor() == 1 && !whiteTurn)
            return true;
        List<Square> legalMoves = currPiece.getLegalMoves(this);
        System.out.println("TESTING");
        if (legalMoves.contains(sq)
//                    && movable.contains(sq)
                && cmd.testMove(currPiece, sq)) {
            System.out.println("GOOD");

            System.out.println();
            sq.setDisplay(true);
            currPiece.move(sq);

            Game.didMoveWentThough = true;

            cmd.update();

            if (whiteTurn && cmd.blackCheckMated()) {
                currPiece = null;
                view.repaint();
                view.removeMouseListener(controller);
                view.removeMouseMotionListener(controller);
                gameWindow.checkmateOccurred(0);
            } else if (!whiteTurn && cmd.whiteCheckMated()) {
                currPiece = null;
                view.repaint();
                view.removeMouseListener(controller);
                view.removeMouseMotionListener(controller);
                gameWindow.checkmateOccurred(1);
            } else {
                currPiece = null;
                whiteTurn = !whiteTurn;
//                    movable = cmd.getAllowableSquares(whiteTurn);
            }

        } else {
            currPiece.getSquare().setDisplay(true);
            currPiece = null;
        }
        return false;
    }

    private void drawAttackSpots() {
        int[][] wAttacks = new int[8][8];
        for(Square square: cmd.wMoves.keySet()){
            if(!cmd.wMoves.get(square).isEmpty()){
                wAttacks[square.getYNum()][square.getXNum()] = 1;
            }
        }
        int[][] bAttacks = new int[8][8];
        for(Square square: cmd.bMoves.keySet()){
            if(!cmd.bMoves.get(square).isEmpty()) {
                bAttacks[square.getYNum()][square.getXNum()] = 1;
            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                System.out.print(wAttacks[i][j]);
            }
            System.out.print("     ");
            for (int j = 0; j < 8; j++) {
                System.out.print(bAttacks[i][j]);
            }
            System.out.println();
        }
    }

    public void reactToMouseDragged(MouseEvent e) {
        currX = e.getX() - 24;
        currY = e.getY() - 24;

        view.repaint();
    }


    public void reactToKeyPress(KeyEvent e) {
    }

    public String generateAlgebraicMove(Piece movingPiece, Square from, Square to, boolean isCapture) {
        if(movingPiece == null)
            return "NoPIECE";
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
}
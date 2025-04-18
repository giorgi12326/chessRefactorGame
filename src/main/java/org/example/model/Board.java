package org.example.model;

import org.example.controller.Controller;
import org.example.view.GameWindow;
import org.example.view.View;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class Board  {
	// Resource location constants for piece images
    private static final String RESOURCES_WBISHOP_PNG = "/wbishop.png";
	private static final String RESOURCES_BBISHOP_PNG = "/bbishop.png";
	private static final String RESOURCES_WKNIGHT_PNG = "/wknight.png";
	private static final String RESOURCES_BKNIGHT_PNG = "/bknight.png";
	private static final String RESOURCES_WROOK_PNG = "/wrook.png";
	private static final String RESOURCES_BROOK_PNG = "/brook.png";
	 static final String RESOURCES_WKING_PNG = "/wking.png";
	 static final String RESOURCES_BKING_PNG = "/bking.png";
	private static final String RESOURCES_BQUEEN_PNG = "/bqueen.png";
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
    public final Controller controller;
    public  final View view;

    List<PGNParser.PGNMove> moveList;

    @Override
    public String toString() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(board[i][j].getOccupyingPiece() instanceof King)
                    System.out.print( "K ");
                if(board[i][j].getOccupyingPiece() instanceof Pawn)
                    System.out.print( "P ");
                if(board[i][j].getOccupyingPiece() instanceof Knight)
                    System.out.print( "Z ");
                if(board[i][j].getOccupyingPiece() instanceof Queen)
                    System.out.print( "Q ");
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
        if(PGN != null)
            moveList = PGNParser.parsePGN(PGN);

        board = new Square[8][8];
        Bpieces = new LinkedList<>();
        Wpieces = new LinkedList<>();

        initializePieces();

        whiteTurn = true;
        controller = new Controller(this);
        view = new View(this);

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

            if (sq.isOccupied()) {
                currPiece = sq.getOccupyingPiece();
                if (currPiece.getColor() == 0 && whiteTurn)
                    return;
                if (currPiece.getColor() == 1 && !whiteTurn)
                    return;
                sq.setDisplay(false);
            }
            view.repaint();
        }
        else{
            PGNParser.PGNMove nextMove = moveList.removeFirst();
            int color = nextMove.isWhite?1:0;
            System.out.println(color);

            if(nextMove.isCastleKingSide){
                if(getSquareArray()[color*7][4].isOccupied() &&
                        getSquareArray()[color*7][4].getOccupyingPiece() instanceof King &&
                        getSquareArray()[color*7][4].getOccupyingPiece().getColor() == color)
                    getSquareArray()[color*7][4].getOccupyingPiece().move(getSquareArray()[color*7][6]);
                return;

            }
            else if(nextMove.isCastleQueenSide){
                if(getSquareArray()[color*7][4].isOccupied() &&
                        getSquareArray()[color*7][4].getOccupyingPiece() instanceof King &&
                        getSquareArray()[color*7][4].getOccupyingPiece().getColor() == color) {
                    getSquareArray()[color * 7][4].getOccupyingPiece().move(getSquareArray()[color * 7][2]);
                }
                return;
            }
            List<Piece> list;
            if(nextMove.isWhite) {
                 list = cmd.wMoves.get(getSquareArray()[nextMove.to[0]][nextMove.to[1]]).stream().filter(t -> nextMove.piece.isInstance(t)).toList();
            }
            else{
                list = cmd.bMoves.get(getSquareArray()[nextMove.to[0]][nextMove.to[1]]).stream().filter(t -> nextMove.piece.isInstance(t)).toList();

            }
            int size = list.size();
            if(size == 0){
                System.out.println("count find move!");

            }
            else if(size == 1)
                list.getFirst().move(getSquareArray()[nextMove.to[0]][nextMove.to[1]]);


            cmd.update();
        }
    }


    public void reactToMouseReleased(MouseEvent e) {


        if (currPiece != null) {
            Square sq;
            if(PGN == null) {
                sq = (Square) view.getComponentAt(new Point(e.getX(), e.getY()));
                if (currPiece.getColor() == 0 && whiteTurn)
                    return;
                if (currPiece.getColor() == 1 && !whiteTurn)
                    return;
            }
            else{
                System.out.println("asfghjk");
                sq = getSquareArray()[4][0];
            }
            List<Square> legalMoves = currPiece.getLegalMoves(this);


//            List<Square> movable = cmd.getAllowableSquares(whiteTurn);


            if (legalMoves.contains(sq)
//                    && movable.contains(sq)
                    && cmd.testMove(currPiece, sq)) {
                sq.setDisplay(true);
                currPiece.move(sq);

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
        }

//        drawAttackSpots();



        view.repaint();
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
        if(e.getExtendedKeyCode() == KeyEvent.VK_LEFT){
        }
    }
}
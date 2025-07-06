package org.example.model;

import org.example.dtos.Message;
import org.example.dtos.PGNMove;
import org.example.dtos.SquareDto;
import org.example.view.GameWindow;
import org.example.view.StartMenu;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Game {
    public static GameWindow gameWindow;
    public static StartMenu doRun;
    static Board board;
    static ObjectInputStream in;
    static ObjectOutputStream out;
    public static boolean didMoveWentThough = false;
    
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server listening...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            board = new Board(null, null);

            while (true) {
                try {
                    if(in.readObject() instanceof Message obj) {
                        System.out.println("read");
                        if (obj.type.equals("mouseRelease")) {
                            SquareDto[] payload = (SquareDto[]) obj.getPayload();
                            Square fromSquare = board.getSquareArray()[payload[0].getX()][payload[0].getY()];
                            Square toSquare = board.getSquareArray()[payload[1].getX()][payload[1].getY()];

                            String s = board.generateAlgebraicMove(fromSquare.getOccupyingPiece(), fromSquare, toSquare, toSquare.isOccupied());
                            System.out.println(s);

                            board.setCurrPiece(board.getSquareArray()[payload[0].getX()][payload[0].getY()].getOccupyingPiece());
                            board.reactToMouseReleasedDto(payload[1]);


                            sendBoardToClient();

                        }
                        if (obj.type.equals("pgn")){
                            SquareDto[] squareDtos = board.elsePart((PGNMove) obj.getPayload());
                            out.writeObject(squareDtos);
                            out.flush();
                        }
                    }

                    didMoveWentThough = false;

                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static void sendBoardToClient() throws IOException {
//        Square[][] squareArray = board.getSquareArray();
//        SquareDto[][] dtos = new SquareDto[8][8];
//        for (int i = 0; i < 8; i++) {
//            for (int j = 0; j < 8; j++) {
//                Piece occupyingPiece = squareArray[i][j].getOccupyingPiece();
//                if(occupyingPiece != null)
//                    dtos[i][j] = new SquareDto(squareArray[i][j].getXNum(),'A',
//                        PGNParser.getPieceChar(occupyingPiece.getClass()),
//                            occupyingPiece.getColor());
//            }
//        }

        out.writeObject(didMoveWentThough);
        out.flush();
        System.out.println("board sent!");

    }
}

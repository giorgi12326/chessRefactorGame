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
import java.util.List;

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
                        if (obj.type.equals("mouseRelease")) {
                            SquareDto[] payload = (SquareDto[]) obj.getPayload();
                            Square fromSquare = board.getSquareArray()[payload[0].getX()][payload[0].getY()];
                            Square toSquare = board.getSquareArray()[payload[1].getX()][payload[1].getY()];

                            String s = board.generateAlgebraicMove(fromSquare.getOccupyingPiece(), fromSquare, toSquare, toSquare.isOccupied());

                            board.setCurrPiece(board.getSquareArray()[payload[0].getX()][payload[0].getY()].getOccupyingPiece());
                            board.reactToMouseReleasedDto(payload[1]);

                            if(Game.didMoveWentThough){
                                out.writeObject(new Message("pgnMove",s));
                                out.flush();
                            }
                            else
                                sendBoardToClient();

                        }
                        if (obj.type.equals("pgn")){
                            SquareDto[] squareDtos = board.elsePart((PGNMove) obj.getPayload());
                            System.out.println("kaka");
                            if(Board.castleString != null && (Board.castleString.equals("O-O") || Board.castleString.equals("O-O-O"))   ){
                                out.writeObject(new Message("castleMove",Board.castleString));
                                out.flush();
                                Board.castleString = null;
                            }else {
                                out.writeObject(new Message("squareArr",squareDtos));
                                out.flush();
                            }
                        }
                        if(obj.type.equals("Multiple")){
                            List<String> payload = (List<String>) (obj.getPayload());
                            for(String str: payload) {
                                board = new Board(null, str);
                                board.elsePart2();
                                out.writeObject(new Message("valid?", board.isValid));

                                System.out.println("senenen");
                            }
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
        out.writeObject(didMoveWentThough);
        out.flush();
        System.out.println("board sent!");

    }
}

package org.example.model;

import org.example.dtos.Message;
import org.example.dtos.SquareDto;
import org.example.view.GameWindow;
import org.example.view.StartMenu;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
        board = new Board(null,null);

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server listening...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            while (true) {
                try {
                    sendBoardToClient();

                    didMoveWentThough = false;

                    if(in.readObject() instanceof Message obj) {
                        if (obj.type.equals("mouseRelease")) {

                            SquareDto[] payload = (SquareDto[]) obj.getPayload();
                            board.setCurrPiece(board.getSquareArray()[payload[0].getX()][payload[0].getY()].getOccupyingPiece());
                            board.reactToMouseReleasedDto(payload[1]);
                        }
                    }
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

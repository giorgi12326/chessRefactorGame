package org.example.model;

import org.example.dtos.Message;
import org.example.dtos.SquareDto;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Game {
    public static Board board;
    public static boolean didMoveWentThough = false;
    static ObjectInputStream in;
    static ObjectOutputStream out;

    public static void main(String[] args) {
        board = new Board(null, null);

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server listening...");
            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            while (true) {
                sendBoardToClient();
                didMoveWentThough = false;

                Object recv = in.readObject();
                if (!(recv instanceof Message)) continue;
                Message msg = (Message) recv;

                switch (msg.getType()) {
                    case "mousePress" -> {
                        SquareDto dto = ((SquareDto[]) msg.getPayload())[0];
                        board.reactToMousePressDto(dto);
                    }
                    case "mouseRelease" -> {
                        SquareDto dto = ((SquareDto[]) msg.getPayload())[1];
                        board.reactToMouseReleasedDto(dto);
                    }
                    case "requestPGN" -> {
                        // Client asked for the PGN text:
                        String full = board.getFullPGN();
                        out.writeObject(new Message("deliverPGN", full));
                        out.flush();
                    }
                }
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected.");
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

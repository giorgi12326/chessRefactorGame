package org.example.model;

import org.example.dtos.SquareDto;
import org.example.view.GameWindow;
import org.example.view.StartMenu;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Game implements Runnable {
    public static GameWindow gameWindow;

    public void run() {

        StartMenu doRun = new StartMenu();
        if(gameWindow != null)
            gameWindow = doRun.gameWindow;
        else
            System.out.println("nogamewondoe");
        SwingUtilities.invokeLater(doRun);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server listening...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            while (true) {
                try {
                    Object obj = in.readObject();
                    System.out.println(obj);

                    String reply = new String("Server");
                    Square[][] squareArray = gameWindow.board.getSquareArray();
                    SquareDto[][] dtos = new SquareDto[8][8];
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            dtos[i][j] = new SquareDto(squareArray[i][j].getXNum(),'A','P');
                        }
                    }

                    System.out.println(dtos);
                    out.writeObject(dtos);
                    out.flush();

                } catch (EOFException e) {
                    System.out.println("Client disconnected.");
                    break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

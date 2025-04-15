package org.example.view;

import org.example.Board;
import org.example.Square;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel {
    private final Board board;

    public View(Board board){
        this.board = board;
        this.setPreferredSize(new Dimension(400, 400));
        this.setMaximumSize(new Dimension(400, 400));
        this.setMinimumSize(this.getPreferredSize());
        this.setSize(new Dimension(400, 400));
        setLayout(new GridLayout(8, 8, 0, 0));


        addMouseMotionListener(board.controller);
        addMouseListener(board.controller);


        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int xMod = x % 2;
                int yMod = y % 2;

                if ((xMod == 0 && yMod == 0) || (xMod == 1 && yMod == 1)) {
                    this.add(board.getSquareArray()[x][y]);
                } else {
                    this.add(board.getSquareArray()[x][y]);
                }
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        // super.paintComponent(g);

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Square sq = board.getSquareArray()[y][x];
                sq.paintComponent(g);
            }
        }

        if (board.getCurrPiece() != null) {
            if ((board.getCurrPiece().getColor() == 1 && board.getTurn())
                    || (board.getCurrPiece().getColor() == 0 && !board.getTurn())) {
                final Image i = board.getCurrPiece().getImage();
                g.drawImage(i, board.currX, board.currY, null);
            }
        }
    }


}

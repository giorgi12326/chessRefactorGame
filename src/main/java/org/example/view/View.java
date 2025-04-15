package org.example.view;

import org.example.Board;
import org.example.Piece;
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
        super.paintComponent(g);

        int tileSize = 50;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Square sq = board.getSquareArray()[y][x];

                if (sq.getColor() == 1) {
                    g.setColor(new Color(221,192,127));
                } else {
                    g.setColor(new Color(101,67,33));
                }

                int px = x * tileSize;
                int py = y * tileSize;

                g.fillRect(px, py, tileSize, tileSize);

                Piece piece = sq.getOccupyingPiece();
                if (piece != null && sq.dispPiece) {
                    Image img = piece.getImage();
                    g.drawImage(img, px, py, tileSize, tileSize, null);
                }
            }
        }

        if (board.getCurrPiece() != null) {
            Piece curr = board.getCurrPiece();
            if ((curr.getColor() == 1 && board.getTurn()) ||
                    (curr.getColor() == 0 && !board.getTurn())) {
                Image img = curr.getImage();
                g.drawImage(img, board.currX, board.currY, tileSize, tileSize, null);
            }
        }
    }
}

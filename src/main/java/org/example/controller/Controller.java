package org.example.controller;

import org.example.Board;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Controller implements MouseListener, MouseMotionListener {
    private final Board board;

    public Controller(Board board){
        this.board = board;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        board.reactToMousePress(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        board.reactToMouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        board.reactToMouseDragged(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}

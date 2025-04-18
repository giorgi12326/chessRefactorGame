package org.example.controller;

import org.example.model.Board;

import java.awt.event.*;

public class Controller implements MouseListener, KeyListener, MouseMotionListener {
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

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        board.reactToKeyPress(e);

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

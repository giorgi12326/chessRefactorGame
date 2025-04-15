package org.example;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.*;

@SuppressWarnings("serial")
public class Square extends JComponent {
    private Board b;
    
    private final int color;
    private Piece occupyingPiece;
    public boolean dispPiece;
    
    private int xNum;//width
    private int yNum;//height
    
    public Square(Board b, int c, int xNum, int yNum) {
        
        this.b = b;
        this.color = c;
        this.dispPiece = true;
        this.xNum = xNum;
        this.yNum = yNum;
        
        
        this.setBorder(BorderFactory.createEmptyBorder());
    }
    
    public int getColor() {
        return this.color;
    }
    
    public Piece getOccupyingPiece() {
        return occupyingPiece;
    }
    
    public boolean isOccupied() {
        return (this.occupyingPiece != null);
    }
    
    public int getXNum() {
        return this.xNum;
    }
    
    public int getYNum() {
        return this.yNum;
    }
    
    public void setDisplay(boolean v) {
        this.dispPiece = v;
    }

    public void put(Piece p) {
        this.occupyingPiece = p;
        p.setPosition(this);
    }
    
    public Piece liftUpThePiece() {
        Piece p = occupyingPiece;
        occupyingPiece = null;
        return p;
    }
    
    public void removePieceOnThis() {
        Piece p = liftUpThePiece();
        p.setPosition(null);
        if (p.getColor() == 0) b.Bpieces.remove(p);
        if (p.getColor() == 1) b.Wpieces.remove(p);
    }

    @Override
    public String toString() {
        return "Square{" +
                ", color=" + color +
                ", occupyingPiece=" + occupyingPiece +
                ", dispPiece=" + dispPiece +
                ", xNum=" + xNum +
                ", yNum=" + yNum +
                '}';
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + xNum;
        result = prime * result + yNum;
        return result;
    }
    
}

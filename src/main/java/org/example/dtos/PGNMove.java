package org.example.dtos;

import java.io.Serializable;

public class PGNMove implements Serializable {
    private static final long serialVersionUID = 1L;

    public int[] to;
    public boolean isWhite;
    public String disambiguation;
    public char piece;
    public boolean isCapture = false;
    public boolean isCastleKingSide = false;
    public boolean isCastleQueenSide = false;
    public boolean isPromotion = false;
    public char promoteTo;

    public String toString() {
        if (isCastleKingSide) return "O-O";
        if (isCastleQueenSide) return "O-O-O";
        return piece + ": "  + " -> " + to + (isCapture ? " (x)" : "");
    }
}
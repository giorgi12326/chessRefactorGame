package org.example.model;
import java.util.*;
import java.util.regex.*;

public class PGNParser {

    public static class PGNMove {
        public int[] to;
        public boolean isWhite;
        public String disambiguation;
        public Class<?> piece;
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

    // Just converts board coordinates like e2 -> (row, col)
    public static int[] algebraicToCoords(String pos) {
        int file = pos.charAt(0) - 'a';
        int rank = 8 - Character.getNumericValue(pos.charAt(1));
        return new int[]{rank, file};
    }

    public static List<PGNMove> parsePGN(String pgn) {
        List<PGNMove> moves = new ArrayList<>();
        pgn = pgn.replaceAll("\\{[^}]*\\}", ""); // remove comments
        pgn = pgn.replaceAll("\\d+\\.", "");     // remove move numbers
        pgn = pgn.replaceAll("\\s+", " ").trim();

        String[] tokens = pgn.split(" ");
        boolean isWhite = false;

        for (String token : tokens) {
            PGNMove move = new PGNMove();
            isWhite = !isWhite;
            move.isWhite = isWhite;


            if (token.equals("O-O")) {
                move.isCastleKingSide = true;
                moves.add(move);
                continue;
            } else if (token.equals("O-O-O")) {
                move.isCastleQueenSide = true;
                moves.add(move);
                continue;
            }

            // Regex to extract the move
            Pattern movePattern = Pattern.compile("([KQRBN])?([a-h]?[1-8]?)x?([a-h][1-8])(=([QRBN]))?([+#]?)");
            Matcher matcher = movePattern.matcher(token);

            if (matcher.matches()) {
                String piece = matcher.group(1);
                String disambiguation = matcher.group(2);
                String destination = matcher.group(3);
                String promotion = matcher.group(5);

                move.piece = piece != null ? parsePiece(piece.charAt(0)) : Pawn.class;
                move.to = algebraicToCoords(destination);
                move.isCapture = token.contains("x");
                move.isPromotion = promotion != null;
                move.disambiguation = disambiguation;
                if (move.isPromotion) move.promoteTo = promotion.charAt(0);

                // TODO: Find `from` square based on current board state
                // For now we set it to null or placeholder:
                moves.add(move);
            }

        }      return moves;
    }

    private static Class<?> parsePiece(char c) {
        if(c == 'R')
            return Rook.class;
        else if(c == 'B')
            return Bishop.class;
        else if(c == 'K')
            return King.class;
        else if(c == 'N')
            return Knight.class;
        else if(c == 'Q')
            return Queen.class;
        else return null;
    }

    public static void main(String[] args) {
        // Example: Loading PGN
        List<PGNParser.PGNMove> moveList = PGNParser.parsePGN("[Event \"Tbilisi FIDE GP 2015\"]\n" +
                "[Site \"Tbilisi GEO\"]\n" +
                "[Date \"2015.02.20\"]\n" +
                "[Round \"5.6\"]\n" +
                "[White \"Jobava,Ba\"]\n" +
                "[Black \"Mamedyarov,S\"]\n" +
                "[Result \"1-0\"]\n" +
                "[WhiteTitle \"GM\"]\n" +
                "[BlackTitle \"GM\"]\n" +
                "[WhiteElo \"2696\"]\n" +
                "[BlackElo \"2759\"]\n" +
                "[ECO \"A01\"]\n" +
                "[Opening \"Nimzovich-Larsen attack\"]\n" +
                "[Variation \"Indian variation\"]\n" +
                "[WhiteFideId \"13601520\"]\n" +
                "[BlackFideId \"13401319\"]\n" +
                "[EventDate \"2015.02.15\"]\n" +
                " \n" +
                "1. b3 Nf6 2. Bb2 g6 3. Nc3 Bg7 4. d4 c5 5. e3 cxd4 6. exd4 d5 7. Qd2 Nc6 8.\n" +
                "O-O-O Qa5 9. f3 h5 10. Kb1 Bf5 11. Bd3 Nxd4 12. Nge2 Nxe2 13. Qxe2 Bd7 14. Rhe1\n" +
                "e6 15. Bxg6 fxg6 16. Nxd5 Nxd5 17. Bxg7 Rg8 18. Qe5 Rxg7 19. Rxd5 Qb4 20. Rd6\n" +
                "Kf8 21. Red1 Bc6 22. R1d4 Qb5 23. Rd8+ Rxd8 24. Rxd8+ Ke7 25. Qd6+ Kf6 26. Qd4+\n" +
                "Kf7 27. Qf4+ Ke7 1-0");

// Convert algebraic squares to your Square[][]
        System.out.println(moveList);

// Now find the right piece to move and call its move method
// (you'll need from-coordinates too, which needs board state tracking logic)

    }
}

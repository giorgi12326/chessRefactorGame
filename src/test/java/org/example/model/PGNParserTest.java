package org.example.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PGNParserTest {

    @Test
    public void testAlgebraicToCoords() {
        assertArrayEquals(new int[]{7, 0}, PGNParser.algebraicToCoords("a1"));
        assertArrayEquals(new int[]{0, 7}, PGNParser.algebraicToCoords("h8"));
        assertArrayEquals(new int[]{4, 3}, PGNParser.algebraicToCoords("d4"));
    }

    @Test
    public void testParsePGNSplitsGamesCorrectly() {
        String multiGamePGN = """
                [Event "Game1"]
                [White "PlayerA"]
                [Black "PlayerB"]
                1. e4 e5 2. Nf3 Nc6

                [Event "Game2"]
                [White "PlayerC"]
                [Black "PlayerD"]
                1. d4 d5 2. c4 c6
                """;

        List<String> games = PGNParser.parsePGN(multiGamePGN);
        assertEquals(2, games.size());
        assertTrue(games.get(0).contains("[Event \"Game1\"]"));
        assertTrue(games.get(1).contains("[Event \"Game2\"]"));
    }

    @Test
    public void testParsePGNSetsPlayerNames() {
        String pgn = """
                [Event "Example"]
                [White "Alpha"]
                [Black "Beta"]
                1. e4 e5 2. Nf3 Nc6
                """;

        PGNParser.parsePGN(pgn);

        assertEquals("Alpha", PGNParser.whitePlayer);
        assertEquals("Beta", PGNParser.blackPlayer);
    }

    @Test
    public void testPGNMoveToString() {
        PGNParser.PGNMove move = new PGNParser.PGNMove();
        move.to = new int[]{3, 3};
        move.isWhite = true;
        move.piece = org.example.model.Rook.class;
        move.isCapture = true;

        String str = move.toString();
        assertTrue(str.contains("Rook"));
        assertTrue(str.contains(" -> "));
        assertTrue(str.contains("x"));
    }

    @Test
    public void testPGNMoveCastleToString() {
        PGNParser.PGNMove move = new PGNParser.PGNMove();

        move.isCastleKingSide = true;
        assertEquals("O-O", move.toString());

        move.isCastleKingSide = false;
        move.isCastleQueenSide = true;
        assertEquals("O-O-O", move.toString());
    }
}

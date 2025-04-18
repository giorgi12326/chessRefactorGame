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


}

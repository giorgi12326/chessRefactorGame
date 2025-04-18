package org.example.model;

import java.util.List;

public class BoardContext {
    public BoardContext(String pgn) {
        List<String> strings = PGNParser.parsePGN(pgn);
        new Thread(()->{
            new Board(null, strings.getFirst());
        }).start();
        new Thread(()->{
            new Board(null, strings.get(1));
        }).start();
        new Thread(()->{
            new Board(null, strings.get(2));
        }).start();

    }
}

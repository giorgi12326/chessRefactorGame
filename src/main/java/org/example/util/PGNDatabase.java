package org.example.util;

import java.sql.*;

public class PGNDatabase {

    private static final String DB_URL = "jdbc:h2:./chessdb";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static void init() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS pgn_games (id IDENTITY PRIMARY KEY, pgn CLOB)");
        }
    }

    public static void savePGN(String pgn) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO pgn_games (pgn) VALUES (?)")) {
            stmt.setString(1, pgn);
            stmt.executeUpdate();
        }
    }

    public static void printAllPGNs() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM pgn_games")) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("PGN: " + rs.getString("pgn"));
            }
        }
    }
}

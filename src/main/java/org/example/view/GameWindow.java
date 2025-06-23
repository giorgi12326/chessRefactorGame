// src/main/java/org/example/view/GameWindow.java
package org.example.view;

import org.example.model.Board;
import org.example.model.Clock;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class GameWindow {
    private JFrame gameWindow;
    public Clock blackClock;
    public Clock whiteClock;
    private Timer timer;
    private Board board;
    public String blackName;
    public String whiteName;

    public GameWindow(String blackName, String whiteName, int hh, int mm, int ss, String PGN) {
        this.blackName = blackName;
        this.whiteName = whiteName;
        blackClock = new Clock(hh, mm, ss);
        whiteClock = new Clock(hh, mm, ss);
        gameWindow = new JFrame("Chess");
        try {
            java.awt.Image whiteImg = ImageIO.read(getClass().getResource("/wp.png"));
            gameWindow.setIconImage(whiteImg);
        } catch (Exception e) {
            System.out.println("Game file wp.png not found");
        }
        gameWindow.setLocation(100, 100);
        gameWindow.setLayout(new BorderLayout(20, 20));
        board = new Board(this, PGN);
        JPanel gameData = gameDataPanel(this.blackName, this.whiteName, hh, mm, ss);
        gameWindow.add(gameData, BorderLayout.NORTH);
        gameWindow.add(board.view, BorderLayout.CENTER);
        gameWindow.add(buttons(), BorderLayout.SOUTH);
        gameWindow.pack();
        gameWindow.setResizable(false);
        gameWindow.setVisible(true);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JPanel gameDataPanel(final String bn, final String wn, final int hh, final int mm, final int ss) {
        JPanel gameData = new JPanel(new GridLayout(3, 2, 0, 0));
        JLabel w = new JLabel(wn);
        JLabel b = new JLabel(bn);
        w.setHorizontalAlignment(JLabel.CENTER);
        w.setVerticalAlignment(JLabel.CENTER);
        b.setHorizontalAlignment(JLabel.CENTER);
        b.setVerticalAlignment(JLabel.CENTER);
        gameData.add(w);
        gameData.add(b);
        final JLabel bTime = new JLabel(blackClock.getTime());
        final JLabel wTime = new JLabel(whiteClock.getTime());
        bTime.setHorizontalAlignment(JLabel.CENTER);
        bTime.setVerticalAlignment(JLabel.CENTER);
        wTime.setHorizontalAlignment(JLabel.CENTER);
        wTime.setVerticalAlignment(JLabel.CENTER);
        if (!(hh == 0 && mm == 0 && ss == 0)) {
            timer = new Timer(1000, null);
            timer.addActionListener((ActionEvent e) -> {
                boolean turn = board.getTurn();
                if (turn) {
                    whiteClock.decr();
                    wTime.setText(whiteClock.getTime());
                    if (whiteClock.outOfTime()) {
                        timer.stop();
                        int n = JOptionPane.showConfirmDialog(gameWindow,
                                bn + " wins by time! Play a new game? \n" +
                                        "Choosing \"No\" quits the game.",
                                bn + " wins!", JOptionPane.YES_NO_OPTION);
                        if (n == JOptionPane.YES_OPTION) {
                            new GameWindow(bn, wn, hh, mm, ss, null);
                            gameWindow.dispose();
                        } else gameWindow.dispose();
                    }
                } else {
                    blackClock.decr();
                    bTime.setText(blackClock.getTime());
                    if (blackClock.outOfTime()) {
                        timer.stop();
                        int n = JOptionPane.showConfirmDialog(gameWindow,
                                wn + " wins by time! Play a new game? \n" +
                                        "Choosing \"No\" quits the game.",
                                wn + " wins!", JOptionPane.YES_NO_OPTION);
                        if (n == JOptionPane.YES_OPTION) {
                            new GameWindow(bn, wn, hh, mm, ss, null);
                            gameWindow.dispose();
                        } else gameWindow.dispose();
                    }
                }
            });
            timer.start();
        } else {
            wTime.setText("Untimed game");
            bTime.setText("Untimed game");
        }
        gameData.add(wTime);
        gameData.add(bTime);
        return gameData;
    }

    private JPanel buttons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton instr = new JButton("How to play");
        instr.addActionListener(e ->
                JOptionPane.showMessageDialog(gameWindow,
                        "Move the chess pieces on the board by clicking\n" +
                                "and dragging. The game will watch out for illegal\n" +
                                "moves. You can win either by your opponent running\n" +
                                "out of time or by checkmating your opponent.\n\n" +
                                "Good luck, hope you enjoy the game!",
                        "How to play",
                        JOptionPane.PLAIN_MESSAGE));
        JButton nGame = new JButton("New game");
        nGame.addActionListener(e -> {
            int n = JOptionPane.showConfirmDialog(gameWindow,
                    "Are you sure you want to begin a new game?",
                    "Confirm new game",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        });
        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> {
            int n = JOptionPane.showConfirmDialog(gameWindow,
                    "Are you sure you want to quit?",
                    "Confirm quit",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                if (timer != null) timer.stop();
                gameWindow.dispose();
            }
        });
        JButton showPGN = new JButton("Show PGN");
        showPGN.addActionListener(e -> {
            String fullPGN = board.getFullPGN();
            JTextArea textArea = new JTextArea(fullPGN, 20, 40);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            JScrollPane scroll = new JScrollPane(textArea);

            JButton downloadPGNButton = new JButton("Download PGN");
            downloadPGNButton.addActionListener(ev -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Save PGN File");
                chooser.setApproveButtonText("Save");
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                String defaultName = String.format("%s_vs_%s_%s.pgn",
                        whiteName.replaceAll("\\s+", "_"),
                        blackName.replaceAll("\\s+", "_"),
                        java.time.LocalDate.now());
                chooser.setSelectedFile(new File(defaultName));
                int userChoice = chooser.showSaveDialog(gameWindow);
                if (userChoice == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    String path = file.getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".pgn")) {
                        file = new File(path + ".pgn");
                    }
                    if (file.exists()) {
                        int resp = JOptionPane.showConfirmDialog(gameWindow,
                                "File already exists. Overwrite?",
                                "Confirm Overwrite",
                                JOptionPane.YES_NO_OPTION);
                        if (resp != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    try {
                        Files.writeString(
                                file.toPath(),
                                fullPGN,
                                StandardCharsets.UTF_8
                        );
                        JOptionPane.showMessageDialog(gameWindow,
                                "PGN saved to:\n" + file.getAbsolutePath(),
                                "Saved",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(gameWindow,
                                "Error saving PGN: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(downloadPGNButton);

            JPanel dialogPanel = new JPanel(new BorderLayout());
            dialogPanel.add(scroll, BorderLayout.CENTER);
            dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(gameWindow, dialogPanel, "PGN", JOptionPane.PLAIN_MESSAGE);
        });
        buttons.add(instr);
        buttons.add(nGame);
        buttons.add(quit);
        buttons.add(showPGN);
        return buttons;
    }

    public void incorrectPgnMessage(String message) {
        JOptionPane.showMessageDialog(gameWindow, message, "Warning!", JOptionPane.PLAIN_MESSAGE);
    }

    public void checkmateOccurred(int c) {
        if (c == 0) {
            if (timer != null) timer.stop();
            int n = JOptionPane.showConfirmDialog(gameWindow,
                    "White wins by checkmate! Set up a new game? \n" +
                            "Choosing \"No\" lets you look at the final situation.",
                    "White wins!",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        } else {
            if (timer != null) timer.stop();
            int n = JOptionPane.showConfirmDialog(gameWindow,
                    "Black wins by checkmate! Set up a new game? \n" +
                            "Choosing \"No\" lets you look at the final situation.",
                    "Black wins!",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                SwingUtilities.invokeLater(new StartMenu());
                gameWindow.dispose();
            }
        }
    }
}

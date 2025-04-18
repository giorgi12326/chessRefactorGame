package org.example.view;

import org.example.model.Board;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class PgnField implements Runnable {
    public void run() {
        final JFrame startWindow = new JFrame("Chess");
        startWindow.setLocation(300, 100);
        startWindow.setResizable(false);
        startWindow.setSize(500, 600); // Slightly taller for PGN field

        Box components = Box.createVerticalBox();
        startWindow.add(components);

        // Game title
        final JPanel titlePanel = new JPanel();
        final JLabel titleLabel = new JLabel("Chess");
        titlePanel.add(titleLabel);
        components.add(titlePanel);

        // Player Panel
        final JPanel playerPanel = new JPanel();
        components.add(playerPanel);



        // PGN Input Field
        JPanel pgnPanel = new JPanel();
        pgnPanel.setLayout(new BorderLayout());
        JLabel pgnLabel = new JLabel("Enter PGN:");
        pgnPanel.add(pgnLabel, BorderLayout.NORTH);

        JTextArea pgnTextArea = new JTextArea(10, 40);
        pgnTextArea.setLineWrap(true);
        pgnTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(pgnTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        pgnPanel.add(scrollPane, BorderLayout.CENTER);
        components.add(pgnPanel);

        // Load PGN Button
        JButton loadButton = new JButton("Simulate One PGN");
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        components.add(loadButton);

        loadButton.addActionListener(e -> {
            String pgn = pgnTextArea.getText();
            new GameWindow("bn", "wn", 0,0,0,pgn);

            startWindow.dispose();
        });
        JButton loadButton1 = new JButton("Test Validity Of Games");
        loadButton1.setAlignmentX(Component.CENTER_ALIGNMENT);
        components.add(loadButton1);

        loadButton1.addActionListener(e -> {
            String pgn = pgnTextArea.getText();

            new Board(null, pgn);

            startWindow.dispose();
        });

        startWindow.setVisible(true);
    }

}

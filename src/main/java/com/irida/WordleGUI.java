package com.irida;

import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class WordleGUI extends JFrame {

    private Wordle game;
    private JButton newGameButton;
    private JLabel wordLengthLabel, difficultyLabel;
    private JComboBox<String> difficultyComboBox;
    private JSpinner wordLengthSpinner;
    private JPanel guessPanel;
    private int maxGuesses = 6;
    private JLabel[][] guessGrid; // 2D array of JLabels for guesses
    private int currentRow = 0;
    private int currentCol = 0;
    private int wordLength;
    private String currentGuess = "";

    private Color boxColor = new Color(20, 20, 40);
    private Font futuristicFont;

    public WordleGUI() {
        // Set up the window
        setTitle("Night Wordle");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/wordleIcon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the main background color
        getContentPane().setBackground(new Color(15, 15, 30)); // Dark purple background

        try {
            futuristicFont = Font.createFont(Font.TRUETYPE_FONT,
                    getClass().getResourceAsStream("/goodtimes.otf")).deriveFont(32f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(futuristicFont);

        } catch (FontFormatException | IOException e) {
            futuristicFont = new Font("monospaced", Font.BOLD, 28);
        }

        // Top panel for game configuration
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 2));
        topPanel.setBackground(boxColor); // Darker panel background
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add margin around panel

        // Custom Font for Labels and Buttons
        Font cyberpunkFont = futuristicFont.deriveFont(18f);

        // Difficulty selection
        difficultyLabel = new JLabel("select difficulty:");
        difficultyLabel.setForeground(Color.CYAN); // Neon cyan text
        difficultyLabel.setFont(cyberpunkFont);

        String[] difficulties = { "EASY", "MEDIUM", "HARD", "ELITE", "LUNATIC", "MACHINE" };
        difficultyComboBox = new JComboBox<>(difficulties);
        difficultyComboBox.setBackground(new Color(30, 30, 60)); // Dark background for dropdown
        difficultyComboBox.setForeground(Color.CYAN); // Neon cyan text
        difficultyComboBox.setFont(cyberpunkFont);

        // Word length selection
        wordLengthLabel = new JLabel("word length < 3 - 12 >");
        wordLengthLabel.setForeground(Color.CYAN);
        wordLengthLabel.setFont(cyberpunkFont);

        wordLengthSpinner = new JSpinner(new SpinnerNumberModel(5, 3, 12, 1)); // Default word length: 5, max: 16
        wordLengthSpinner.setFont(cyberpunkFont);
        wordLengthSpinner.getEditor().getComponent(0).setBackground(new Color(30, 30, 60));
        wordLengthSpinner.getEditor().getComponent(0).setForeground(Color.CYAN);

        // Adding components to the top panel
        topPanel.add(difficultyLabel);
        topPanel.add(difficultyComboBox);
        topPanel.add(wordLengthLabel);
        topPanel.add(wordLengthSpinner);

        newGameButton = new JButton("Start New Game");
        newGameButton.setFont(cyberpunkFont);
        newGameButton.setBackground(new Color(0, 255, 255)); // Neon cyan button
        newGameButton.setForeground(Color.BLACK);
        newGameButton.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2)); // Neon yellow border
        newGameButton.setFocusPainted(false); // Remove focus border

        newGameButton.addActionListener(e -> {
            startNewGame();
            requestFocusInWindow(); // Request focus after starting a new game
        });

        topPanel.add(newGameButton);

        // Guess panel to hold the grid of guesses
        guessPanel = new JPanel();
        guessPanel.setLayout(new GridLayout(maxGuesses, 5, 5, 5)); // maxGuesses rows, wordLength columns, padding
                                                                   // between
        guessPanel.setBackground(new Color(15, 15, 30)); // Match background color for guess area

        add(topPanel, BorderLayout.NORTH);
        add(guessPanel, BorderLayout.CENTER);

        // Set key listener for guessing directly on grid
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (game == null)
                    return; // No game in progress

                char keyChar = e.getKeyChar();
                // Check for valid letters (both lowercase and uppercase)
                if (Character.isLetter(keyChar)) {
                    handleLetterInput(Character.toUpperCase(keyChar)); // Convert to uppercase before passing
                } else if (keyChar == KeyEvent.VK_BACK_SPACE) {
                    handleBackspace();
                } else if (keyChar == KeyEvent.VK_ENTER) {
                    handleSubmitGuess();
                }
            }
        });

        setFocusable(true); // Make sure the window can receive key inputs

        // Add focus listener to refocus window when focus is lost
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                requestFocusInWindow(); // Refocus window
            }
        });
    }

    // Start a new game with the selected difficulty and word length
    private void startNewGame() {
        try {
            wordLength = (int) wordLengthSpinner.getValue();
            String difficultyString = (String) difficultyComboBox.getSelectedItem();
            Wordle.Difficulty difficulty = Wordle.Difficulty.valueOf(difficultyString);

            if (wordLength > 8) {
                maxGuesses = 8;
            } else {
                maxGuesses = 6;
            }

            // Initialize the Wordle game with selected difficulty and word length
            game = new Wordle(difficulty, wordLength);

            // Clear previous results
            guessPanel.removeAll();
            guessPanel.setLayout(new GridLayout(maxGuesses, wordLength, 5, 5)); // Adjust grid to the new word length
            guessGrid = new JLabel[maxGuesses][wordLength]; // Reset grid

            for (int row = 0; row < maxGuesses; row++) {
                for (int col = 0; col < wordLength; col++) {
                    guessGrid[row][col] = new JLabel(" ", SwingConstants.CENTER);
                    guessGrid[row][col].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2)); // Dark red
                                                                                                       // border
                    guessGrid[row][col].setOpaque(true);
                    guessGrid[row][col].setBackground(new Color(20, 20, 40)); // Dark cell background
                    guessGrid[row][col].setFont(futuristicFont);
                    guessGrid[row][col].setForeground(new Color(0, 255, 255)); // Neon text color
                    guessPanel.add(guessGrid[row][col]);
                }
            }

            guessPanel.revalidate();
            guessPanel.repaint();
            currentRow = 0;
            currentCol = 0;
            currentGuess = "";
            setFocusable(true); // Ensure focus for key events
            requestFocusInWindow(); // Make sure the focus is set on the game grid

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to start the game: " + ex.getMessage());
        }
    }

    private void handleLetterInput(char letter) {
        if (currentCol < wordLength) { // Check if within the word length
            guessGrid[currentRow][currentCol].setText(String.valueOf(letter));
            currentGuess += letter; // Update current guess
            currentCol++; // Move to the next column
        }
    }

    // Handle backspace key to delete the last letter entered
    private void handleBackspace() {
        if (currentCol > 0) {
            currentCol--;
            guessGrid[currentRow][currentCol].setText(" ");
            currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
        }
    }

    private void handleSubmitGuess() {
        if (currentCol == wordLength) {
            if (game == null)
                return;

            // Check if all characters in currentGuess are valid
            boolean isValidGuess = true;
            for (char ch : currentGuess.toCharArray()) {
                if (ch < 'A' || ch > 'Z') {
                    isValidGuess = false;
                    break;
                }
            }

            if (!isValidGuess) {
                JOptionPane.showMessageDialog(this,
                        "Invalid guess! Please enter only uppercase English letters (A-Z).");
                return;
            }

            // Validate if the current guess is a word in the dictionary
            if (!game.validateGuess(currentGuess)) {
                indicateInvalidWord();
            } else {
                String targetWord = game.getCurrentWord().toUpperCase();

                // Check for a correct guess
                if (currentGuess.equals(targetWord)) {
                    // Change all letter borders to cyan
                    for (int i = 0; i < wordLength; i++) {
                        guessGrid[currentRow][i].setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2)); // Cyan
                                                                                                                       // border
                                                                                                                       // for
                                                                                                                       // all
                                                                                                                       // letters
                    }
                    // Show the congratulations message
                    JOptionPane.showMessageDialog(this, "Congratulations! You've guessed the word: " + targetWord);
                    return; // Exit the method if the game is won
                }

                boolean[] isCorrectPosition = new boolean[wordLength];
                int[] letterCountInTarget = new int[26];

                // First pass: identify correct positions
                for (int i = 0; i < wordLength; i++) {
                    char targetChar = targetWord.charAt(i);
                    letterCountInTarget[targetChar - 'A']++;

                    char guessChar = currentGuess.charAt(i);
                    if (guessChar == targetChar) {
                        guessGrid[currentRow][i].setBackground(Color.BLACK);
                        guessGrid[currentRow][i].setBorder(BorderFactory.createLineBorder(new Color(0, 255, 255), 2)); // Cyan
                                                                                                                       // border
                                                                                                                       // for
                                                                                                                       // correct
                                                                                                                       // letter
                        isCorrectPosition[i] = true;
                        letterCountInTarget[targetChar - 'A']--;
                    }
                }

                // Second pass: handle letters in wrong positions or extra occurrences
                for (int i = 0; i < wordLength; i++) {
                    if (isCorrectPosition[i])
                        continue;

                    char guessChar = currentGuess.charAt(i);
                    if (letterCountInTarget[guessChar - 'A'] > 0) {
                        guessGrid[currentRow][i].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                        letterCountInTarget[guessChar - 'A']--;
                    } else {
                        guessGrid[currentRow][i].setBackground(Color.DARK_GRAY); // Incorrect letter
                    }
                }

                currentRow++;
                currentCol = 0;
                currentGuess = "";

                if (currentRow >= maxGuesses) {
                    JOptionPane.showMessageDialog(this, "Game Over! The word was: " + targetWord);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a full guess before submitting.");
        }
        requestFocusInWindow(); // Keep focus on the game window
    }

    private void indicateInvalidWord() {
        for (int i = 0; i < wordLength; i++) {
            guessGrid[currentRow][i].setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));
        }

        // Create a timer to reset the borders back to neon cyan after 0.2 seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < wordLength; i++) {
                    guessGrid[currentRow][i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                }
            }
        }, 200); // 0.2 seconds delay

        requestFocusInWindow(); // Refocus after indicating an invalid word
    }

}
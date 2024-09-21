package com.irida;

import java.io.IOException;

/*
 * Handles worlde logic.
 */
public class Wordle {

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD,
        ELITE,
        LUNATIC,
        MACHINE
    }

    private DictionaryTool dictionaryTool;

    private String currentWord;

    public Wordle(Difficulty difficulty, int wordLength) throws IOException {

        dictionaryTool = DictionaryTool.getInstance();

        if (wordLength < 3 || wordLength > 12) {
            System.out.println("Invalid word length! Must be between 3-12");
            return;
        }

        int listIndex = 1;

        switch (difficulty) {
            case EASY:
                listIndex = 1;
                break;

            case MEDIUM:
                listIndex = 2;
                break;

            case HARD:
                listIndex = 3;
                break;

            case ELITE:
                listIndex = 4;
                break;

            case LUNATIC:
                listIndex = 5;
                break;

            case MACHINE:
                listIndex = 6;
                break;

            default:
                break;
        }

        currentWord = dictionaryTool.getRandomWord(wordLength, listIndex).toUpperCase();

    }

    public String getCurrentWord() {
        return currentWord;
    }

    public boolean validateGuess(String guess) {
        if (dictionaryTool.isValidWord(guess))
            return true;
        else
            return false;
    }
}

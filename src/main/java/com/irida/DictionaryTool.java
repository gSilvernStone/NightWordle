package com.irida;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class DictionaryTool {

    private static DictionaryTool instance;

    private List<Set<String>> dictionaries;

    private final List<String> dictionaryFiles = List.of(
        "/english-words.10",
        "/english-words.20",
        "/english-words.35",
        "/english-words.40",
        "/english-words.50",
        "/english-words.55"
    );

    private DictionaryTool() throws IOException {
        dictionaries = new ArrayList<>();
        loadDictionaries();
    }

    public static DictionaryTool getInstance() throws IOException {
        if (instance == null) {
            instance = new DictionaryTool();
        }
        return instance;
    }

    public boolean isValidWord(String word) {
        word = word.toLowerCase();
        for (Set<String> dictionary : dictionaries) {
            if (dictionary.contains(word)) {
                return true;
            }
        }
        return false;
    }

    public String getRandomWord(int wordLength, int listNumber) throws IllegalArgumentException {
        if (listNumber < 1 || listNumber > dictionaries.size()) {
            throw new IllegalArgumentException("List number must be between 1 and " + dictionaries.size());
        }

        Set<String> selectedDictionary = dictionaries.get(listNumber - 1);

        List<String> filteredWords = new ArrayList<>();
        for (String word : selectedDictionary) {
            if (word.length() == wordLength) {
                filteredWords.add(word);
            }
        }

        if (filteredWords.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return filteredWords.get(random.nextInt(filteredWords.size()));
    }

    private void loadDictionaries() throws IOException {
        for (String dictionaryFile : dictionaryFiles) {
            dictionaries.add(loadDictionary(dictionaryFile));
        }
    }

    private Set<String> loadDictionary(String fileName) throws IOException {
        Set<String> dictionary = new HashSet<>();

        try (InputStream inputStream = DictionaryTool.class.getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String word;
            while ((word = reader.readLine()) != null) {
                dictionary.add(word.trim().toLowerCase());
            }
        }

        return dictionary;
    }
}
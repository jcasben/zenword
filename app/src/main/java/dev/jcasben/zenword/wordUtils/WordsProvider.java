package dev.jcasben.zenword.wordUtils;

import dev.jcasben.zenword.mappings.UnsortedArrayMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class WordsProvider {
    
    // El catàleg de paraules vàlides amb els seus dos formats: paraula amb accents i paraula sense accents.
    private final HashMap<String, String> validWords = new HashMap<>();
    // El catàleg de longituds amb les paraules de cada longitud.
    private final HashMap<Integer, HashSet<String>> wordsLengths = new HashMap<>();
    // El catàleg de solucions amb les solucions de cada longitud.
    private final UnsortedArrayMapping<Integer, HashSet<String>> solutions = new UnsortedArrayMapping<>(5);
    // El catàleg de les paraules per descobrir (paraules ocultes), juntamentamb la informació de la seva posició a la
    // pantalla. Una vegada que l’usuari descobreix una de les paraules ocultes, aquesta ja no ha de formar part del catàleg
    private final UnsortedArrayMapping<Integer, String> hiddenWords = new UnsortedArrayMapping<>(5);
    // El catàleg de les solucions trobades.
    private final TreeSet<String> found = new TreeSet<>();
    // El cat`aleg de les lletres disponibles: n´umero d’aparicions de cada lletra a la paraula triada, per determinar
    // si una paraula es pot formar amb les lletres disponibles (´es a dir, si ´es una soluci´o possible).
    private final UnsortedArrayMapping<Character, Integer> availableLetters = new UnsortedArrayMapping<>(7);
    
    private final int[] sizes = new int[5];
    private final int[] sizesSolutions = new int[5];
    private int wordLength;
    private String chosenWord;
    
    public WordsProvider(InputStream stream) {
        initWordsLengths(stream);
    }
    
    private void initWordsLengths(InputStream stream) {
        for (int i = 3; i < 8; i++) {
            wordsLengths.put(i, new HashSet<>());
        }
        try (InputStream inputStream = stream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                String word = line.substring(0, line.indexOf(';'));
                String unformatWord = line.substring(line.indexOf(';') + 1);
                int lengthWord = unformatWord.length();
                if (lengthWord >= 3 && lengthWord <= 7 ) {
                    validWords.put(unformatWord, word);
                    if (wordsLengths.get(lengthWord).add(unformatWord)) sizes[lengthWord - 3]++;
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void initializeGameWords() {
        pickWord();
        generateHiddenWords();
    }
    
    private void generateHiddenWords() {
        Random ran = new Random();

        //Inicializar los HashSet de solution.
        for (int i = 3; i < 8; i++) {
            solutions.put(i, new HashSet<>());
        }

        //Para cada tamaño de la palabra
        Iterator<Map.Entry<Integer, HashSet<String>>> lengthsIterator = wordsLengths.entrySet().iterator();
        while (lengthsIterator.hasNext()) {
            Map.Entry<Integer, HashSet<String>> entry = lengthsIterator.next();
            Iterator<String> wordsIterator = entry.getValue().iterator();
            HashSet<String> solutionWordLegth = solutions.get(entry.getKey());
            while (wordsIterator.hasNext()) {
                String word = wordsIterator.next();
                if (isSolutionWord(chosenWord, word)) {
                    solutionWordLegth.add(word);
                    sizesSolutions[entry.getKey() - 3]++;
                }
            }
        }

        //generate the 5 hidden words
        int pos = 4;
        for (int i = wordLength; i > 3; i--) {
            int tam = sizesSolutions[i - 3];
            if (tam > 0) {
                int randomNumber = ran.nextInt(sizesSolutions[i - 3]);
                String hidenWord = "";
                Iterator<String> hidenIterator = solutions.get(i).iterator();
                while (randomNumber >= 0 && hidenIterator.hasNext()) {
                    hidenWord = hidenIterator.next();
                    randomNumber--;
                }
                hiddenWords.put(pos, hidenWord);
                pos--;
            }
        }

        int tam = sizesSolutions[0];
        if (tam >= 0) {
            String hidenWord = "";
            Iterator<String> hidenIterator = solutions.get(3).iterator();
            while (pos >= 0){
                int randomNumber = ran.nextInt(sizesSolutions[0]);
                while (randomNumber >= 0 && hidenIterator.hasNext()) {
                    hidenWord = hidenIterator.next();
                    randomNumber--;
                }
                hiddenWords.put(pos, hidenWord);
                pos--;
            }
        }
    }
    
    public HashMap<Integer, HashSet<String>> getWordsLengths() {
        return wordsLengths;
    }
    
    public UnsortedArrayMapping<Integer, String> getHiddenWords() {
        return hiddenWords;
    }
    
    public String getChosenWord() {
        return chosenWord;
    }
    
    private boolean isSolutionWord(String word1, String word2){
        UnsortedArrayMapping<Character,Integer> catalogue = new UnsortedArrayMapping<>(word1.length());
        // generate the catalogue of word 1
        for (int i = 0; i < word1.length(); i++) {
            Integer v = catalogue.put(word1.charAt(i), 1);
            if (v != null) {
                catalogue.put(word1.charAt(i), v+1);
            }
        }

        // check if word2 could be generated with the catalogue
        for (int i = 0; i < word2.length(); i++) {
            Integer value = catalogue.get(word2.charAt(i));
            if (value == null || value == 0) return false;
            catalogue.put(word2.charAt(i), value - 1);
        }
        // if the program arrives here is because word 2 can be formed with the letters of word1
        return true;
    }
    
    private void pickWord() {
        Random random = new Random();
        wordLength = random.nextInt(5) + 3;
        Iterator<String> iterator = wordsLengths.get(wordLength).iterator();
        int numWord = random.nextInt(sizes[wordLength - 3]);
        int i = 0;
        while (i <= numWord && iterator.hasNext()) {
        chosenWord = iterator.next();
        i++;
        }
    }
}
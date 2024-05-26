package dev.jcasben.zenword.wordUtils;

import android.util.Log;

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
    private UnsortedArrayMapping<Integer, HashSet<String>> solutions;
    // El catàleg de les paraules per descobrir (paraules ocultes), juntamentamb la informació de la seva posició a la
    // pantalla. Una vegada que l’usuari descobreix una de les paraules ocultes, aquesta ja no ha de formar part del catàleg
    //private final UnsortedArrayMapping<Integer, String> hiddenWords = new UnsortedArrayMapping<>(5);
    private TreeMap<Integer, String> hiddenWords;
    // El catàleg de les solucions trobades.
    private TreeSet<String> found;
    // El cat`aleg de les lletres disponibles: n´umero d’aparicions de cada lletra a la paraula triada, per determinar
    // si una paraula es pot formar amb les lletres disponibles (´es a dir, si ´es una soluci´o possible).
    private UnsortedArrayMapping<Character, Integer> availableLetters;

    private final int[] sizes = new int[5];
    private int[] sizesSolutions = new int[5];
    private Integer hiddenWordsNumber = 5;
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
                if (lengthWord >= 3 && lengthWord <= 7) {
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
        hiddenWords = new TreeMap<>();
        found = new TreeSet<>(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
        availableLetters = new UnsortedArrayMapping<>(7);
        solutions = new UnsortedArrayMapping<>(5);
        sizesSolutions = new int[5];
        pickWord();
        generateHiddenWords();
    }

    private void generateHiddenWords() {
        Comparator<String> comparator =
                Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder());
        TreeSet<String> aux = new TreeSet<>(comparator);
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
            HashSet<String> solutionWordLength = solutions.get(entry.getKey());
            while (wordsIterator.hasNext()) {
                String word = wordsIterator.next();
                if (isSolutionWord(chosenWord, word)) {
                    solutionWordLength.add(word);
                    sizesSolutions[entry.getKey() - 3]++;
                }
            }
        }

//        hiddenWords.put(4, chosenWord);
        aux.add(chosenWord);

        int pos = 3;

        for (int i = wordLength - 1; i > 3; i--) {
            if (sizesSolutions[i - 3] > 0) {
                int random = ran.nextInt(sizesSolutions[i - 3]);
                String hiddenWord = "";
                Iterator<String> hiddenIterator = solutions.get(i).iterator();
                while (hiddenIterator.hasNext() && random > 0) {
                    hiddenWord = hiddenIterator.next();
                    random--;
                }
                if (!Objects.equals(hiddenWord, "")) {
                    aux.add(hiddenWord);
                    pos--;
//                    hiddenWords.put(pos--, hiddenWord);
                }
            }
        }

        int tam = sizesSolutions[0];
        Log.i("testHidden", "Cantidad de palabras de 3letras: " + tam);

        if (tam >= 0) {
            int count3 = 0;
            HashSet<Integer> accessed = new HashSet<>();
            while (pos >= 0 && count3 < tam) {
                String hiddenWord = "";
                ran = new Random();
                int randomNumber = ran.nextInt(sizesSolutions[0]);
                Log.i("testHidden", "Numero random " + randomNumber);
                Iterator<String> hidenIterator = solutions.get(3).iterator();
                // Con este if nos ahorramos tener que sacar la palabra antes de mirar si ya habia salido esa misma
                // palabra antes.
                if (accessed.add(randomNumber)) {
                    while (randomNumber >= 0 && hidenIterator.hasNext()) {
                        hiddenWord = hidenIterator.next();
                        randomNumber--;
                    }
                    pos--;
                    count3++;
                    aux.add(hiddenWord);
                }
//                    hiddenWords.put(pos, hiddenWord);

                Log.i("testHidden", "Hidden word:" + hiddenWord);
            }
        }

        int j = 0;
        Iterator<String> auxIterator = aux.iterator();
        while (auxIterator.hasNext()) {
            hiddenWords.put(j, auxIterator.next());
            j++;
        }

        Log.i("test", "ChosenWord = " + chosenWord);
        Log.i("test", hiddenWords.toString());
        Log.i("testHidden", "--------------------------------------");
    }

    public HashMap<Integer, HashSet<String>> getWordsLengths() {
        return wordsLengths;
    }

    public TreeMap<Integer, String> getHiddenWords() {
        return hiddenWords;
    }

    public String getChosenWord() {
        return chosenWord;
    }

    public UnsortedArrayMapping<Integer, HashSet<String>> getSolutions() {
        return solutions;
    }

    public TreeSet<String> getFound() {
        return found;
    }

    public Integer getHiddenWordsNumber() { return hiddenWordsNumber;}

    public HashMap<String, String> getValidWords() {
        return validWords;
    }

    private boolean isSolutionWord(String word1, String word2) {
        availableLetters = new UnsortedArrayMapping<>(word1.length());
        // generate the catalogue of word 1
        for (int i = 0; i < word1.length(); i++) {
            Integer v = availableLetters.put(word1.charAt(i), 1);
            if (v != null) {
                availableLetters.put(word1.charAt(i), v + 1);
            }
        }

        // check if word2 could be generated with the catalogue
        for (int i = 0; i < word2.length(); i++) {
            Integer value = availableLetters.get(word2.charAt(i));
            if (value == null || value == 0) return false;
            availableLetters.put(word2.charAt(i), value - 1);
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
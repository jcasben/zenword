package dev.jcasben.zenword;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import dev.jcasben.zenword.mappings.UnsortedArrayMapping;
import dev.jcasben.zenword.mappings.UnsortedLinkedListMapping;
import dev.jcasben.zenword.sets.TrieSet;
import dev.jcasben.zenword.sets.UnsortedArraySet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    private String chosenWord;
    private final int[] ids ={R.id.buttonL0, R.id.buttonL1, R.id.buttonL2, R.id.buttonL3,
            R.id.buttonL4, R.id.buttonL5, R.id.buttonL6};
    private final int[] guidesIds = {R.id.guideW0, R.id.guideW1, R.id.guideW2,
            R.id.guideW3, R.id.guideW4};
    private final UnsortedArrayMapping<String, Drawable[]> drawableColors =
            new UnsortedArrayMapping<>(4);
    private final UnsortedArrayMapping<String, int[]> buttonColors =
             new UnsortedArrayMapping<>(4);
    private int[][] letterTVId;
    private final String [] colors = {"YELLOW", "GREEN", "RED", "ORANGE"};

    //                     --------------- Catalogos ---------------

    // El catàleg de paraules vàlides amb els seus dos formats: paraula amb accents i paraula sense accents.
    private final HashMap<String, String> validWords = new HashMap<>();
    // El catàleg de longituds amb les paraules de cada longitud.
    private final HashMap<Integer, HashSet<String>> lengths = new HashMap<>();
    // El catàleg de solucions amb les solucions de cada longitud.
    private final UnsortedArrayMapping<Integer, HashSet<String>> solutions = new UnsortedArrayMapping<>(5);
    // El catàleg de les paraules per descobrir (paraules ocultes), juntamentamb la informació de la seva posició a la
    // pantalla. Una vegada que l’usuari descobreix una de les paraules ocultes, aquesta ja no ha de formar part del catàleg
    private final UnsortedArrayMapping<Integer, String> hiddenWords = new UnsortedArrayMapping<>(5);
    // El catàleg de les solucions trobades.
    private final TrieSet found = new TrieSet();
    // El cat`aleg de les lletres disponibles: n´umero d’aparicions de cada lletra a la paraula triada, per determinar
    // si una paraula es pot formar amb les lletres disponibles (´es a dir, si ´es una soluci´o possible).
    private final UnsortedArrayMapping<Character, Integer> availableLetters = new UnsortedArrayMapping<>(7);

    //                     -----------------------------------------
    private final int[] sizes = new int[5];

    private Drawable letterBackground;
    private int widthDisplay;
    private int heightDisplay;
    private int wordLength;

    // variable temporal hasta introducir mejoras.
    private int[] lenghtWord = {3,7,7,7,7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Declaration of colors and drawables for UI
        initUIColorsAndDrawables();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUIColor("YELLOW");
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics) ;
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;

        initWords();
        pickWord();
        generateHiddenWords();
        // Generate buttons of the circle

        setCircleButtonLetters();
        suffle(null);

        // inicializar la matriz de letras para poder guardar los id de los tv
        letterTVId = new int[5][];
        for (int i = 0; i < letterTVId.length; i++) {
            int l = hiddenWords.get(i).length();
            letterTVId[i] = new int[l];
        }
        // generar las filas de las palabras
        /*
        for (int i = 0; i < guidesIds.length; i++) {
            generateRowTextViews(guidesIds[i], lenghtWord[i], i);
        }
         */
        Iterator<UnsortedArrayMapping.Pair<Integer, String>> hiddensIterator = hiddenWords.iterator();
        while (hiddensIterator.hasNext()) {
            UnsortedArrayMapping.Pair<Integer, String> pair = hiddensIterator.next();
            generateRowTextViews(guidesIds[pair.getKey()], pair.getValue().length(), pair.getKey());
        }

        //prueba de metodos
        showWord("mec", 0);
        showFirstLetter("castaña",1);
    }

    // onClick letter buttons function
    public void setLetter(View view){
        Button but = (Button) view;
        //Get the letter of the button.
        String letter = but.getText().toString();
        // Put it on the text view
        TextView piceWord = findViewById(R.id.textVWordFormation);
        String word = piceWord.getText().toString();
        word += letter;
        word = word.toUpperCase();
        piceWord.setText(word);

        //Enable button.
        but.setEnabled(false);
    }

    public TextView[] generateRowTextViews (int guide, int letters, int numline) {
        TextView[] line = new TextView[letters];
        ConstraintLayout main = findViewById(R.id.main);
        for (int i = 0; i < letters; i++) {
            TextView x = new TextView(this);
            int id = View.generateViewId();
            //save id
            letterTVId[numline][i] = id;
            x.setId(id);
            x.setText("");
            x.setBackground(letterBackground);
            x.setTextColor(Color.WHITE);
            x.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            x.setTextSize(24);
            x.setTypeface(null, Typeface.BOLD);
            x.setTextColor(Color.BLACK);
            line[i] = x;
            main.addView(x);
        }
        int whidth = (widthDisplay/7) - (int)(0.05*widthDisplay);
        ConstraintSet constraintSet = new ConstraintSet();
        for (int i = 0; i < letters; i++) {
            constraintSet.connect(
                    line[i].getId(),
                    ConstraintSet.TOP,
                    guide,
                    ConstraintSet.BOTTOM,
                    10
            );

            if (i == 0){
                constraintSet.connect(
                        line[i].getId(),
                        ConstraintSet.START,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.END,
                        whidth*(7-letters)/2
                );
            }
            else {
                constraintSet.connect(
                        line[i].getId(),
                        ConstraintSet.START,
                        line[i-1].getId(),
                        ConstraintSet.END,
                        5
                );
            }

            if (i == letters-1) {
                constraintSet.connect(
                        line[i].getId(),
                        ConstraintSet.END,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.START,
                        whidth*(7-letters)/2
                );
            }
            else  {
                constraintSet.connect(
                        line[i].getId(),
                        ConstraintSet.END,
                        line[i+1].getId(),
                        ConstraintSet.START,
                        5
                );
            }

            constraintSet.constrainWidth(line[i].getId(),whidth);
            constraintSet.constrainHeight(line[i].getId(),whidth);
            constraintSet.applyTo(main);
        }

        return line;
    }


    //name of the buttons: l0, l1, l2, l3, l4, l5, l6
    private void setCircleButtonLetters(){
        int i;
        for (i = 0; i < chosenWord.length(); i++) {
            Button bLetter = findViewById(ids[i]);
            bLetter.setOnClickListener(e -> setLetter(bLetter));
            bLetter.setText(new char[]{chosenWord.charAt(i)}, 0, 1);
        }
        for ( ;i < 7; i++) {
            Button button = findViewById(ids[i]);
            button.setVisibility(View.GONE);
        }
    }

    // onClick random button function
    public void suffle (View view) {
        clear(null);
        Random ran = new Random();
        char[] word = chosenWord.toCharArray();
        // suffle the word
        for (int i = 0; i < word.length; i++) {
            int j = ran.nextInt(word.length);
            char aux = word[i];
            word[i] = word[j];
            word[j] = aux;
        }
        // put the suffle word on the buttons
        for (int i = 0; i < chosenWord.length(); i++) {
            Button bLetter = findViewById(ids[i]);
            bLetter.setText(word, i, 1);
        }
    }

    // onClick bonus button
    public void bonus(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("encertades i possibles");
        builder.setMessage("La llista de trobades: \n");

        // OK button for close the window
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clear(View view) {
        //restart buttons
        for (int i = 0; i < chosenWord.length(); i++) {
            Button b = findViewById(ids[i]);
            b.setEnabled(true);
        }
        //clear text view
        TextView tv = findViewById(R.id.textVWordFormation);
        tv.setText("");
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

    private void setUIColor(String color) {
        ImageView circle = findViewById(R.id.circleView);
        circle.setImageDrawable(drawableColors.get(color)[0]);
        letterBackground = drawableColors.get(color)[1];
        Button clear = findViewById(R.id.buttonClear);
        clear.setBackgroundColor(buttonColors.get(color)[0]);
        Button send = findViewById(R.id.buttonSend);
        send.setBackgroundColor(buttonColors.get(color)[0]);
        TextView textView = findViewById(R.id.textVWordFormation);
        textView.setBackgroundColor(buttonColors.get(color)[1]);
    }

    private void showWord(String word, int pos) {
        for (int i = 0; i < letterTVId[pos].length; i++) {
            TextView textView = findViewById(letterTVId[pos][i]);
            textView.setText(String.format("%s", word.charAt(i)).toUpperCase());
        }
    }

    private void showFirstLetter(String word, int pos) {
        TextView textView = findViewById(letterTVId[pos][0]);
        textView.setText(String.format("%s", word.charAt(0)).toLowerCase());
    }

    private void showMessage(String message, boolean longTime) {
        int duration;
        if (longTime) duration = Toast.LENGTH_LONG;
        else duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }

    public void onClickRestart(View view) {
        setUIColor(pickRandomColor());
        /*
        En aquest apartat s’ha d’implementar la funcionalitat del bot´o reiniciar, que
        ha de:
            • Esborrar de la pantalla les caselles de les lletres amagades actuals.
            • Mostrar les noves paraules amagades.
            • Reiniciar totes les variables necess`aries per comen¸car una nova partida.
        Pensau que, de moment, no podem reiniciar totes les coses (encara no tenim
        les paraules i no es pot determinar quantes ni quines solucions tenim, per`o
        ho podem simular amb valors constants).
         */
    }

    private void enableViews(int parent) {
        ViewGroup group = (ViewGroup) findViewById(R.id.main);
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            v.setEnabled(true);
        }
    }

    private void disableViews(int parent) {
        int bonusId = R.id.buttonBonus;
        int resetId = R.id.buttonReset;
        ViewGroup group = (ViewGroup) findViewById(R.id.main);
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if ((v.getId() != bonusId) && (v.getId() != resetId)) v.setEnabled(false);
        }
    }

    private String pickRandomColor() {
        Random ran = new Random();
        return colors[ran.nextInt(4)];
    }

    private void pickWord() {
        Random random = new Random();
        wordLength = random.nextInt(5);
        Iterator<String> iterator = lengths.get(wordLength).iterator();
        int numWord = random.nextInt(sizes[wordLength - 3]);
        int i = 0;
        while (i <= numWord && iterator.hasNext()) {
            chosenWord = iterator.next();
            i++;
        }
    }

    private void generateHiddenWords() {
        Random ran = new Random();

        //Inicializar los HashSet de solution.
        for (int i = 3; i < 8; i++) {
            solutions.put(i, new HashSet<>());
        }

        //Para cada tamaño de la palabra
        Iterator<Map.Entry<Integer, HashSet<String>>> lengthsIterator = lengths.entrySet().iterator();
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
    }

    private void initWords() {
        for (int i = 3; i < 8; i++) {
            lengths.put(i, new HashSet<>());
        }
        try (InputStream inputStream = getResources().openRawResource(R.raw.paraules)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                String word = line.substring(0, line.indexOf(';'));
                String unformatWord = line.substring(line.indexOf(';') + 1);
                int lengthWord = unformatWord.length();
                if (lengthWord >= 3 && lengthWord <= 7 ) {
                    validWords.put(unformatWord, word);
                    if (lengths.get(lengthWord).add(unformatWord)) sizes[lengthWord - 3]++;
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initUIColorsAndDrawables() {
        drawableColors.put("YELLOW", new Drawable[]{
                getDrawable(R.drawable.circle_yellow),
                getDrawable(R.drawable.square_letter_yellow)
        });
        buttonColors.put("YELLOW", new int[]{
                0xBFFFF281, 0x79FFF281
        });

        drawableColors.put("GREEN", new Drawable[]{
                getDrawable(R.drawable.circle_green),
                getDrawable(R.drawable.square_letter_green)
        });
        buttonColors.put("GREEN", new int[]{
                0xBF89FF81, 0x7889FF81
        });

        drawableColors.put("RED", new Drawable[]{
                getDrawable(R.drawable.circle_red),
                getDrawable(R.drawable.square_letter_red)
        });
        buttonColors.put("RED", new int[]{
                0xBFFF8181, 0x78FF8181
        });

        drawableColors.put("ORANGE", new Drawable[]{
                getDrawable(R.drawable.circle_orange),
                getDrawable(R.drawable.square_letter_orange)
        });
        buttonColors.put("ORANGE", new int[]{
                0xBFFF9800, 0x78FF9800
        });
    }
}
package dev.jcasben.zenword;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import dev.jcasben.zenword.mappings.UnsortedArrayMapping;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private String referenceWord;
    private int[] ids ={R.id.buttonL0, R.id.buttonL1, R.id.buttonL2, R.id.buttonL3,
            R.id.buttonL4, R.id.buttonL5, R.id.buttonL6};
    private int widthDisplay;
    private int heightDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics) ;
        widthDisplay = metrics.widthPixels;
        heightDisplay = metrics.heightPixels;
        // Get reference word
        referenceWord = getReferenceWord();
        // Generate buttons of the circle
        setCircleButtonLetters();
        suffle(null);
        generateRowTextViews(R.id.guideW0, 7);
        generateRowTextViews(R.id.guideW1, 7);
        generateRowTextViews(R.id.guideW2, 7);
        generateRowTextViews(R.id.guideW3, 7);
        generateRowTextViews(R.id.guideW4, 7);
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
        piceWord.setText(word);

        //Enable button.
        but.setEnabled(false);
    }

    private String getReferenceWord(){
        return "castaña".toUpperCase();
    }

    public TextView[] generateRowTextViews (int guide, int letters) {
        TextView[] line = new TextView[letters];
        ConstraintLayout main = findViewById(R.id.main);
        for (int i = 0; i < letters; i++) {
            TextView x = new TextView(this);
            x.setId(View.generateViewId());
            x.setText("");
            x.setBackground(getDrawable(R.drawable.square_letter_yellow));
            x.setTextColor(Color.WHITE);
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
        for (i = 0; i < referenceWord.length(); i++) {
            Button bLetter = findViewById(ids[i]);
            bLetter.setOnClickListener(e -> setLetter(bLetter));
            bLetter.setText(new char[]{referenceWord.charAt(i)}, 0, 1);
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
        char[] word = referenceWord.toCharArray();
        // suffle the word
        for (int i = 0; i < word.length; i++) {
            int j = ran.nextInt(word.length);
            char aux = word[i];
            word[i] = word[j];
            word[j] = aux;
        }
        // put the suffle word on the buttons
        for (int i = 0; i < referenceWord.length(); i++) {
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
        for (int i = 0; i < referenceWord.length(); i++) {
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
}
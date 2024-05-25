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

import dev.jcasben.zenword.wordUtils.WordsProvider;

import java.util.*;

public class MainActivity extends AppCompatActivity {

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

    private Drawable letterBackground;
    private int widthDisplay;
    private int heightDisplay;

    private WordsProvider wordsProvider;

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

        wordsProvider = new WordsProvider(getResources().openRawResource(R.raw.paraules));
        wordsProvider.initializeGameWords();
        // Generate buttons of the circle
        startNewGame(null);
        
        //prueba de metodos
//        showWord("mec", 0);
//        showFirstLetter("castaña",1);
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

    public void generateRowTextViews (int guide, int letters, int numline) {
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
        int width = (widthDisplay/7) - (int)(0.05*widthDisplay);
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
                        width*(7-letters)/2
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
                        width*(7-letters)/2
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

            constraintSet.constrainWidth(line[i].getId(),width);
            constraintSet.constrainHeight(line[i].getId(),width);
            constraintSet.applyTo(main);
        }
    }

    private void removeTextViews() {
        ConstraintLayout main = findViewById(R.id.main);
        for (int[] ints : letterTVId) {
            for (int anInt : ints) {
                main.removeView(findViewById(anInt));
            }
        }
    }


    //name of the buttons: l0, l1, l2, l3, l4, l5, l6
    private void setCircleButtonLetters() {
        for (int id : ids) {
            Button button = findViewById(id);
            button.setVisibility(View.VISIBLE);
        }
        int i;
        for (i = 0; i < wordsProvider.getChosenWord().length(); i++) {
            Button bLetter = findViewById(ids[i]);
            bLetter.setOnClickListener(e -> setLetter(bLetter));
            bLetter.setText(new char[]{wordsProvider.getChosenWord().charAt(i)}, 0, 1);
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
        char[] word = wordsProvider.getChosenWord().toCharArray();
        // suffle the word
        for (int i = 0; i < word.length; i++) {
            int j = ran.nextInt(word.length);
            char aux = word[i];
            word[i] = word[j];
            word[j] = aux;
        }
        // put the suffle word on the buttons
        for (int i = 0; i < wordsProvider.getChosenWord().length(); i++) {
            Button bLetter = findViewById(ids[i]);
            bLetter.setText(word, i, 1);
        }
    }

    public void send(View view) {
        TextView textView = findViewById(R.id.textVWordFormation);
        String word = (String) textView.getText();
        clear(null);
        System.out.println(word);
    }

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
        for (int i = 0; i < wordsProvider.getChosenWord().length(); i++) {
            Button b = findViewById(ids[i]);
            b.setEnabled(true);
        }
        //clear text view
        TextView tv = findViewById(R.id.textVWordFormation);
        tv.setText("");
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

    public void startNewGame(View view) {
        if (letterTVId != null) removeTextViews();
        setUIColor(pickRandomColor());
        wordsProvider.initializeGameWords();
        setCircleButtonLetters();
        suffle(null);

        letterTVId = new int[5][];
        for (int i = 0; i < letterTVId.length; i++) {
            int l = wordsProvider.getHiddenWords().get(i).length();
            letterTVId[i] = new int[l];
        }

        Iterator<UnsortedArrayMapping.Pair<Integer, String>> hiddensIterator = wordsProvider.getHiddenWords().iterator();
        while (hiddensIterator.hasNext()) {
            UnsortedArrayMapping.Pair<Integer, String> pair = hiddensIterator.next();
            generateRowTextViews(guidesIds[pair.getKey()], pair.getValue().length(), pair.getKey());
        }
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
        ViewGroup group = findViewById(R.id.main);
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
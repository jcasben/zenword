package dev.jcasben.zenword;

import android.graphics.Color;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    String referenceWord;
    int[] ids ={R.id.buttonL0, R.id.buttonL1, R.id.buttonL2, R.id.buttonL3,
            R.id.buttonL4, R.id.buttonL5, R.id.buttonL6};
    int widthDisplay;
    int heightDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics ) ;
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

    private void setPossibleSolutionWordsTextV(){

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
}
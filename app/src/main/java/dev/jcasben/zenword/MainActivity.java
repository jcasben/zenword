package dev.jcasben.zenword;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    String referenceWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get reference word
        referenceWord = getReferenceWord();
        // Generate the buttons of the possible solution words.
        setPossibleSolutionWordsButtons();
        // Generate buttons of the circle
        setCircleButtonLetters();

    }


    // onClick buttons function
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
        return "banc";
    }

    private void setPossibleSolutionWordsButtons(){

    }
    //name of the buttons: l0, l1, l2, l3, l4, l5, l6
    private void setCircleButtonLetters(){
        int[] ids ={R.id.buttonL0, R.id.buttonL1, R.id.buttonL2, R.id.buttonL3,
                R.id.buttonL4, R.id.buttonL5, R.id.buttonL6};
        int i;
        for (i = 0; i < referenceWord.length(); i++) {
            Button bLetter = findViewById(ids[i]);
            bLetter.setOnClickListener(e -> setLetter(bLetter));
            bLetter.setText(referenceWord.charAt(i));
        }
        for (i = referenceWord.length() - i; i < referenceWord.length(); i++) {
            Button button = findViewById(ids[i]);
            button.setVisibility(View.INVISIBLE);
        }
    }
}
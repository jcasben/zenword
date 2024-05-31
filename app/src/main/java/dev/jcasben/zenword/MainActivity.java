package dev.jcasben.zenword;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
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

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import dev.jcasben.zenword.mappings.UnsortedArrayMapping;

import java.util.*;

public class MainActivity extends AppCompatActivity {

    private final int[] ids = {R.id.buttonL0, R.id.buttonL1, R.id.buttonL2, R.id.buttonL3,
            R.id.buttonL4, R.id.buttonL5, R.id.buttonL6};
    private final int[] guidesIds = {R.id.guideW0, R.id.guideW1, R.id.guideW2,
            R.id.guideW3, R.id.guideW4};
    private final UnsortedArrayMapping<String, Drawable[]> drawableColors =
            new UnsortedArrayMapping<>(4);
    private final UnsortedArrayMapping<String, int[]> buttonColors =
            new UnsortedArrayMapping<>(4);
    private int[][] letterTVId;
    private final String[] colors = {"YELLOW", "GREEN", "RED", "ORANGE"};
    private Drawable letterBackground;
    private int widthDisplay;
    private final int PUNTOSPARABONUS = 5;
    private int bonusPoints = 0;
    private WordsProvider wordsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Declaracion de los colores y los drawables para la UI
        initUIColorsAndDrawables();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        widthDisplay = metrics.widthPixels;
        // Generar los botones del circulo
        wordsProvider = new WordsProvider(getResources().openRawResource(R.raw.paraules));
        startNewGame(null);
    }

    // Funcion onClick para los botones del circulo
    public void setLetter(View view) {
        Button but = (Button) view;
        // Obtener la letra del boton
        String letter = but.getText().toString();
        // Ponerla en el textView
        TextView piceWord = findViewById(R.id.textVWordFormation);
        String word = piceWord.getText().toString();
        word += letter;
        word = word.toUpperCase();
        piceWord.setText(word);

        //Activar el boton
        but.setTextColor(ContextCompat.getColor(this, R.color.disabled));
        but.setEnabled(false);
    }

    public void generateRowTextViews(int guide, int letters, int numline) {
        TextView[] line = new TextView[letters];
        ConstraintLayout main = findViewById(R.id.main);
        // Creamos los textView i les ponemos los parametros que queremos
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
            line[i] = x;
            main.addView(x);
        }
        // Calculamos la separacion entre los botones
        int width = (widthDisplay / 7) - (int) (0.05 * widthDisplay);
        // Añadimos las restricciones y lo añadimos
        ConstraintSet constraintSet = new ConstraintSet();
        for (int i = 0; i < letters; i++) {
            constraintSet.connect(
                    line[i].getId(),
                    ConstraintSet.TOP,
                    guide,
                    ConstraintSet.BOTTOM,
                    10
            );

            if (i == 0) {
                constraintSet.connect(
                        line[i].getId(),
                        ConstraintSet.START,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.END,
                        width * (7 - letters) / 2
                );
            } else {
                constraintSet.connect(
                        line[i].getId(),
                        ConstraintSet.START,
                        line[i - 1].getId(),
                        ConstraintSet.END,
                        5
                );
            }

            if (i == letters - 1) {
                constraintSet.connect(
                        line[i].getId(),
                        ConstraintSet.END,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.START,
                        width * (7 - letters) / 2
                );
            } else {
                constraintSet.connect(
                        line[i].getId(),
                        ConstraintSet.END,
                        line[i + 1].getId(),
                        ConstraintSet.START,
                        5
                );
            }

            // Añadimos los TextViews a la pantalla
            constraintSet.constrainWidth(line[i].getId(), width);
            constraintSet.constrainHeight(line[i].getId(), width);
            constraintSet.applyTo(main);
        }
    }

    private void removeTextViews() {
        // Bora los TextViews
        ConstraintLayout main = findViewById(R.id.main);
        for (int[] ints : letterTVId) {
            for (int anInt : ints) {
                main.removeView(findViewById(anInt));
            }
        }
    }

    // Nombre de los botones: l0, l1, l2, l3, l4, l5, l6
    private void setCircleButtonLetters() {
        // Primero ponemos todos los botones visibles
        for (int id : ids) {
            Button button = findViewById(id);
            button.setVisibility(View.VISIBLE);
        }
        // Los id de los botones los tenemos ordenados para que queden bien
        // Ponemos las letras en los botones y añadimos el metodo onClick
        int i;
        for (i = 0; i < wordsProvider.getChosenWord().length(); i++) {
            Button bLetter = findViewById(ids[i]);
            bLetter.setOnClickListener(e -> setLetter(bLetter));
            bLetter.setText(new char[]{wordsProvider.getChosenWord().charAt(i)}, 0, 1);
        }
        // Los botones que sobran los quitamos de la vista con View.GONE
        for (; i < 7; i++) {
            Button button = findViewById(ids[i]);
            button.setVisibility(View.GONE);
        }
    }

    // Funcion onClick para el boton random
    public void suffle(View view) {
        clear(null);
        Random ran = new Random();
        char[] word = wordsProvider.getChosenWord().toCharArray();
        // Mezclar la palabra
        for (int i = 0; i < word.length; i++) {
            int j = ran.nextInt(word.length);
            char aux = word[i];
            word[i] = word[j];
            word[j] = aux;
        }
        // Poner la palabra mezclada en los botones
        for (int i = 0; i < wordsProvider.getChosenWord().length(); i++) {
            Button bLetter = findViewById(ids[i]);
            bLetter.setText(word, i, 1);
        }
    }

    public void send(View view) {
        boolean found = false;
        TextView textView = findViewById(R.id.textVWordFormation);
        String word = (String) textView.getText();
        if (word.isEmpty()) return;

        word = word.toLowerCase();
        clear(null);
        Iterator<Map.Entry<Integer, String>> hiddenIterator = wordsProvider.getHiddenWords().entrySet().iterator();

        if (word.length() >= 3) {
            // Miramos si se encuentra en las HiddenWors
            while (hiddenIterator.hasNext()) {
                Map.Entry<Integer, String> entry = hiddenIterator.next();
                // Si la palabra esta en el conjunto de palabras escondidas
                if (entry.getValue().equals(word)) {
                    showWord(wordsProvider.getValidWords().get(word), entry.getKey());
                    showMessage("Encertada!", false);
                    wordsProvider.getFound().add(word);
                    updateFoundWords(null);
                    hiddenIterator.remove();
                    wordsProvider.getWithoutBonus().remove(entry.getKey());
                    wordsProvider.decreaseHiddenWordsNumber();
                    wordsProvider.decreaseRemainingWordsBonus();
                    found = true;
                    break;
                }
            }
            // Si no se ha encontrado en las HiddenWords miramos si seria una posible solucion
            if (!found) {
                HashSet<String> sol = wordsProvider.getSolutions().get(word.length());
                if (sol.contains(word)) {
                    // Miramos si la palabra ya la habia puesto antes
                    if (!wordsProvider.getFound().contains(word)) {
                        // No la habia puesto antes
                        wordsProvider.getFound().add(word);
                        updateFoundWords(null);
                        showMessage("Paraula vàlida! Tens un bonus", false);
                        bonusPoints++;
                        Button showPointsBonus = findViewById(R.id.buttonBonus);
                        showPointsBonus.setText(String.valueOf(bonusPoints));
                    } else {
                        // Ya habia puesto la palabra
                        showMessage("Aquesta ja la tens", false);
                        updateFoundWords(wordsProvider.getValidWords().get(word));
                    }
                    found = true;
                }
            }
        }
        // Si no es ni una posible solucion
        if (!found) {
            showMessage("Paraula no vàlida", false);
            return;
        }

        // Si ya no quedan palabras escondidas significa que las has acertado todas
        if (wordsProvider.getHiddenWords().isEmpty()) {
            showMessage("Enhorabona! has guanyat", true);
            disableViews();
        }
    }

    // Funcion onClick para el boton bonus
    public void bonus(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(String.format(
                "Encertades (%d de %d):",
                wordsProvider.getNumberOfFound(),
                wordsProvider.getNumberOfPossibleSolutions()
        ));
        StringBuilder foundWords = new StringBuilder();
        Iterator<String> foundsIterator = wordsProvider.getFound().iterator();
        while (foundsIterator.hasNext()) {
            String nextWord = wordsProvider.getValidWords().get(foundsIterator.next());
            if (foundWords.length() == 0) foundWords.append(nextWord);
            else {
                foundWords.append(", ").append(nextWord);
            }
        }
        builder.setMessage(foundWords);

        // Boton OK para cerrar la ventana
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Funcion onClick para el boton de ayuda
    public void butonHelp(View view) {
        // Si tiene suficientes puntos para bonus se muestra la primera letra de una palabra random.
        int hiddenWordsNumber = wordsProvider.getRemainingWordsBonus();

        if (bonusPoints < PUNTOSPARABONUS) {
            showMessage("No tens punts sufiecients per una ajuda!", false);
            return;
        } else if (hiddenWordsNumber <= 0) {
            showMessage("Ja has emprat totes les ajudes possibles!", false);
            return;
        }

        bonusPoints -= PUNTOSPARABONUS;
        // Actualitzar el numero del boton
        Button showPointsBonus = findViewById(R.id.buttonBonus);
        showPointsBonus.setText(String.valueOf(bonusPoints));

        // Sacar una de las palabras random que están escondidas y poner la primera letra
        Random ran = new Random();
        int aux = ran.nextInt(hiddenWordsNumber);
        //while (givenBonus.contains(aux)) aux = ran.nextInt(hiddenWordsNumber);
        Iterator<Map.Entry<Integer, String>> withoutBonusIterator = wordsProvider.getWithoutBonus().entrySet().iterator();
        while (withoutBonusIterator.hasNext() && (aux >= 0)) {
            Map.Entry<Integer, String> entry = withoutBonusIterator.next();
            if (aux == 0) {
                showFirstLetter(entry.getValue(), entry.getKey());
                wordsProvider.getWithoutBonus().remove(entry.getKey());
                wordsProvider.decreaseRemainingWordsBonus();
                break;
            }
            aux--;
        }
    }

    // Limpia el campo donde se forma la palabra y se resetea el estado de los botones
    public void clear(View view) {
        // Reseteo de los botones
        for (int i = 0; i < wordsProvider.getChosenWord().length(); i++) {
            Button b = findViewById(ids[i]);
            b.setEnabled(true);
            b.setTextColor(ContextCompat.getColor(this, R.color.white));
        }

        TextView tv = findViewById(R.id.textVWordFormation);
        tv.setText("");
    }

    // Cambia los colores de la UI dado un nombre de color
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

    // Muestra la palabra @param word en la posicion @param pos
    private void showWord(String word, int pos) {
        for (int i = 0; i < letterTVId[pos].length; i++) {
            TextView textView = findViewById(letterTVId[pos][i]);
            textView.setText(String.format("%s", word.charAt(i)).toUpperCase());
        }
    }

    // Meuestra la primera letra de la palabra @param word en la posicion @param pos
    private void showFirstLetter(String word, int pos) {
        TextView textView = findViewById(letterTVId[pos][0]);
        textView.setText(String.format("%s", word.charAt(0)).toLowerCase());
    }

    // Muestra un mensaje en un toast por pantalla
    private void showMessage(String message, boolean longTime) {
        int duration;
        if (longTime) duration = Toast.LENGTH_LONG;
        else duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getApplicationContext(), message, duration);
        toast.show();
    }

    // Metodo para inicializar una partida
    public void startNewGame(View view) {
        enableViews();
        wordsProvider.initializeGameWords();
        if (letterTVId != null) removeTextViews();
        bonusPoints = 0;
        setUIColor(pickRandomColor());Button showPointsBonus = findViewById(R.id.buttonBonus);
        showPointsBonus.setText(String.valueOf(bonusPoints));
        setCircleButtonLetters();
        suffle(null);

        int lines = wordsProvider.getHiddenWords().entrySet().size();
        letterTVId = new int[lines][];
        for (int i = 0; i < letterTVId.length; i++) {
            int l = wordsProvider.getHiddenWords().get(i).length();
            letterTVId[i] = new int[l];
        }

        Iterator<Map.Entry<Integer, String>> hiddensIterator = wordsProvider.getHiddenWords().entrySet().iterator();
        while (hiddensIterator.hasNext()) {
            Map.Entry<Integer, String> pair = hiddensIterator.next();
            generateRowTextViews(guidesIds[pair.getKey()], pair.getValue().length(), pair.getKey());
        }
        updateFoundWords(null);
        Iterator<UnsortedArrayMapping.Pair<Integer, HashSet<String>>> iterator = wordsProvider.getSolutions().iterator();
        while (iterator.hasNext()) {
            UnsortedArrayMapping.Pair<Integer, HashSet<String>> pair = iterator.next();
        }
    }

    // Metodo para activar todos los Views
    private void enableViews() {
        ViewGroup group = findViewById(R.id.main);
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            v.setEnabled(true);
        }
    }

    // Itera sobre el catalogo de palabras encontradas para mostrarlas por pantalla.
    // Si se le pasa una palabra por parametro, es porque se sabe que esta va a estar
    // dentro de este catalogo y se quiere que se muestre en rojo, ya que el usuario ya
    // la habia encontrado
    public void updateFoundWords(String wordToRed) {
        TextView founds = findViewById(R.id.correctWordsTextView);
        StringBuilder foundWords = new StringBuilder();
        Iterator<String> foundsIterator = wordsProvider.getFound().iterator();
        while (foundsIterator.hasNext()) {
            String nextWord = wordsProvider.getValidWords().get(foundsIterator.next());
            if (Objects.equals(wordToRed, nextWord)) {
                nextWord = String.format("<font color='red'>%s</font>", nextWord);
            }
            if (foundWords.length() == 0) foundWords.append(nextWord);
            else {
                foundWords.append(", ").append(nextWord);
            }
        }
        founds.setText(Html.fromHtml(String.format(
                "Has encertat %d de %d possibles: %s",
                wordsProvider.getNumberOfFound(),
                wordsProvider.getNumberOfPossibleSolutions(),
                foundWords
        )));
    }

    private void disableViews() {
        int bonusId = R.id.buttonBonus;
        int resetId = R.id.buttonReset;
        ViewGroup group = findViewById(R.id.main);
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if ((v.getId() != bonusId) && (v.getId() != resetId)) v.setEnabled(false);
        }
    }

    // Funcion auxiliar para elegir un color random
    private String pickRandomColor() {
        Random ran = new Random();
        return colors[ran.nextInt(4)];
    }

    // Inicializa la interfaz de usuario con una paleta de colores
    private void initUIColorsAndDrawables() {
        drawableColors.put("YELLOW", new Drawable[]{
                AppCompatResources.getDrawable(this, R.drawable.circle_yellow),
                AppCompatResources.getDrawable(this, R.drawable.square_letter_yellow)
        });
        buttonColors.put("YELLOW", new int[]{
                0xCCFFEF63, 0x79FFF281
        });

        drawableColors.put("GREEN", new Drawable[]{
                AppCompatResources.getDrawable(this, R.drawable.circle_green),
                AppCompatResources.getDrawable(this, R.drawable.square_letter_green)
        });
        buttonColors.put("GREEN", new int[]{
                0xBF89FF81, 0x7889FF81
        });

        drawableColors.put("RED", new Drawable[]{
                AppCompatResources.getDrawable(this, R.drawable.circle_red),
                AppCompatResources.getDrawable(this, R.drawable.square_letter_red)
        });
        buttonColors.put("RED", new int[]{
                0xBFFF8181, 0x78FF8181
        });

        drawableColors.put("ORANGE", new Drawable[]{
                AppCompatResources.getDrawable(this, R.drawable.circle_orange),
                AppCompatResources.getDrawable(this, R.drawable.square_letter_orange)
        });
        buttonColors.put("ORANGE", new int[]{
                0xBFFF9800, 0x78FF9800
        });
    }
}
# 🪴 ZenWord

ZenWord is a clone of the famous original game where you have to guess a certain number of
words given the letters of the longest one. It is called "zenword" because it has been with the
intention of inducing some kind of "relaxation" using zen backgrounds and smooth colors.

This project was developed for the final project of the course Algorithms and Data Structures II 
using Android Studio and with the collaboration of [Marc Link](https://github.com/linkcla).

## 🎮 Gameplay

### Starting a new game

When you start a game, you will see the main screen, where the game is played.
<p align="center">
    <img src="main-screen.png" alt="main screen of the game" width="620" height="650"/>
</p>

In the upper panel you will see all the words that you guessed.

In the middle section are located the hidden words that you have to guess to win,

Surrounded by the buttons "clear" and "send" appears the word that you are constructing.

In the circle you have the letters that you can use to form words.

### How to play

The game consists in guessing the words that are hidden in the middle section by clicking the letters given in the circle.
You can only use each letter one time.

While you are constructing different words with the letters, you may find a solution that it is not one of the hidden
words, but it is valid. Every time that this happens, you will receive a bonus. You can exchange 5 bonus for 1 help,
pressing the button "help" (shown in the image). Each help will reveal the first letter of one of the hidden words until
all the first letters of the hidden words are revealed. 

You can also use the button shuffle to rearrange the letters in a different way, so maybe you can see a word that 
you weren't seeing before.

If you missclick a letter while you are forming a word, you can use the button clear to start constructing the word from
the beginning.

You have no time limit to guess all the words. You can spend as much time as you want. However, if you just want to
start a new game, press the "new game" button (shown in the image).

## 🔨 How to try it

To try our game, you have two options:

### From source code

- Clone our repository with the following command:

```
https://github.com/jcasben/zenword.git
```

- Open the cloned directory with Android Studio
- Select your device / emulator
- Start playing!
  
### From the apk

- Download the current release in your phone
- Install de apk
- Start playing!
package assignment1.server.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Object that handles the Hangman game.
 */
public class HangmanGame {

    private final String FILE = "words.txt";

    private int score;
    private int fails;
    private String word;
    private boolean isPlaying = false;
    private ArrayList<Character> completedWord;
    private ArrayList<String> wordList = new ArrayList<>();

    public HangmanGame() {
        loadWordlist();
    }
    
    /**
     * Starts a new round in the Hangman game.
     *
     * @return Returns a response in text.
     */
    public String newRound() {
        fails = 0;
        generateNewWord();
        startGame();
        return "Your new word is " + word.length() + " characters long";
    }

    private void generateNewWord() {
        word = getRandomWord().toLowerCase();
        completedWord = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            completedWord.add('-');
        }
    }

    private String getRandomWord() {
        Random random = new Random();
        int randomIndex = random.nextInt(wordList.size());
        return wordList.get(randomIndex);
    }

    private void loadWordlist() {
        try (BufferedReader wordFile = new BufferedReader(new FileReader(FILE))) {
            wordFile.lines().forEachOrdered(line -> wordList.add(line));
        } catch (FileNotFoundException ex) {
            wordList.add("Garden");
            wordList.add("Trumpet");
            wordList.add("Playground");
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * @return Returns the current word in the Hangman game.
     */
    public String getWord() {
        return word;
    }

    /**
     * @return Returns the current score in the Hangman game.
     */
    public int getScore() {
        return score;
    }

    /**
     * Performs a guess in the Hangman game.
     *
     * @param guess The guess. Can either be a single character or a word.
     * Performs a word guess if the guess is longer than 1 character.
     * @return Returns a response in text.
     */
    public String performGuess(String guess) {
        guess = guess.toLowerCase();
        if (guess.length() > 1) {
            if (guess.equals(word)) {
                return correctGuess();
            } else {
                return incorrectTry();
            }
        } else {
            if (word.contains(guess)) {
                return correctTry(guess.charAt(0));
            } else {
                return incorrectTry();
            }
        }
    }

    private String correctGuess() {
        score++;
        endRound();
        return "Correct!";
    }

    private String correctTry(char character) {
        String currentWord = "";
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == character) {
                completedWord.set(i, character);
            }
            currentWord += completedWord.get(i);
        }

        if (word.equals(currentWord)) {
            score++;
            endRound();
            return "Correct! The word was " + word + "!";
        } else {
            return "Correct! " + character + " is a character of the word. " + getCurrentProgress();
        }
    }

    private String incorrectTry() {
        fails++;
        if (word.length() <= fails) {
            score--;
            endRound();
            return "Incorrect! Game over! Correct word was: " + word;
        } else {
            return "Incorrect, " + fails + " of " + word.length() + " guesses made. " + getCurrentProgress();
        }
    }

    private String getCurrentProgress() {
        String text = "Current progress: ";
        for (char c : completedWord) {
            text += c;
        }
        return text;
    }

    /**
     * @return Returns the current state of the game.
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    private void startGame() {
        isPlaying = true;
    }

    private void endRound() {
        isPlaying = false;
    }
}

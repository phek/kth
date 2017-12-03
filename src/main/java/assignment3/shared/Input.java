package assignment3.shared;

import java.io.Serializable;

/**
 * Handles user input
 */
public class Input implements Serializable {

    private String[] input;
    private String delimiter = " ";

    /**
     * Creates a new InputHandler.
     *
     * @param input The user input.
     */
    public Input(String input) {
        this.input = input.trim().split(delimiter);
    }

    /**
     * @return The command of the input.
     */
    public String getCommand() {
        return input[0];
    }

    /**
     * @param index The index of the param.
     * @return The specified param from the user input.
     */
    public String getParam(int index) {
        return input[index + 1];
    }

    /**
     * @return The whole input string.
     */
    public String getInput() {
        String text = "";
        for (String t : input) {
            text += t;
        }
        return text;
    }
}

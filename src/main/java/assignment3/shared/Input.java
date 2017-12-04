package assignment3.shared;

import java.io.Serializable;

/**
 * Handles user input
 */
public class Input implements Serializable {

    private String[] input;
    private String delimiter = " ";

    /**
     * Creates an object to handle the data.
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
     * @return The specified param from the user input or null if it doesn't
     * exist.
     */
    public String getParam(int index) {
        if (input.length > index + 1) {
            return input[index + 1];
        } else {
            return null;
        }
    }

    /**
     * Converts all parameters after the starting point to a space-separated string.
     *
     * @param start The starting point. 
     * @return All parameters after the starting point separated by a space.
     */
    public String getParamsAsString(int start) {
        start++;
        String params = "";
        boolean first = true;
        for (int i = start; i < input.length; i++) {
            if (first) {
                params += input[i];
                first = false;
            } else {
                params += " " + input[i];
            }
        }
        return params;
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

package assignment1.shared;

/**
 * Object that provides thread safe output, all methods are synchronized.
 */
public class ThreadSafeOutput {

    /**
     * Prints the specified output to <code>System.out</code>.
     *
     * @param output The output to print.
     */
    public synchronized void print(String output) {
        System.out.print(output);
    }

    /**
     * Prints the specified output, plus a line break, to <code>System.out</code>.
     *
     * @param output The output to print.
     */
    public synchronized void println(String output) {
        System.out.println(output);
    }
}

package assignment2.server.startup;

import assignment2.server.net.Server;

public class ServerStartup {

    /**
     * Main method for starting a server.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        new Server().start();
    }

}

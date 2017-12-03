package assignment3.server.model.file;

import java.util.ArrayList;

public class FileHandler {

    private final FileSystem fileSystem = new FileSystem();

    public void addFile(String creator, String filename) {
        fileSystem.addFile(creator, filename);
    }
    
    public ArrayList<String> getFilelist() {
        return fileSystem.getFilelist();
    }
    
}

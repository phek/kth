package assignment3.server.model.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileSystem {

    private final Map<String, File> files = Collections.synchronizedMap(new HashMap<>());

    public ArrayList<File> getFiles() {
        return new ArrayList<>(files.values());
    }

    public void addFile(String username, String filename) {
        ArrayList<File> fileList = getFiles();
        for (File file : fileList) {
            if (file.getFilename().equals(filename)) {
                throw new FileAlreadyExistsException("A file with that name already exists.");
            }
        }
        files.put(username, new File(filename));
    }
    
    public ArrayList<String> getFilelist() {
        ArrayList<File> fileList = getFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        for (File file : fileList) {
            fileNames.add(file.getFilename());
        }
        return fileNames;
    }

}

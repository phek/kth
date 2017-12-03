package assignment3.server.model.file;

public class File {

    private String filename;
    private int size;
    private boolean isReadable = false;
    private boolean isWriteable = false;
    
    public File(String filename) {
        this.filename = filename;
        this.size = 50;
    }
    
    public File(String filename, boolean isReadable, boolean isWriteable) {
        this.filename = filename;
        this.isReadable = isReadable;
        this.isWriteable = isWriteable;
    }
    
    public boolean isPublic() {
        return isReadable || isWriteable;
    }
    
    public boolean isReadable() {
        return isReadable;
    }
    
    public boolean isWriteable() {
        return isWriteable;
    }
    
    public String getFilename() {
        return filename;
    }
}

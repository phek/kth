package assignment3.dto;

import java.io.Serializable;

public interface FileDTO extends Serializable {

    public boolean isPublic();

    public boolean isReadable();

    public boolean isWriteable();

    public String getFilename();
    
    public long getCreator();
    
    public int getSize();
    
    public String getContent();

}

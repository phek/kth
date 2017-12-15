package assignment3.dto;

import java.io.Serializable;

/**
 * The read only view of a file.
 */
public interface FileDTO extends Serializable {

    /**
     * @return If the file is public.
     */
    public boolean isPublic();

    /**
     * @return If the file is readable.
     */
    public boolean isReadable();

    /**
     * @return If the file is writable.
     */
    public boolean isWritable();

    /**
     * @return The filename.
     */
    public String getFilename();
    
    /**
     * @return The creator of the file.
     */
    public long getCreator();
    
    /**
     * @return The size of the file.
     */
    public int getSize();
    
    /**
     * @return The content of the file.
     */
    public String getContent();

}

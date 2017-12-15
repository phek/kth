package assignment3.server.model.file;

import assignment3.dto.FileDTO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@NamedQueries({
    @NamedQuery(
            name = "removeFileByName",
            query = "DELETE FROM File f WHERE f.filename LIKE :filename"
    )
    ,
    @NamedQuery(
            name = "getFileByName",
            query = "SELECT f FROM File f WHERE f.filename LIKE :filename",
            lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
            name = "getAllFiles",
            query = "SELECT f FROM File f",
            lockMode = LockModeType.OPTIMISTIC
    )
})

@Entity(name = "File")
public class File implements FileDTO {

    @Id
    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "creator", nullable = false)
    private long creator;

    @Column(name = "filesize", nullable = false)
    private int filesize;

    @Column(name = "isReadable", nullable = false)
    private boolean isReadable;

    @Column(name = "isWritable", nullable = false)
    private boolean isWritable;

    @Column(name = "content", nullable = false)
    private String content;

    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    /**
     * A public no-arg constructor is required by JPA.
     */
    public File() {
        this(null, 0, false, false, "");
    }

    /**
     * Creates a private file.
     *
     * @param filename The filename.
     * @param creator The userID of the creator.
     */
    public File(String filename, long creator) {
        this(filename, creator, false, false, "");
    }

    /**
     * Creates a file with specific permissions.
     *
     * @param filename The filename.
     * @param creator The userID of the creator.
     * @param isReadable If it's readable.
     * @param isWritable If it's writable.
     * @param content The content of the file.
     */
    public File(String filename, long creator, boolean isReadable, boolean isWritable, String content) {
        this.filename = filename;
        this.creator = creator;
        this.isReadable = isReadable;
        this.isWritable = isWritable;
        this.content = content;
        this.filesize = content.length();
    }

    /**
     * Writes to the file.
     *
     * @param content The content to be written.
     */
    public void write(String content) {
        this.content = content;
        this.filesize = content.length();
    }

    @Override
    public boolean isPublic() {
        return isReadable || isWritable;
    }

    @Override
    public boolean isReadable() {
        return isReadable;
    }

    @Override
    public boolean isWritable() {
        return isWritable;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public long getCreator() {
        return creator;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public int getSize() {
        return filesize;
    }
}

package assignment3.server.integration;

import assignment3.dto.FileDTO;
import assignment3.server.model.file.File;
import assignment3.shared.LoginData;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import assignment3.dto.UserDataDTO;
import assignment3.server.model.user.UserData;

/**
 * This is a data access object (DAO) that encapsulates all database calls in
 * the program.
 */
public class DatabaseHandler {

    private final EntityManagerFactory factory;
    private final ThreadLocal<EntityManager> localManager = new ThreadLocal<>();

    public DatabaseHandler() {
        factory = Persistence.createEntityManagerFactory("KTH");
    }

    /**
     * Adds the file to the database. This method does not commit your request.
     * This needs to be done manually after all desired requests are performed.
     *
     * @param file The file to add.
     */
    public void addFile(FileDTO file) {
        EntityManager manager = startTransaction();
        manager.persist(file);
    }

    /**
     * Checks if the file exists in the database. This method does not commit
     * your request. This needs to be done manually after all desired requests
     * are performed.
     *
     * @param filename The name of the file.
     * @return true if the file exists, false otherwise.
     */
    public boolean fileExists(String filename) {
        return getFile(filename) != null;
    }

    /**
     * Gets the file from the database. This method does not commit your
     * request. This needs to be done manually after all desired requests are
     * performed.
     *
     * @param filename The name of the file.
     * @return The file if found, null otherwise.
     */
    public File getFile(String filename) {
        EntityManager manager = startTransaction();
        try {
            return manager.createNamedQuery("getFileByName", File.class)
                    .setParameter("filename", filename)
                    .getSingleResult();
        } catch (NoResultException fileNotFund) {
            return null;
        }
    }

    /**
     * Updates the content of the file. This method does not commit your
     * request. This needs to be done manually after all desired requests are
     * performed.
     *
     * @param filename The name of the file.
     * @param content The content to be written.
     */
    public void updateFileContent(String filename, String content) {
        File file = getFile(filename);
        file.write(content);
    }

    /**
     * Removes the file from the database. This method does not commit your
     * request. This needs to be done manually after all desired requests are
     * performed.
     *
     * @param filename The filename.
     */
    public void removeFile(String filename) {
        EntityManager manager = startTransaction();
        manager.createNamedQuery("removeFileByName", File.class)
                .setParameter("filename", filename).executeUpdate();
    }

    /**
     * Gets all the files from the database. This method does not commit your
     * request. This needs to be done manually after all desired requests are
     * performed.
     *
     * @return A list of the files.
     */
    public List<File> getAllFiles() {
        EntityManager manager = startTransaction();
        try {
            return manager.createNamedQuery("getAllFiles", File.class).getResultList();
        } catch (NoResultException fileNotFund) {
            return new ArrayList<>();
        }
    }

    /**
     * Adds the user to the database. This method commits automatically.
     *
     * @param user The user to be added.
     */
    public void addUser(UserDataDTO user) {
        try {
            EntityManager manager = startTransaction();
            manager.persist(user);
        } finally {
            endTransaction();
        }
    }

    /**
     * Checks if the username exists in the database. This method does not
     * commit your request. This needs to be done manually after all desired
     * requests are performed.
     *
     * @param username The username.
     * @return true if the user exists, false otherwise.
     */
    public boolean usernameExists(String username) {
        return getUser(username) != null;
    }

    /**
     * Updates the username of the user. This method does not commit your
     * request. This needs to be done manually after all desired requests are
     * performed.
     *
     * @param username The current username.
     * @param newName The new username.
     */
    public void changeUsername(String username, String newName) {
        UserData userData = getUser(username);
        userData.setUsername(newName);
    }

    /**
     * Gets the user-data from the database. This method does not commit your
     * request. This needs to be done manually after all desired requests are
     * performed.
     *
     * @param loginData The login data.
     * @return The user if found, null otherwise.
     */
    public UserData getUser(LoginData loginData) {
        EntityManager manager = startTransaction();
        try {
            return manager.createNamedQuery("getUserByLogin", UserData.class)
                    .setParameter("username", loginData.getUsername())
                    .setParameter("password", loginData.getPassword())
                    .getSingleResult();
        } catch (NoResultException userNotFund) {
            return null;
        }
    }

    /**
     * Commits all uncommitted requests.
     */
    public void commit() {
        endTransaction();
    }

    private UserData getUser(String username) {
        EntityManager manager = startTransaction();
        try {
            return manager.createNamedQuery("getUserByName", UserData.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException userNotFund) {
            return null;
        }
    }

    private EntityManager startTransaction() {
        EntityManager manager = factory.createEntityManager();
        localManager.set(manager);
        EntityTransaction transaction = manager.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        return manager;
    }

    private void endTransaction() {
        localManager.get().getTransaction().commit();
    }

}

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

public class DatabaseHandler {

    private final EntityManagerFactory factory;
    private final ThreadLocal<EntityManager> localManager = new ThreadLocal<>();

    public DatabaseHandler() {
        factory = Persistence.createEntityManagerFactory("KTH");
    }

    public void addFile(FileDTO file) {
        EntityManager manager = startTransaction();
        manager.persist(file);
    }

    public boolean fileExists(String filename) {
        return getFile(filename) != null;
    }

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

    public void updateFileContent(String filename, String content) {
        File file = getFile(filename);
        file.write(content);
    }

    public boolean removeFile(String filename) {
        EntityManager manager = startTransaction();
        try {
            manager.createNamedQuery("removeFileByName", File.class)
                    .setParameter("filename", filename).executeUpdate();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<File> getAllFiles() {
        EntityManager manager = startTransaction();
        try {
            return manager.createNamedQuery("getAllFiles", File.class).getResultList();
        } catch (NoResultException fileNotFund) {
            return new ArrayList<>();
        }
    }

    public void addUser(UserDataDTO user) {
        try {
            EntityManager manager = startTransaction();
            manager.persist(user);
        } finally {
            endTransaction();
        }
    }

    public boolean usernameExists(String username) {
        return getUser(username) != null;
    }

    public void changeUsername(String username, String newName) {
        UserData userData = getUser(username);
        userData.setUsername(newName);
    }

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

package assignment3.server.model.user;

import assignment3.dto.UserDataDTO;
import assignment3.shared.LoginData;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;

@NamedQueries({
    @NamedQuery(
            name = "getUserByName",
            query = "SELECT user FROM UserData user WHERE user.username LIKE :username",
            lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
            name = "getUserByLogin",
            query = "SELECT user FROM UserData user WHERE user.username LIKE :username AND user.password LIKE :password",
            lockMode = LockModeType.OPTIMISTIC
    )
    ,
    @NamedQuery(
            name = "getAllUsers",
            query = "SELECT user FROM UserData user",
            lockMode = LockModeType.OPTIMISTIC
    )
})

@Entity(name = "UserData")
public class UserData implements UserDataDTO {

    @Id
    @Column(name = "userID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userID;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Version
    @Column(name = "OPTLOCK")
    private int versionNum;

    /**
     * A public no-arg constructor is required by JPA.
     */
    public UserData() {
        this.username = null;
        this.password = null;
    }

    /**
     * Creates user-data from the login-data.
     *
     * @param loginData The login-data.
     */
    public UserData(LoginData loginData) {
        this.username = loginData.getUsername();
        this.password = loginData.getPassword();
    }

    @Override
    public long getUserID() {
        return userID;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}

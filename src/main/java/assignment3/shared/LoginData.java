package assignment3.shared;

import java.io.Serializable;

/**
 * Stores the login-data.
 */
public class LoginData implements Serializable {

    private final String username;
    private final String password;

    /**
     * Creates a new instance with the specified username and password.
     *
     * @param username The username.
     * @param password The password.
     */
    public LoginData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Get the value of password
     *
     * @return The value of password or an empty string if the password is null.
     */
    public String getPassword() {
        return password != null ? password : "";
    }

    /**
     * Get the value of username
     *
     * @return The value of username.
     */
    public String getUsername() {
        return username;
    }
}

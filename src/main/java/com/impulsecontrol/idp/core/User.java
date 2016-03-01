package com.impulsecontrol.idp.core;

import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kerrk on 2/19/16.
 */
@Entity
@Table(name = "user")
public class User implements Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "user", cascade = { CascadeType.ALL })
    private List<UserToRole> userRoles = new ArrayList<>();

    public User() {

    }

    private static int workload = 12;

    public User(String username, String password) {
        this.username = username;
        this.password = encryptPassword(password);
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserToRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserToRole> userRoles) {
        this.userRoles = userRoles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static String encryptPassword(String password) {
        String salt = BCrypt.gensalt(workload);
        String hashedPW = BCrypt.hashpw(password, salt);
        return(hashedPW);
    }

    public static Boolean verifyPassword(String pw, String encryptedPW) {
        boolean verified = false;

        if(encryptedPW == null || !encryptedPW.startsWith("$2a$")) {
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
        }

        return BCrypt.checkpw(pw, encryptedPW);
    }
}

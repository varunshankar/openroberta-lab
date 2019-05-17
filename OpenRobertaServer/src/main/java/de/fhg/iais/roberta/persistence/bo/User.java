package de.fhg.iais.roberta.persistence.bo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.fhg.iais.roberta.util.Encryption;
import de.fhg.iais.roberta.util.Util1;

@Entity
@Table(name = "USER")
public class User implements WithSurrogateId {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "USERGROUP_ID")
    private UserGroup userGroup;

    @Column(name = "ACCOUNT")
    private String account;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @Column(name = "CREATED")
    private Timestamp created;

    @Column(name = "LAST_LOGIN")
    private Timestamp lastLogin;

    @Column(name = "TAGS")
    private String tags;

    @Column(name = "ACTIVATED")
    private boolean activated;

    @Column(name = "YOUNGER_THAN_14")
    private boolean youngerThen14;

    protected User() {
        // Hibernate
    }

    /**
     * create a new user
     * @param account the system wide unique account of a new user
     */
    public User(UserGroup userGroup, String account) {
        this.account = account;
        this.userGroup = userGroup;
        this.created = Util1.getNow();
        this.lastLogin = Util1.getNow();
    }

    public boolean isPasswordCorrect(String passwordToCheck) throws Exception {
        return Encryption.isPasswordCorrect(this.password, passwordToCheck);
    }

    public void setPassword(String password) throws Exception {
        this.password = Encryption.createHash(password);
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * @return the userGroup
     */
    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getTags() {
        return this.tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public String getAccount() {
        return this.account;
    }

    public Timestamp getCreated() {
        return this.created;
    }

    public Timestamp getLastLogin() {
        return this.lastLogin;
    }

    public void setLastLogin() {
        this.lastLogin = Util1.getNow();
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isYoungerThen14() {
        return this.youngerThen14;
    }

    public void setYoungerThen14(boolean youngerThen14) {
        this.youngerThen14 = youngerThen14;
    }

    @Override
    public String toString() {
        return "User [id=" + this.id + ", group=" + this.userGroup + ", account=" + this.account + ", userName=" + this.userName + "]";
    }

}

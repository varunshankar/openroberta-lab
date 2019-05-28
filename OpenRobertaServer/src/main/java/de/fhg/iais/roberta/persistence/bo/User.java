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
    private Group group;

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
     * create a new user. The pair (group,account) must be unique
     *
     * @param group the group the (new) user belongs to. May be null.
     * @param account the account of a (new) user
     */
    public User(Group group, String account) {
        this.group = group;
        this.account = account;
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
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the group this user belongs to (this is not a group, which this user can own)
     */
    public Group getGroup() {
        return this.group;
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

    /**
     * true, if the user has a email, has requested an activation of his/her account, has has shown evidence, that he/she has got the activation email by
     * clicking a link sent with the email
     *
     * @return
     */
    public boolean isActivated() {
        return this.activated;
    }

    /**
     * set the activation flag to true or false. Setting to true means,<br>
     * - the user has a email,<br>
     * - has requested an activation of his/her account,<br>
     * - has has shown evidence, that he/she has got the activation email by clicking a link sent with the email
     *
     * @param activated true, if the user account IS activated; false, if not activated
     */
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
        return "User [id=" + this.id + ", group=" + this.group + ", account=" + this.account + ", userName=" + this.userName + "]";
    }

}

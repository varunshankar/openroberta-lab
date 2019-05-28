package de.fhg.iais.roberta.persistence.bo;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.fhg.iais.roberta.util.Util1;
import de.fhg.iais.roberta.util.dbc.Assert;

@Entity
@Table(name = "USERGROUP")
public class Group implements WithSurrogateId {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "OWNER_ID")
    private User owner;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CREATED")
    private Timestamp created;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "USERGROUP_ROBOTS", joinColumns = @JoinColumn(name = "USERGROUP_ID"), inverseJoinColumns = @JoinColumn(name = "ROBOT_ID"))
    private Set<Robot> robots;

    protected Group() {
        // Hibernate
    }

    /**
     * create a new group
     *
     * @param name the name of the group, not null
     * @param owner the user who created and thus owns the group
     */
    public Group(String name, User owner) {
        Assert.notNull(name);
        Assert.notNull(owner);
        this.name = name;
        this.owner = owner;
        this.created = Util1.getNow();
    }

    @Override
    public int getId() {
        return this.id;
    }

    /**
     * get the name
     *
     * @return the name, never <code>null</code>
     */

    public String getName() {
        return this.name;
    }

    /**
     * get the owner
     *
     * @return the owner, never <code>null</code>
     */
    public User getOwner() {
        return this.owner;
    }

    public Timestamp getCreated() {
        return this.created;
    }

    public void addRobot(Robot robot) {
        if ( this.robots == null ) {
            this.robots = new HashSet<>();
        }
        this.robots.add(robot);
    }

    public void removeRobot(Robot robot) {
        if ( this.robots == null ) {
            this.robots = new HashSet<>();
        }
        this.robots.remove(robot);
    }

    public Set<Robot> getRobots() {
        if ( this.robots == null ) {
            this.robots = new HashSet<>();
        }
        return new HashSet<>(this.robots);
    }

    @Override
    public String toString() {
        return "Group [id="
            + this.id
            + ", name="
            + this.name
            + ", ownerId="
            + (this.owner == null ? "???" : this.owner.getId())
            + ", created="
            + this.created
            + "]";
    }
}

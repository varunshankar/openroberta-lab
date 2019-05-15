package de.fhg.iais.roberta.persistence.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.iais.roberta.persistence.bo.User;
import de.fhg.iais.roberta.persistence.bo.UserGroup;
import de.fhg.iais.roberta.persistence.util.DbSession;
import de.fhg.iais.roberta.util.Key;
import de.fhg.iais.roberta.util.Pair;
import de.fhg.iais.roberta.util.dbc.Assert;

/**
 * DAO class to load and store group objects. A DAO object is always bound to a session. This session defines the transactional context, in which the
 * database access takes place.
 *
 * @author eovchinnik
 */
public class UserGroupDao extends AbstractDao<UserGroup> {
    private static final Logger LOG = LoggerFactory.getLogger(UserGroupDao.class);

    /**
     * create a new DAO for groups. This creation is cheap.
     *
     * @param session the session used to access the database.
     */
    public UserGroupDao(DbSession session) {
        super(UserGroup.class, session);
    }

    /**
     * persist a group object that is owned by the caller
     *
     * @param name the name of the group, never null
     * @param owner the user who is the author of this program, never null
     * @param programTimestamp timestamp of the last change of the program (if it already existed); <code>null</code> if a new program is saved
     * @return a pair of (message-key, userGroup). If the program is persisted successfully, the program is NOT null.
     */
    public Pair<Key, UserGroup> persistUserGroup(String name, User owner, Timestamp timestamp) //
    {
        Assert.notNull(name);
        Assert.notNull(owner);
        UserGroup userGroup = load(name, owner);
        if ( userGroup == null ) {
            if ( timestamp == null ) {
                userGroup = new UserGroup(name, owner);
                this.session.save(userGroup);
                return Pair.of(Key.GROUP_CREATE_SUCCESS, userGroup);
            } else {
                return Pair.of(Key.GROUP_CREATE_ERROR_GROUP_TO_UPDATE_NOT_FOUND, null);
            }
        } else {
            return Pair.of(Key.GROUP_ALREADY_EXISTS, null); //??
        }
    }

    /**
     * load a userGroup from the database, identified by its owner, its name (both make up the "business" key of a group)<br>
     * The timestamp used for optimistic locking is <b>not</b> checked here. <b>The caller is responsible to do that!</b>
     *
     * @param name the name of the program, never null
     * @param owner user who owns the program, never null
     * @return the userGroup, null if the userGroup is not found
     */
    public UserGroup load(String name, User owner) {
        Assert.notNull(name);
        Assert.notNull(owner);
        Query hql = this.session.createQuery("from UserGroup where name=:name and owner=:owner");
        hql.setString("name", name);
        hql.setEntity("owner", owner);
        @SuppressWarnings("unchecked")
        List<UserGroup> il = hql.list();
        Assert.isTrue(il.size() <= 1);
        return il.size() == 0 ? null : il.get(0);
    }

    public void addRobotsToUserGroup(String name, User owner, int robotId) {

    }

    public ArrayList loadRobots(String name, User owner) {
        return null;
    }

    //TODO: check if it's correct
    public void rename(String name, User owner, String newName) {
        Assert.notNull(name);
        Assert.notNull(owner);
        UserGroup userGroup = new UserGroup(name, owner);
        userGroup.setName(newName);
    }

    public int deleteByName(String name, User owner) {
        UserGroup toBeDeleted = load(name, owner);
        if ( toBeDeleted == null ) {
            return 0;
        } else {
            this.session.delete(toBeDeleted);
            return 1;
        }
    }

    /**
     * load all userGroups persisted in the database which are owned by a given user
     *
     * @return the list of all userGroups, may be an empty list, but never null
     */
    public List<UserGroup> loadAll(User owner) {
        Query hql = this.session.createQuery("from UserGroup where owner=:owner");
        hql.setEntity("owner", owner);
        @SuppressWarnings("unchecked")
        List<UserGroup> il = hql.list();
        return Collections.unmodifiableList(il);
    }

    /**
     * load all userGroups persisted in the database
     *
     * @return the list of all userGroup, may be an empty list, but never null
     */
    public List<UserGroup> loadAll() {
        Query hql = this.session.createQuery("from UserGroup");
        @SuppressWarnings("unchecked")
        List<UserGroup> il = hql.list();
        return Collections.unmodifiableList(il);
    }
}

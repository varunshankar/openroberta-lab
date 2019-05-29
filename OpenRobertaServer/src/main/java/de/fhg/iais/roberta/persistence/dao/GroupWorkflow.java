package de.fhg.iais.roberta.persistence.dao;

import de.fhg.iais.roberta.persistence.bo.AccessRight;
import de.fhg.iais.roberta.persistence.bo.AccessRightHistory;
import de.fhg.iais.roberta.persistence.bo.Group;
import de.fhg.iais.roberta.persistence.bo.User;
import de.fhg.iais.roberta.persistence.util.DbSession;
import de.fhg.iais.roberta.util.Key;
import de.fhg.iais.roberta.util.dbc.Assert;

public class GroupWorkflow {

    private final DbSession session;

    /**
     * create a new GroupWorkflow object.
     */
    public GroupWorkflow(DbSession session) {
        this.session = session;
    }

    public Key changeGroupAccessRight(String userName, String groupName, AccessRight newAccessRight) {
        UserDao userDao = new UserDao(this.session);
        GroupDao groupDao = new GroupDao(this.session);
        User groupOwner = userDao.loadUser(null, userName);
        Assert.notNull(groupOwner);
        Group group = groupDao.load(groupName, groupOwner);
        Assert.notNull(group);
        AccessRight accessRightOld = group.getAccessRight();
        group.setAccessRight(newAccessRight);
        AccessRightHistory accessRightToSave = new AccessRightHistory(group, accessRightOld);
        this.session.save(accessRightToSave);
        return Key.ACCESS_RIGHT_CHANGED;
    }
}

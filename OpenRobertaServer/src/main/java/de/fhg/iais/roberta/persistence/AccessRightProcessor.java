package de.fhg.iais.roberta.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.iais.roberta.persistence.bo.Program;
import de.fhg.iais.roberta.persistence.bo.Relation;
import de.fhg.iais.roberta.persistence.bo.Robot;
import de.fhg.iais.roberta.persistence.bo.User;
import de.fhg.iais.roberta.persistence.dao.AccessRightDao;
import de.fhg.iais.roberta.persistence.dao.ProgramDao;
import de.fhg.iais.roberta.persistence.dao.RobotDao;
import de.fhg.iais.roberta.persistence.dao.UserDao;
import de.fhg.iais.roberta.persistence.util.DbSession;
import de.fhg.iais.roberta.persistence.util.HttpSessionState;
import de.fhg.iais.roberta.util.Key;

public class AccessRightProcessor extends AbstractProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(AccessRightProcessor.class);

    public AccessRightProcessor(DbSession dbSession, HttpSessionState httpSessionState) {
        super(dbSession, httpSessionState);
    }

    /**
     * a program, which is identified by the triple (ownerId, robotId, programName) has to be shared with another user. The right on the shared program may be
     * either "WRITE" or "READ".
     *
     * @param ownerId the owner (that is the actor on which behalf this request is executed)
     * @param robotId
     * @param programName
     * @param userToShareName the account name (a String!) of the user who should get access to a program
     * @param right "WRITE" or "READ"
     */
    public void shareToUser(int ownerId, String robotName, String programName, int authorId, String userToShareName, String right) {
        UserDao userDao = new UserDao(this.dbSession);
        User owner = userDao.get(ownerId);
        User author = userDao.get(authorId);
        User userToShare = userDao.loadUser(null, userToShareName);
        executeShare(owner, robotName, programName, author, userToShare, right);
    }

    /**
     * a program, which is identified by the triple (ownerId, robotId, programName) has to be shared with another user. The right on the shared program may be
     * either "WRITE" or "READ".
     *
     * @param ownerId the owner (that is the actor on which behalf this request is executed)
     * @param robotId
     * @param programName
     * @param userToShareName the account name (a String!) of the user who should get access to a program
     * @param right "WRITE" or "READ"
     */
    public void shareDelete(String ownerName, String robotName, String programName, String authorName, int userToShareId) {
        UserDao userDao = new UserDao(this.dbSession);
        User owner = userDao.loadUser(null, ownerName);
        User userToShare = userDao.get(userToShareId);
        User author = userDao.loadUser(null, authorName);
        executeShare(owner, robotName, programName, author, userToShare, "NONE");
    }

    /**
     * a program, which is identified by the triple (ownerId, robotId, programName)<br>
     * - has to be shared with another user (right is either "WRITE" or "READ") or <br>
     * - the access right has to be removed (right is "NONE")
     *
     * @param owner
     * @param robotId
     * @param programName
     * @param userToShare
     * @param right
     */
    public void executeShare(User owner, String robotName, String programName, User author, User userToShare, String right) {
        ProgramDao programDao = new ProgramDao(this.dbSession);
        RobotDao robotDao = new RobotDao(this.dbSession);

        Program programToShare = null;
        Key responseKey = null;
        if ( owner == null ) {
            responseKey = Key.OWNER_DOES_NOT_EXIST;
        } else if ( userToShare == null ) {
            responseKey = Key.USER_TO_SHARE_DOES_NOT_EXIST;
        } else {
            Robot robot = robotDao.loadRobot(robotName);
            if ( robot == null ) {
                responseKey = Key.ROBOT_DOES_NOT_EXIST;
            } else {
                programToShare = programDao.load(programName, owner, robot, author);
                if ( programToShare == null ) {
                    responseKey = Key.PROGRAM_TO_SHARE_DOES_NOT_EXIST;
                }
            }
        }
        if ( responseKey != null ) {
            setError(responseKey);
            return;
        }

        AccessRightDao accessRightDao = new AccessRightDao(this.dbSession);
        try {
            if ( right.equals("NONE") ) {
                accessRightDao.deleteAccessRight(userToShare, programToShare);
                setSuccess(Key.ACCESS_RIGHT_DELETED);
            } else {
                Relation relation = Relation.valueOf(right);
                accessRightDao.persistAccessRight(userToShare, programToShare, relation);
                setSuccess(Key.ACCESS_RIGHT_CHANGED);
            }
        } catch ( Exception e ) {
            String msg =
                "Invalid share request. Owner:" + owner + ", robot:" + robotName + ", program:" + programName + ", with:" + userToShare + ", right:" + right;
            AccessRightProcessor.LOG.error(msg);
            setError(Key.SERVER_ERROR);
        }
    }
}
package de.fhg.iais.roberta.javaServer.basics;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fhg.iais.roberta.persistence.bo.Group;
import de.fhg.iais.roberta.persistence.bo.Robot;
import de.fhg.iais.roberta.persistence.bo.User;
import de.fhg.iais.roberta.persistence.dao.GroupDao;
import de.fhg.iais.roberta.persistence.dao.RobotDao;
import de.fhg.iais.roberta.persistence.dao.UserDao;
import de.fhg.iais.roberta.persistence.util.DbSession;
import de.fhg.iais.roberta.persistence.util.SessionFactoryWrapper;
import de.fhg.iais.roberta.util.Key;
import de.fhg.iais.roberta.util.Pair;

public class GroupTest {
    private static SessionFactoryWrapper sessionFactoryWrapper;

    @BeforeClass
    public static void setup() {
        sessionFactoryWrapper = new SessionFactoryWrapper("hibernate-cfg.xml", "jdbc:hsqldb:file:./db-3.3.2/openroberta-db");
    }

    @AfterClass
    public static void tearDown() {
        sessionFactoryWrapper.getNativeSession().createSQLQuery("shutdown").executeUpdate();
    }

    @Test
    public void testUser() {
        Session nativeSession = sessionFactoryWrapper.getNativeSession();
        nativeSession.beginTransaction();
        String sqlGetQuery = "select * from USER";

        @SuppressWarnings("unchecked")
        List<Object[]> resultSet = nativeSession.createSQLQuery(sqlGetQuery).list();

        int counter = 0;
        for ( Object[] object : resultSet ) {
            System.out.println(Arrays.toString(object));
            counter++;
        }

        System.out.println(resultSet.size());
        System.out.println(counter);
        nativeSession.close();
    }

    @Test
    public void testGroup() {
        {
            final DbSession session = sessionFactoryWrapper.getSession();
            UserDao userDao = new UserDao(session);
            User testUser = userDao.loadUser(null, "testUser");
            assertNotNull(testUser);
            testUser.setActivated(false);
            session.close();
        }
        try {
            final DbSession session = sessionFactoryWrapper.getSession();
            UserDao userDao = new UserDao(session);
            User testUser = userDao.loadUser(null, "testUser");
            assertNotNull(testUser);
            GroupDao groupDao = new GroupDao(session);
            groupDao.persistGroup("minscha", testUser, null);
            fail();
        } catch ( Exception e ) {
            // everything is fine
        }
        {
            final DbSession session = sessionFactoryWrapper.getSession();
            UserDao userDao = new UserDao(session);
            User testUser = userDao.loadUser(null, "testUser");
            assertNotNull(testUser);
            testUser.setActivated(true);
            session.close();
        }
        {
            final DbSession session = sessionFactoryWrapper.getSession();
            UserDao userDao = new UserDao(session);
            User testUser = userDao.loadUser(null, "testUser");
            assertNotNull(testUser);
            GroupDao groupDao = new GroupDao(session);
            Pair<Key, Group> result = groupDao.persistGroup("minscha3", testUser, null);
            System.out.println(result.getFirst());
            session.commit();
            session.close();
        }
        {
            final DbSession session = sessionFactoryWrapper.getSession();
            UserDao userDao = new UserDao(session);
            User testUser = userDao.loadUser(null, "testUser");
            assertNotNull(testUser);
            GroupDao groupDao = new GroupDao(session);
            Group minscha = groupDao.get(93);
            RobotDao robotDao = new RobotDao(session);
            Robot r1 = robotDao.load(49);
            Robot r2 = robotDao.load(51);
            Robot r3 = robotDao.load(42);
            minscha.addRobot(r1);
            minscha.addRobot(r2);
            minscha.addRobot(r3);
            session.commit();
            session.close();
        }
    }
}

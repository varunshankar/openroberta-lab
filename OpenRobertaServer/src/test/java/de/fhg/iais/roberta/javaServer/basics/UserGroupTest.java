package de.fhg.iais.roberta.javaServer.basics;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.junit.Test;

import de.fhg.iais.roberta.persistence.util.SessionFactoryWrapper;

public class UserGroupTest {

    @Test
    public void test() {
        SessionFactoryWrapper sessionFactoryWrapper = new SessionFactoryWrapper("hibernate-cfg.xml", "jdbc:hsqldb:file:./db-3.3.2/openroberta-db");
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

}

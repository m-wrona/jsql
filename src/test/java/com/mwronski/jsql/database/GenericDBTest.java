package com.mwronski.jsql.database;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mwronski.jsql.test.TestDB;

/**
 * Generic test cases related with database
 * 
 * @author Michal Wronski
 */
public class GenericDBTest {

    private EntityManager em;

    @Before
    public void setUp() {
        em = TestDB.getEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
        em = null;
    }

    @Test
    public void testDBConnection() {
        assertNotNull(em.createNativeQuery("select 1").getResultList());
    }

}

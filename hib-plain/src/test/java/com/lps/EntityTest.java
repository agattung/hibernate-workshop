package com.lps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lps.model.LoyaltyEvent;

public class EntityTest {

	private static SessionFactory sessionFactory;

	private Session session;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory(new StandardServiceRegistryBuilder().build());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");			
		}
	}
	
	@Before
	public void setUp() {
		session = sessionFactory.openSession();		
	}

	@After
	public void tearDown() {
		if (session != null) {
			session.close();
		}
	}
	
	@Test
	public void test01_createEvent() {
		newEvent();
	}
		
	@Test
	public void test02_updateEvent() {
		LoyaltyEvent event = newEvent();
		Transaction tx = session.beginTransaction();
		
		// Hibernate detects unchanged objects
		String oldHappening = event.getWhatHappened();
		String newHappening = "Hibernate workshop continued";
		event.setWhatHappened(newHappening);
		event.setWhatHappened(oldHappening);

		
		session.save(event);
		assertEquals(0, event.getVersion());
		tx.commit();
		assertEquals(0, event.getVersion());
		assertTrue(session.contains(event));
		
		session = sessionFactory.openSession();
		assertFalse(session.contains(event));
		tx = session.beginTransaction();
		event = (LoyaltyEvent) session.merge(event);
		
		// "implicit" persistence, no additional save necessary
		// flush increases the version number
		event.setWhatHappened(newHappening);

		session.flush();
		assertTrue(event.getVersion() > 0 );
		
		tx.commit();

		long eventId = event.getId();
		
		session.close();
		
		session = sessionFactory.openSession();
		assertFalse(session.contains(event));
		
		event = (LoyaltyEvent) session.get(LoyaltyEvent.class, eventId);
		assertEquals(newHappening, event.getWhatHappened());
	}
	
	private LoyaltyEvent newEvent() {
		Transaction tx = session.beginTransaction();
		
		LoyaltyEvent event = new LoyaltyEvent();
		event.setWhatHappened("Hibernate workshop started");
		assertEquals(0, event.getId());
		assertEquals(0, event.getVersion());
		
		// save sets the id
		session.save(event);
		long eventId = event.getId();
		assertTrue(eventId > 0 );
		tx.commit();

		assertTrue(event.getId() > 0 );
		assertEquals(0, event.getVersion());
		return event;
	}

}
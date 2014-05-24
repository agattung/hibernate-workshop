package com.lps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lps.model.LoyaltyEvent;

public class EntityTest {

	private static EntityManagerFactory emf;
	
	private EntityManager em;
	
	@BeforeClass
	public static void setUpClass() {
		try {
			emf = Persistence.createEntityManagerFactory("data");						 
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unexpected exception");			
		}
	}
	
	@Before
	public void setUp() {
		em = emf.createEntityManager();
	}

	@After
	public void tearDown() {
		if (em != null) {
			em.close();
		}
	}
	
	@Test
	public void test01_createEvent() {
		newEvent();
	}
		
	@Test
	public void test02_updateEvent() {
		LoyaltyEvent event = newEvent();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		// Hibernate detects unchanged objects
		String oldHappening = event.getWhatHappened();
		String newHappening = "Hibernate workshop continued";
		event.setWhatHappened(newHappening);
		event.setWhatHappened(oldHappening);

		
		em.persist(event);
		assertEquals(0, event.getVersion());
		tx.commit();
		assertEquals(0, event.getVersion());
		assertTrue(em.contains(event));
		
		em = emf.createEntityManager();
		assertFalse(em.contains(event));
		
		tx = em.getTransaction();		
		tx.begin();
		
		event = (LoyaltyEvent) em.merge(event);
		
		// "implicit" persistence, no additional save necessary
		// flush increases the version number
		event.setWhatHappened(newHappening);

		em.flush();
		assertTrue(event.getVersion() > 0 );
		
		tx.commit();

		long eventId = event.getId();
		
		em.close();
		
		em = emf.createEntityManager();
		assertFalse(em.contains(event));
		
		event = (LoyaltyEvent) em.find(LoyaltyEvent.class, eventId);
		assertEquals(newHappening, event.getWhatHappened());
	}
	
	private LoyaltyEvent newEvent() {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		LoyaltyEvent event = new LoyaltyEvent();
		event.setWhatHappened("Hibernate workshop started");
		assertEquals(0, event.getId());
		assertEquals(0, event.getVersion());
		
		// save sets the id
		em.persist(event);
		long eventId = event.getId();
		assertTrue(eventId > 0 );
		tx.commit();

		assertTrue(event.getId() > 0 );
		assertEquals(0, event.getVersion());
		return event;
	}

}
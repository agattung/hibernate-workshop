package com.lps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DelegatingSmartContextLoader;
import org.springframework.transaction.annotation.Transactional;

import com.lps.model.LoyaltyEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-application.xml"}, loader = DelegatingSmartContextLoader.class)
public class EntityWithJavaxTransactionalTest {

	public static final String NEW_HAPPENING = "Hibernate workshop continued";
	
	private Session createSession;
	private Session doNotChangeSession;
	
	@PersistenceContext(unitName = "data")
	private EntityManager em;
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@Before
	public void setUp() {
		//em = emf.createEntityManager();
	}

	@After
	public void tearDown() {
		if (em != null) {
			em.close();
		}
	}
	
	@Test
	public void test01_createEvent() {
		LoyaltyEvent event = newEvent();
		
		assertTrue(event.getId() > 0 );
		assertEquals(0, event.getVersion());
	}
		
	
	@Test
	public void test02_updateEvent() {
		LoyaltyEvent event = newEvent();
		event = doNotChangeEvent(event);
		
		assertEquals(0, event.getVersion());
		
		assertEquals(0, event.getVersion());
		
		// hibernate session of last @Transactional was closed
		// -> em.contains is false
		assertFalse(em.contains(event));
		
		event = implicitSave(event);		

		long eventId = event.getId();
		
		// same as above: em.contains returns false
		assertFalse(em.contains(event));
		
		event = (LoyaltyEvent) em.find(LoyaltyEvent.class, eventId);
		assertEquals(NEW_HAPPENING, event.getWhatHappened());
	}
	

	@Transactional
	private LoyaltyEvent newEvent() {
		createSession = em.unwrap(Session.class);
		
		LoyaltyEvent event = new LoyaltyEvent();
		event.setWhatHappened("Hibernate workshop started");
		assertEquals(0, event.getId());
		assertEquals(0, event.getVersion());
		
		// save sets the id
		em.persist(event);
		em.flush();
		long eventId = event.getId();
		assertTrue(eventId > 0 );


		return event;
	}
	
	@Transactional
	private LoyaltyEvent doNotChangeEvent(LoyaltyEvent event) {
		doNotChangeSession = em.unwrap(Session.class);
		Assert.assertNotEquals(doNotChangeSession, createSession);
		// Hibernate detects unchanged objects
		String oldHappening = event.getWhatHappened();
		
		event.setWhatHappened(NEW_HAPPENING);
		event.setWhatHappened(oldHappening);
		
		// plain hibernate example: em.persist
		// here we have new session due to @Transactional
		event = em.merge(event);
		assertEquals(0, event.getVersion());

		return event;
	}

	@Transactional
	private LoyaltyEvent implicitSave(LoyaltyEvent event) {
		event = em.merge(event);
		
		// "implicit" persistence, no additional save necessary
		// flush increases the version number
		event.setWhatHappened(NEW_HAPPENING);

		em.flush();
		assertTrue(event.getVersion() > 0 );

		return event;
	}
}
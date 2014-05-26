package com.lps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.lps.commons.spring.SpringApplicationContextProvider;
import com.lps.model.LoyaltyEvent;
import com.lps.repository.LoyaltyEventRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-application.xml"}, loader = TestContextLoader.class)
public class EntityWithJavaxTransactionalTest {

	public static final String NEW_HAPPENING = "Hibernate workshop continued";
	
	@Resource
	private LoyaltyEventRepository loyaltyEventRepository;
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
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
				
		event = implicitSave(event);		
		assertTrue(event.getVersion() > 0 );
		
		long eventId = event.getId();
		
		EntityManager em = getEntityManagerStatic();
		event = (LoyaltyEvent) em.find(LoyaltyEvent.class, eventId);
		assertEquals(NEW_HAPPENING, event.getWhatHappened());
	}
	

	@Transactional
	private LoyaltyEvent newEvent() {
		LoyaltyEvent event = new LoyaltyEvent();
		event.setWhatHappened("Hibernate workshop started");
		assertEquals(0, event.getId());
		assertEquals(0, event.getVersion());

		LoyaltyEvent persistedEvent = loyaltyEventRepository.save(event);
		assertNotSame(event, persistedEvent);
		event = persistedEvent;
		//em.flush();
		long eventId = event.getId();
		assertTrue(eventId > 0 );

		EntityManager em = loyaltyEventRepository.getEntityManager();
		Session session = em.unwrap(Session.class);
		session.contains(event);
		assertTrue(em.contains(event));

		return event;
	}
	
	@Transactional
	private LoyaltyEvent doNotChangeEvent(LoyaltyEvent event) {
		// Hibernate detects unchanged objects
		String oldHappening = event.getWhatHappened();
		
		event.setWhatHappened(NEW_HAPPENING);
		event.setWhatHappened(oldHappening);
		
		event = loyaltyEventRepository.save(event);
		assertEquals(0, event.getVersion());

		return event;
	}

	@Transactional
	private LoyaltyEvent implicitSave(LoyaltyEvent event) {
		event = loyaltyEventRepository.findOne(event.getId());
		
		event.setWhatHappened(NEW_HAPPENING);
		
		// "implicit" persistence, no additional save necessary
		// flush increases the version number, happens in surrounding transaction (aspect)		

		return event;
	}

    /**
     * @return (Shared and therefore reusable accross threads) entity manager created by EntityManagerFactory bean
     */
    public static EntityManager getEntityManagerStatic() {
        EntityManagerFactory entityManagerFactory = (EntityManagerFactory) SpringApplicationContextProvider.getApplicationContext().getBean("entityManagerFactory");
        EntityManager em = SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
        return em;
    }
	
}
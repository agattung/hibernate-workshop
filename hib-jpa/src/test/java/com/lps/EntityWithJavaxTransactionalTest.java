package com.lps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.lps.commons.spring.SpringApplicationContextProvider;
import com.lps.model.LoyaltyEvent;
import com.lps.model.LoyaltyTransaction;
import com.lps.model.LoyaltyTransactionType;
import com.lps.model.VirtualCurrency;
import com.lps.repository.LoyaltyEventRepository;
import com.lps.system.EnvironmentDetectionSystem;

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

	@Test
	public void test03_CreateProxyOnYourOwn() {
		LoyaltyEvent event = newEvent();
		createAndCheckProxyFor(event.getId());
	}
	
	@Transactional
	private void createAndCheckProxyFor(Long id) {
		LoyaltyEvent eventProxy = getEntityManagerStatic().getReference(LoyaltyEvent.class, id);
		HibernateProxy proxy = (HibernateProxy) eventProxy;
		LoyaltyEvent event = (LoyaltyEvent) proxy.getHibernateLazyInitializer().getImplementation();
		assertEquals(1, event.getTransactions().size());
	}
	
	@Test
	public void test04_Environment() {
		ApplicationContext applicationContext = TestContextLoader.STATIC_APPLICATION_CONTEXT;
		DataSource ds = (DataSource) applicationContext.getBean("dataSource");
		Map<String, String> properties = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
        if (ds instanceof org.apache.tomcat.jdbc.pool.DataSource) {
            org.apache.tomcat.jdbc.pool.DataSource dataSource = (org.apache.tomcat.jdbc.pool.DataSource) ds;

            properties.put("ds.name", dataSource.getName());
            properties.put("ds.idle", "" + dataSource.getIdle());
            //properties.put("ds.active", "" + activeConnections);
            properties.put("ds.maxActive", "" + dataSource.getMaxActive());
            properties.put("ds.maxIdle", "" + dataSource.getMaxIdle());
            properties.put("ds.minIdle", "" + dataSource.getMinIdle());
            properties.put("ds.url", "" + dataSource.getUrl());
            properties.put("ds.user", "" + dataSource.getUsername());

        }
        String appProfile = EnvironmentDetectionSystem.getInstance().getAppProfile();
        String url = properties.get("ds.url");
        if(appProfile.contains("mysql")) {
        	assertTrue(url.startsWith("jdbc:mysql"));
        } else if (appProfile.contains("oracle")) {
        	assertTrue(url.startsWith("jdbc:oracle"));
        } else {
        	assertTrue(url.startsWith("jdbc:hsqldb"));
        }
	}
	
	@Transactional
	private LoyaltyEvent newEvent() {
		LoyaltyEvent event = new LoyaltyEvent();
		event.setWhatHappened("Hibernate workshop started");
		
		LoyaltyTransaction trx = new LoyaltyTransaction();
		trx.setAmount(100);
		trx.setCurrency(VirtualCurrency.AWARD);
		trx.setType(LoyaltyTransactionType.CREDIT);
		trx.setEvent(event);
		Set<LoyaltyTransaction> transactions = new HashSet<LoyaltyTransaction>();
		transactions.add(trx);
		event.setTransactions(transactions);
				
		assertEquals(0, event.getId());
		assertEquals(0, event.getVersion());
		
		LoyaltyEvent persistedEvent = loyaltyEventRepository.save(event);
		assertNotSame(event, persistedEvent);
		event = persistedEvent;
		//em.flush();
		long eventId = event.getId();
		assertTrue(eventId > 0 );
		assertFalse(event.getTransactions().isEmpty());
		assertTrue(event.getTransactions().iterator().next().getId() > 0);

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
package com.lps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

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
import com.lps.model.EventLocation;
import com.lps.model.LoyaltyEvent;
import com.lps.services.EventService;
import com.lps.system.EnvironmentDetectionSystem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-application.xml"}, loader = TestContextLoader.class)
public class EntityWithJavaxTransactionalTest extends PersistentBaseTest {

	public static final String NEW_HAPPENING = "Hibernate workshop continued";
	
	@Resource
	private EventService eventService;
	
	@Test
	public void test01_createEvent() {
		LoyaltyEvent event = eventService.createEvent();
		
		assertTrue(event.getId() > 0 );
		assertEquals(0, event.getVersion());
	}
		
	
	@Test
	public void test02_updateEvent() {
		LoyaltyEvent event = eventService.createEvent();
		
		event = doNotChangeEvent(event);		
		//TODO
		//assertEquals(0, event.getVersion());
				
		event = implicitSave(event);		
		assertTrue(event.getVersion() > 0 );
		
		long eventId = event.getId();
		
		
		loadEventInSeparateTransaction(eventId);
	}

	@Transactional
	private void loadEventInSeparateTransaction(long eventId) {
		EntityManager em = getEntityManagerStatic();
		LoyaltyEvent event = (LoyaltyEvent) em.find(LoyaltyEvent.class, eventId);
		assertEquals(NEW_HAPPENING, event.getWhatHappened());
		EventLocation location = event.getLocation();
		assertTrue(location instanceof HibernateProxy);
		String city = location.getCity();
		assertNotNull(city);
		assertTrue(event.getTransactions().size() > 0);
		
	}
	
	@Test
	public void test03_CreateProxyOnYourOwn() {
		LoyaltyEvent event = eventService.createEvent();
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
	private LoyaltyEvent doNotChangeEvent(LoyaltyEvent event) {
		// Hibernate detects unchanged objects
		String oldHappening = event.getWhatHappened();
		
		event.setWhatHappened(NEW_HAPPENING);
		event.setWhatHappened(oldHappening);
		
		event = eventService.saveEvent(event);
		// TODO
		//assertEquals(0, event.getVersion());

		return event;
	}

	@Transactional
	private LoyaltyEvent implicitSave(LoyaltyEvent event) {
		event = eventService.loadEvent(event.getId());
		
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
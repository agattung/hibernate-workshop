package com.lps;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.internal.SessionFactoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.lps.model.EventType;
import com.lps.model.LoyaltyEvent;
import com.lps.model.LoyaltyTransaction;
import com.lps.model.LoyaltyTransactionType;
import com.lps.model.VirtualCurrency;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-application.xml"}, loader = TestContextLoader.class)
public class EntityPerformanceTest extends PersistentBaseTest {

	public static final String NEW_HAPPENING = "Hibernate workshop continued";	

	private Logger LOGGER = LoggerFactory.getLogger(EntityPerformanceTest.class);
	
	@Before
	public void setUp() {
		super.setUp();
		SessionFactoryImpl sf = (SessionFactoryImpl) loyaltyEventRepository.getEntityManager().getEntityManagerFactory().unwrap(SessionFactory.class);
		String batchSizeString = (String)sf.getProperties().get(Environment.STATEMENT_BATCH_SIZE);
		Assert.assertNotNull(batchSizeString,Environment.STATEMENT_BATCH_SIZE + " must be set in jpa properties in spring-application.xml");
	}

	
	@Test
	public void test01_insertEvents() {		
		
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();		
		boolean tooVerbose = Level.DEBUG.equals(loggerContext.getLogger("org.hibernate").getEffectiveLevel());
		
		int testCount;
		
		if (tooVerbose) {
			testCount = 200;
			LOGGER.warn("Loglevel too verbose, just inserting " + testCount + " events. Change logback.xml and run again.");
		} else {
			testCount = 10000;
		}		
		
		long startMillis = System.currentTimeMillis();
		insertEvents(testCount);
		long endMillis = System.currentTimeMillis();
		
		System.out.println("Insert and flush and commit of " + testCount + " events took " + (endMillis-startMillis) + "ms");
	}
		
	
	@Transactional
	private void insertEvents(int count) {
		
		EventType eventType = eventTypeRepository.findByName("FlightEvent");
		
		long startMillis = System.currentTimeMillis();

		for (int i = 1; i <= count; i++) {
			LoyaltyEvent event = createEvent("createdEvent" + i, eventType);
			loyaltyEventRepository.save(event);
			if ( i % 1000 == 0) {
				loyaltyEventRepository.flush();
				loyaltyEventRepository.clear();
				System.out.println("Saved " + i + "/" + count + " events.");
			}
		}
		long endMillis = System.currentTimeMillis();
		System.out.println("Insert and of " + count + " events took " + (endMillis-startMillis) + "ms");
		
		
		endMillis = System.currentTimeMillis();
		System.out.println("Insert and flush of " + count + " events took " + (endMillis-startMillis) + "ms");
	}
	
	protected LoyaltyEvent createEvent(String whatHappened, EventType type) {
		LoyaltyEvent event = new LoyaltyEvent();
		event.setWhatHappened(whatHappened);
		event.setType(type);
		LoyaltyTransaction trx = new LoyaltyTransaction();
		trx.setAmount(100);
		trx.setCurrency(VirtualCurrency.AWARD);
		trx.setType(LoyaltyTransactionType.CREDIT);
		trx.setEvent(event);
		Set<LoyaltyTransaction> transactions = new HashSet<LoyaltyTransaction>();
		transactions.add(trx);
		event.setTransactions(transactions);
		return event;
	}
	
}
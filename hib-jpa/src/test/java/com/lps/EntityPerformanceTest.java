package com.lps;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.internal.SessionFactoryImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.lps.model.LoyaltyEvent;
import com.lps.model.LoyaltyTransaction;
import com.lps.model.LoyaltyTransactionType;
import com.lps.model.VirtualCurrency;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-application.xml"}, loader = TestContextLoader.class)
public class EntityPerformanceTest extends PersistentBaseTest {

	public static final String NEW_HAPPENING = "Hibernate workshop continued";	
	
	@Before
	public void setUp() {
		super.setUp();
		SessionFactoryImpl sf = (SessionFactoryImpl) loyaltyEventRepository.getEntityManager().getEntityManagerFactory().unwrap(SessionFactory.class);
		String batchSizeString = (String)sf.getProperties().get(Environment.STATEMENT_BATCH_SIZE);
		Assert.assertNotNull(batchSizeString,Environment.STATEMENT_BATCH_SIZE + " must be set in jpa properties in spring-application.xml");
	}

	
	@Test
	public void test01_insertEvents() {

		int testCount = 10000;
		long startMillis = System.currentTimeMillis();
		insertEvents(testCount);
		long endMillis = System.currentTimeMillis();
		
		System.out.println("Insert and flush and commit of " + testCount + " events took " + (endMillis-startMillis) + "ms");
	}
		
	
	@Transactional
	private void insertEvents(int count) {
		
		long startMillis = System.currentTimeMillis();

		// sample for manually setting Logback Logger level
		//LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();		
		//loggerContext.getLogger("org.hibernate.engine.jdbc.batch.internal").setLevel(Level.DEBUG);

		for (int i = 1; i <= count; i++) {
			LoyaltyEvent event = createEvent("createdEvent" + i);
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
	
	protected LoyaltyEvent createEvent(String whatHappened) {
		LoyaltyEvent event = new LoyaltyEvent();
		event.setWhatHappened(whatHappened);
		
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
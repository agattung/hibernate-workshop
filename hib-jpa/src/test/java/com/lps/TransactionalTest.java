package com.lps;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.lps.model.LoyaltyEvent;
import com.lps.repository.LoyaltyEventRepository;
import com.lps.services.EventService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-application.xml"}, loader = TestContextLoader.class)
public class TransactionalTest extends PersistentBaseTest {

	private static final String CHANGED_IN_NEW_TRX = "changed in new trx";
	private static final String CHANGED_IN_SAME_TRX = "changed in same trx";
	
	@Resource
	private EventService eventService;

	@Resource
	private LoyaltyEventRepository eventRepository;

	@Test
	public void test01() {
		createAndUpdateEventInDifferentTrx();
	}
	
	@Transactional
	private void createAndUpdateEventInDifferentTrx() {
		LoyaltyEvent event = eventService.createEvent();
		String parentTrxName = TransactionSynchronizationManager.getCurrentTransactionName();
		System.out.println(parentTrxName);
		LoyaltyEvent updatedEvent = updateEventInNewTrx(event, parentTrxName);
		Assert.assertEquals(CHANGED_IN_NEW_TRX, updatedEvent.getWhatHappened());
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private LoyaltyEvent updateEventInNewTrx(LoyaltyEvent event, String parentTrxName) {
		String currentTrxName = TransactionSynchronizationManager.getCurrentTransactionName();
		Assert.assertNotEquals(parentTrxName, currentTrxName);
		eventRepository.merge(event);		
		
		event.setWhatHappened(CHANGED_IN_NEW_TRX);
		eventRepository.save(event);
		return event;
	}
}
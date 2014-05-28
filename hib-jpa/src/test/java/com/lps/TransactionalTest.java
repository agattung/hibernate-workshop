package com.lps;

import javax.annotation.Resource;

import org.infinispan.transaction.TransactionMode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
		LoyaltyEvent updatedEvent = updateEventInNewTrx(event);
		Assert.assertEquals(CHANGED_IN_NEW_TRX,event.getWhatHappened());
	}
	
	@Transactional
	private LoyaltyEvent updateEventInNewTrx(LoyaltyEvent event) {
		eventRepository.merge(event);
		event.setWhatHappened(CHANGED_IN_NEW_TRX);
		eventRepository.save(event);
		return event;
	}
}
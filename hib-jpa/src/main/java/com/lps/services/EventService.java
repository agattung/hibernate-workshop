package com.lps.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lps.model.EventLocation;
import com.lps.model.LoyaltyEvent;
import com.lps.model.LoyaltyTransaction;
import com.lps.model.LoyaltyTransactionType;
import com.lps.model.VirtualCurrency;
import com.lps.repository.LoyaltyEventRepository;

@Transactional
@Service
public class EventService {

	@Autowired
	private LoyaltyEventRepository loyaltyEventRepository;
	

	public LoyaltyEvent loadEvent(long id) {
		return loyaltyEventRepository.findOne(id);
	}

	
	public LoyaltyEvent saveEvent(LoyaltyEvent event) {
		return loyaltyEventRepository.save(event);
	}
	
	@Transactional
	public LoyaltyEvent createEvent() {
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
		
		EventLocation location = new EventLocation();
		location.setCity("München");
		
		event.setLocation(location);
				
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


	public List<LoyaltyEvent> findEventByWhatHappenedViaHql(String whatHappened) {
		Session session = loyaltyEventRepository.getEntityManager().unwrap(Session.class); 
		org.hibernate.Query query = session.getNamedQuery("event.findByWhatHappenedViaQl");
		query.setString("whatHappened", whatHappened);
		List<LoyaltyEvent> resultList = query.list();
		return resultList;
	}


	public List<LoyaltyEvent> findEventByWhatHappenedViaJpql(String whatHappened) {
		// TODO Auto-generated method stub
		return null;
	}


	public List<LoyaltyEvent> findEventByWhatHappenedViaRawSql(String whatHappened) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<LoyaltyEvent> findEventByWhatHappenedViaCriteria(String whatHappened) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<LoyaltyEvent> findEventByWhatHappenedViaQbe(String whatHappened) {
		// TODO Auto-generated method stub
		return null;
	}


	public List<LoyaltyEvent> findEventByWhatHappenedViaSpringDataRepository(
			String whatHappened) {
		// TODO Auto-generated method stub
		return null;
	}
}

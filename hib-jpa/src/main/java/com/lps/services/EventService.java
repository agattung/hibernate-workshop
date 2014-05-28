package com.lps.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lps.model.EventLocation;
import com.lps.model.EventType;
import com.lps.model.LoyaltyEvent;
import com.lps.model.LoyaltyTransaction;
import com.lps.model.LoyaltyTransactionType;
import com.lps.model.VirtualCurrency;
import com.lps.repository.EventTypeRepository;
import com.lps.repository.LoyaltyEventRepository;

@Transactional
@Service
public class EventService {

	@Autowired
	private LoyaltyEventRepository loyaltyEventRepository;

	@Autowired
	private EventTypeRepository eventTypeRepository;
	

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

		EventType type = eventTypeRepository.findByName("FlightEvent");
		event.setType(type);

		
		LoyaltyEvent persistedEvent = loyaltyEventRepository.save(event);
		event = persistedEvent;
		//em.flush();
		long eventId = event.getId();

		EntityManager em = loyaltyEventRepository.getEntityManager();
		Session session = em.unwrap(Session.class);
		session.contains(event);

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
		EntityManager em = loyaltyEventRepository.getEntityManager(); 
		TypedQuery<LoyaltyEvent> query = em.createNamedQuery("event.findByWhatHappenedViaQl", LoyaltyEvent.class);
		query.setParameter("whatHappened", whatHappened);
		List<LoyaltyEvent> resultList = query.getResultList();
		return resultList;
	}


	public List<LoyaltyEvent> findEventByWhatHappenedViaRawSql(String whatHappened) {
		EntityManager em = loyaltyEventRepository.getEntityManager(); 
		TypedQuery<LoyaltyEvent> query = em.createNamedQuery("event.findByWhatHappenedViaSql", LoyaltyEvent.class);
		query.setParameter("whatHappened", whatHappened);
		List<LoyaltyEvent> resultList = query.getResultList();
		return resultList;
	}

	public List<LoyaltyEvent> findEventByWhatHappenedViaCriteria(String whatHappened) {
		EntityManager em = loyaltyEventRepository.getEntityManager();
	    CriteriaBuilder cb = em.getCriteriaBuilder();	    

        CriteriaQuery<LoyaltyEvent> cq = cb.createQuery(LoyaltyEvent.class);
        Root<LoyaltyEvent> root = cq.from(LoyaltyEvent.class);
        cq.where(cb.equal(root.get("whatHappened"), whatHappened));
        
        List<LoyaltyEvent> resultList = em.createQuery(cq).getResultList();

		return resultList;
	}
	
	public List<LoyaltyEvent> findEventByWhatHappenedViaQbe(String whatHappened) {
		Session session = (Session) loyaltyEventRepository.getEntityManager().getDelegate();
		
		LoyaltyEvent event = new LoyaltyEvent();
		event.setWhatHappened(whatHappened);
		
		Example eventExample = Example.create(event);
		org.hibernate.Criteria criteria= session.createCriteria(LoyaltyEvent.class).add(eventExample);
		
		return criteria.list();
	}


	public List<LoyaltyEvent> findEventByWhatHappenedViaSpringDataRepository(String whatHappened) {
		return loyaltyEventRepository.findByWhatHappened(whatHappened);
	}
}

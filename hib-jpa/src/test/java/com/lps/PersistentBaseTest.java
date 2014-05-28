package com.lps;

import javax.annotation.Resource;

import org.junit.Before;
import org.springframework.transaction.annotation.Transactional;

import com.lps.model.EventType;
import com.lps.repository.EventTypeRepository;
import com.lps.repository.LoyaltyEventRepository;

public abstract class PersistentBaseTest {

	private static boolean typesInitialized;
	
	@Resource
	protected LoyaltyEventRepository loyaltyEventRepository;

	@Resource
	protected EventTypeRepository eventTypeRepository;

	
	@Before
	public void setUp() {
		if (typesInitialized) {
			return;
		}
		createTypes();
	}
	
	@Transactional
	private void createTypes() {
		EventType type = new EventType();
		type.setId(1);
		type.setName("FlightEvent");
		
		eventTypeRepository.save(type);

		type = new EventType();
		type.setId(2);
		type.setName("NonFlightEvent");
		
		eventTypeRepository.save(type);
	}
}
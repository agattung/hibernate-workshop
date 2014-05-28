package com.lps;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.lps.model.EventType;
import com.lps.repository.EventTypeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-application.xml"}, loader = TestContextLoader.class)
public class CacheTest {

	public static final String NEW_HAPPENING = "Hibernate workshop continued";
	
	@Resource
	private EventTypeRepository eventTypeRepository;
	
	
	@Test
	public void test01() {
        Statistics stat = getHibernateStatistics();
        stat.clear();
        
		createType(1, "FlightEvent");
		createType(2, "NonFlightEvent");
		
		eventTypeRepository.findByName("FlightEvent");
		
		
        assertEquals(0, stat.getQueryCacheHitCount());
        assertEquals(0, stat.getSecondLevelCacheHitCount());

        eventTypeRepository.findByName("FlightEvent");
        
        // it seems the cache sometimes might take a while to be up-to-date
        while (stat.getQueryCacheHitCount() == 0) {
            sleep(10);
            eventTypeRepository.findByName("FlightEvent");
        }
        assertEquals(1, stat.getQueryCacheHitCount());
        assertEquals(1, stat.getSecondLevelCacheHitCount());
        
        eventTypeRepository.findByName("FlightEvent");
        assertEquals(2, stat.getQueryCacheHitCount());
        assertEquals(2, stat.getSecondLevelCacheHitCount());
        
        
        eventTypeRepository.findOne(1);
        assertEquals(2, stat.getQueryCacheHitCount());
        assertEquals(3, stat.getSecondLevelCacheHitCount());
	}	
	
	
	private void createType(int id, String name) {
		EventType type = new EventType();
		type.setId(id);
		type.setName(name);
		// implitict transaction at save method
		eventTypeRepository.save(type);		
	}
	

	@Transactional
    private Statistics getHibernateStatistics() {
        EntityManager em = eventTypeRepository.getEntityManager();
        EntityManagerFactory emf = em.getEntityManagerFactory();
        SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
        Statistics stat = sessionFactory.getStatistics();
        return stat;
    }	
	
	
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(Exception e ) {
			throw new RuntimeException("Don't wake me up!");
		}
		
	}
}
package com.lps;

import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

import com.lps.model.LoyaltyEvent;
import com.lps.services.EventService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-application.xml"}, loader = TestContextLoader.class)
public class QueryTest extends PersistentBaseTest {

	public static final String NEW_HAPPENING = "Hibernate workshop continued";
	
	@Resource
	private EventService eventService;
	
	@BeforeClass
	public static void setUpClass() {
		// make sure we see the statements and bind variables
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();		
		loggerContext.getLogger("org.hibernate.SQL").setLevel(Level.DEBUG);
		loggerContext.getLogger("org.hibernate.type").setLevel(Level.TRACE);
	}
	
	@Test
	public void testHql() {
		LoyaltyEvent event = eventService.createEvent();
		String whatHappened = event.getWhatHappened();
		
		List<LoyaltyEvent> resultList;
		resultList = eventService.findEventByWhatHappenedViaHql(whatHappened);
		event = resultList.get(0);
		Assert.assertEquals(whatHappened, event.getWhatHappened());
	}
	
	@Test
	public void testJpql() {
		LoyaltyEvent event = eventService.createEvent();
		String whatHappened = event.getWhatHappened();
		
		List<LoyaltyEvent> resultList;

		resultList = eventService.findEventByWhatHappenedViaJpql(whatHappened);
		event = resultList.get(0);
		Assert.assertEquals(whatHappened, event.getWhatHappened());
	
	}
	
	@Test
	public void testRawSql() {
		LoyaltyEvent event = eventService.createEvent();
		String whatHappened = event.getWhatHappened();
		
		List<LoyaltyEvent> resultList;

		resultList = eventService.findEventByWhatHappenedViaRawSql(whatHappened);
		event = resultList.get(0);
		Assert.assertEquals(whatHappened, event.getWhatHappened());
	}
	
	@Test
	public void testCriteria() {
		LoyaltyEvent event = eventService.createEvent();
		String whatHappened = event.getWhatHappened();
		
		List<LoyaltyEvent> resultList;

		resultList = eventService.findEventByWhatHappenedViaCriteria(whatHappened);
		event = resultList.get(0);
		Assert.assertEquals(whatHappened, event.getWhatHappened());
	}
	
	@Test
	public void testQueryByExample() {
		LoyaltyEvent event = eventService.createEvent();
		String whatHappened = event.getWhatHappened();
		
		List<LoyaltyEvent> resultList;

		resultList = eventService.findEventByWhatHappenedViaQbe(whatHappened);
		event = resultList.get(0);
		Assert.assertEquals(whatHappened, event.getWhatHappened());
	}
	
	@Test
	public void testSpringDataRepository() {
		LoyaltyEvent event = eventService.createEvent();
		String whatHappened = event.getWhatHappened();
		
		List<LoyaltyEvent> resultList;

		resultList = eventService.findEventByWhatHappenedViaSpringDataRepository(whatHappened);
		event = resultList.get(0);
		Assert.assertEquals(whatHappened, event.getWhatHappened());
	}
	
}
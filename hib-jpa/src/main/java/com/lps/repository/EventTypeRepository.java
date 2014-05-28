package com.lps.repository;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;

import com.lps.commons.spring.LpsJpaRepository;
import com.lps.model.EventType;

public interface EventTypeRepository extends LpsJpaRepository<EventType, Integer> {
	
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value ="true") })	
	EventType findByName(String name);

}

package com.lps.repository;

import javax.validation.constraints.NotNull;

import com.lps.commons.spring.LpsJpaRepository;
import com.lps.model.EventType;

public interface EventTypeRepository extends LpsJpaRepository<EventType, Long> {
	
	EventType findByName(String name);

}

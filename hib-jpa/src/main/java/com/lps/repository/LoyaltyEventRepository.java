package com.lps.repository;

import java.util.List;

import com.lps.commons.spring.LpsJpaRepository;
import com.lps.model.LoyaltyEvent;

public interface LoyaltyEventRepository extends LpsJpaRepository<LoyaltyEvent, Long> {
	List<LoyaltyEvent> findByWhatHappened(String whatHappened);

}

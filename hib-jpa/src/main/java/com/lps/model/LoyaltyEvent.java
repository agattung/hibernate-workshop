package com.lps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class LoyaltyEvent {
	
	@Id
	@GeneratedValue
	private long id;

	@Version
	private int version;
	
	@Column(length = 100, nullable = false)
	private String whatHappened;

	void setId(long id) {
		this.id = id;
	}
		
	public long getId() {
		return id;
	}

	void setVersion(int version) {
		this.version = version;
	}
	public int getVersion() {
		return version;
	}


	public String getWhatHappened() {
		return whatHappened;
	}

	public void setWhatHappened(String whatHappened) {
		this.whatHappened = whatHappened;
	}	
	
}

package com.lps.model;

public class LoyaltyEvent {
	
	private long id;

	private int version;
	
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

package com.lps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
public class LoyaltyTransaction {
	
	@Id
	@GeneratedValue
	private long id;

	@Version
	private int version;
	
	@Column(nullable = false)
	private int amount;

	@Enumerated(EnumType.STRING)
	private LoyaltyTransactionType type;
	
	@Enumerated(EnumType.STRING)
	private VirtualCurrency currency;
	
	@NotNull
    @ManyToOne
    private LoyaltyEvent event;
    
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

	public LoyaltyEvent getEvent() {
		return event;
	}

	public void setEvent(LoyaltyEvent event) {
		this.event = event;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public LoyaltyTransactionType getType() {
		return type;
	}

	public void setType(LoyaltyTransactionType type) {
		this.type = type;
	}

	public VirtualCurrency getCurrency() {
		return currency;
	}

	public void setCurrency(VirtualCurrency currency) {
		this.currency = currency;
	}
	
}

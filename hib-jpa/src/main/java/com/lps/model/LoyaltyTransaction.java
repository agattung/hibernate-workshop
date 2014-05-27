package com.lps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class LoyaltyTransaction {

	@GenericGenerator(name="tx_seq_gen", strategy = "seqhilo",
			parameters = {
			    @Parameter(name="max_lo", value = "1000"),
			    @Parameter(name="sequence", value="transaction_seq")
			}
			)
	@Id
	@GeneratedValue(generator="tx_seq_gen", strategy = GenerationType.SEQUENCE)
	private long id;

	@Version
	private int version;
	
	@Column(nullable = false, length = 5)
	private int amount;

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

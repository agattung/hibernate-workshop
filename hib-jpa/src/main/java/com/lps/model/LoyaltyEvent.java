package com.lps.model;

import java.sql.Date;
import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
public class LoyaltyEvent {
	
	@Id
	@GeneratedValue
	private long id;

	@Version
	private int version;
	
	@Column(length = 100, nullable = false)
	private String whatHappened;

	
	//@NotNull @OneToMany(mappedBy="event", cascade = javax.persistence.CascadeType.ALL)
	@OneToMany(mappedBy="event") @Cascade({CascadeType.ALL})
	private Set<LoyaltyTransaction> transactions;
	
	@NotNull
	private Date createdDate;
	
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

	public Set<LoyaltyTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<LoyaltyTransaction> transactions) {
		this.transactions = transactions;
	}	

	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	@PrePersist
	public void prePersist() {
		if (createdDate == null) {
			this.createdDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		}		
	}
}

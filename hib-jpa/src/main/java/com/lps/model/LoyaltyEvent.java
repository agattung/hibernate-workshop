package com.lps.model;

import java.sql.Date;
import java.util.Calendar;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class LoyaltyEvent {

	@GenericGenerator(name="evt_seq_gen", strategy = "seqhilo",
			parameters = {
			    @Parameter(name="max_lo", value = "1000")
			    ,@Parameter(name="sequence", value="event_seq")
			}
			)
	@Id
	@GeneratedValue(generator="evt_seq_gen", strategy=GenerationType.SEQUENCE)
	private long id;

	@Version
	private int version;
	
	@Column(length = 100, nullable = false)
	private String whatHappened;

	
	//@NotNull @OneToMany(mappedBy="event", cascade = javax.persistence.CascadeType.ALL)
	@OneToMany(mappedBy="event", fetch = FetchType.EAGER)
	@Cascade({CascadeType.ALL})
	private Set<LoyaltyTransaction> transactions;
	
	@OneToOne(fetch=FetchType.LAZY)
	@Cascade({CascadeType.SAVE_UPDATE, CascadeType.PERSIST, CascadeType.MERGE})
	private EventLocation location;
	
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

	
	public EventLocation getLocation() {
		return location;
	}

	public void setLocation(EventLocation location) {
		this.location = location;
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

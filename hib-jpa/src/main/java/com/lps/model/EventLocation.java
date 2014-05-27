package com.lps.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


@SequenceGenerator(name="evt_loc_gen", sequenceName="evt_loc_seq")
@Entity
public class EventLocation {

	@GenericGenerator(name="evt_loc_gen", strategy = "seqhilo",
			parameters = {
			    @Parameter(name="max_lo", value = "1000"),
			    @Parameter(name="sequence", value="evt_loc_seq")
			}
			)
	@Id
	@GeneratedValue(generator="evt_loc_gen", strategy = GenerationType.SEQUENCE)
	private long id;

	@Version
	private int version;

	@Column(length = 100, nullable = false)
	private String city;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	
}

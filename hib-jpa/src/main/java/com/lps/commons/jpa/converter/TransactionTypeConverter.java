package com.lps.commons.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.lps.model.LoyaltyTransactionType;

@Converter(autoApply = true)
public class TransactionTypeConverter implements AttributeConverter<LoyaltyTransactionType, String> {

 @Override
 public String convertToDatabaseColumn(LoyaltyTransactionType type) {
	  switch (type) {
	  	case CREDIT:
	  		return "CRD";
	  	case DEBIT:
	  		return "DBT";
	  	case UNKNOWN:
	  		return "UKWN";
	  	default:
	  		throw new IllegalArgumentException("Unknown value: " + type);
	  }
 }

 @Override
  public LoyaltyTransactionType convertToEntityAttribute(String dbData) {
	  if("CRD".equals(dbData)) {
		  return LoyaltyTransactionType.CREDIT;
	  } else if ("DBT".equals(dbData)) {
		  return LoyaltyTransactionType.DEBIT;
	  } else if ("UKWN".equals(dbData)) {
		  return LoyaltyTransactionType.UNKNOWN;
	  } else {
		  throw new IllegalArgumentException("Unknown value: " + dbData);
	  }
 }
}
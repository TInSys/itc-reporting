package com.tinsys.itc_reporting.model;

import java.io.Serializable;

public class Company implements Serializable {

  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  private Long id;
  private String name;
  private String currencyISO;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCurrencyISO() {
    return currencyISO;
  }

  public void setCurrencyISO(String currencyISO) {
    this.currencyISO = currencyISO;
  }

  @Override
  public String toString() {
    return this.getName();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && (obj.getClass().equals(this.getClass()))) {
      Company company = (Company) obj;
      return this.id == company.getId();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(this.id).hashCode();
  }
}

package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;

public class CompanyDTO implements Serializable {

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
  public boolean equals(Object obj) {
    if (obj != null && (obj.getClass().equals(this.getClass()))) {
      CompanyDTO zoneDTO = (CompanyDTO) obj;
      return this.id == zoneDTO.getId();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(this.id).hashCode();
  }
}

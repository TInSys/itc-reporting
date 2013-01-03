package com.tinsys.itc_reporting.model;

import java.io.Serializable;

public class Zone implements Serializable {

  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  private Long id;
  private String code;
  private String name;
  private String currencyISO;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
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
    return this.getCode();
  }

}

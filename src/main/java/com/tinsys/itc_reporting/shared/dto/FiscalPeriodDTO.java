package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;

public class FiscalPeriodDTO implements Serializable {

  /**
     * 
     */
  private static final long serialVersionUID = 1L;

  private Long id;
  private int month;
  private int year;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && (obj.getClass().equals(this.getClass()))) {
      FiscalPeriodDTO fiscalPeriodDTO = (FiscalPeriodDTO) obj;
      return this.id == fiscalPeriodDTO.getId();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(this.id).hashCode();
  }
  
  @Override
  public String toString() {
    return month + "/" + year;
  }

}

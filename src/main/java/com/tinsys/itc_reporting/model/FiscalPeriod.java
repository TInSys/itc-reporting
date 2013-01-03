package com.tinsys.itc_reporting.model;

import java.io.Serializable;

public class FiscalPeriod implements Serializable {

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
  public String toString() {
    // TODO Auto-generated method stub
    return this.month + "/" + this.year;
  }

}

package com.tinsys.itc_reporting.model;

import java.io.Serializable;

public class FiscalPeriod implements Serializable, Comparable<FiscalPeriod> {

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
    return this.month + "/" + this.year;
  }

  @Override
  public int compareTo(FiscalPeriod o) {
    int result = this.getYear()-o.getYear();
    if (result != 0){
      if (result > 0){
        return -1;
      } else {
        return 1;
      }
    }

    result = this.getMonth()-o.getMonth();
    if (result > 0) {
      return -1;
    } else if (result < 0) {
      return 1;
    } else {
      return 0;
    } 
  }
  
}

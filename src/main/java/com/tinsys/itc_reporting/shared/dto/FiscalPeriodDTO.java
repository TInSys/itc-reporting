package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;

public class FiscalPeriodDTO implements Serializable,Comparable<FiscalPeriodDTO> {

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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + month;
    result = prime * result + year;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FiscalPeriodDTO other = (FiscalPeriodDTO) obj;
    if (month != other.month)
      return false;
    if (year != other.year)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return month + "/" + year;
  }

  @Override
  public int compareTo(FiscalPeriodDTO o) {
    int result = this.getYear()-o.getYear();
    if (result != 0){
      if (result > 0){
        return 1;
      } else {
        return -1;
      }
    }

    result = this.getMonth()-o.getMonth();
    if (result > 0) {
      return 1;
    } else if (result < 0) {
      return -1;
    } else {
      return 0;
    } 
  }

}

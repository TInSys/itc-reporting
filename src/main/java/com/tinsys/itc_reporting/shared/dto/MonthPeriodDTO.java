package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;

public class MonthPeriodDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;
    private int month;
    private int year;
    private String periodType;

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

    public void setPeriodType(String periodType) {
      this.periodType = periodType;
   }

   public String getPeriodType() {
      return periodType;
   }

   @Override
   public String toString() {
      return month+"/"+year;
   }

}

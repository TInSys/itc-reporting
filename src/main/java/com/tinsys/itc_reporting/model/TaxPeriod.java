package com.tinsys.itc_reporting.model;

import java.io.Serializable;
import java.util.Date;

public class TaxPeriod implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;
    private Date startDate;
    private Date stopDate;
    private String periodType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

   public void setPeriodType(String periodType) {
      this.periodType = periodType;
   }

   public String getPeriodType() {
      return periodType;
   }

}

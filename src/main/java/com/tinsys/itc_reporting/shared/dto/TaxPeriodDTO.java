package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;
import java.util.Date;

public class TaxPeriodDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;
    private Date startDate;
    private Date stopDate;
    
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

   @Override
   public String toString() {
      return startDate +((stopDate!=null)?" to "+stopDate:" ");
   }

}

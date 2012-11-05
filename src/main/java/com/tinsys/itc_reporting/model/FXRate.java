package com.tinsys.itc_reporting.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class FXRate implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;
    private BigDecimal rate;
    private Zone zone;
    private FiscalPeriod period;
    private String currencyIso;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public FiscalPeriod getPeriod() {
        return period;
    }

    public void setPeriod(FiscalPeriod period) {
        this.period = period;
    }

   public void setCurrencyIso(String currencyIso) {
      this.currencyIso = currencyIso;
   }

   public String getCurrencyIso() {
      return currencyIso;
   }

}

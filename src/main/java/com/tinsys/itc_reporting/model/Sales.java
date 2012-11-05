package com.tinsys.itc_reporting.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Sales implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private Integer soldUnits;
    private BigDecimal individualPrice;
    private BigDecimal totalPrice;
    private String countryCode;
    private FiscalPeriod period;
    private Zone zone;
    private Application application;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getSoldUnits() {
        return soldUnits;
    }
    public void setSoldUnits(Integer soldUnits) {
        this.soldUnits = soldUnits;
    }
    public BigDecimal getIndividualPrice() {
        return individualPrice;
    }
    public void setIndividualPrice(BigDecimal individualPrice) {
        this.individualPrice = individualPrice;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public FiscalPeriod getPeriod() {
        return period;
    }
    public void setPeriod(FiscalPeriod period) {
        this.period = period;
    }
    public Zone getZone() {
        return zone;
    }
    public void setZone(Zone zone) {
        this.zone = zone;
    }
    public Application getApplication() {
        return application;
    }
    public void setApplication(Application application) {
        this.application = application;
    }
    
    @Override
   public String toString() {
      // TODO Auto-generated method stub
      return "Id:"+this.getId()+"  SoldUnits:"+this.getSoldUnits()+" IndividualPrice:"+this.getIndividualPrice()+" TotaPrice:"+this.getTotalPrice()+" Country code:"+this.getCountryCode()+" Period:"+this.getPeriod()+" Zone:"+this.getZone()+" App:"+this.getApplication();
   }
}

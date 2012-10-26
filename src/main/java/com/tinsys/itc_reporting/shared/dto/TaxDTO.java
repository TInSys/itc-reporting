package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class TaxDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private BigDecimal rate;
    private ZoneDTO zone;
    private TaxPeriodDTO period;

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

    public ZoneDTO getZone() {
        return zone;
    }

    public void setZone(ZoneDTO zone) {
        this.zone = zone;
    }

    public TaxPeriodDTO getPeriod() {
        return period;
    }

    public void setPeriod(TaxPeriodDTO period) {
        this.period = period;
    }

}

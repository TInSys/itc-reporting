package com.tinsys.itc_reporting.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Royalty implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private BigDecimal shareRate;
    private Company company;
    private Application application;
    private List<Zone> zones;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public BigDecimal getShareRate() {
        return shareRate;
    }
    public void setShareRate(BigDecimal shareRate) {
        this.shareRate = shareRate;
    }
    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
    public Application getApplication() {
        return application;
    }
    public void setApplication(Application application) {
        this.application = application;
    }
    public List<Zone> getZones() {
        return zones;
    }
    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }
    
}

package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class RoyaltyDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private BigDecimal shareRate;
    private CompanyDTO company;
    private ApplicationDTO application;
    private List<ZoneDTO> zones;
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
    public CompanyDTO getCompany() {
        return company;
    }
    public void setCompany(CompanyDTO company) {
        this.company = company;
    }
    public ApplicationDTO getApplication() {
        return application;
    }
    public void setApplication(ApplicationDTO application) {
        this.application = application;
    }
    public List<ZoneDTO> getZones() {
        return zones;
    }
    public void setZones(List<ZoneDTO> zones) {
        this.zones = zones;
    }
    
}

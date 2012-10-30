package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;
import java.util.List;


public class MonthReportSummary implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String zoneName;
    private List<ApplicationReportSummary> applications;
    private ApplicationReportSummary total;
    public String getZoneName() {
        return zoneName;
    }
    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }
    public List<ApplicationReportSummary> getApplications() {
        return applications;
    }
    public void setApplications(List<ApplicationReportSummary> applications) {
        this.applications = applications;
    }
    public ApplicationReportSummary getTotal() {
        return total;
    }
    public void setTotal(ApplicationReportSummary total) {
        this.total = total;
    }
    
    
}

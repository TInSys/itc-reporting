package com.tinsys.itc_reporting.shared.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class SalesDTO implements Serializable {

   /**
     * 
     */
   private static final long serialVersionUID = 1L;
   private Long id;
   private Integer soldUnits;
   private BigDecimal individualPrice;
   private BigDecimal totalPrice;
   private BigDecimal individualProceeds;
   private BigDecimal totalProceeds;
   private String countryCode;
   private String promocode;
   private FiscalPeriodDTO periodDTO;
   private ZoneDTO zoneDTO;
   private ApplicationDTO applicationDTO;

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

   public BigDecimal getIndividualProceeds() {
      return individualProceeds;
   }

   public void setIndividualProceeds(BigDecimal individualProceeds) {
      this.individualProceeds = individualProceeds;
   }

   public BigDecimal getTotalProceeds() {
      return totalProceeds;
   }

   public void setTotalProceeds(BigDecimal totalProceeds) {
      this.totalProceeds = totalProceeds;
   }

   public String getCountryCode() {
      return countryCode;
   }

   public void setCountryCode(String countryCode) {
      this.countryCode = countryCode;
   }

   public void setPromocode(String promocode) {
      this.promocode = promocode;
   }

   public String getPromocode() {
      return promocode;
   }

   public FiscalPeriodDTO getPeriod() {
      return periodDTO;
   }

   public void setPeriod(FiscalPeriodDTO periodDTO) {
      this.periodDTO = periodDTO;
   }

   public ZoneDTO getZone() {
      return zoneDTO;
   }

   public void setZone(ZoneDTO zoneDTO) {
      this.zoneDTO = zoneDTO;
   }

   public ApplicationDTO getApplication() {
      return applicationDTO;
   }

   public void setApplication(ApplicationDTO applicationDTO) {
      this.applicationDTO = applicationDTO;
   }

}

package com.tinsys.itc_reporting.server.utils;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.Company;
import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Royalty;
import com.tinsys.itc_reporting.model.TaxPeriod;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.TaxPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class DTOUtils {

   public static FiscalPeriodDTO periodToPeriodDTO(FiscalPeriod aPeriod){
      FiscalPeriodDTO periodDTO = new FiscalPeriodDTO();
      periodDTO.setId(aPeriod.getId());
      periodDTO.setMonth(aPeriod.getMonth());
      periodDTO.setYear(aPeriod.getYear());
      return periodDTO;
   }

   public static FiscalPeriod periodDTOtoPeriod(FiscalPeriodDTO aPeriodDTO){
      FiscalPeriod period = new FiscalPeriod();
      period.setId(aPeriodDTO.getId());
      period.setMonth(aPeriodDTO.getMonth());
      period.setYear(aPeriodDTO.getYear());
      return period;
   }

   public static CompanyDTO companyToCompanyDTO(Company aCompany){
       CompanyDTO companyDTO = new CompanyDTO();
       companyDTO.setId(aCompany.getId());
       companyDTO.setName(aCompany.getName());
       companyDTO.setCurrencyISO(aCompany.getCurrencyISO());
       return companyDTO;
    }

   
   public static Company companyDTOToCompany(CompanyDTO aCompanyDTO){
       Company company = new Company();
       company.setId(aCompanyDTO.getId());
       company.setName(aCompanyDTO.getName());
       company.setCurrencyISO(aCompanyDTO.getCurrencyISO());
       return company;
    }
       
   public static ZoneDTO zoneToZoneDTO(Zone aZone){
      ZoneDTO zoneDTO = new ZoneDTO();
      zoneDTO.setId(aZone.getId());
      zoneDTO.setCode(aZone.getCode());
      zoneDTO.setCurrencyISO(aZone.getCurrencyISO());
      zoneDTO.setName(aZone.getName());
      return zoneDTO;
   }

   public static Zone zoneDTOToZone(ZoneDTO aZoneDTO){
      Zone zone = new Zone();
      zone.setId(aZoneDTO.getId());
      zone.setCode(aZoneDTO.getCode());
      zone.setCurrencyISO(aZoneDTO.getCurrencyISO());
      zone.setName(aZoneDTO.getName());
      return zone;
   }
   
   public static ApplicationDTO applicationToApplicationDTO(Application aApplication){
      ApplicationDTO applicationDTO = new ApplicationDTO();
      applicationDTO.setId(aApplication.getId());
      applicationDTO.setVendorID(aApplication.getVendorID());
      applicationDTO.setName(aApplication.getName());
      return applicationDTO;
   }
   
   public static Application applicationDTOToApplication(ApplicationDTO aApplicationDTO){
      Application application = new Application();
      application.setId(aApplicationDTO.getId());
      application.setVendorID(aApplicationDTO.getVendorID());
      application.setName(aApplicationDTO.getName());
      return application;
   }

public static TaxPeriod taxPeriodDTOtoTaxPeriod(TaxPeriodDTO aPeriodDTO) {
    TaxPeriod taxPeriod = new TaxPeriod();
    taxPeriod.setId(aPeriodDTO.getId());
    taxPeriod.setStartDate(aPeriodDTO.getStartDate());
    taxPeriod.setStopDate(aPeriodDTO.getStopDate());
    return taxPeriod;
}

public static TaxPeriodDTO taxPeriodtoTaxPeriodDTO(TaxPeriod aPeriod) {
    TaxPeriodDTO taxPeriodDTO = new TaxPeriodDTO();
    taxPeriodDTO.setId(aPeriod.getId());
    taxPeriodDTO.setStartDate(aPeriod.getStartDate());
    taxPeriodDTO.setStopDate(aPeriod.getStopDate());
    return taxPeriodDTO;
}

public static Royalty royaltyDTOToRoyalty(RoyaltyDTO aRoyaltyDTO){
    Royalty royalty = new Royalty();
    royalty.setId(aRoyaltyDTO.getId());
    royalty.setShareRate(aRoyaltyDTO.getShareRate());
    royalty.setShareRateCalculationField(aRoyaltyDTO.getShareRateCalculationField());
    royalty.setApplication(DTOUtils.applicationDTOToApplication(aRoyaltyDTO.getApplication()));
    royalty.setCompany(DTOUtils.companyDTOToCompany(aRoyaltyDTO.getCompany()));
    List<Zone> zones = new ArrayList<Zone>();
    for (ZoneDTO zoneDTO : aRoyaltyDTO.getZones()) {
        Zone zone = DTOUtils.zoneDTOToZone(zoneDTO);
        zones.add(zone);  
    }
    royalty.setZones(zones);
    return royalty;
 }

public static RoyaltyDTO royaltyToRoyaltyDTO(Royalty aRoyalty){
    RoyaltyDTO royaltyDTO = new RoyaltyDTO();
    royaltyDTO.setId(aRoyalty.getId());
    royaltyDTO.setShareRate(aRoyalty.getShareRate());
    royaltyDTO.setShareRateCalculationField(aRoyalty.getShareRateCalculationField());
    royaltyDTO.setApplication(DTOUtils.applicationToApplicationDTO(aRoyalty.getApplication()));
    royaltyDTO.setCompany(DTOUtils.companyToCompanyDTO(aRoyalty.getCompany()));
    List<ZoneDTO> zonesDTO = new ArrayList<ZoneDTO>();
    for (Zone zone : aRoyalty.getZones()) {
        ZoneDTO zoneDTO = DTOUtils.zoneToZoneDTO(zone);
        zonesDTO.add(zoneDTO);  
    }
    royaltyDTO.setZones(zonesDTO);
    return royaltyDTO;
 }

}

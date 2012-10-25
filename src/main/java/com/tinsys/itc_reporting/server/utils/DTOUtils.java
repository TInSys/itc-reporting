package com.tinsys.itc_reporting.server.utils;

import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.model.Period;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class DTOUtils {

   public static PeriodDTO periodToPeriodDTO(Period aPeriod){
      PeriodDTO periodDTO = new PeriodDTO();
      periodDTO.setId(aPeriod.getId());
      periodDTO.setPeriodType(aPeriod.getPeriodType());
      periodDTO.setStartDate(aPeriod.getStartDate());
      periodDTO.setStopDate(aPeriod.getStopDate());
      return periodDTO;
   }

   public static Period periodDTOtoPeriod(PeriodDTO aPeriodDTO){
      Period period = new Period();
      period.setId(aPeriodDTO.getId());
      period.setPeriodType(aPeriodDTO.getPeriodType());
      period.setStartDate(aPeriodDTO.getStartDate());
      period.setStopDate(aPeriodDTO.getStopDate());
      return period;
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
   
}

package com.tinsys.itc_reporting.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class FXrateDAOTest implements FXRateDAO {

  @Override
  public ArrayList<FXRateDTO> getAllFXRates(ZoneDTO zoneDTO) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FXRateDTO createFXRate(FXRateDTO aFXRate) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FXRateDTO updateFXRate(FXRateDTO aFXRate) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteFXRate(FXRateDTO aFXRate) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public ArrayList<FXRateDTO> getAllFXRatesForPeriod(FiscalPeriodDTO monthYearToPeriod) {
    
    ArrayList<FXRateDTO> fxRateList = new ArrayList<FXRateDTO>();
    
    if (monthYearToPeriod.getYear()==2013 && monthYearToPeriod.getMonth()!=3){
      FXRateDTO fxRateDTO = new FXRateDTO();
      fxRateDTO.setId(0L);
      fxRateDTO.setPeriod(monthYearToPeriod);
      ZoneDTO zone = new ZoneDTO();
      zone.setId(0L);
      zone.setCode("ZONE0");
      zone.setName("ZONE0");
      fxRateDTO.setZone(zone);
      fxRateDTO.setRate(new BigDecimal(0.12453));
      fxRateList.add(fxRateDTO);

      fxRateDTO = new FXRateDTO();
      fxRateDTO.setId(1L);
      fxRateDTO.setPeriod(monthYearToPeriod);
      zone = new ZoneDTO();
      zone.setId(1L);
      zone.setCode("ZONE1");
      zone.setName("ZONE1");
      fxRateDTO.setZone(zone);
      fxRateDTO.setRate(new BigDecimal(0.99999));
      fxRateList.add(fxRateDTO);
    }
    return fxRateList;
  }

  @Override
  public void saveOrUpdate(List<FXRateDTO> fxRateList) {
    // TODO Auto-generated method stub
    
  }

}

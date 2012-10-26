package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public interface FXRateDAO {

    public ArrayList<FXRateDTO> getAllFXRates(ZoneDTO zoneDTO);

    public FXRateDTO findFXRate(Long id);

    public FXRateDTO createFXRate(FXRateDTO aFXRate);

    public FXRateDTO updateFXRate(FXRateDTO aFXRate);

    public void deleteFXRate(FXRateDTO aFXRate);

   public ArrayList<FXRateDTO> getAllFXRatesForPeriod(
         FiscalPeriodDTO monthYearToPeriod);
   
   public void saveOrUpdate(List<FXRateDTO> fxRateList);
}

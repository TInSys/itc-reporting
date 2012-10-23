package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.MonthPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@RemoteServiceRelativePath("springGwtServices/fxRateService")
public interface FXRateService extends RemoteService {

    public ArrayList<FXRateDTO> getAllFXRates(ZoneDTO zoneDTO) throws RuntimeException;

    public FXRateDTO findFXRate(Long id);

    public FXRateDTO createFXRate(FXRateDTO aFXRate) throws RuntimeException;

    public FXRateDTO updateFXRate(FXRateDTO aFXRate);

    public void deleteFXRate(FXRateDTO aFXRate);
    
    ArrayList<FXRateDTO> getAllFXRatesForPeriod(MonthPeriodDTO monthPeriodDto);
    
    void saveOrUpdate(List<FXRateDTO> fxRateList);
}

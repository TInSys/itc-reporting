package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public interface FXRateDAO {

    public ArrayList<FXRateDTO> getAllFXRates(ZoneDTO zoneDTO);

    public FXRateDTO findFXRate(Long id);

    public FXRateDTO createFXRate(FXRateDTO aFXRate);

    public FXRateDTO updateFXRate(FXRateDTO aFXRate);

    public void deleteFXRate(FXRateDTO aFXRate);
}

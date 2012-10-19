package com.tinsys.itc_reporting.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.FXRateService;
import com.tinsys.itc_reporting.dao.PeriodDAO;
import com.tinsys.itc_reporting.dao.FXRateDAO;
import com.tinsys.itc_reporting.server.utils.DateUtils;
import com.tinsys.itc_reporting.shared.dto.MonthPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Service("fxRateService")
@Transactional
public class FXRateServiceImpl implements FXRateService {

    @Autowired
    @Qualifier("fxRateDAO")
    private FXRateDAO fxRateDAO;
    @Autowired
    @Qualifier("periodDAO")
    private PeriodDAO periodDAO;

    @Override
    public ArrayList<FXRateDTO> getAllFXRates(ZoneDTO zoneDTO) {
        return fxRateDAO.getAllFXRates(zoneDTO);
    }

    @Override
    public FXRateDTO findFXRate(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FXRateDTO createFXRate(FXRateDTO aFXRate) {
        periodDAO.createPeriod(DateUtils.monthYearToPeriod(aFXRate.getPeriod().getId(),aFXRate.getPeriod().getMonth(), aFXRate.getPeriod().getYear()));
        aFXRate.setPeriod(aFXRate.getPeriod());
        return fxRateDAO.createFXRate(aFXRate);
    }

    @Override
    public FXRateDTO updateFXRate(FXRateDTO aFXRate) {
        periodDAO.updatePeriod(DateUtils.monthYearToPeriod(aFXRate.getPeriod().getId(),aFXRate.getPeriod().getMonth(), aFXRate.getPeriod().getYear()));
        aFXRate.setPeriod(aFXRate.getPeriod());
        return fxRateDAO.updateFXRate(aFXRate);
    }

    @Override
    public void deleteFXRate(FXRateDTO aFXRate) {
        fxRateDAO.deleteFXRate(aFXRate);
        periodDAO.deletePeriod(DateUtils.monthYearToPeriod(aFXRate.getPeriod().getId(),aFXRate.getPeriod().getMonth(), aFXRate.getPeriod().getYear()));

    }

}

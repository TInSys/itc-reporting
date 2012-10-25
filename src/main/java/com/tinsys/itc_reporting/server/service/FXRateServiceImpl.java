package com.tinsys.itc_reporting.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.FXRateService;
import com.tinsys.itc_reporting.dao.FXRateDAO;
import com.tinsys.itc_reporting.dao.PeriodDAO;
import com.tinsys.itc_reporting.server.utils.DateUtils;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.MonthPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;
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
       PeriodDTO aPeriod = DateUtils.monthYearToPeriod(null,aFXRate.getPeriod().getMonth(), aFXRate.getPeriod().getYear(), aFXRate.getPeriod().getPeriodType());
       aPeriod = periodDAO.createPeriod(aPeriod);
       aFXRate.getPeriod().setId(aPeriod.getId());
       aFXRate.getPeriod().setPeriodType("F");
       return fxRateDAO.createFXRate(aFXRate);
    }

    @Override
    public FXRateDTO updateFXRate(FXRateDTO aFXRate) {
        periodDAO.updatePeriod(DateUtils.monthYearToPeriod(aFXRate.getPeriod().getId(),aFXRate.getPeriod().getMonth(), aFXRate.getPeriod().getYear(),aFXRate.getPeriod().getPeriodType()));
        aFXRate.setPeriod(aFXRate.getPeriod());
        return fxRateDAO.updateFXRate(aFXRate);
    }

    @Override
    public void deleteFXRate(FXRateDTO aFXRate) {
        fxRateDAO.deleteFXRate(aFXRate);
        periodDAO.deletePeriod(DateUtils.monthYearToPeriod(aFXRate.getPeriod().getId(),aFXRate.getPeriod().getMonth(), aFXRate.getPeriod().getYear(),aFXRate.getPeriod().getPeriodType()));

    }

   @Override
   public ArrayList<FXRateDTO> getAllFXRatesForPeriod(MonthPeriodDTO monthPeriodDto) {
      return fxRateDAO.getAllFXRatesForPeriod(DateUtils.monthYearToPeriod(null, monthPeriodDto.getMonth(), monthPeriodDto.getYear(),monthPeriodDto.getPeriodType()));
   }

@Override
public void saveOrUpdate(List<FXRateDTO> fxRateList) {
    fxRateDAO.saveOrUpdate(fxRateList);
    
}

}

package com.tinsys.itc_reporting.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.FXRateService;
import com.tinsys.itc_reporting.dao.FXRateDAO;
import com.tinsys.itc_reporting.dao.FiscalPeriodDAO;
import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.FXRateDTO;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Service("fxRateService")
@Transactional
public class FXRateServiceImpl implements FXRateService {

  @Autowired
  @Qualifier("fxRateDAO")
  private FXRateDAO fxRateDAO;
  @Autowired
  @Qualifier("fiscalPeriodDAO")
  private FiscalPeriodDAO periodDAO;

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
    FiscalPeriodDTO aPeriod;
    FiscalPeriod period = DTOUtils.periodDTOtoPeriod(aFXRate.getPeriod());
    aPeriod = DTOUtils.periodToPeriodDTO(periodDAO.findPeriod(period));
    if (aPeriod == null) {
      aPeriod = periodDAO.createPeriod(aFXRate.getPeriod());
    }
    aFXRate.getPeriod().setId(aPeriod.getId());
    return fxRateDAO.createFXRate(aFXRate);
  }

  @Override
  public FXRateDTO updateFXRate(FXRateDTO aFXRate) {
    periodDAO.updatePeriod(aFXRate.getPeriod());
    aFXRate.setPeriod(aFXRate.getPeriod());
    return fxRateDAO.updateFXRate(aFXRate);
  }

  @Override
  public void deleteFXRate(FXRateDTO aFXRate) {
    fxRateDAO.deleteFXRate(aFXRate);
  }

  @Override
  public ArrayList<FXRateDTO> getAllFXRatesForPeriod(FiscalPeriodDTO fiscalPeriodDTO) {
    FiscalPeriodDTO aPeriod;
    FiscalPeriod period = DTOUtils.periodDTOtoPeriod(fiscalPeriodDTO);
    period = periodDAO.findPeriod(period);
    if (period == null) {
      aPeriod = periodDAO.createPeriod(fiscalPeriodDTO);
    } else {
      aPeriod = DTOUtils.periodToPeriodDTO(period);
    }
    return fxRateDAO.getAllFXRatesForPeriod(aPeriod);
  }

  @Override
  public void saveOrUpdate(List<FXRateDTO> fxRateList) {
    fxRateDAO.saveOrUpdate(fxRateList);

  }

}

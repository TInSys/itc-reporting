package com.tinsys.itc_reporting.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.TaxService;
import com.tinsys.itc_reporting.dao.TaxDAO;
import com.tinsys.itc_reporting.dao.TaxPeriodDAO;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.TaxPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Service("taxService")
@Transactional
public class TaxServiceImpl implements TaxService {

  @Autowired
  @Qualifier("taxDAO")
  private TaxDAO taxDAO;
  @Autowired
  @Qualifier("taxPeriodDAO")
  private TaxPeriodDAO periodDAO;

  @Override
  public ArrayList<TaxDTO> getAllTaxs(ZoneDTO zoneDTO) {
    return taxDAO.getAllTaxs(zoneDTO);
  }

  @Override
  public TaxDTO createTax(TaxDTO aTax, TaxPeriodDTO aPeriod) {
    if (aPeriod != null) {
      periodDAO.updatePeriod(aPeriod);
    }
    aTax.setPeriod(periodDAO.createPeriod(aTax.getPeriod()));
    return taxDAO.createTax(aTax);
  }

  @Override
  public TaxDTO updateTax(TaxDTO aTax) {
    aTax.setPeriod(periodDAO.updatePeriod(aTax.getPeriod()));
    return taxDAO.updateTax(aTax);
  }

  @Override
  public void deleteTax(TaxDTO aTax) {
    taxDAO.deleteTax(aTax);
    periodDAO.deletePeriod(aTax.getPeriod());

  }
}

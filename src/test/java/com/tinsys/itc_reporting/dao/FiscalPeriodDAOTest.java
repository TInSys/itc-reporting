package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;

public class FiscalPeriodDAOTest implements FiscalPeriodDAO {

  private List<FiscalPeriodDTO> fiscalPeriodList = new ArrayList<FiscalPeriodDTO>();
  
  @Override
  public FiscalPeriodDTO createPeriod(FiscalPeriodDTO aPeriod) {
    aPeriod.setId((long) fiscalPeriodList.size());
    fiscalPeriodList.add(aPeriod);
    return aPeriod;
  }

  @Override
  public FiscalPeriodDTO updatePeriod(FiscalPeriodDTO aPeriod) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public FiscalPeriod findPeriod(FiscalPeriod aPeriod) {
    for (FiscalPeriodDTO fiscalPeriodDTO : this.fiscalPeriodList) {
      if (fiscalPeriodDTO.getMonth()==aPeriod.getMonth() && fiscalPeriodDTO.getYear() == aPeriod.getYear()) {
         return DTOUtils.periodDTOtoPeriod(fiscalPeriodDTO);
      }
    };
    return null;
  }

}

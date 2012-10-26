package com.tinsys.itc_reporting.dao;

import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;


public interface FiscalPeriodDAO {

    public FiscalPeriodDTO createPeriod(FiscalPeriodDTO aPeriod);

    public FiscalPeriodDTO updatePeriod(FiscalPeriodDTO aPeriod);

    public void deletePeriod(FiscalPeriodDTO aPeriod);

   public FiscalPeriod findPeriod(FiscalPeriod aPeriod);
}

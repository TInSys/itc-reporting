package com.tinsys.itc_reporting.dao;

import com.tinsys.itc_reporting.model.TaxPeriod;
import com.tinsys.itc_reporting.shared.dto.TaxPeriodDTO;


public interface TaxPeriodDAO {

    public TaxPeriodDTO createPeriod(TaxPeriodDTO aPeriod);

    public TaxPeriodDTO updatePeriod(TaxPeriodDTO aPeriod);

    public void deletePeriod(TaxPeriodDTO aPeriod);

   public TaxPeriod findPeriod(TaxPeriod aPeriod);
}

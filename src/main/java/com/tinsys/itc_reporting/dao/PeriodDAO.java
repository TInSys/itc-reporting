package com.tinsys.itc_reporting.dao;

import com.tinsys.itc_reporting.model.Period;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;


public interface PeriodDAO {

    public PeriodDTO createPeriod(PeriodDTO aPeriod);

    public PeriodDTO updatePeriod(PeriodDTO aPeriod);

    public void deletePeriod(PeriodDTO aPeriod);

   public Period findPeriod(Period aPeriod);
}

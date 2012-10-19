package com.tinsys.itc_reporting.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.PeriodService;
import com.tinsys.itc_reporting.dao.PeriodDAO;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;

@Service("periodService")
@Transactional
public class PeriodServiceImpl implements PeriodService {

    @Autowired
    @Qualifier("periodDAO")
    private PeriodDAO periodDAO;

    @Override
    public PeriodDTO updatePeriod(PeriodDTO aPeriod) {
        return periodDAO.updatePeriod(aPeriod);
    }

}

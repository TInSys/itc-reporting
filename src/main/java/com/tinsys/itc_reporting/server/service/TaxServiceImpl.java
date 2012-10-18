package com.tinsys.itc_reporting.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.TaxService;
import com.tinsys.itc_reporting.client.service.ZoneService;
import com.tinsys.itc_reporting.dao.PeriodDAO;
import com.tinsys.itc_reporting.dao.TaxDAO;
import com.tinsys.itc_reporting.dao.ZoneDAO;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Service("taxService")
@Transactional
public class TaxServiceImpl implements TaxService {

    @Autowired
    @Qualifier("taxDAO")
    private TaxDAO taxDAO;
    @Autowired
    @Qualifier("periodDAO")
    private PeriodDAO periodDAO;
    
   @Override
   public ArrayList<TaxDTO> getAllTaxs(ZoneDTO zoneDTO) {
      return taxDAO.getAllTaxs(zoneDTO);
   }

   @Override
   public TaxDTO findTax(Long id) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public TaxDTO createTax(TaxDTO aTax,PeriodDTO aPeriod) {
      if (aPeriod!=null){
        periodDAO.updatePeriod(aPeriod);
      }
      aTax.setPeriod(periodDAO.createPeriod(aTax.getPeriod()));
      return taxDAO.createTax(aTax);
   }

   @Override
   public TaxDTO updateTax(TaxDTO aTax) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void deleteTax(TaxDTO aTax) {
      // TODO Auto-generated method stub
      
   }

   

}

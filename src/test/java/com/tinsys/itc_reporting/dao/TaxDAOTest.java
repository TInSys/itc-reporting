package com.tinsys.itc_reporting.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.tinsys.itc_reporting.model.Tax;
import com.tinsys.itc_reporting.model.TaxPeriod;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class TaxDAOTest implements TaxDAO {

  private List<Tax> taxList = new ArrayList<Tax>();
  
  public TaxDAOTest() {
    Tax tax = new Tax();
    tax.setId(0L);
    TaxPeriod period = new TaxPeriod();
    period.setId(0L);
    
    Date startDate;
    Calendar cal1 = new GregorianCalendar();
    cal1.set(Calendar.YEAR, 2013);
    cal1.set(Calendar.MONTH, 4 - 1);
    cal1.set(Calendar.DAY_OF_MONTH, 1);
    cal1.set(Calendar.HOUR_OF_DAY, 0);
    cal1.set(Calendar.MINUTE, 0);
    cal1.set(Calendar.SECOND, 0);
    cal1.set(Calendar.MILLISECOND, 0);
    startDate = cal1.getTime();
    Date endOfMonthDate = cal1.getTime();
    CalendarUtil.addMonthsToDate(endOfMonthDate, 1);
    CalendarUtil.addDaysToDate(endOfMonthDate, -1);
    
    period.setStartDate(startDate);
    period.setStopDate(endOfMonthDate);
    tax.setPeriod(period);
    tax.setRate(new BigDecimal(0.25));
    Zone zone = new Zone();
    zone.setId(0L);
    zone.setName("ZONE0");
    zone.setCode("ZONE0");
    tax.setZone(zone);
    taxList.add(tax);
    
    
    tax = new Tax();
    tax.setId(1L);
    period = new TaxPeriod();
    period.setId(1L);
    
    cal1 = new GregorianCalendar();
    cal1.set(Calendar.YEAR, 2013);
    cal1.set(Calendar.MONTH, 10 - 1);
    cal1.set(Calendar.DAY_OF_MONTH, 1);
    cal1.set(Calendar.HOUR_OF_DAY, 0);
    cal1.set(Calendar.MINUTE, 0);
    cal1.set(Calendar.SECOND, 0);
    cal1.set(Calendar.MILLISECOND, 0);
    startDate = cal1.getTime();
    endOfMonthDate = cal1.getTime();
    CalendarUtil.addMonthsToDate(endOfMonthDate, 1);
    CalendarUtil.addDaysToDate(endOfMonthDate, -1);
    
    period.setStartDate(startDate);
    period.setStopDate(endOfMonthDate);
    tax.setPeriod(period);
    tax.setRate(new BigDecimal(0.21));
    zone = new Zone();
    zone.setId(4L);
    zone.setName("ZONE4");
    zone.setCode("ZONE4");
    tax.setZone(zone);
    taxList.add(tax);
    
  }
  
  @Override
  public ArrayList<TaxDTO> getAllTaxs(ZoneDTO zoneDTO) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TaxDTO createTax(TaxDTO aTax) {
    // TODO Auto-generated method stub
    return null;
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

  @Override
  public List<Tax> getTaxesForPeriod(FiscalPeriodDTO period) {
    List<Tax> tmpTaxList = new ArrayList<Tax>();
    for (Tax tax : taxList) {
      Calendar cal = Calendar.getInstance();
      cal.setTime(tax.getPeriod().getStartDate());
      int month = cal.get(Calendar.MONTH);
      if (month == period.getMonth()-1) {
        tmpTaxList.add(tax);
      }
    }
    return tmpTaxList;
  }

}

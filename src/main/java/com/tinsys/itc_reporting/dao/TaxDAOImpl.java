package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.tinsys.itc_reporting.model.Tax;
import com.tinsys.itc_reporting.model.TaxPeriod;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.TaxPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

@Repository
public class TaxDAOImpl implements TaxDAO {

  private SessionFactory factory;

  public SessionFactory getFactory() {
    return factory;
  }

  public void setFactory(SessionFactory factory) {
    this.factory = factory;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ArrayList<TaxDTO> getAllTaxs(ZoneDTO zoneDTO) {
    ArrayList<TaxDTO> result = new ArrayList<TaxDTO>();
    Zone zone = new Zone();
    zone.setId(zoneDTO.getId());
    zone.setCode(zoneDTO.getCode());
    zone.setName(zoneDTO.getName());
    zone.setCurrencyISO(zoneDTO.getCurrencyISO());

    ArrayList<Tax> taxList;
    try {
      Criteria criteria = factory.getCurrentSession().createCriteria(Tax.class);
      Criteria subCriteria = criteria.createCriteria("period");
      subCriteria.addOrder(Order.asc("startDate"));
      taxList = (ArrayList<Tax>) criteria.add(Restrictions.eq("zone", zone)).list();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    for (Tax tax : taxList) {
      TaxDTO taxDTO = new TaxDTO();
      taxDTO.setId(tax.getId());
      taxDTO.setRate(tax.getRate());
      taxDTO.setZone(zoneDTO);
      TaxPeriodDTO periodDTO = new TaxPeriodDTO();
      periodDTO.setId(tax.getPeriod().getId());
      periodDTO.setStartDate(tax.getPeriod().getStartDate());
      periodDTO.setStopDate(tax.getPeriod().getStopDate());
      taxDTO.setPeriod(periodDTO);
      result.add(taxDTO);
    }

    return result;
  }

  @Override
  public TaxDTO findTax(Long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TaxDTO createTax(TaxDTO aTax) {

    Tax tax = new Tax();
    tax.setRate(aTax.getRate());
    Zone zone = new Zone();
    zone.setId(aTax.getZone().getId());
    zone.setCode(aTax.getZone().getCode());
    zone.setName(aTax.getZone().getName());
    zone.setCurrencyISO(aTax.getZone().getCurrencyISO());
    tax.setZone(zone);
    TaxPeriod period = new TaxPeriod();
    period.setStartDate(aTax.getPeriod().getStartDate());
    period.setStopDate(aTax.getPeriod().getStopDate());
    period.setId(aTax.getPeriod().getId());
    tax.setPeriod(period);
    try {
      factory.getCurrentSession().persist(tax);
      Long id = (Long) factory.getCurrentSession().getIdentifier(tax);
      aTax.setId(id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return aTax;
  }

  @Override
  public TaxDTO updateTax(TaxDTO aTax) {
    Tax tax = (Tax) factory.getCurrentSession().get(Tax.class, aTax.getId());
    tax.setRate(aTax.getRate());
    Zone zone = new Zone();
    zone.setId(aTax.getZone().getId());
    zone.setCode(aTax.getZone().getCode());
    zone.setName(aTax.getZone().getName());
    zone.setCurrencyISO(aTax.getZone().getCurrencyISO());
    tax.setZone(zone);
    TaxPeriod period = new TaxPeriod();
    period.setStartDate(aTax.getPeriod().getStartDate());
    period.setStopDate(aTax.getPeriod().getStopDate());
    period.setId(aTax.getPeriod().getId());
    tax.setPeriod(period);
    factory.getCurrentSession().saveOrUpdate(tax);
    return aTax;
  }

  @Override
  public void deleteTax(TaxDTO aTax) {
    Tax tax = (Tax) factory.getCurrentSession().get(Tax.class, aTax.getId());
    tax.setRate(aTax.getRate());
    Zone zone = new Zone();
    zone.setId(aTax.getZone().getId());
    zone.setCode(aTax.getZone().getCode());
    zone.setName(aTax.getZone().getName());
    zone.setCurrencyISO(aTax.getZone().getCurrencyISO());
    tax.setZone(zone);
    TaxPeriod period = new TaxPeriod();
    period.setStartDate(aTax.getPeriod().getStartDate());
    period.setStopDate(aTax.getPeriod().getStopDate());
    period.setId(aTax.getPeriod().getId());
    tax.setPeriod(period);
    factory.getCurrentSession().delete(tax);

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Tax> getTaxesForPeriod(FiscalPeriodDTO period) {
    Date startDate;
    Date endDate;
    Calendar cal1 = new GregorianCalendar();
    cal1.set(Calendar.YEAR, period.getYear());
    cal1.set(Calendar.MONTH, period.getMonth() - 1);
    cal1.set(Calendar.DAY_OF_MONTH, 1);
    cal1.set(Calendar.HOUR_OF_DAY, 0);
    cal1.set(Calendar.MINUTE, 0);
    cal1.set(Calendar.SECOND, 0);
    cal1.set(Calendar.MILLISECOND, 0);
    startDate = cal1.getTime();
    Date endOfMonthDate = cal1.getTime();
    CalendarUtil.addMonthsToDate(endOfMonthDate, 1);
    CalendarUtil.addDaysToDate(endOfMonthDate, -1);
    endDate = endOfMonthDate;

    Query query = factory.getCurrentSession().createQuery(
        "select tax from Tax tax where tax.period.startDate <= :sStartDate and (tax.period.stopDate >= :sStopDate or tax.period.stopDate = null) ");
    query.setParameter("sStartDate", startDate);
    query.setParameter("sStopDate", endDate);
    return (ArrayList<Tax>) query.list();

  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Tax> getAllTaxs() {
    return factory.getCurrentSession().createCriteria(Tax.class).list();
  }

}

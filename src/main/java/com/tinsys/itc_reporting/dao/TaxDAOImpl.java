package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.Period;
import com.tinsys.itc_reporting.model.Tax;
import com.tinsys.itc_reporting.model.Zone;
import com.tinsys.itc_reporting.shared.dto.PeriodDTO;
import com.tinsys.itc_reporting.shared.dto.TaxDTO;
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
            Criteria criteria = factory.getCurrentSession().createCriteria(
                    Tax.class);
            Criteria subCriteria = criteria.createCriteria("period");
            subCriteria.addOrder(Order.asc("startDate"));
            taxList = (ArrayList<Tax>) criteria.add(
                    Restrictions.eq("zone", zone)).list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Tax tax : taxList) {
            TaxDTO taxDTO = new TaxDTO();
            taxDTO.setId(tax.getId());
            taxDTO.setRate(tax.getRate());
            taxDTO.setZone(zoneDTO);
            PeriodDTO periodDTO = new PeriodDTO();
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
        Period period = new Period();
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
        Tax tax = (Tax) factory.getCurrentSession()
                .get(Tax.class, aTax.getId());
        tax.setRate(aTax.getRate());
        Zone zone = new Zone();
        zone.setId(aTax.getZone().getId());
        zone.setCode(aTax.getZone().getCode());
        zone.setName(aTax.getZone().getName());
        zone.setCurrencyISO(aTax.getZone().getCurrencyISO());
        tax.setZone(zone);
        Period period = new Period();
        period.setStartDate(aTax.getPeriod().getStartDate());
        period.setStopDate(aTax.getPeriod().getStopDate());
        period.setId(aTax.getPeriod().getId());
        tax.setPeriod(period);
        factory.getCurrentSession().saveOrUpdate(tax);
        return aTax;
    }

    @Override
    public void deleteTax(TaxDTO aTax) {
        Tax tax = (Tax) factory.getCurrentSession()
                .get(Tax.class, aTax.getId());
        tax.setRate(aTax.getRate());
        Zone zone = new Zone();
        zone.setId(aTax.getZone().getId());
        zone.setCode(aTax.getZone().getCode());
        zone.setName(aTax.getZone().getName());
        zone.setCurrencyISO(aTax.getZone().getCurrencyISO());
        tax.setZone(zone);
        Period period = new Period();
        period.setStartDate(aTax.getPeriod().getStartDate());
        period.setStopDate(aTax.getPeriod().getStopDate());
        period.setId(aTax.getPeriod().getId());
        tax.setPeriod(period);
        factory.getCurrentSession().delete(tax);

    }

}

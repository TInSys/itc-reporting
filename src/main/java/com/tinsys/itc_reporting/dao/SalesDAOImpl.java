package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.FiscalPeriod;
import com.tinsys.itc_reporting.model.Sales;
import com.tinsys.itc_reporting.shared.dto.FiscalPeriodDTO;
import com.tinsys.itc_reporting.shared.dto.SalesDTO;

@Repository
public class SalesDAOImpl implements SalesDAO {

    private SessionFactory factory;

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public ArrayList<SalesDTO> getAllSales() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SalesDTO findSale(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteSale(SalesDTO aSale) {
        // TODO Auto-generated method stub

    }

    @Override
    public Sales findSale(Sales aSale) {
        Sales sales = (Sales) factory
                .getCurrentSession()
                .createCriteria(Sales.class)
                .add(Restrictions.eq("application", aSale.getApplication()))
                .add(Restrictions.eq("zone", aSale.getZone()))
                .add(Restrictions.eq("period", aSale.getPeriod()))
                .add(Restrictions.eq("countryCode", aSale.getCountryCode()))
                .add(Restrictions.eq("individualPrice",
                        aSale.getIndividualPrice())).uniqueResult();
        System.out.println("Found sale "
                + ((sales != null) ? sales.getId() : sales) + " for "
                + aSale.getApplication().getId());
        return sales;
    }

    @Override
    public Sales createSale(Sales aSale) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Sales updateSale(Sales aSale) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveOrUpdate(List<Sales> summarizedSales) {
        for (Sales sale : summarizedSales) {
            factory.getCurrentSession().merge(sale);
        }
    }

    @Override
    public List<Sales> getAllSales(FiscalPeriodDTO aFiscalPeriodDTO) {
        FiscalPeriod period = (FiscalPeriod) factory.getCurrentSession()
                .createCriteria(FiscalPeriod.class)
                .add(Restrictions.eq("month", aFiscalPeriodDTO.getMonth()))
                .add(Restrictions.eq("year", aFiscalPeriodDTO.getYear()))
                .uniqueResult();
        if (period != null) {
            Criteria criteria = factory.getCurrentSession().createCriteria(
                    Sales.class);
            Criteria subCriteriaZone = criteria.createCriteria("zone");
            subCriteriaZone.addOrder(Order.asc("name"));
            Criteria subCriteriaApplication = criteria
                    .createCriteria("application");
            subCriteriaApplication.addOrder(Order.asc("name"));
            @SuppressWarnings("unchecked")
            List<Sales> sales = criteria.add(Restrictions.eq("period", period))
                    .list();
            
            return sales;
        }
        return null;
    }

}

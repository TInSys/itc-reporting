package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;

@Repository
public class ApplicationDAOImpl implements ApplicationDAO {

    private SessionFactory factory;

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    public ArrayList<ApplicationDTO> getAllApplications() {
        ArrayList<ApplicationDTO> result = new ArrayList<ApplicationDTO>();
        @SuppressWarnings("unchecked")
        ArrayList<Application> applicationList = (ArrayList<Application>) factory
                .getCurrentSession().createCriteria(Application.class)
                .addOrder(Order.asc("vendorID")).list();

        for (Application application : applicationList) {
            ApplicationDTO applicationDTO = new ApplicationDTO();
            applicationDTO.setId(application.getId());
            applicationDTO.setVendorID(application.getVendorID());
            applicationDTO.setName(application.getName());
            result.add(applicationDTO);
        }

        return result;
    }

    public ApplicationDTO findApplication(Long id) {
        Application application = (Application) factory.getCurrentSession().get(Application.class, id);
        ApplicationDTO result = new ApplicationDTO();
        result.setId(application.getId());
        result.setVendorID(application.getVendorID());
        result.setName(application.getName());
        return result;
    }

    public ApplicationDTO createApplication(ApplicationDTO aApplication) {
        Application application = new Application();
        application.setVendorID(aApplication.getVendorID());
        application.setName(aApplication.getName());
        factory.getCurrentSession().persist(application);
        Long id = (Long) factory.getCurrentSession().getIdentifier(application);
        aApplication.setId(id);
        return aApplication;
    }

    public ApplicationDTO updateApplication(ApplicationDTO aApplication) {
        Application application = (Application) factory.getCurrentSession().get(Application.class,
                aApplication.getId());
        application.setVendorID(aApplication.getVendorID());
        application.setName(aApplication.getName());
        factory.getCurrentSession().saveOrUpdate(application);
        return aApplication;
    }

    public void deleteApplication(ApplicationDTO aApplication) {
        Application application = new Application();
        application.setId(aApplication.getId());
        application.setVendorID(aApplication.getVendorID());
        application.setName(aApplication.getName());
        factory.getCurrentSession().delete(application);
    }

   @Override
   public Application findApplicationByVendorID(String vendorID) {
      Application application = (Application) factory.getCurrentSession().createCriteria(Application.class).add(Restrictions.eq("vendorID", vendorID)).uniqueResult();
      return application;
   }

}

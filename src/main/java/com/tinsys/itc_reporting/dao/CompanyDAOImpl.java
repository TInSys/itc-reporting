package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import com.tinsys.itc_reporting.model.Company;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;

@Repository
public class CompanyDAOImpl implements CompanyDAO {

  private SessionFactory factory;

  public SessionFactory getFactory() {
    return factory;
  }

  public void setFactory(SessionFactory factory) {
    this.factory = factory;
  }

  public ArrayList<CompanyDTO> getAllCompanies() {
    ArrayList<CompanyDTO> result = new ArrayList<CompanyDTO>();
    @SuppressWarnings("unchecked")
    ArrayList<Company> companyList = (ArrayList<Company>) factory.getCurrentSession().createCriteria(Company.class).addOrder(Order.asc("name")).list();

    for (Company company : companyList) {
      CompanyDTO companyDTO = new CompanyDTO();
      companyDTO.setId(company.getId());
      companyDTO.setName(company.getName());
      companyDTO.setCurrencyISO(company.getCurrencyISO());
      result.add(companyDTO);
    }

    return result;
  }

  public CompanyDTO findCompany(Long id) {
    Company company = (Company) factory.getCurrentSession().get(Company.class, id);
    CompanyDTO result = new CompanyDTO();
    result.setId(company.getId());
    result.setName(company.getName());
    result.setCurrencyISO(company.getCurrencyISO());
    return result;
  }

  public CompanyDTO createCompany(CompanyDTO aCompany) {
    Company company = new Company();
    company.setName(aCompany.getName());
    company.setCurrencyISO(aCompany.getCurrencyISO());
    factory.getCurrentSession().persist(company);
    Long id = (Long) factory.getCurrentSession().getIdentifier(company);
    aCompany.setId(id);
    return aCompany;
  }

  public CompanyDTO updateCompany(CompanyDTO aCompany) {
    Company company = (Company) factory.getCurrentSession().get(Company.class, aCompany.getId());
    company.setName(aCompany.getName());
    company.setCurrencyISO(aCompany.getCurrencyISO());
    factory.getCurrentSession().saveOrUpdate(company);
    return aCompany;
  }

  public void deleteCompany(CompanyDTO aCompany) {
    Company company = new Company();
    company.setId(aCompany.getId());
    company.setName(aCompany.getName());
    company.setCurrencyISO(aCompany.getCurrencyISO());
    factory.getCurrentSession().delete(company);
  }

}

package com.tinsys.itc_reporting.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.CompanyService;
import com.tinsys.itc_reporting.dao.CompanyDAO;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;

@Service("companyService")
@Transactional
public class CompanyServiceImpl implements CompanyService {

  @Autowired
  @Qualifier("companyDAO")
  private CompanyDAO companyDAO;

  public ArrayList<CompanyDTO> getAllCompanies() {
    return companyDAO.getAllCompanies();
  }

  public CompanyDTO findCompany(Long id) {
    return companyDAO.findCompany(id);
  }

  public CompanyDTO createCompany(CompanyDTO aCompany) {
    return companyDAO.createCompany(aCompany);
  }

  public CompanyDTO updateCompany(CompanyDTO aCompany) {
    return companyDAO.updateCompany(aCompany);
  }

  public void deleteCompany(CompanyDTO aCompany) {
    companyDAO.deleteCompany(aCompany);
  }

}

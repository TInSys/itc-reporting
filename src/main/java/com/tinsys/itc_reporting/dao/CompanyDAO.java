package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import com.tinsys.itc_reporting.shared.dto.CompanyDTO;

public interface CompanyDAO {

    public ArrayList<CompanyDTO> getAllCompanies();

    public CompanyDTO findCompany(Long id);

    public CompanyDTO createCompany(CompanyDTO aCompany);

    public CompanyDTO updateCompany(CompanyDTO aCompany);

    public void deleteCompany(CompanyDTO aCompany);


}

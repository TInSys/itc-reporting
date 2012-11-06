package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;

@RemoteServiceRelativePath("springGwtServices/companyService")
public interface CompanyService extends RemoteService {

    public ArrayList<CompanyDTO> getAllCompanies();

    public CompanyDTO findCompany(Long id);

    public CompanyDTO createCompany(CompanyDTO aCompany);

    public CompanyDTO updateCompany(CompanyDTO aCompany);

    public void deleteCompany(CompanyDTO aCompany);
}

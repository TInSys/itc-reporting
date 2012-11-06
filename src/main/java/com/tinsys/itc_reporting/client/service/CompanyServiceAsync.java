package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;

public interface CompanyServiceAsync {

    void createCompany(CompanyDTO aCompany, AsyncCallback<CompanyDTO> callback);

    void deleteCompany(CompanyDTO aCompany, AsyncCallback<Void> callback);

    void findCompany(Long id, AsyncCallback<CompanyDTO> callback);

    void getAllCompanies(AsyncCallback<ArrayList<CompanyDTO>> callback);

    void updateCompany(CompanyDTO aCompany, AsyncCallback<CompanyDTO> callback);

}

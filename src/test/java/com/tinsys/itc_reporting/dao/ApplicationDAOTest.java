package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;

public class ApplicationDAOTest implements ApplicationDAO {

  private ArrayList<ApplicationDTO> applicationDTOList = new ArrayList<ApplicationDTO>();

  public ApplicationDAOTest() {
    ApplicationDTO newApplication = new ApplicationDTO();
    newApplication.setId(0L);
    newApplication.setName("Application 1");
    newApplication.setVendorID("VD1");

    applicationDTOList.add(newApplication);

    newApplication = new ApplicationDTO();
    newApplication.setId(1L);
    newApplication.setName("Application 2");
    newApplication.setVendorID("VD2");

    applicationDTOList.add(newApplication);

    newApplication = new ApplicationDTO();
    newApplication.setId(2L);
    newApplication.setName("Application 3");
    newApplication.setVendorID("VD3");

    applicationDTOList.add(newApplication);
  }

  @Override
  public ArrayList<ApplicationDTO> getAllApplications() {
    return applicationDTOList;
  }

  @Override
  public ApplicationDTO findApplication(Long id) {
    for (ApplicationDTO app : applicationDTOList) {
      if (app.getId() == id) {
        return app;
      }
    }
    return null;
  }

  @Override
  public ApplicationDTO createApplication(ApplicationDTO aApplication) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ApplicationDTO updateApplication(ApplicationDTO aApplication) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteApplication(ApplicationDTO aApplication) {
    // TODO Auto-generated method stub

  }

  @Override
  public Application findApplicationByVendorID(String vendorID) {
    // TODO Auto-generated method stub
    return null;
  }

}

package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;

public class ApplicationDAOTest implements ApplicationDAO {

  private ArrayList<ApplicationDTO> applicationDTOList = new ArrayList<ApplicationDTO>();
  
  @Override
  public ArrayList<ApplicationDTO> getAllApplications() {
    if (applicationDTOList == null || applicationDTOList.size()==0) {
      this.init();
    }
    return null;
  }

  @Override
  public ApplicationDTO findApplication(Long id) {
    // TODO Auto-generated method stub
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
  
  private void init() {
    ApplicationDTO newApplication = new ApplicationDTO();
    newApplication.setId(0L);
    newApplication.setName("Application 1");
    newApplication.setVendorID("VD1");
    
    applicationDTOList.add(newApplication);
    
    newApplication = new ApplicationDTO();
    newApplication.setId(0L);
    newApplication.setName("Application 2");
    newApplication.setVendorID("VD2");
    
    applicationDTOList.add(newApplication);
    
    newApplication = new ApplicationDTO();
    newApplication.setId(0L);
    newApplication.setName("Application 3");
    newApplication.setVendorID("VD3");
    
    applicationDTOList.add(newApplication);
  }

}

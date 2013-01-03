package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import com.tinsys.itc_reporting.model.Application;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;

public interface ApplicationDAO {

  public ArrayList<ApplicationDTO> getAllApplications();

  public ApplicationDTO findApplication(Long id);

  public ApplicationDTO createApplication(ApplicationDTO aApplication);

  public ApplicationDTO updateApplication(ApplicationDTO aApplication);

  public void deleteApplication(ApplicationDTO aApplication);

  public Application findApplicationByVendorID(String vendorID);
}

package com.tinsys.itc_reporting.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.ApplicationService;
import com.tinsys.itc_reporting.dao.ApplicationDAO;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;

@Service("applicationService")
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

  @Autowired
  @Qualifier("applicationDAO")
  private ApplicationDAO applicationDAO;

  public ArrayList<ApplicationDTO> getAllApplications() {
    return applicationDAO.getAllApplications();
  }

  public ApplicationDTO findApplication(Long id) {
    return applicationDAO.findApplication(id);
  }

  public ApplicationDTO createApplication(ApplicationDTO aApplication) {
    return applicationDAO.createApplication(aApplication);
  }

  public ApplicationDTO updateApplication(ApplicationDTO aApplication) {
    return applicationDAO.updateApplication(aApplication);
  }

  public void deleteApplication(ApplicationDTO aApplication) {
    applicationDAO.deleteApplication(aApplication);
  }

}

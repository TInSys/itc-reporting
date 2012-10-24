package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;

@RemoteServiceRelativePath("springGwtServices/applicationService")
public interface ApplicationService extends RemoteService {

    public ArrayList<ApplicationDTO> getAllApplications();

    public ApplicationDTO findApplication(Long id);

    public ApplicationDTO createApplication(ApplicationDTO aApplication);

    public ApplicationDTO updateApplication(ApplicationDTO aApplication);

    public void deleteApplication(ApplicationDTO aApplication);
}

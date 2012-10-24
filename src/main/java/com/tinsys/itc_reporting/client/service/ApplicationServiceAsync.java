package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;

public interface ApplicationServiceAsync {

    void createApplication(ApplicationDTO aApplication,
            AsyncCallback<ApplicationDTO> callback);

    void deleteApplication(ApplicationDTO aApplication,
            AsyncCallback<Void> callback);

    void findApplication(Long id, AsyncCallback<ApplicationDTO> callback);

    void getAllApplications(AsyncCallback<ArrayList<ApplicationDTO>> callback);

    void updateApplication(ApplicationDTO aApplication,
            AsyncCallback<ApplicationDTO> callback);

}

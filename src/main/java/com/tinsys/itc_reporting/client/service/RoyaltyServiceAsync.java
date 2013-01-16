package com.tinsys.itc_reporting.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;

public interface RoyaltyServiceAsync {

  void createRoyalty(RoyaltyDTO aRoyalty, AsyncCallback<RoyaltyDTO> callback);

  void deleteRoyalty(RoyaltyDTO aRoyalty, AsyncCallback<Void> callback);

  void findRoyalty(Long id, AsyncCallback<RoyaltyDTO> callback);

  void getAllRoyalty(CompanyDTO aCompanyDTO, AsyncCallback<List<RoyaltyDTO>> callback);

  void updateRoyalty(RoyaltyDTO aRoyalty, AsyncCallback<RoyaltyDTO> callback);

}

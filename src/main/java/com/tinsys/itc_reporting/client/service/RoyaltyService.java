package com.tinsys.itc_reporting.client.service;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;

@RemoteServiceRelativePath("springGwtServices/royaltyService")
public interface RoyaltyService extends RemoteService {

    public ArrayList<RoyaltyDTO> getAllRoyalties();

    public List<RoyaltyDTO> getAllRoyalty(CompanyDTO aCompanyDTO);

    public RoyaltyDTO findRoyalty(Long id);

    public RoyaltyDTO createRoyalty(RoyaltyDTO aRoyalty);

    public RoyaltyDTO updateRoyalty(RoyaltyDTO aRoyalty);

    public void deleteRoyalty(RoyaltyDTO aRoyalty);
}

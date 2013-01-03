package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;

public interface RoyaltyDAO {

  public ArrayList<RoyaltyDTO> getAllRoyalties();

  public List<RoyaltyDTO> getAllRoyalty(CompanyDTO aCompanyDTO);

  public RoyaltyDTO findRoyalty(Long id);

  public RoyaltyDTO createRoyalty(RoyaltyDTO aRoyalty);

  public RoyaltyDTO updateRoyalty(RoyaltyDTO aRoyalty);

  public void deleteRoyalty(RoyaltyDTO aRoyalty);

}

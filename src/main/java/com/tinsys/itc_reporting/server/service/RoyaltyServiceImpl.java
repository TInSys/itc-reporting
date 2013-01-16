package com.tinsys.itc_reporting.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tinsys.itc_reporting.client.service.RoyaltyService;
import com.tinsys.itc_reporting.dao.RoyaltyDAO;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;

@Service("royaltyService")
@Transactional
public class RoyaltyServiceImpl implements RoyaltyService {

  @Autowired
  @Qualifier("royaltyDAO")
  private RoyaltyDAO royaltyDAO;

  @Override
  public List<RoyaltyDTO> getAllRoyalty(CompanyDTO aCompanyDTO) {
    return royaltyDAO.getAllRoyalty(aCompanyDTO);
  }

  @Override
  public RoyaltyDTO findRoyalty(Long id) {
    return royaltyDAO.findRoyalty(id);
  }

  @Override
  public RoyaltyDTO createRoyalty(RoyaltyDTO aRoyalty) {
    return royaltyDAO.createRoyalty(aRoyalty);
  }

  @Override
  public RoyaltyDTO updateRoyalty(RoyaltyDTO aRoyalty) {
    return royaltyDAO.updateRoyalty(aRoyalty);
  }

  @Override
  public void deleteRoyalty(RoyaltyDTO aRoyalty) {
    royaltyDAO.deleteRoyalty(aRoyalty);
  }

}

package com.tinsys.itc_reporting.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.tinsys.itc_reporting.shared.dto.ApplicationDTO;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public class RoyaltyDAOTest implements RoyaltyDAO {

  private ArrayList<RoyaltyDTO> royaltyDTOList;
  private ApplicationDAOTest applicationDAOTest;
  
  public RoyaltyDAOTest() {
    royaltyDTOList = new ArrayList<RoyaltyDTO>();
    applicationDAOTest = new ApplicationDAOTest();
    RoyaltyDTO newRoyaltyDTO = new RoyaltyDTO();
    CompanyDTO companyDTO = new CompanyDTO();
    companyDTO.setId(0L);
    companyDTO.setName("Company 0");
    for (ApplicationDTO applicationDTO : applicationDAOTest.getAllApplications()) {
      if (applicationDTO.getId()==0){
      newRoyaltyDTO.setId(0L);
      newRoyaltyDTO.setApplication(applicationDTO);
      newRoyaltyDTO.setCompany(companyDTO);
      newRoyaltyDTO.setShareRate(new BigDecimal(15));
      newRoyaltyDTO.setShareRateCalculationField("S");
      ZoneDTO zone = new ZoneDTO();
      zone.setId(0L);
      zone.setCode("ZONE0");
      zone.setName("ZONE0");
      List<ZoneDTO> zoneList = new ArrayList<ZoneDTO>();
      zoneList.add(zone);
      zone = new ZoneDTO();
      zone.setId(1L);
      zone.setCode("ZONE1");
      zone.setName("ZONE1");
      zoneList.add(zone);
      newRoyaltyDTO.setZones(zoneList);
      royaltyDTOList.add(newRoyaltyDTO);
    }}
    newRoyaltyDTO = new RoyaltyDTO();
    companyDTO = new CompanyDTO();
    companyDTO.setId(1L);
    companyDTO.setName("Company 1");
    for (ApplicationDTO applicationDTO : applicationDAOTest.getAllApplications()) {
      if (applicationDTO.getId()==1){
      newRoyaltyDTO.setId(1L);
      newRoyaltyDTO.setApplication(applicationDTO);
      newRoyaltyDTO.setCompany(companyDTO);
      newRoyaltyDTO.setShareRate(new BigDecimal(10));
      newRoyaltyDTO.setShareRateCalculationField("P");
      ZoneDTO zone = new ZoneDTO();
      zone.setId(0L);
      zone.setCode("ZONE0");
      zone.setName("ZONE0");
      List<ZoneDTO> zoneList = new ArrayList<ZoneDTO>();
      zoneList.add(zone);
      newRoyaltyDTO.setZones(zoneList);
      royaltyDTOList.add(newRoyaltyDTO);}
    }
  }
  
  @Override
  public ArrayList<RoyaltyDTO> getAllRoyalties() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<RoyaltyDTO> getAllRoyalty(CompanyDTO aCompanyDTO) {
    List<RoyaltyDTO> result = new ArrayList<RoyaltyDTO>();
    for (RoyaltyDTO royaltyDTO : this.royaltyDTOList) {
      if (royaltyDTO.getCompany().equals(aCompanyDTO)){
        result.add(royaltyDTO);
      }
    }
    return result;
  }

  @Override
  public RoyaltyDTO findRoyalty(Long id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RoyaltyDTO createRoyalty(RoyaltyDTO aRoyalty) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public RoyaltyDTO updateRoyalty(RoyaltyDTO aRoyalty) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void deleteRoyalty(RoyaltyDTO aRoyalty) {
    // TODO Auto-generated method stub
    
  }

}

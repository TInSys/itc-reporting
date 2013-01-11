package com.tinsys.itc_reporting.client;

import java.math.BigDecimal;
import java.math.RoundingMode;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tinsys.itc_reporting.server.service.RoyaltyComputer;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;

public class RoyaltyComputerTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test(expected = RuntimeException.class)
  public void testComputeWithNullValues() {
    RoyaltyComputer.compute(null, null, null);
  }

  @Test(expected = RuntimeException.class)
  public void testComputeWithZeroValues() {
    RoyaltyComputer.compute(null, new BigDecimal(0), new BigDecimal(0));
  }

  @Test
  public void testComputeRoyaltyOnSalesWithZeroShareRate() {
    RoyaltyDTO royaltyDTO = new RoyaltyDTO();
    royaltyDTO.setShareRate(new BigDecimal(0));
    royaltyDTO.setShareRateCalculationField("S");
    BigDecimal expected = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
    Assert.assertEquals(expected, RoyaltyComputer.compute(royaltyDTO, new BigDecimal(100.01), new BigDecimal(99)).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testComputeRoyaltyOnProceedsWithZeroShareRate() {
    RoyaltyDTO royaltyDTO = new RoyaltyDTO();
    royaltyDTO.setShareRate(new BigDecimal(0));
    royaltyDTO.setShareRateCalculationField("P");
    BigDecimal expected = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
    Assert.assertEquals(expected, RoyaltyComputer.compute(royaltyDTO, new BigDecimal(100.01), new BigDecimal(99)).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testComputeRoyaltyOnSales() {
    RoyaltyDTO royaltyDTO = new RoyaltyDTO();
    royaltyDTO.setShareRate(new BigDecimal(10));
    royaltyDTO.setShareRateCalculationField("S");
    BigDecimal expected = new BigDecimal(14.35).setScale(2, RoundingMode.HALF_UP);
    Assert.assertEquals(expected, RoyaltyComputer.compute(royaltyDTO, new BigDecimal(100.01), new BigDecimal(99)).setScale(2, RoundingMode.HALF_UP));
  }

  @Test
  public void testComputeRoyaltyOnProceeds() {
    RoyaltyDTO royaltyDTO = new RoyaltyDTO();
    royaltyDTO.setShareRate(new BigDecimal(10));
    royaltyDTO.setShareRateCalculationField("P");
    BigDecimal expected = new BigDecimal(10.001).setScale(2, RoundingMode.HALF_UP);
    Assert.assertEquals(expected, RoyaltyComputer.compute(royaltyDTO, new BigDecimal(100.01), new BigDecimal(99)).setScale(2, RoundingMode.HALF_UP));
  }

  @Test(expected = RuntimeException.class)
  public void testComputeRoyaltyOnUnkownCalculationMethod() {
    RoyaltyDTO royaltyDTO = new RoyaltyDTO();
    royaltyDTO.setShareRate(new BigDecimal(10));
    royaltyDTO.setShareRateCalculationField("XYZ");
    BigDecimal expected = new BigDecimal(10.001).setScale(2, RoundingMode.HALF_UP);
    Assert.assertEquals(expected, RoyaltyComputer.compute(royaltyDTO, new BigDecimal(100.01), new BigDecimal(99)).setScale(2, RoundingMode.HALF_UP));
  }

}

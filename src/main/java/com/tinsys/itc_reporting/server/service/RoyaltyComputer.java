package com.tinsys.itc_reporting.server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;

public class RoyaltyComputer {

  public static BigDecimal compute(RoyaltyDTO royalty, BigDecimal proceedsAfterTax, BigDecimal totalAmount){
    
    BigDecimal result = new BigDecimal(0);
    BigDecimal vatFactor = new BigDecimal(0);
    try {
      if (royalty.isOnSale()) {
        vatFactor = (totalAmount.multiply(new BigDecimal(0.7))).divide((proceedsAfterTax), 2, RoundingMode.HALF_UP);
        result = totalAmount.divide(vatFactor, 5, RoundingMode.HALF_UP).multiply(
            royalty.getShareRate().divide(new BigDecimal(100), 5, RoundingMode.HALF_UP));
      } else if (royalty.isOnProceeds()) {
        result = proceedsAfterTax.multiply(royalty.getShareRate().divide(new BigDecimal(100), 5, RoundingMode.HALF_UP));
      } else {
        throw new Exception("Could not find instructions for "+royalty.getShareRateCalculationField()+" calculation method");
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    }
    return result;    
  }
}

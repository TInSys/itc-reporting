package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;

import com.tinsys.itc_reporting.shared.dto.TaxDTO;
import com.tinsys.itc_reporting.shared.dto.ZoneDTO;

public interface TaxDAO {

    public ArrayList<TaxDTO> getAllTaxs(ZoneDTO zoneDTO);

    public TaxDTO findTax(Long id);

    public TaxDTO createTax(TaxDTO aTax);

    public TaxDTO updateTax(TaxDTO aTax);

    public void deleteTax(TaxDTO aTax);
}

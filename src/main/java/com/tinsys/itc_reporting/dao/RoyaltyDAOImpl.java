package com.tinsys.itc_reporting.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.tinsys.itc_reporting.model.Company;
import com.tinsys.itc_reporting.model.Royalty;
import com.tinsys.itc_reporting.server.utils.DTOUtils;
import com.tinsys.itc_reporting.shared.dto.CompanyDTO;
import com.tinsys.itc_reporting.shared.dto.RoyaltyDTO;

public class RoyaltyDAOImpl implements RoyaltyDAO {

    private SessionFactory factory;

    public SessionFactory getFactory() {
        return factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public ArrayList<RoyaltyDTO> getAllRoyalties() {
        // TODO Auto-generated method stub
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<RoyaltyDTO> getAllRoyalty(CompanyDTO aCompanyDTO) {
        ArrayList<RoyaltyDTO> result = new ArrayList<RoyaltyDTO>();

        Company company = DTOUtils.companyDTOToCompany(aCompanyDTO);
        ArrayList<Royalty> royaltyList;
        try {
            Criteria criteria = factory.getCurrentSession().createCriteria(
                    Royalty.class);
 /*           Criteria subCriteria = criteria.createCriteria("application");
            subCriteria.addOrder(Order.asc("name"));*/
            royaltyList = (ArrayList<Royalty>) criteria.add(
                    Restrictions.eq("company", company)).list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Royalty royalty : royaltyList) {
            RoyaltyDTO royaltyDTO = DTOUtils.royaltyToRoyaltyDTO(royalty);
            result.add(royaltyDTO);
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

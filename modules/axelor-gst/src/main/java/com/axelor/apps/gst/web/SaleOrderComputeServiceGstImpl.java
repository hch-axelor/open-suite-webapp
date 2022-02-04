package com.axelor.apps.gst.web;

import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineTaxService;
import com.axelor.apps.supplychain.service.SaleOrderComputeServiceSupplychainImpl;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

public class SaleOrderComputeServiceGstImpl extends SaleOrderComputeServiceSupplychainImpl {

  @Inject
  public SaleOrderComputeServiceGstImpl(
      SaleOrderLineService saleOrderLineService, SaleOrderLineTaxService saleOrderLineTaxService) {
    super(saleOrderLineService, saleOrderLineTaxService);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void _computeSaleOrder(SaleOrder saleOrder) throws AxelorException {
    super._computeSaleOrder(saleOrder);
    if (!Beans.get(AppBaseService.class).isApp("gst")) {

        return ;
      }

    BigDecimal taxTotal = saleOrder.getTaxTotal();

    List<SaleOrderLine> saleOrderLineList = saleOrder.getSaleOrderLineList();

    System.out.println(
        "Before: "
            + saleOrder.getExTaxTotal()
            + " : "
            + saleOrder.getCompanyExTaxTotal()
            + " : "
            + saleOrder.getInTaxTotal()
            + " : "
            + saleOrder.getCompanyCostTotal());
    BigDecimal igst =
        saleOrderLineList.stream().map(i -> i.getIgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal cgst =
        saleOrderLineList.stream().map(i -> i.getCgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal sgst =
        saleOrderLineList.stream().map(i -> i.getSgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
    saleOrder.setNetCGST(cgst);
    saleOrder.setNetSGST(sgst);
    saleOrder.setNetIGST(igst);

    System.out.println(saleOrder.getInAti());
    if(!saleOrder.getInAti()) {
    	
    	saleOrder.setTaxTotal(saleOrder.getTaxTotal().add(sgst).add(cgst).add(igst));
    	saleOrder.setInTaxTotal(saleOrder.getInTaxTotal().add(sgst).add(cgst).add(igst));
    	System.out.println(sgst + " : " + igst + " : " + cgst);
    	
    	System.out.println(
    			"After: "
    					+ saleOrder.getExTaxTotal()
    					+ " : "
    					+ saleOrder.getCompanyExTaxTotal()
    					+ " : "
    					+ saleOrder.getInTaxTotal()
    			);
    	
    }else {
    	saleOrder.setInTaxTotal(new BigDecimal(0));
	    BigDecimal allTaxIncluded = 
	    		saleOrderLineList.stream().map(i -> i.getInTaxTotal()).reduce(BigDecimal.ZERO,BigDecimal::add);

//	    saleOrder.setExTaxTotal(allTaxIncluded.subtract(igst).subtract(sgst).subtract(cgst));
//    	saleOrder.setTaxTotal(saleOrder.getTaxTotal().add(sgst).add(cgst).add(igst));
    	saleOrder.setInTaxTotal(allTaxIncluded);
    	saleOrder.setTaxTotal(saleOrder.getInTaxTotal().subtract(saleOrder.getExTaxTotal()));
    	
    	System.out.println(saleOrder.getTaxTotal());
    	System.out.println(sgst + " : " + igst + " : " + cgst);
    	
    	System.out.println(
    			"After: "
    					+ saleOrder.getExTaxTotal()
    					+ " : "
    					+ saleOrder.getCompanyExTaxTotal()
    					+ " : "
    					+ saleOrder.getInTaxTotal()
    			);

    }

    

  }
}

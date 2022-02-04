package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.ProductMultipleQtyService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.tax.AccountManagementService;
import com.axelor.apps.businessproduction.service.SaleOrderLineBusinessProductionServiceImpl;
//import com.axelor.apps.purchase.db.saleOrderLine;
//import com.axelor.apps.purchase.db.saleOrderLine;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderLineRepository;
import com.axelor.apps.sale.service.app.AppSaleService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;

//import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;

public class SaleOrderLineBusinessProductionGstServiceImpl extends SaleOrderLineBusinessProductionServiceImpl{

	@Inject
	public SaleOrderLineBusinessProductionGstServiceImpl(CurrencyService currencyService,
			PriceListService priceListService, ProductMultipleQtyService productMultipleQtyService,
			AppBaseService appBaseService, AppSaleService appSaleService,
			AccountManagementService accountManagementService, SaleOrderLineRepository saleOrderLineRepo,
			AppAccountService appAccountService, AnalyticMoveLineService analyticMoveLineService,
			AppSupplychainService appSupplychainService) {
		super(currencyService, priceListService, productMultipleQtyService, appBaseService, appSaleService,
				accountManagementService, saleOrderLineRepo, appAccountService, analyticMoveLineService, appSupplychainService);
		// TODO Auto-generated constructor stub
	}
	
	 @Override
	  public Map<String, BigDecimal> computeValues(SaleOrder saleOrder, SaleOrderLine saleOrderLine)  throws AxelorException{
		    if (!Beans.get(AppBaseService.class).isApp("gst")) {

		        return super.computeValues(saleOrder,saleOrderLine);
		      }


		 String partnerInvoiceAddressState =
			        saleOrder.getMainInvoicingAddress().getState() != null
			            ? saleOrder.getMainInvoicingAddress().getState().getName()
			            : "";

			    System.out.println(partnerInvoiceAddressState);

		String companyAddressState =
			        saleOrder.getCompany().getAddress().getState() != null
			            ? saleOrder.getCompany().getAddress().getState().getName()
			            : "";
			    System.out.println(saleOrder.getCompany().getAddress());



		    BigDecimal gstRateP = saleOrderLine.getProduct().getProductCategory().getGstRate();
		    saleOrderLine.setGstRate(gstRateP);

		    BigDecimal price = saleOrderLine.getPrice();
		    BigDecimal gstRate = saleOrderLine.getGstRate();
		    BigDecimal qty = saleOrderLine.getQty();


		 
		 HashMap<String, BigDecimal> map = new HashMap<>();
		    if (saleOrder == null
		        || saleOrderLine.getPrice() == null
		        || saleOrderLine.getInTaxPrice() == null
		        || saleOrderLine.getQty() == null) {
		      return map;
		    }
		    saleOrderLine.setSaleOrder(saleOrder);

		    BigDecimal exTaxTotal;
		    BigDecimal companyExTaxTotal;
		    BigDecimal inTaxTotal;
		    BigDecimal companyInTaxTotal;
		    BigDecimal priceDiscounted = this.computeDiscount(saleOrderLine, saleOrder.getInAti());
		    BigDecimal taxRate = BigDecimal.ZERO;
		    BigDecimal subTotalCostPrice = BigDecimal.ZERO;
		    boolean isSameState =!companyAddressState.equals("") &&
		    		 companyAddressState.equals(partnerInvoiceAddressState);

		    
		    if (saleOrderLine.getTaxLine() != null) {
		      taxRate = saleOrderLine.getTaxLine().getValue();
		    }

		    System.out.println(saleOrderLine.getSaleOrder().getInAti());
		    if (!saleOrder.getInAti()) {
			      exTaxTotal = computeAmount(saleOrderLine.getQty(), priceDiscounted);
			      saleOrderLine = computeGst(isSameState, saleOrderLine, exTaxTotal);
			      inTaxTotal =
		 exTaxTotal.add(exTaxTotal.multiply(taxRate)).add(saleOrderLine.getCgst()).add(saleOrderLine.getSgst()).add(saleOrderLine.getIgst());
			      companyExTaxTotal = this.getAmountInCompanyCurrency(exTaxTotal, saleOrder);
			      SaleOrderLine companysaleOrderLine = computeGst(isSameState,
		 saleOrderLine,companyExTaxTotal);
			      companyInTaxTotal =
		 companyExTaxTotal.add(companyExTaxTotal.multiply(taxRate)).add(companysaleOrderLine.getCgst()).add(companysaleOrderLine.getSgst()).add(companysaleOrderLine.getIgst());
			    } else {
			      inTaxTotal = computeAmount(saleOrderLine.getQty(), priceDiscounted);
			      saleOrderLine = computeGst(isSameState,saleOrderLine, inTaxTotal );
			      exTaxTotal = (inTaxTotal.divide(taxRate.add(BigDecimal.ONE), 2,
		 BigDecimal.ROUND_HALF_UP)).subtract(saleOrderLine.getIgst().add(saleOrderLine.getSgst().add(saleOrderLine.getCgst())));
			      companyInTaxTotal = this.getAmountInCompanyCurrency(inTaxTotal, saleOrder);
			      SaleOrderLine companysaleOrderLine = computeGst(isSameState,saleOrderLine, companyInTaxTotal );
			      companyExTaxTotal =
			          (companyInTaxTotal.divide(taxRate.add(BigDecimal.ONE), 2,
		 BigDecimal.ROUND_HALF_UP)).subtract(saleOrderLine.getIgst().add(saleOrderLine.getSgst().add(saleOrderLine.getCgst())));
			    }


		    if (saleOrderLine.getProduct() != null
		        && ((BigDecimal)
		                    productCompanyService.get(
		                        saleOrderLine.getProduct(), "costPrice", saleOrder.getCompany()))
		                .compareTo(BigDecimal.ZERO)
		            != 0) {
		      subTotalCostPrice =
		          ((BigDecimal)
		                  productCompanyService.get(
		                      saleOrderLine.getProduct(), "costPrice", saleOrder.getCompany()))
		              .multiply(saleOrderLine.getQty());
		    }
		    
		    saleOrderLine.setInTaxTotal(inTaxTotal);
		    saleOrderLine.setExTaxTotal(exTaxTotal);
		    saleOrderLine.setPriceDiscounted(priceDiscounted);
		    saleOrderLine.setCompanyInTaxTotal(companyInTaxTotal);
		    saleOrderLine.setCompanyExTaxTotal(companyExTaxTotal);
		    saleOrderLine.setSubTotalCostPrice(subTotalCostPrice);
		    map.put("gstRate", gstRate);
		    map.put("igst", saleOrderLine.getIgst());
		    map.put("sgst", saleOrderLine.getSgst());
		    map.put("cgst", saleOrderLine.getCgst());

		    map.put("inTaxTotal", inTaxTotal);
		    map.put("exTaxTotal", exTaxTotal);
		    map.put("priceDiscounted", priceDiscounted);
		    map.put("companyExTaxTotal", companyExTaxTotal);
		    map.put("companyInTaxTotal", companyInTaxTotal);
		    map.put("subTotalCostPrice", subTotalCostPrice);

		    map.putAll(this.computeSubMargin(saleOrder, saleOrderLine));

		    return map;

	  
	  
}

	 public static SaleOrderLine computeGst(boolean isSameState,SaleOrderLine saleOrderLine,
			 BigDecimal price){
				
					
					

					BigDecimal gstRate = saleOrderLine.getGstRate();
				
				

				System.out.println(saleOrderLine.getSaleOrder());
				System.out.println(saleOrderLine.getSaleOrder().getInAti());
				
					if(!saleOrderLine.getSaleOrder().getInAti()) {
						if (isSameState) {
							BigDecimal rate = gstRate.divide(new BigDecimal(100)).divide(new BigDecimal(2));
							
							saleOrderLine.setCgst(
									(

											price.multiply(rate))
									.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
							saleOrderLine.setSgst(
									(
											price.multiply(rate))
									.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
						} else {
							
							System.out.println(gstRate);
							System.out.println(price.multiply(gstRate.divide(new BigDecimal(100))));
							saleOrderLine.setIgst((
									price.multiply(gstRate.divide(new BigDecimal(100))))
									.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
						}
						
					}else {

						BigDecimal totalRate1 = (gstRate.divide(new BigDecimal(2))).add(new BigDecimal(100));
						BigDecimal totalRate2 = gstRate.add(new BigDecimal(100));
						BigDecimal totalRate3 = gstRate.divide(new BigDecimal(2));
						if(isSameState) {

							saleOrderLine.setCgst
									(price.multiply(totalRate3).divide(totalRate1,2,RoundingMode.HALF_UP).setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
									);
							saleOrderLine.setSgst(
									price.multiply(totalRate3).divide(totalRate1,RoundingMode.HALF_UP).setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
									);

						}else {
							BigDecimal value = price.multiply(gstRate);
							BigDecimal finalValue = value.divide(totalRate2 ,2,
									 BigDecimal.ROUND_HALF_UP);
							saleOrderLine.setIgst(
									finalValue.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)

									);
						}
					}
					System.out.println(price+" : "+saleOrderLine.getCgst()+" : "+saleOrderLine.getSgst()+" : "+saleOrderLine.getIgst());
					 return saleOrderLine;

				}

}

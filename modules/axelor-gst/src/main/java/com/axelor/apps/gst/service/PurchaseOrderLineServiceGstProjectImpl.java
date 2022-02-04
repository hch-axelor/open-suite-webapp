 package com.axelor.apps.gst.service;

 import java.math.BigDecimal;
 import java.math.RoundingMode;
 import java.util.HashMap;
 import java.util.Map;

 import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.app.AppBaseServiceImpl;
import com.axelor.apps.businessproject.service.PurchaseOrderLineServiceProjectImpl;
 import com.axelor.apps.purchase.db.PurchaseOrder;
 import com.axelor.apps.purchase.db.PurchaseOrderLine;
import com.axelor.apps.sale.service.app.AppSaleService;
// import com.axelor.apps.sale.db.repo.purchaseOrderRepository;
 import com.axelor.exception.AxelorException;
 import com.axelor.inject.Beans;

 public class PurchaseOrderLineServiceGstProjectImpl extends PurchaseOrderLineServiceProjectImpl{
	
	public static PurchaseOrderLine computeGst(boolean isSameState,PurchaseOrderLine purchaseOrderLine,
 BigDecimal price){
	
		
		
//		BigDecimal qty = purchaseOrderLine.getQty();
		BigDecimal gstRate = purchaseOrderLine.getGstRate();
	
	
	
		if(!purchaseOrderLine.getPurchaseOrder().getInAti()) {
			if (isSameState) {
				purchaseOrderLine.setCgst(
						(
								price.multiply(gstRate.divide(new BigDecimal(100).divide(new BigDecimal(2)))))
						.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
				purchaseOrderLine.setSgst(
						(
								price.multiply(gstRate.divide(new BigDecimal(100).divide(new BigDecimal(2)))))
						.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
			} else {
				
				purchaseOrderLine.setIgst((
						price.multiply(gstRate.divide(new BigDecimal(100))))
						.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
//		    	purchaseOrderLine.setIgst((	qty.multiply(price.multiply(gstRate.divide(new
// BigDecimal(100))))).setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
			}
			
		}else {
//			BigDecimal rate = (gstRate.divide(new BigDecimal(100))).setScale(2, BigDecimal.ROUND_UP);
			BigDecimal totalRate1 = (gstRate.divide(new BigDecimal(2))).add(new BigDecimal(100));
			BigDecimal totalRate2 = gstRate.add(new BigDecimal(100));
			if(isSameState) {
				purchaseOrderLine.setCgst
						(price.multiply(gstRate).divide((gstRate.divide(new BigDecimal(2)).add(new BigDecimal(100)))).setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
						);
				purchaseOrderLine.setSgst(
						price.multiply(gstRate).divide(totalRate1).setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
						);
			}else {
				BigDecimal value = price.multiply(gstRate);
				BigDecimal finalValue = value.divide(totalRate2 ,2,
						 BigDecimal.ROUND_HALF_UP);
				purchaseOrderLine.setIgst(
						finalValue.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
//						(price.multiply(gstRate)).divide(totalRate2).setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
						);
			}
		}
		 return purchaseOrderLine;

	}
	@Override
	  public Map<String, BigDecimal> compute(
			  
	      PurchaseOrderLine purchaseOrderLine, PurchaseOrder purchaseOrder) throws AxelorException {

	
	
	    if (!Beans.get(AppBaseService.class).isApp("gst")) {

	        return super.compute(purchaseOrderLine,purchaseOrder);
	      }

		    //    purchaseOrder purchaseOrder = Beans.get(purchaseOrder.class).find(s);
		    System.out.println(purchaseOrder);

		    String partnerInvoiceAddressState =
		        purchaseOrder.getSupplierPartner().getMainAddress().getState() != null
		            ? purchaseOrder.getSupplierPartner().getMainAddress().getState().getName()
		            : "";

		    System.out.println(partnerInvoiceAddressState);




		    BigDecimal gstRateP = purchaseOrderLine.getProduct().getProductCategory().getGstRate();
		    purchaseOrderLine.setGstRate(gstRateP);

		    BigDecimal price = purchaseOrderLine.getPrice();
		    BigDecimal gstRate = purchaseOrderLine.getGstRate();
		    BigDecimal qty = purchaseOrderLine.getQty();
		    String companyAddressState =
		    		purchaseOrder.getStockLocation().getAddress().getState() != null
		            ? purchaseOrder.getStockLocation().getAddress().getState().getName()
		            : "";
		    System.out.println(purchaseOrder.getCompany().getAddress());

	
	    HashMap<String, BigDecimal> map = new HashMap<>();
	    if (purchaseOrder == null
	        || purchaseOrderLine.getPrice() == null
	        || purchaseOrderLine.getInTaxPrice() == null
	        || purchaseOrderLine.getQty() == null) {
	      return map;
	    }

	    BigDecimal exTaxTotal;
	    BigDecimal companyExTaxTotal;
	    BigDecimal inTaxTotal;
	    BigDecimal companyInTaxTotal;
	    BigDecimal priceDiscounted = this.computeDiscount(purchaseOrderLine,
 purchaseOrder.getInAti());
	    BigDecimal taxRate = BigDecimal.ZERO;
	    boolean isSameState =!companyAddressState.equals("") &&
 companyAddressState.equals(partnerInvoiceAddressState);


	    if (purchaseOrderLine.getTaxLine() != null) {
	      taxRate = purchaseOrderLine.getTaxLine().getValue();
	    }

	    if (!purchaseOrder.getInAti()) {
	      exTaxTotal = computeAmount(purchaseOrderLine.getQty(), priceDiscounted);
	      purchaseOrderLine = computeGst(isSameState, purchaseOrderLine, exTaxTotal);
	      inTaxTotal =
 exTaxTotal.add(exTaxTotal.multiply(taxRate)).add(purchaseOrderLine.getCgst()).add(purchaseOrderLine.getSgst()).add(purchaseOrderLine.getIgst());
	      companyExTaxTotal = getCompanyExTaxTotal(exTaxTotal, purchaseOrder);
	      PurchaseOrderLine companyPurchaseOrderLine = computeGst(isSameState,
 purchaseOrderLine,companyExTaxTotal);
	      companyInTaxTotal =
 companyExTaxTotal.add(companyExTaxTotal.multiply(taxRate)).add(companyPurchaseOrderLine.getCgst()).add(companyPurchaseOrderLine.getSgst()).add(companyPurchaseOrderLine.getIgst());
	    } else {
	      inTaxTotal = computeAmount(purchaseOrderLine.getQty(), priceDiscounted);
	      purchaseOrderLine = computeGst(isSameState,purchaseOrderLine, inTaxTotal );
	      exTaxTotal = (inTaxTotal.divide(taxRate.add(BigDecimal.ONE), 2,
 BigDecimal.ROUND_HALF_UP)).subtract(purchaseOrderLine.getIgst().add(purchaseOrderLine.getSgst().add(purchaseOrderLine.getCgst())));
	      companyInTaxTotal = getCompanyExTaxTotal(inTaxTotal, purchaseOrder);
	      PurchaseOrderLine companyPurchaseOrderLine = computeGst(isSameState,purchaseOrderLine, companyInTaxTotal );
	      companyExTaxTotal =
	          (companyInTaxTotal.divide(taxRate.add(BigDecimal.ONE), 2,
 BigDecimal.ROUND_HALF_UP)).subtract(purchaseOrderLine.getIgst().add(purchaseOrderLine.getSgst().add(purchaseOrderLine.getCgst())));
	    }

	    if (purchaseOrderLine.getProduct() != null) {
	      map.put("maxPurchasePrice", getPurchaseMaxPrice(purchaseOrder, purchaseOrderLine));
	    }
	    
//	    purchaseOrderLine.setIgst(gstRate);
	    System.out.println(purchaseOrderLine.getExTaxTotal()+" : "+purchaseOrderLine.getInTaxTotal()+" : "+purchaseOrderLine.getCompanyExTaxTotal()+" : "+purchaseOrderLine.getCompanyInTaxTotal());
	    System.out.println(purchaseOrderLine.getIgst()+" : "+purchaseOrderLine.getSgst()+" : "+purchaseOrderLine.getCgst());
	    map.put("gstRate", gstRate);
	    map.put("igst", purchaseOrderLine.getIgst());
	    map.put("sgst", purchaseOrderLine.getSgst());
	    map.put("cgst", purchaseOrderLine.getCgst());

	    map.put("exTaxTotal", exTaxTotal);
	    map.put("inTaxTotal", inTaxTotal);
	    map.put("companyExTaxTotal", companyExTaxTotal);
	    map.put("companyInTaxTotal", companyInTaxTotal);
	    map.put("priceDiscounted", priceDiscounted);
	    purchaseOrderLine.setExTaxTotal(exTaxTotal);
	    purchaseOrderLine.setInTaxTotal(inTaxTotal);
	    purchaseOrderLine.setPriceDiscounted(priceDiscounted);
	    purchaseOrderLine.setCompanyExTaxTotal(companyExTaxTotal);
	    purchaseOrderLine.setCompanyInTaxTotal(companyInTaxTotal);
	    purchaseOrderLine.setMaxPurchasePrice(getPurchaseMaxPrice(purchaseOrder, purchaseOrderLine));
	    
	    return map;
	  }


	
 }

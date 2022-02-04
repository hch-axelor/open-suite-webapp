 package com.axelor.apps.gst.service;

 import java.math.BigDecimal;
 import java.util.List;

 import com.axelor.apps.account.service.app.AppAccountService;
 import com.axelor.apps.account.service.config.AccountConfigService;
 import com.axelor.apps.base.service.app.AppBaseService;
 import com.axelor.apps.production.service.PurchaseOrderServiceProductionImpl;
 import com.axelor.apps.purchase.db.PurchaseOrder;
 import com.axelor.apps.purchase.db.PurchaseOrderLine;
 import com.axelor.apps.purchase.db.repo.PurchaseOrderLineRepository;
 import com.axelor.apps.purchase.service.PurchaseOrderLineService;
import com.axelor.apps.sale.service.app.AppSaleService;
// import com.axelor.apps.sale.db.purchaseOrderLine;
 import com.axelor.apps.supplychain.service.BudgetSupplychainService;
 import com.axelor.apps.supplychain.service.PurchaseOrderStockService;
 import com.axelor.apps.supplychain.service.app.AppSupplychainService;
 import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;

 public class PurchaseOrderServiceGstProductionImpl extends PurchaseOrderServiceProductionImpl{

	@Inject
	public PurchaseOrderServiceGstProductionImpl(AppSupplychainService appSupplychainService,
			AccountConfigService accountConfigService, AppAccountService appAccountService,
			AppBaseService appBaseService, PurchaseOrderStockService purchaseOrderStockService,
			BudgetSupplychainService budgetSupplychainService, PurchaseOrderLineRepository
 purchaseOrderLineRepository,
			PurchaseOrderLineService purchaseOrderLineService) {
		super(appSupplychainService, accountConfigService, appAccountService, appBaseService,
 purchaseOrderStockService,
				budgetSupplychainService, purchaseOrderLineRepository, purchaseOrderLineService);
		// TODO Auto-generated constructor stub
	}
	
	  @Override
	  public void _computePurchaseOrder(PurchaseOrder purchaseOrder) throws AxelorException {
		  super._computePurchaseOrder(purchaseOrder);
		    if (!Beans.get(AppBaseService.class).isApp("gst")) {

		        return;
		      }

		    BigDecimal taxTotal = purchaseOrder.getTaxTotal();

		    List<PurchaseOrderLine> purchaseOrderLineList = purchaseOrder.getPurchaseOrderLineList();

		    System.out.println(
		        "Before: "
		            + purchaseOrder.getExTaxTotal()
		            + " : "
		            + purchaseOrder.getCompanyExTaxTotal()
		            + " : "
		            + purchaseOrder.getInTaxTotal());
		    BigDecimal igst =
		        purchaseOrderLineList.stream().map(i -> i.getIgst()).reduce(BigDecimal.ZERO,
 BigDecimal::add);
		    BigDecimal cgst =
		        purchaseOrderLineList.stream().map(i -> i.getCgst()).reduce(BigDecimal.ZERO,
 BigDecimal::add);
		    BigDecimal sgst =
		        purchaseOrderLineList.stream().map(i -> i.getSgst()).reduce(BigDecimal.ZERO,
 BigDecimal::add);
		    
		    purchaseOrder.setNetCGST(cgst);
		    purchaseOrder.setNetSGST(sgst);
		    purchaseOrder.setNetIGST(igst);
		    
		    if(!purchaseOrder.getInAti()) {
		    	
		    	purchaseOrder.setTaxTotal(purchaseOrder.getTaxTotal().add(sgst).add(cgst).add(igst));
		    	purchaseOrder.setInTaxTotal(purchaseOrder.getInTaxTotal().add(sgst).add(cgst).add(igst));
		    	System.out.println(sgst + " : " + igst + " : " + cgst);
		    	
		    	System.out.println(
		    			"After: "
		    					+ purchaseOrder.getExTaxTotal()
		    					+ " : "
		    					+ purchaseOrder.getCompanyExTaxTotal()
		    					+ " : "
		    					+ purchaseOrder.getInTaxTotal()
		    			);
		    	
		    }else {
			    BigDecimal allTaxIncluded = 
			    		purchaseOrderLineList.stream().map(i -> i.getInTaxTotal()).reduce(BigDecimal.ZERO,BigDecimal::add);

//			    purchaseOrder.setExTaxTotal(allTaxIncluded.subtract(igst).subtract(sgst).subtract(cgst));
		    	purchaseOrder.setTaxTotal(purchaseOrder.getTaxTotal().add(sgst).add(cgst).add(igst));
		    	purchaseOrder.setInTaxTotal(allTaxIncluded);
		    	purchaseOrder.setTaxTotal(purchaseOrder.getInTaxTotal().subtract(purchaseOrder.getExTaxTotal()));
		    	
		    	System.out.println(purchaseOrder.getTaxTotal());
		    	System.out.println(sgst + " : " + igst + " : " + cgst);
		    	
		    	System.out.println(
		    			"After: "
		    					+ purchaseOrder.getExTaxTotal()
		    					+ " : "
		    					+ purchaseOrder.getCompanyExTaxTotal()
		    					+ " : "
		    					+ purchaseOrder.getInTaxTotal()
		    			);

		    }
	  }
	

 }

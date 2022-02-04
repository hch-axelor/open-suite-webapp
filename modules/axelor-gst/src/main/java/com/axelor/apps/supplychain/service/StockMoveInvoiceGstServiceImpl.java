package com.axelor.apps.supplychain.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.businessproject.service.ProjectStockMoveInvoiceServiceImpl;
import com.axelor.apps.gst.service.PurchaseOrderLineServiceGstProjectImpl;
import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.apps.purchase.db.PurchaseOrderLine;
import com.axelor.apps.purchase.db.repo.PurchaseOrderRepository;
import com.axelor.apps.purchase.service.PurchaseOrderLineServiceImpl;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.stock.db.StockMove;
import com.axelor.apps.stock.db.StockMoveLine;
import com.axelor.apps.stock.db.repo.StockMoveLineRepository;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.apps.supplychain.service.config.SupplyChainConfigService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class StockMoveInvoiceGstServiceImpl extends ProjectStockMoveInvoiceServiceImpl {

  @Inject
  public StockMoveInvoiceGstServiceImpl(
      SaleOrderInvoiceService saleOrderInvoiceService,
      PurchaseOrderInvoiceService purchaseOrderInvoiceService,
      StockMoveLineServiceSupplychain stockMoveLineServiceSupplychain,
      InvoiceRepository invoiceRepository,
      SaleOrderRepository saleOrderRepo,
      PurchaseOrderRepository purchaseOrderRepo,
      StockMoveLineRepository stockMoveLineRepository,
      InvoiceLineRepository invoiceLineRepository,
      SupplyChainConfigService supplyChainConfigService,
      AppSupplychainService appSupplychainService) {
    super(
        saleOrderInvoiceService,
        purchaseOrderInvoiceService,
        stockMoveLineServiceSupplychain,
        invoiceRepository,
        saleOrderRepo,
        purchaseOrderRepo,
        stockMoveLineRepository,
        invoiceLineRepository,
        supplyChainConfigService,
        appSupplychainService);
    // TODO Auto-generated constructor stub
  }

  @Override
  @Transactional(rollbackOn = {Exception.class})
  public Invoice createInvoice(
      StockMove stockMove,
      Integer operationSelect,
      List<Map<String, Object>> stockMoveLineListContext)
      throws AxelorException {

    Invoice invoice = super.createInvoice(stockMove, operationSelect, stockMoveLineListContext);
    if (!Beans.get(AppBaseService.class).isApp("gst")) {

        return invoice;
      }

    System.out.println(invoice);
    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();

    System.err.println(
        "before: "
            + invoice.getExTaxTotal()
            + " : "
            + invoice.getCompanyExTaxTotal()
            + " : "
            + invoice.getInTaxTotal()
            + " : "
            + invoice.getCompanyInTaxTotal());

    BigDecimal igst =
        invoiceLineList.stream()
            .map(i -> i.getIgst())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, BigDecimal.ROUND_UP);
    BigDecimal cgst =
        invoiceLineList.stream()
            .map(i -> i.getCgst())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, BigDecimal.ROUND_UP);
    BigDecimal sgst =
        invoiceLineList.stream()
            .map(i -> i.getSgst())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, BigDecimal.ROUND_UP);
    //    	    cgst.setScale(2,BigDecimal.ROUND_UP);
    //    	    sgst.setScale(2,BigDecimal.ROUND_UP);
    //    	    igst.setScale(2,BigDecimal.ROUND_UP);

    //

    invoice.setNetCGST(cgst);
    invoice.setNetIGST(igst);
    invoice.setNetSGST(sgst);
//    invoice.setTaxTotal(sto);
//    invoice.setTaxTotal(invoice.getTaxTotal().add(sgst).add(cgst).add(igst));
    
//    invoice.setInAti(stockMove.getStockMoveLineList().get(0).getPurchaseOrderLine().getPurchaseOrder().getInAti());
    BigDecimal inTaxTotal = 
    		invoiceLineList.stream()
    			.map(i -> i.getInTaxTotal())
    			.reduce(BigDecimal.ZERO, BigDecimal::add)
    			.setScale(2, BigDecimal.ROUND_UP);
    invoice.setInTaxTotal(inTaxTotal);
    invoice.setTaxTotal(inTaxTotal.subtract(invoice.getExTaxTotal()));
//    invoice.setInTaxTotal(invoice.getInTaxTotal().add(sgst).add(igst).add(cgst));
    System.err.println(sgst + " : " + igst + " : " + cgst);
    System.err.println(
        "before: "
            + invoice.getExTaxTotal()
            + " : "
            + invoice.getCompanyExTaxTotal()
            + " : "
            + invoice.getInTaxTotal()
            + " : "
            + invoice.getCompanyInTaxTotal());

    return invoice;
  }

  @Override
  public InvoiceLine createInvoiceLine(Invoice invoice, StockMoveLine stockMoveLine, BigDecimal qty)
      throws AxelorException {

    InvoiceLine invoiceLine = super.createInvoiceLine(invoice, stockMoveLine, qty);
    if (!Beans.get(AppBaseService.class).isApp("gst")) {

        return invoiceLine;
      }

    if (stockMoveLine.getSaleOrderLine() != null) {
    SaleOrderLine saleOrderLine = stockMoveLine.getSaleOrderLine();
      BigDecimal gstRate = stockMoveLine.getSaleOrderLine().getGstRate();
      BigDecimal price = stockMoveLine.getSaleOrderLine().getPrice();
      System.out.println(gstRate + " : " + price + " : " + qty);

      System.out.println(invoice.getCompany());
      invoiceLine.setGstRate(gstRate);

      String partnerInvoiceAddressState =
          invoice.getAddress().getState() != null ? invoice.getAddress().getState().getName() : "";

      System.out.println(partnerInvoiceAddressState);

      System.out.println(
          invoiceLine.getInTaxTotal()
              + " : "
              + invoiceLine.getCgst()
              + " : "
              + invoiceLine.getSgst()
              + " : "
              + invoiceLine.getIgst());
      String companyAddressState =
          invoice.getCompany().getAddress().getState() != null
              ? invoice.getCompany().getAddress().getState().getName()
              : "";
      System.out.println(companyAddressState);
      invoiceLine.getInvoice().setInAti(stockMoveLine.getSaleOrderLine().getSaleOrder().getInAti());
//      if (!companyAddressState.equals("")
//          && companyAddressState.equals(partnerInvoiceAddressState)) {
//
//        invoiceLine.setCgst(
//            qty.multiply(
//                price.multiply(gstRate.divide(new BigDecimal(100))).divide(new BigDecimal(2))));
//        invoiceLine.setSgst(
//            qty.multiply(
//                price.multiply(gstRate.divide(new BigDecimal(100))).divide(new BigDecimal(2))));
//        invoiceLine.setInTaxTotal(
//            invoiceLine.getInTaxTotal().add(invoiceLine.getCgst()).add(invoiceLine.getSgst()));
//      } else {
//        invoiceLine.setIgst(qty.multiply(price.multiply(gstRate.divide(new BigDecimal(100)))));
//        invoiceLine.setInTaxTotal(invoiceLine.getInTaxTotal().add(invoiceLine.getIgst()));
//      }


System.out.println(saleOrderLine.getExTaxTotal()+" : "+saleOrderLine.getTaxLine());
	    invoiceLine.setIgst(saleOrderLine.getIgst());
	    invoiceLine.setCgst(saleOrderLine.getCgst());
	    invoiceLine.setSgst(saleOrderLine.getSgst());
	    invoiceLine.setGstRate(saleOrderLine.getProduct().getProductCategory().getGstRate());
	    invoiceLine.setExTaxTotal(saleOrderLine.getExTaxTotal());
	    invoiceLine.setInTaxTotal(saleOrderLine.getInTaxTotal());
	    invoiceLine.setCompanyExTaxTotal(saleOrderLine.getCompanyExTaxTotal());
	    invoiceLine.setCompanyInTaxTotal(saleOrderLine.getCompanyInTaxTotal());
	 
      
      invoiceLine.setHsbn(invoiceLine.getProduct().getHsbn());
      System.out.println(
    		  invoiceLine.getHsbn()+" : "+
          invoiceLine.getInTaxTotal()
              + " : "
              + invoiceLine.getCgst()
              + " : "
              + invoiceLine.getSgst()
              + " : "
              + invoiceLine.getIgst());

      System.out.println(invoiceLine.getSaleOrderLine().getSaleOrder().getCompany());

    } else if (stockMoveLine.getPurchaseOrderLine() != null) {
    	PurchaseOrderLine purchaseOrderLine = stockMoveLine.getPurchaseOrderLine();
    	PurchaseOrder purchaseOrder = purchaseOrderLine.getPurchaseOrder();
    	
    	 invoiceLine.getInvoice().setInAti(stockMoveLine.getPurchaseOrderLine().getPurchaseOrder().getInAti());
    String partnerInvoiceAddressState =
            invoice.getAddress().getState() != null ? invoice.getAddress().getState().getName() : "";

        System.out.println(partnerInvoiceAddressState);
        
        System.out.println(
            invoiceLine.getInTaxTotal()
                + " : "
                + invoiceLine.getCgst()
                + " : "
                + invoiceLine.getSgst()
                + " : "
                + invoiceLine.getIgst());
        String companyAddressState =
            invoice.getCompany().getAddress().getState() != null
                ? invoice.getCompany().getAddress().getState().getName()
                : "";
        System.out.println(companyAddressState);
        BigDecimal exTaxTotal;
	    BigDecimal companyExTaxTotal;
	    BigDecimal inTaxTotal;
	    BigDecimal companyInTaxTotal;

System.out.println(purchaseOrderLine.getExTaxTotal()+" : "+purchaseOrderLine.getTaxLine());
	    invoiceLine.setIgst(purchaseOrderLine.getIgst());
	    invoiceLine.setCgst(purchaseOrderLine.getCgst());
	    invoiceLine.setSgst(purchaseOrderLine.getSgst());
	    invoiceLine.setGstRate(purchaseOrderLine.getProduct().getProductCategory().getGstRate());
	    invoiceLine.setExTaxTotal(purchaseOrderLine.getExTaxTotal());
	    invoiceLine.setInTaxTotal(purchaseOrderLine.getInTaxTotal());
	    invoiceLine.setCompanyExTaxTotal(purchaseOrderLine.getCompanyExTaxTotal());
	    invoiceLine.setCompanyInTaxTotal(purchaseOrderLine.getCompanyInTaxTotal());
	    invoiceLine.setHsbn(invoiceLine.getProduct().getHsbn());
//	    invoiceLine.setPriceDiscounted(priceDiscounted);
	      System.out.println(
	              invoiceLine.getInTaxTotal()
	                  + " : "
	                  + invoiceLine.getCgst()
	                  + " : "
	                  + invoiceLine.getSgst()
	                  + " : "
	                  + invoiceLine.getIgst());

	          System.out.println(purchaseOrderLine.getInTaxTotal());
    
    }
    return invoiceLine;
  }
}

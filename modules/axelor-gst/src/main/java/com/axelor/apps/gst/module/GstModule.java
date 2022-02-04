package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
//import com.axelor.apps.account.service.*;
import com.axelor.apps.account.web.InvoiceLineController;
import com.axelor.apps.businessproduction.service.SaleOrderLineBusinessProductionServiceImpl;
import com.axelor.apps.businessproject.service.ProjectStockMoveInvoiceServiceImpl;
import com.axelor.apps.businessproject.service.PurchaseOrderLineServiceProjectImpl;
//import com.axelor.apps.businessproject.service.PurchaseOrderLineServiceProjectImpl;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.apps.gst.service.PurchaseOrderLineServiceGstProjectImpl;
//import com.axelor.apps.gst.service.PurchaseOrderLineServiceGstProjectImpl;
import com.axelor.apps.gst.service.PurchaseOrderServiceGstProductionImpl;
import com.axelor.apps.gst.service.PurchaseOrderStockGstServiceImpl;
import com.axelor.apps.gst.service.SaleOrderInvoiceProjectGstServiceImpl;
import com.axelor.apps.gst.service.SaleOrderLineBusinessProductionGstServiceImpl;
import com.axelor.apps.gst.service.StockMoveToolGstServiceImpl;
import com.axelor.apps.gst.service.saleorder.print.SaleOrderPrintGstServiceImpl;
import com.axelor.apps.gst.web.InvoiceLineGstController;
import com.axelor.apps.gst.web.InvoiceServiceGstImpl;
import com.axelor.apps.gst.web.SaleOrderComputeServiceGstImpl;
//import com.axelor.apps.gst.web.SaleOrderLineGstController;
import com.axelor.apps.production.service.PurchaseOrderServiceProductionImpl;
import com.axelor.apps.sale.service.saleorder.print.SaleOrderPrintServiceImpl;
import com.axelor.apps.sale.web.SaleOrderLineController;
import com.axelor.apps.stock.service.StockMoveToolServiceImpl;
import com.axelor.apps.supplychain.service.PurchaseOrderStockServiceImpl;
import com.axelor.apps.supplychain.service.SaleOrderComputeServiceSupplychainImpl;
import com.axelor.apps.supplychain.service.StockMoveInvoiceGstServiceImpl;

public class GstModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(InvoiceLineController.class).to(InvoiceLineGstController.class);
    bind(InvoiceServiceManagementImpl.class).to(InvoiceServiceGstImpl.class);
    //    //
    //sale order line calculation
    bind(SaleOrderLineBusinessProductionServiceImpl.class).to(SaleOrderLineBusinessProductionGstServiceImpl.class);
    
    bind(ProjectStockMoveInvoiceServiceImpl.class).to(StockMoveInvoiceGstServiceImpl.class);
    bind(SaleOrderComputeServiceSupplychainImpl.class).to(SaleOrderComputeServiceGstImpl.class);
    bind(SaleOrderInvoiceProjectServiceImpl.class).to(SaleOrderInvoiceProjectGstServiceImpl.class);
    
    
    //
    bind(PurchaseOrderLineServiceProjectImpl.class)
    .to(PurchaseOrderLineServiceGstProjectImpl.class);
    
    bind(PurchaseOrderServiceProductionImpl.class).to(PurchaseOrderServiceGstProductionImpl.class);
    bind(PurchaseOrderStockServiceImpl.class).to(PurchaseOrderStockGstServiceImpl.class);
    
    
    //sale-order print
    bind(SaleOrderPrintServiceImpl.class).to(SaleOrderPrintGstServiceImpl.class);
    
    
    //
    // bind(SaleOrderLineBusinessProductionServiceImpl.class).to(SaleOrderLineServiceGStImpl.class);
    //    bind(InvoiceGeneratorSupplyChain.class).to(InvoiceGeneratorGstSupplyChain.class);
//    bind(SaleOrderLineController.class).to(SaleOrderLineGstController.class);
    //    bind(SaleOrderController.class).to(SaleOrderGstController.class);

    //
    //
    //    bind(StockMoveInvoiceServiceImpl.class).to(StockMoveInvoiceGstServiceImpl.class);
    
    //stock compute
//    bind(StockMoveToolServiceImpl.class).to(StockMoveToolGstServiceImpl.class);
    
    
    //    //
    //    //
  }
}

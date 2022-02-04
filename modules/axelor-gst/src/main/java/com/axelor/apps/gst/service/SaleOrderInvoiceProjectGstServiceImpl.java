package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.invoice.InvoiceService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.businessproject.service.SaleOrderInvoiceProjectServiceImpl;
import com.axelor.apps.businessproject.service.app.AppBusinessProjectService;
import com.axelor.apps.sale.db.SaleOrder;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.sale.db.repo.SaleOrderRepository;
import com.axelor.apps.sale.service.saleorder.SaleOrderLineService;
import com.axelor.apps.sale.service.saleorder.SaleOrderWorkflowServiceImpl;
import com.axelor.apps.stock.db.repo.StockMoveRepository;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SaleOrderInvoiceProjectGstServiceImpl extends SaleOrderInvoiceProjectServiceImpl {
  @Inject
  public SaleOrderInvoiceProjectGstServiceImpl(
      AppBaseService appBaseService,
      AppSupplychainService appSupplychainService,
      SaleOrderRepository saleOrderRepo,
      InvoiceRepository invoiceRepo,
      InvoiceService invoiceService,
      AppBusinessProjectService appBusinessProjectService,
      StockMoveRepository stockMoveRepository,
      SaleOrderLineService saleOrderLineService,
      SaleOrderWorkflowServiceImpl saleOrderWorkflowServiceImpl) {
    super(
        appBaseService,
        appSupplychainService,
        saleOrderRepo,
        invoiceRepo,
        invoiceService,
        appBusinessProjectService,
        stockMoveRepository,
        saleOrderLineService,
        saleOrderWorkflowServiceImpl);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Invoice createInvoice(
      SaleOrder saleOrder,
      List<SaleOrderLine> saleOrderLineList,
      Map<Long, BigDecimal> qtyToInvoiceMap)
      throws AxelorException {
	  
    Invoice invoice = super.createInvoice(saleOrder, saleOrderLineList);
    if (!Beans.get(AppBaseService.class).isApp("gst")) {

        return invoice;
      }

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
    //    String companyStateName= invoice.getCompany().getAddress().getState() != null ?
    // invoice.getCompany().getAddress().getState().getName() : "";
    //    String partnerA
    //    if(!companyStateName.equals("") && companyStateName.equals()) {
    //
    //    }

    BigDecimal igst =
        invoiceLineList.stream().map(i -> i.getIgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal cgst =
        invoiceLineList.stream().map(i -> i.getCgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal sgst =
        invoiceLineList.stream().map(i -> i.getSgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
    invoice.setNetCGST(cgst);
    invoice.setNetIGST(igst);
    invoice.setNetSGST(sgst);
    invoice.setTaxTotal(invoice.getTaxTotal().add(sgst).add(cgst).add(igst));
    invoice.setInTaxTotal(invoice.getInTaxTotal().add(sgst).add(igst).add(cgst));
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
}

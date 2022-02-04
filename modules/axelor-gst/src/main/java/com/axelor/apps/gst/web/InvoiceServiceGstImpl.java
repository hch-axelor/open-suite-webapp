package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceLineRepository;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.config.AccountConfigService;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.account.service.move.MoveToolService;
import com.axelor.apps.base.service.PartnerService;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.cash.management.service.InvoiceEstimatedPaymentService;
import com.axelor.apps.cash.management.service.InvoiceServiceManagementImpl;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvoiceServiceGstImpl extends InvoiceServiceManagementImpl {

  @Inject
  public InvoiceServiceGstImpl(
      ValidateFactory validateFactory,
      VentilateFactory ventilateFactory,
      CancelFactory cancelFactory,
      AlarmEngineService<Invoice> alarmEngineService,
      InvoiceRepository invoiceRepo,
      AppAccountService appAccountService,
      PartnerService partnerService,
      InvoiceLineService invoiceLineService,
      AccountConfigService accountConfigService,
      MoveToolService moveToolService,
      InvoiceLineRepository invoiceLineRepo,
      InvoiceEstimatedPaymentService invoiceEstimatedPaymentService) {
    super(
        validateFactory,
        ventilateFactory,
        cancelFactory,
        alarmEngineService,
        invoiceRepo,
        appAccountService,
        partnerService,
        invoiceLineService,
        accountConfigService,
        moveToolService,
        invoiceLineRepo,
        invoiceEstimatedPaymentService);
    // TODO Auto-generated constructor stub
  }

  private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public Invoice compute(final Invoice invoice) throws AxelorException {

    Invoice computedInvoice = super.compute(invoice);
    if (!Beans.get(AppBaseService.class).isApp("gst")) {

        return computedInvoice;
      }

    List<InvoiceLine> invoiceLineList = computedInvoice.getInvoiceLineList();
    /*
     * BigDecimal igst = invoiceLineList.stream().map(i->
     * i.getIgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
     */
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
        invoiceLineList.stream().map(i -> i.getIgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal cgst =
        invoiceLineList.stream().map(i -> i.getCgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal sgst =
        invoiceLineList.stream().map(i -> i.getSgst()).reduce(BigDecimal.ZERO, BigDecimal::add);
    computedInvoice.setNetCGST(cgst);
    computedInvoice.setNetIGST(igst);
    computedInvoice.setNetSGST(sgst);
    BigDecimal inTaxTotal = invoiceLineList.stream()
    		.map(i-> i.getInTaxTotal()).reduce(BigDecimal.ZERO, BigDecimal::add);
    System.out.println("taxtotal" + invoice.getTaxTotal());
//    BigDecimal exTaxTotal = 
    invoice.setInTaxTotal(inTaxTotal);
    
//    invoice.setTaxTotal()
    invoice.setTaxTotal(invoice.getInTaxTotal().subtract(invoice.getExTaxTotal()));
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

    return computedInvoice;
  }
}

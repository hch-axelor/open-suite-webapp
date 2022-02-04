package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.invoice.InvoiceLineService;
import com.axelor.apps.account.service.invoice.generator.line.InvoiceLineManagement;
import com.axelor.apps.account.web.InvoiceLineController;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.gst.db.State;
import com.axelor.apps.purchase.db.PurchaseOrderLine;
import com.axelor.exception.AxelorException;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class InvoiceLineGstController extends InvoiceLineController {
  @Override
  public void compute(ActionRequest request, ActionResponse response) throws AxelorException {

    super.compute(request, response);
    if (!Beans.get(AppBaseService.class).isApp("gst")) {

        return ;
      }

    Context context = request.getContext();
    InvoiceLine invoiceLine = context.asType(InvoiceLine.class);
    State partnerInvoiceAddress = null;

    Invoice invoice = null;
    if (context.get("_parent") != null) {
      Map<String, Object> _parent = ((Map<String, Object>) context.get("_parent"));
      invoice =
          Beans.get(InvoiceRepository.class).find(Long.parseLong(_parent.get("id").toString()));
    }

    //    Invoice Invoice = Beans.get(Invoice.class).find(s);
    System.out.println(invoice);
    String partnerInvoiceAddressState =
        invoice.getAddress().getState() != null ? invoice.getAddress().getState().getName() : "";

    System.out.println(partnerInvoiceAddressState);

    //    partnerInvoiceAddress  =
    // InvoiceLine.getInvoice().getMainInvoicingAddress().getState();
    //    for (PartnerAddress partnerAddress :
    // InvoiceLine.getInvoice().getClientPartner().getPartnerAddressList()) {
    //      if (partnerAddress.getIsInvoicingAddr()) {
    //
    //        partnerInvoiceAddress = partnerAddress.getAddress().getState();
    //        System.out.println(partnerInvoiceAddress);
    //      }
    //    }
    BigDecimal gstRateP = invoiceLine.getProduct().getProductCategory().getGstRate();
    invoiceLine.setGstRate(gstRateP);

//    BigDecimal price = invoiceLine.getPrice();
    BigDecimal gstRate = invoiceLine.getGstRate();
    BigDecimal qty = invoiceLine.getQty();

    String companyAddressState =
        invoice.getCompany().getAddress().getState() != null
            ? invoice.getCompany().getAddress().getState().getName()
            : "";
    System.out.println(companyAddressState);
//    if (!companyAddressState.equals("") && companyAddressState.equals(partnerInvoiceAddressState)) {
//
//      response.setValue(
//          "cgst",
//          qty.multiply(
//              price.multiply(gstRate.divide(new BigDecimal(100))).divide(new BigDecimal(2))));
//      response.setValue(
//          "sgst",
//          qty.multiply(
//              price.multiply(gstRate.divide(new BigDecimal(100))).divide(new BigDecimal(2))));
//
//    } else {
//      response.setValue("igst", qty.multiply(price.multiply(gstRate.divide(new BigDecimal(100)))));
//    }
    BigDecimal exTaxTotal;
    BigDecimal companyExTaxTotal;
    BigDecimal inTaxTotal;
    BigDecimal companyInTaxTotal;
    
    InvoiceLineService invoiceLineService = Beans.get(InvoiceLineService.class);
//    BigDecimal exTaxTotal;
//    BigDecimal companyExTaxTotal;
//    BigDecimal inTaxTotal;
//    BigDecimal companyInTaxTotal;
    BigDecimal priceDiscounted =
        invoiceLineService.computeDiscount(invoiceLine, invoice.getInAti());

    response.setValue("priceDiscounted", priceDiscounted);
    BigDecimal taxRate = BigDecimal.ZERO;
    if (invoiceLine.getTaxLine() != null) {
      taxRate = invoiceLine.getTaxLine().getValue();
      response.setValue("taxRate", taxRate);
      response.setValue("taxCode", invoiceLine.getTaxLine().getTax().getCode());
    }
    System.out.println(priceDiscounted+" : "+invoiceLine.getQty());
    System.out.println(invoice.getInAti());
    if (!invoice.getInAti()) {
      exTaxTotal = InvoiceLineManagement.computeAmount(invoiceLine.getQty(), priceDiscounted);
      
      
  		if(!companyAddressState.equals("") && companyAddressState.equals(partnerInvoiceAddressState)) {
  			BigDecimal rate = gstRate.divide(new BigDecimal(100)).divide(new BigDecimal(2));
			
			invoiceLine.setCgst(
					(
//							price.multiply(gstRate.divide(new BigDecimal(100).divide(new BigDecimal(2)))))
							exTaxTotal.multiply(rate))
					.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
			invoiceLine.setSgst(
					(
							exTaxTotal.multiply(rate))
					.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
  			
  		}else {
  			invoiceLine.setIgst((
  					exTaxTotal.multiply(gstRate.divide(new BigDecimal(100))))
					.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP));
//	    	
  		}

      
      
      inTaxTotal = exTaxTotal.add(exTaxTotal.multiply(taxRate)).add(invoiceLine.getCgst()).add(invoiceLine.getSgst()).add(invoiceLine.getIgst());
    } else {
      inTaxTotal = InvoiceLineManagement.computeAmount(invoiceLine.getQty(), priceDiscounted);
      BigDecimal totalRate1 = (gstRate.divide(new BigDecimal(2))).add(new BigDecimal(100));
      BigDecimal totalRate2 = gstRate.add(new BigDecimal(100));
      BigDecimal totalRate3 = gstRate.divide(new BigDecimal(2));
      System.out.println(inTaxTotal);
      if(!companyAddressState.equals("") && companyAddressState.equals(partnerInvoiceAddressState)) {

			invoiceLine.setCgst
					(inTaxTotal.multiply(totalRate3).divide(totalRate1,2,RoundingMode.HALF_UP).setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
					);
			invoiceLine.setSgst(
					inTaxTotal.multiply(totalRate3).divide(totalRate1,RoundingMode.HALF_UP).setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
					);
		}else {
			BigDecimal value = inTaxTotal.multiply(gstRate);
			BigDecimal finalValue = value.divide(totalRate2 ,2,
					 BigDecimal.ROUND_HALF_UP);
			invoiceLine.setIgst(
					finalValue.setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
//					(price.multiply(gstRate)).divide(totalRate2).setScale(AppBaseService.DEFAULT_NB_DECIMAL_DIGITS, RoundingMode.HALF_UP)
					);
		}
      
      exTaxTotal = inTaxTotal.divide(taxRate.add(BigDecimal.ONE), 2, BigDecimal.ROUND_HALF_UP).subtract((invoiceLine.getCgst().add(invoiceLine.getSgst()).add(invoiceLine.getIgst())));
    }
    invoiceLine.setExTaxTotal(exTaxTotal);
    invoiceLine.setInTaxTotal(inTaxTotal);
    System.out.println("taxrate "+taxRate);
    
    System.out.println(invoice.getInAti()+ " : " +invoiceLine.getInTaxTotal()+" : "+invoiceLine.getExTaxTotal()+" : "+ invoiceLine.getIgst()+" : "+invoiceLine.getSgst()+" : "+invoiceLine.getCgst());
    companyExTaxTotal = invoiceLineService.getCompanyExTaxTotal(exTaxTotal, invoice);
    companyInTaxTotal = invoiceLineService.getCompanyExTaxTotal(inTaxTotal, invoice);
    invoiceLine.setCompanyExTaxTotal(companyExTaxTotal);
    invoiceLine.setCompanyInTaxTotal(companyInTaxTotal);
    response.setValues(invoiceLine); 
  }
}

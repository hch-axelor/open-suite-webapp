package com.axelor.apps.gst.web;

import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceController {

  public void fillInvoiceLineData(ActionRequest request, ActionResponse response) {

    //    //	  System.out.println(gstFlag);
    //    boolean gstFlag;
    //    //	  gstFlag=true;
    //
    //    Context context = request.getContext();
    //    Invoice invoice = context.asType(Invoice.class);
    //    String companyState =
    //        invoice.getCompany().getAddress().getState() != null
    //            ? invoice.getCompany().getAddress().getState().getName()
    //            : "";
    //    String partnerInvoiceState = "";
    //    boolean isSameState = false;
    //    //    for (PartnerAddress partnerAddress : invoice.getPartner().getPartnerAddressList()) {
    //    //    	if (partnerAddress.getIsInvoicingAddr() || partnerAddress.getIsDefaultAddr()) {
    //    String partnerState =
    //        invoice.getAddress().getState() != null ? invoice.getAddress().getState().getName() :
    // "";
    //    if (!companyState.equals("") && companyState.equals(partnerState)) {
    //      partnerInvoiceState = partnerState;
    //      isSameState = true;
    //    }
    //    //    	}
    //    //    }
    //    if (isSameState) {
    //      gstFlag = invoice.getNetCGST().compareTo(new BigDecimal("0")) == 0 ? true : false;
    //    } else {
    //      gstFlag = invoice.getNetIGST().compareTo(new BigDecimal("0")) == 0 ? true : false;
    //    }
    //    System.out.println(gstFlag);
    //
    //    if (gstFlag) {
    //
    //      System.out.println(
    //          invoice.getInvoiceLineList().get(0).getProduct().getProductCategory().getGstRate());
    //      System.out.println(invoice.getInvoiceLineList().get(0));
    //      int count = 0;
    //      BigDecimal totalGst = new BigDecimal("0");
    //      BigDecimal igst = new BigDecimal("0");
    //      BigDecimal sgst = new BigDecimal("0");
    //      BigDecimal cgst = new BigDecimal("0");
    //      for (InvoiceLine invoiceLine : invoice.getInvoiceLineList()) {
    //        BigDecimal gstRate = invoiceLine.getProduct().getProductCategory().getGstRate();
    //        BigDecimal qty = invoiceLine.getQty();
    //        BigDecimal price = invoiceLine.getPrice();
    //        System.out.println(qty + " : " + price + " : " + gstRate);
    //
    //        if (isSameState) {
    //          invoiceLine.setCgst(
    //              qty.multiply(
    //                  price.multiply(gstRate.divide(new BigDecimal(100))).divide(new
    // BigDecimal(2))));
    //          invoiceLine.setSgst(
    //              qty.multiply(
    //                  price.multiply(gstRate.divide(new BigDecimal(100))).divide(new
    // BigDecimal(2))));
    //          cgst.add(invoiceLine.getCgst());
    //          sgst.add(invoiceLine.getSgst());
    //
    //        } else {
    //          invoiceLine.setIgst(qty.multiply(price.multiply(gstRate.divide(new
    // BigDecimal(100)))));
    //          //    		invoiceLine.setIgst( new BigDecimal(30));
    //          igst.add(invoiceLine.getIgst());
    //        }
    //        //    	System.out.println(cgst+" : "+sgst+" : "+igst);
    //        invoice.setNetCGST(invoice.getNetCGST().add(invoiceLine.getCgst()));
    //        invoice.setNetIGST(invoice.getNetIGST().add(invoiceLine.getIgst()));
    //        invoice.setNetSGST(invoice.getNetSGST().add(invoiceLine.getSgst()));
    //        invoice.getInvoiceLineList().set(count, invoiceLine);
    //        count++;
    //        System.out.println(invoiceLine.getIgst());
    //
    //        //
    //        //
    //	invoice.setTaxTotal(invoice.getTaxTotal().add(invoiceLine.getCgst()).add(invoiceLine.getSgst()).add(invoiceLine.getIgst()));
    //        //    	System.out.println(invoice.getInTaxTotal());
    //        //    	invoice.setInTaxTotal(invoice.getInTaxTotal().add(sgst).add(igst).add(cgst));
    //      }
    //      invoice.setTaxTotal(
    //          invoice
    //              .getTaxTotal()
    //              .add(invoice.getNetCGST())
    //              .add(invoice.getNetIGST())
    //              .add(invoice.getNetSGST()));
    //      invoice.setInTaxTotal(
    //          invoice
    //              .getInTaxTotal()
    //              .add(invoice.getNetCGST())
    //              .add(invoice.getNetIGST())
    //              .add(invoice.getNetSGST()));
    //      System.out.println(invoice.getInTaxTotal());
    //      System.out.println(invoice.getInvoiceLineList().get(0).getIgst());
    //      for (InvoiceLine invoiceLine : invoice.getInvoiceLineList()) {
    //
    //        System.out.println("///////////////////////");
    //        System.out.println(invoiceLine.getSgst() + " : " + invoiceLine.getIgst());
    //      }
    //      System.out.println(invoice.getInvoiceLineList());
    //      response.setValue("invoiceLineList", invoice.getInvoiceLineList());
    //      response.setValues(invoice);
    //    }
    //    gstFlag = false;
  }
}

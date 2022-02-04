package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.stock.db.StockMove;
import com.axelor.apps.stock.db.StockMoveLine;
import com.axelor.apps.stock.db.repo.StockMoveLineRepository;
import com.axelor.apps.stock.db.repo.StockMoveRepository;
import com.axelor.apps.stock.service.PartnerProductQualityRatingService;
import com.axelor.apps.stock.service.StockMoveLineService;
import com.axelor.apps.stock.service.StockMoveToolServiceImpl;
import com.axelor.inject.Beans;
import com.google.inject.Inject;

public class StockMoveToolGstServiceImpl extends StockMoveToolServiceImpl{
	@Inject
	public StockMoveToolGstServiceImpl(StockMoveLineService stockMoveLineService, SequenceService sequenceService,
			StockMoveLineRepository stockMoveLineRepository, AppBaseService appBaseService,
			StockMoveRepository stockMoveRepository,
			PartnerProductQualityRatingService partnerProductQualityRatingService) {
		super(stockMoveLineService, sequenceService, stockMoveLineRepository, appBaseService, stockMoveRepository,
				partnerProductQualityRatingService);
		// TODO Auto-generated constructor stub
	}
	
	  @Override
	  public BigDecimal compute(StockMove stockMove) {	
		    if (!Beans.get(AppBaseService.class).isApp("gst")) {

		        return super.compute(stockMove);
		      }

	    BigDecimal exTaxTotal = BigDecimal.ZERO;
	    if (stockMove.getStockMoveLineList() != null && !stockMove.getStockMoveLineList().isEmpty()) {
	      for (StockMoveLine stockMoveLine : stockMove.getStockMoveLineList()) {
	        exTaxTotal =
	            exTaxTotal.add(
	                stockMoveLine.getRealQty().multiply(stockMoveLine.getUnitPriceUntaxed()));
	        BigDecimal value = stockMoveLine.getRealQty().multiply(stockMoveLine.getUnitPriceTaxed());
	        System.out.println(stockMoveLine.getProduct().getProductCategory().getGstRate());
	        stockMoveLine.setGstRate(stockMoveLine.getProduct().getProductCategory().getGstRate());
	        BigDecimal finalValue = value.divide((stockMoveLine.getGstRate().add(new BigDecimal(100))) ,2,
					 BigDecimal.ROUND_HALF_UP);
	        System.out.println(stockMoveLine.getGstRate());
	        System.out.println(value.multiply(stockMoveLine.getGstRate().divide(new BigDecimal(100),2,BigDecimal.ROUND_HALF_UP)));
	        exTaxTotal = exTaxTotal.subtract(value.multiply(stockMoveLine.getGstRate().divide(new BigDecimal(100))));
	        
	        
	      }
	    }
	    System.out.println(exTaxTotal);
	    return exTaxTotal.setScale(2, RoundingMode.HALF_UP);
	  }
	
}

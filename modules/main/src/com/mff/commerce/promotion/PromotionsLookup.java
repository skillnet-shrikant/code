package com.mff.commerce.promotion;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

public class PromotionsLookup extends DynamoServlet{

	private static final String OUTPUT_OPARAM = "output";
	private static final String EMPTY_OPARAM = "empty";
	private static final String INPUT_PARAM_PROMO_ID = "promoId";
	private static final String PMDL_PROPERTY="pmdlRule";
	private static final String OUTPUT_PARAM_PMDL="pmdl";
	
	Repository mPromotions;
	String mBasePromotionItemType;
	String mBaseClosenessQualifierItemType = "closenessQualifier";
	String mPromoStatusDescriptorName;
	String mPromoStatusPromoProperty;
	
	public Repository getPromotions() {
		return mPromotions;
	}
	public void setPromotions(Repository pPromotions) {
		mPromotions = pPromotions;
	}
	public String getBasePromotionItemType() {
		return mBasePromotionItemType;
	}
	public void setBasePromotionItemType(String pBasePromotionItemType) {
		mBasePromotionItemType = pBasePromotionItemType;
	}
	public String getBaseClosenessQualifierItemType() {
		return mBaseClosenessQualifierItemType;
	}
	public void setBaseClosenessQualifierItemType(String pBaseClosenessQualifierItemType) {
		mBaseClosenessQualifierItemType = pBaseClosenessQualifierItemType;
	}
	public String getPromoStatusDescriptorName() {
		return mPromoStatusDescriptorName;
	}
	public void setPromoStatusDescriptorName(String pPromoStatusDescriptorName) {
		mPromoStatusDescriptorName = pPromoStatusDescriptorName;
	}
	public String getPromoStatusPromoProperty() {
		return mPromoStatusPromoProperty;
	}
	public void setPromoStatusPromoProperty(String pPromoStatusPromoProperty) {
		mPromoStatusPromoProperty = pPromoStatusPromoProperty;
	}
	
	@SuppressWarnings("rawtypes")
	  public void service(DynamoHttpServletRequest pRequest,
				DynamoHttpServletResponse pResponse) throws ServletException,
				IOException {
			vlogInfo("com.mff.commerce.promotion.PromotionsLookup: Start");
			try{
				String lPromoId = pRequest.getParameter(INPUT_PARAM_PROMO_ID);
				RepositoryItem promotion=getPromotionFromId(lPromoId);
				if(promotion==null){
					vlogInfo("com.mff.commerce.promotion.PromotionsLookup: No promo item found");
					pRequest.serviceLocalParameter(EMPTY_OPARAM, pRequest, pResponse);
				}
				else {
					vlogInfo("com.mff.commerce.promotion.PromotionsLookup: Promo item found");
					String xmlPmdl=getPmdl(promotion);
					if(xmlPmdl!=null){
						vlogInfo("com.mff.commerce.promotion.PromotionsLookup: Promo item Pmdl found");
						pRequest.setParameter(OUTPUT_PARAM_PMDL, xmlPmdl);
						pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);
					}
					else {
						vlogInfo("com.mff.commerce.promotion.PromotionsLookup: No Promo item Pmdl found");
						pRequest.serviceLocalParameter(EMPTY_OPARAM, pRequest, pResponse);
					}
				}
				
			}
			catch(Exception ex){
				vlogInfo("com.mff.commerce.promotion.PromotionsLookup: Exception occurred");
				ex.getStackTrace();
				pRequest.serviceLocalParameter(EMPTY_OPARAM, pRequest, pResponse);
			}
			vlogInfo("com.mff.commerce.promotion.PromotionsLookup: End");
		}
	
	
	public RepositoryItem getPromotionFromId(String pPromotionId) throws RepositoryException{
		
		RepositoryItem retValue=null;
		if(pPromotionId!=null){
			if(getBasePromotionItemType()!=null){
				RepositoryItemDescriptor baseType = this.getPromotions().getItemDescriptor(this.getBasePromotionItemType());
				retValue = this.getPromotions().getItem(pPromotionId,baseType.getItemDescriptorName());
			}
		}
		return retValue;
	}
	
	public String getPmdl(RepositoryItem pPromotionItem){
		String retValue=null;
		if(pPromotionItem!=null){
			String xmlPmdl= (String)pPromotionItem.getPropertyValue(PMDL_PROPERTY);
			if(xmlPmdl!=null){
				retValue=xmlPmdl;
			}
		}
		return retValue;
	}

}

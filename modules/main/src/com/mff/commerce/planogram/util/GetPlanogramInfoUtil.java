package com.mff.commerce.planogram.util;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import mff.MFFException;

public class GetPlanogramInfoUtil extends GenericService {
	
	
	private static final String ITEM_DESCRIPTOR_DEPARTMENT = "department";
	private static final String ITEM_DESCRIPTOR_PLANOGRAM_INFO = "planoGram";
	private static final String ITEM_ID="itemId";
	private static final String DEPARTMENT_NAME="departmentName";
	private static final String PLANOGRAM_ID="planogramId";
	private static final String STORE_ID="storeId";
	private static final String PLANOGRAM_NAME="planogramName";
	private static final String PLANOGRAM_ADDRESS="planogramAddress";
	private static final String LOCATION_SERIAL="serialNumber";
	
	
	private Repository mPlanogramRepository;
	private String mPlanogramLocationAddressInfoQuery= "((itemId =?0) and (storeId= ?1)) and ((planogramAddress is not null) and (planogramAddress!=''))";
	private String mPlanogramLocationNameInfoQuery="((itemId =?0) and (storeId= ?1)) and ((planogramName is not null) and (planogramName!=''))";
	private String mItemDepartmentNameQuery="(itemId =?0)";
	private boolean mUseItemLocationAddress;
	private boolean mUseItemLocationName;
	private boolean mUseItemDepartment;
	private boolean mShowItemLocationInfo;
	private String recordSeparator=";";
	
	public String getRecordSeparator() {
		return recordSeparator;
	}

	public void setRecordSeparator(String pRecordSeparator) {
		recordSeparator = pRecordSeparator;
	}

	public String getItemDepartmentNameQuery() {
		return mItemDepartmentNameQuery;
	}

	public void setItemDepartmentNameQuery(String pItemDepartmentNameQuery) {
		mItemDepartmentNameQuery = pItemDepartmentNameQuery;
	}

	public Repository getPlanogramRepository() {
		return mPlanogramRepository;
	}

	public void setPlanogramRepository(Repository pPlanogramRepository) {
		mPlanogramRepository = pPlanogramRepository;
	}
	
	public String getPlanogramLocationAddressInfoQuery() {
		return mPlanogramLocationAddressInfoQuery;
	}

	public void setPlanogramLocationAddressInfoQuery(String pPlanogramLocationAddressInfoQuery) {
		mPlanogramLocationAddressInfoQuery = pPlanogramLocationAddressInfoQuery;
	}

	public String getPlanogramLocationNameInfoQuery() {
		return mPlanogramLocationNameInfoQuery;
	}

	public void setPlanogramLocationNameInfoQuery(String pPlanogramLocationNameInfoQuery) {
		mPlanogramLocationNameInfoQuery = pPlanogramLocationNameInfoQuery;
	}
	

	
	public boolean isUseItemLocationAddress() {
		return mUseItemLocationAddress;
	}

	public void setUseItemLocationAddress(boolean pUseItemLocationAddress) {
		mUseItemLocationAddress = pUseItemLocationAddress;
	}

	public boolean isUseItemLocationName() {
		return mUseItemLocationName;
	}

	public void setUseItemLocationName(boolean pUseItemLocationName) {
		mUseItemLocationName = pUseItemLocationName;
	}

	public boolean isUseItemDepartment() {
		return mUseItemDepartment;
	}

	public void setUseItemDepartment(boolean pUseItemDepartment) {
		mUseItemDepartment = pUseItemDepartment;
	}

	
	public boolean isShowItemLocationInfo() {
		return mShowItemLocationInfo;
	}

	public void setShowItemLocationInfo(boolean pShowItemLocationInfo) {
		mShowItemLocationInfo = pShowItemLocationInfo;
	}

	public String getPlanogramLocationInfo(String pItemId,String pStoreId) throws MFFException{
		String retValue=null;

		if(pItemId==null ||  pItemId.trim().isEmpty()){
				throw new MFFException("Item id null while pulling PlanogramLocationInfo from planogram repository");
		}
		if(pStoreId==null ||  pStoreId.trim().isEmpty()){
			throw new MFFException("Store id null while pulling PlanogramLocationInfo from planogram repository");
		}
		else {
			if(isUseItemLocationAddress()){
				retValue=extractPOGAddress(pItemId, pStoreId);
			}
			else if(isUseItemLocationName()){
				retValue=extractPOGName(pItemId, pStoreId);
			}
			else {
				retValue=extractDepartmentName( pItemId, pStoreId);
			}
		}
		return retValue;
	}
	
	private RepositoryItem[] getPlanogramLocationInfo(String locationQuery,String pItemId,String pStoreId,boolean pDepartmentName,String pLocationQuery) throws MFFException{
		try {
			RepositoryItem[] planoGramInfo=null;
			RepositoryItemDescriptor itemPOGDesc = getPlanogramRepository().getItemDescriptor(ITEM_DESCRIPTOR_PLANOGRAM_INFO);
			if(pDepartmentName){
				itemPOGDesc = getPlanogramRepository().getItemDescriptor(ITEM_DESCRIPTOR_DEPARTMENT);
			}
			
			RepositoryView itemPOGView = itemPOGDesc.getRepositoryView();
			RqlStatement statement = RqlStatement.parseRqlStatement(locationQuery);
			Object params[] = null;
			if(pDepartmentName){
				params=new Object[1];
				params[0] = pItemId;
			}
			else {
				params=new Object[2];
				params[0] = pItemId;
				params[1] = pStoreId;
			}
			vlogDebug("Executing query :{0}", statement.getQuery().toString());
			planoGramInfo = statement.executeQuery(itemPOGView, params);
			return planoGramInfo;
		}
		catch(RepositoryException ex){
			vlogWarning("RepositoryException occured while getting "+ pLocationQuery +" Query data for item from planogram repository");
			throw new MFFException(ex);
		}
		catch(NullPointerException ex){
			vlogWarning("Nullpointer occured while getting "+ pLocationQuery +" Query data for item from planogram repository");
			throw new MFFException(ex);
		}
		catch(Exception ex){
			vlogWarning("Exception occured while getting "+ pLocationQuery +" Query data for item from planogram repository");
			throw new MFFException(ex);
		}
		
	}
	
	private String extractDepartmentName(String pItemId,String pStoreId) throws MFFException{
		String retValue=null;
		String locationDepartmentQuery=getItemDepartmentNameQuery();
		if(locationDepartmentQuery!=null&&!locationDepartmentQuery.trim().isEmpty()){
			RepositoryItem[] departmentNameResult=getPlanogramLocationInfo(locationDepartmentQuery, pItemId, pStoreId, true, "locationDepartmentQuery");
			if(departmentNameResult==null || departmentNameResult.length==0){
				retValue= null;
			}
			else {
				retValue= (String) departmentNameResult[0].getPropertyValue(DEPARTMENT_NAME);
			}
		}
		else {
			retValue= null;
		}
		return retValue;
	}
		
	
	private String extractPOGName(String pItemId, String pStoreId) throws MFFException{
		String retValue=null;
		String locationNameQuery=getPlanogramLocationNameInfoQuery();
		if(locationNameQuery!=null&&!locationNameQuery.trim().isEmpty()){
			RepositoryItem[] locationNameResult=getPlanogramLocationInfo(locationNameQuery, pItemId, pStoreId, false, "locationNameQuery");
			if(locationNameResult==null || locationNameResult.length==0){
				retValue=extractDepartmentName(pItemId,pStoreId);
			}
			else {
				if(locationNameResult.length==1){
					retValue= (String) locationNameResult[0].getPropertyValue(PLANOGRAM_NAME);
				}
				else {
					String returnedValue="";
					for(int i=0;i<locationNameResult.length;i++){
						if(i+1==locationNameResult.length){
							returnedValue+=(String)locationNameResult[i].getPropertyValue(PLANOGRAM_NAME);
						}
						else {
							returnedValue+=(String)locationNameResult[i].getPropertyValue(PLANOGRAM_NAME)+getRecordSeparator();
						}
					}
					retValue=returnedValue;
				}
			}
		}
		else {
			retValue=extractDepartmentName(pItemId,pStoreId);
		}		
		return retValue;
	}
	private String extractPOGAddress(String pItemId, String pStoreId) throws MFFException{
		String retValue=null;
		String locationAddressQuery=getPlanogramLocationAddressInfoQuery();
		if(locationAddressQuery!=null&&!locationAddressQuery.trim().isEmpty()){
			RepositoryItem[] locationAddressResult=getPlanogramLocationInfo(locationAddressQuery,pItemId,pStoreId,false,"locationAddressQuery");
			if(locationAddressResult==null || locationAddressResult.length==0){
				retValue=extractPOGName(pItemId, pStoreId);
			}
			else {
				if(locationAddressResult.length==1){
					retValue= (String) locationAddressResult[0].getPropertyValue(PLANOGRAM_ADDRESS);
				}
				else{
					String returnedValue="";
					for(int i=0;i<locationAddressResult.length;i++){
						if(i+1==locationAddressResult.length){
							returnedValue+=(String)locationAddressResult[i].getPropertyValue(PLANOGRAM_ADDRESS);
						}
						else {
							returnedValue+=(String)locationAddressResult[i].getPropertyValue(PLANOGRAM_ADDRESS)+getRecordSeparator();
						}
					}
					retValue=returnedValue;
				}
			}
		}
		else {
			retValue=extractPOGName(pItemId, pStoreId);
		}
		return retValue;
	}



	

	
	
	
	

}

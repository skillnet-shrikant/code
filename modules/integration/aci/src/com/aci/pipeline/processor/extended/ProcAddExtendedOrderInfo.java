package com.aci.pipeline.processor.extended;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.aci.commerce.order.AciOrder;
import com.aci.configuration.AciConfiguration;
import com.aci.constants.AciConstants;
import com.aci.constants.FieldMappingConstants;
import com.aci.pipeline.exception.AciPipelineException;
import com.aci.pipeline.params.AciPipelineProcessParam;
import com.aci.pipeline.processor.ProcAddOrderInfo;
import com.aci.utils.AciUtils;
import com.liveprocessor.LPClient.LPTransaction;

public class ProcAddExtendedOrderInfo extends ProcAddOrderInfo {

	
	@Override
	protected void addExtendedProperties(AciPipelineProcessParam pParams) throws RepositoryException, AciPipelineException {
		
		vlogDebug("ProcAddExtendedOrderInfo:addExtendedProperties:Start");
		Order order = null;
		order=pParams.getOrder();
		
		RepositoryItem repoItem = ((AciOrder)order).getRepositoryItem();
		if(order==null){
			vlogError("Order passed to the pipeline is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}
		
		String orderId=order.getId();
		
		AciConfiguration aciConfiguration =pParams.getAciConfiguration();
		if(aciConfiguration==null){
			vlogError("ACI Configuration component is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}

		LPTransaction request=null;
		request=pParams.getRequest();
		if(request==null){
			vlogError("LPTransaction request object is null. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}
		
		Repository repository=getOrderRepository();
		if(repository ==null){
			vlogError("OrderRepository property is not set. Make sure non null value is passed");
			throw new AciPipelineException(FieldMappingConstants.ACI_CREATE_REQUEST_FAILED_ORDER_INFO, true);
		}
		pParams.setOrderRepository(repository);
		RepositoryItem orderItem=repository.getItem(orderId,"order");
		
		if(!StringUtils.isEmpty(getEmailAddressPropertyName())){
			String contactEmail=(String)orderItem.getPropertyValue(getEmailAddressPropertyName());
			pParams.setEmailAddress(contactEmail);
		}
		
		
		
		String buyerIpAddress= ((AciOrder)order).getBuyerIpAddress();
		if(buyerIpAddress!=null && !buyerIpAddress.trim().isEmpty()){
			boolean useIpv4Only= aciConfiguration.isIp4only();
			if(useIpv4Only){
				boolean isIpv4=AciUtils.isIpv4(buyerIpAddress);
				if(isIpv4){
					vlogDebug("Type of Ip address is ipv4");
					request.setField(FieldMappingConstants.CUST_IP_ADDR, buyerIpAddress.trim());
				}
				else {
					vlogDebug("Type of Ip address is not ipv4");
				}
			}
			else {
				request.setField(FieldMappingConstants.CUST_IP_ADDR, buyerIpAddress.trim());
			}
		}
		
		String redShieldCode=((AciOrder)order).getDeviceId();
		String transactionOrganizationCode=request.getField(FieldMappingConstants.REQ_TYPE_CD);
		if(redShieldCode!=null && !redShieldCode.trim().isEmpty()){
			if(transactionOrganizationCode!=null && !transactionOrganizationCode.trim().isEmpty()){
				if(transactionOrganizationCode.trim().equalsIgnoreCase("E")) {
					request.setField(FieldMappingConstants.EBT_DEVICEPRINT,redShieldCode.trim());
					request.setField(FieldMappingConstants.EBT_SERVICE,"I");
				}
				else {
					request.setField(FieldMappingConstants.EBT_SERVICE,"A");
					request.setField(FieldMappingConstants.EBT_DEVICEPRINT,"");
				}
			}
			else {
				request.setField(FieldMappingConstants.EBT_SERVICE,"A");
				request.setField(FieldMappingConstants.EBT_DEVICEPRINT,"");
			}
		}
		else {
			request.setField(FieldMappingConstants.EBT_SERVICE,"A");
			request.setField(FieldMappingConstants.EBT_DEVICEPRINT,"");
		}
		
		// Adding Profile details
		String profileId=((AciOrder)order).getProfileId();
		addUserProfileInfo(request,profileId);
		
		List<RepositoryItem> shipCommerceItemRelShip=new ArrayList<RepositoryItem>();
		List<RepositoryItem> paymentOrderRelShip=new ArrayList<RepositoryItem>();
		List<RepositoryItem> relList= (List<RepositoryItem>)repoItem.getPropertyValue("relationships");
		for(RepositoryItem item:relList){
			String relItemClassType=(String) item.getPropertyValue("relationshipClassType");
			if(relItemClassType!=null && !relItemClassType.trim().isEmpty()){
				if(relItemClassType.equalsIgnoreCase("shippingGroupCommerceItem")){
					shipCommerceItemRelShip.add(item);
				}
				else if(relItemClassType.equalsIgnoreCase("paymentGroupOrder")){
					paymentOrderRelShip.add(item);
				}
			}
		}
		pParams.setCommerceItemShippingGroupRelationShips(shipCommerceItemRelShip);
		pParams.setOrderPaymentGroupRelationShips(paymentOrderRelShip);
		pParams.setRequest(request);
		vlogDebug("ProcAddExtendedOrderInfo:addExtendedProperties:End");
	}
	
	private void addUserProfileInfo(LPTransaction pRequest, String profileId)throws RepositoryException{
		vlogDebug("ProcAddExtendedOrderInfo:addUserProfileInfo:Start");
		if(profileId!=null){
			Repository profileRepository=getProfileRepository();
			RepositoryItem pProfile=profileRepository.getItem(profileId,"user");
			if(pProfile==null){
				pRequest.setField(FieldMappingConstants.EBT_PREVCUST,"N");
				pRequest.setField(FieldMappingConstants.EBT_TOF,"0");
			}
			else {
				String loginValue=(String)pProfile.getPropertyValue("login");
				Date registrationDate=(Date)pProfile.getPropertyValue("registrationDate");
				if((loginValue!=null&&!loginValue.trim().isEmpty())&& (registrationDate!=null)) {
					pRequest.setField(FieldMappingConstants.EBT_PREVCUST,"Y");
					Date currentDate = new Date();
					long dateDiff=currentDate.getTime()-registrationDate.getTime();
					long diffInDays=dateDiff/(1000 * 60 * 60 * 24);
					pRequest.setField(FieldMappingConstants.EBT_TOF,""+diffInDays);
				}
				else {
					pRequest.setField(FieldMappingConstants.EBT_PREVCUST,"N");
					pRequest.setField(FieldMappingConstants.EBT_TOF,"0");
				}
			}
		}
		vlogDebug("ProcAddExtendedOrderInfo:addUserProfileInfo:End");
	}
	
}

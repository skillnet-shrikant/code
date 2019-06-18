package mff.allocation;

import java.io.IOException;

import javax.servlet.ServletException;

import com.google.common.base.Strings;
import com.mff.commerce.planogram.util.GetPlanogramInfoUtil;

import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import mff.MFFException;

public class GetPlanoGramInfoDroplet extends DynamoServlet {

	/* INPUT PARAMETERS */
	public static final ParameterName PARAM_ITEMID = ParameterName.getParameterName("itemId");
	public static final ParameterName PARAM_STOREID = ParameterName.getParameterName("storeId");
	
	/* OUTPUT PARAMETERS */
	public static final String PARAM_PLANOGRAM_INFO = "planogramInfo";

	/* OPEN PARAMETERS */
	public static final ParameterName OPARAM_OUTPUT = ParameterName.getParameterName("output");
	public static final ParameterName OPARAM_ERROR = ParameterName.getParameterName("error");
	public static final ParameterName OPARAM_EMPTY = ParameterName.getParameterName("empty");
	
	GetPlanogramInfoUtil mPlanogramInfoUtil;
	

	public GetPlanogramInfoUtil getPlanogramInfoUtil() {
		return mPlanogramInfoUtil;
	}


	public void setPlanogramInfoUtil(GetPlanogramInfoUtil pPlanogramInfoUtil) {
		mPlanogramInfoUtil = pPlanogramInfoUtil;
	}


	@Override
	public void service(DynamoHttpServletRequest pReq, DynamoHttpServletResponse pRes)
			throws ServletException, IOException {
		try {
			String itemId = (String) pReq.getObjectParameter(PARAM_ITEMID);
			if(Strings.isNullOrEmpty(itemId)){
				vlogWarning("Missing required Item Id parameter");
				pReq.serviceLocalParameter(OPARAM_ERROR, pReq, pRes);
				return;
			}
			vlogDebug("Item Id {0}", itemId);
			
			String storeId = (String) pReq.getObjectParameter(PARAM_STOREID);
			if(Strings.isNullOrEmpty(storeId)){
				vlogWarning("Missing required storeId Id parameter");
				pReq.serviceLocalParameter(OPARAM_ERROR, pReq, pRes);
				return;
			}
			vlogDebug("storeId Id {0}", storeId);
			
			String planoInfo=getPlanogramInfoUtil().getPlanogramLocationInfo(itemId, storeId);
			boolean showPlanoInfo=getPlanogramInfoUtil().isShowItemLocationInfo();
			PlanoGramInfoResponse planoResponse=new PlanoGramInfoResponse();
			if(planoInfo==null || planoInfo.trim().isEmpty()){
				planoResponse.setPlanogramInfo("");
				planoResponse.setShowPlanogramInfo(showPlanoInfo);
				planoResponse.setRecordSeparator(getPlanogramInfoUtil().getRecordSeparator());
			}
			else {
				planoResponse.setPlanogramInfo(planoInfo);
				planoResponse.setShowPlanogramInfo(showPlanoInfo);
				planoResponse.setRecordSeparator(getPlanogramInfoUtil().getRecordSeparator());
			}
				
			pReq.setParameter(PARAM_PLANOGRAM_INFO, planoResponse);
			if(planoInfo==null || planoInfo.trim().isEmpty()){
				vlogDebug("No Location info to report");
				pReq.serviceLocalParameter(OPARAM_EMPTY, pReq, pRes);
			}else {
				vlogDebug("Item Locations are available to report");
				pReq.serviceLocalParameter(OPARAM_OUTPUT, pReq, pRes);
			}
		}  catch (MFFException e) {
			logError("GetPlanoGramInfoDroplet : Error getting planogram info : " + e);
			pReq.serviceLocalParameter(OPARAM_ERROR, pReq, pRes);
		}
		catch (Exception e) {
				logError("GetPlanoGramInfoDroplet : Error getting planogram info : " + e);
				pReq.serviceLocalParameter(OPARAM_ERROR, pReq, pRes);
		}
	}
}

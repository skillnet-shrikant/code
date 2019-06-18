package oms.dropship.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import mff.dropship.loadReject.xsd.Detail;
import mff.dropship.loadReject.xsd.LineItem;
import mff.dropship.loadReject.xsd.SalesOrderRejection;
import oms.allocation.item.AllocationConstants;

public class DropShipRejectFileProcessorTask extends DropShipFileProcessorTask{

  @Override
  protected Object getMessageToProcess(File pFile) throws Exception {
    JAXBContext jaxbContext = JAXBContext.newInstance(mff.dropship.loadReject.xsd.Message.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    mff.dropship.loadReject.xsd.Message message = (mff.dropship.loadReject.xsd.Message) jaxbUnmarshaller.unmarshal(pFile);
    //System.out.println(message);
    return message;
  }

  @Override
  protected String getOrderNumberFromMessage(Object pMessage) {
    String orderNumber = "";
    if(pMessage instanceof mff.dropship.loadReject.xsd.Message){
      mff.dropship.loadReject.xsd.Message message = (mff.dropship.loadReject.xsd.Message)pMessage;
      SalesOrderRejection rejection = message.getSalesOrderRejection();
      if(rejection != null && rejection.getHeader() != null && rejection.getHeader().getOrderHeader() != null){
        orderNumber = rejection.getHeader().getOrderHeader().getCustomerOrderNumber();
      }
    }
    
    return orderNumber;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  protected Map createPipelineMap(Order pOrder,Object pMessage)throws CommerceException{
    Map lPipelineParams = super.createPipelineMap(pOrder, pMessage);
    List<String> itemsToCancel = new ArrayList<String>();
    
    if(pMessage instanceof mff.dropship.loadReject.xsd.Message){
      mff.dropship.loadReject.xsd.Message message = (mff.dropship.loadReject.xsd.Message)pMessage;
      SalesOrderRejection rejection = message.getSalesOrderRejection();
      Detail details = rejection.getDetail();
      for(LineItem lLineItem : details.getLineItem()){
        String lineNo = "ci" + lLineItem.getLineNo(); // commerceItemId
        String lineStatus = lLineItem.getLineStatus();
        if(lineStatus.equalsIgnoreCase("REJECTED")){
          itemsToCancel.add(lineNo);
        }
      }
    }
    
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_CANCEL,itemsToCancel);
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_CANCEL_DESC,getCancelDescription());
    return lPipelineParams;
  }
  
  private String cancelDescription;

  public String getCancelDescription() {
    return cancelDescription;
  }

  public void setCancelDescription(String cancelDescription) {
    this.cancelDescription = cancelDescription;
  }
  

}

package oms.dropship.task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.google.common.base.Strings;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import mff.dropship.shipAdvice.xsd.Detail;
import mff.dropship.shipAdvice.xsd.LineItem;
import mff.dropship.shipAdvice.xsd.ShipAdvice;
import oms.allocation.item.AllocationConstants;

public class DropShipShipNotificationFileProcessorTask extends DropShipFileProcessorTask{
  
  @Override
  protected Object getMessageToProcess(File pFile) throws Exception {
    JAXBContext jaxbContext = JAXBContext.newInstance(mff.dropship.shipAdvice.xsd.Message.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    mff.dropship.shipAdvice.xsd.Message message = (mff.dropship.shipAdvice.xsd.Message) jaxbUnmarshaller.unmarshal(pFile);
    //System.out.println(message);
    return message;
  }

  @Override
  protected String getOrderNumberFromMessage(Object pMessage) {
    String orderNumber = "";
    if(pMessage instanceof mff.dropship.shipAdvice.xsd.Message){
      mff.dropship.shipAdvice.xsd.Message message = (mff.dropship.shipAdvice.xsd.Message)pMessage;
      List<ShipAdvice> shipAdviceList = message.getShipAdvice();
      if(shipAdviceList != null && !shipAdviceList.isEmpty()){
        ShipAdvice shipAdvice = shipAdviceList.get(0);
        if(shipAdvice != null && shipAdvice.getHeader() != null && shipAdvice.getHeader().getOrderHeader() != null){
          orderNumber = shipAdvice.getHeader().getOrderHeader().getCustomerOrderNumber();
        }
      }
    }

    return orderNumber;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  protected Map createPipelineMap(Order pOrder,Object pMessage)throws CommerceException{
    Map lPipelineParams = super.createPipelineMap(pOrder, pMessage);
    List<String> itemsToShip = new ArrayList<String>();
    HashMap<String,String> itemToTrackingNumberMap = new HashMap<String,String>();
    
    if(pMessage instanceof mff.dropship.shipAdvice.xsd.Message){
      mff.dropship.shipAdvice.xsd.Message message = (mff.dropship.shipAdvice.xsd.Message)pMessage;
      List<ShipAdvice> shipAdviceList = message.getShipAdvice();
      if(shipAdviceList != null && !shipAdviceList.isEmpty()){
        ShipAdvice shipAdvice = shipAdviceList.get(0);
        Detail details = shipAdvice.getDetail();
        for(LineItem lLineItem : details.getLineItem()){
          String lineNo = "ci" + lLineItem.getLineNo(); // commerceItemId
          String lineStatus = lLineItem.getLineStatus();
          String trackingNumber = lLineItem.getBillOfLading();
          if(lineStatus.equalsIgnoreCase("SHIPPED")){
            itemsToShip.add(lineNo);
            if(!Strings.isNullOrEmpty(trackingNumber)){
              itemToTrackingNumberMap.put(lineNo, trackingNumber);
            }
          }
        }
      }
    }
    
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_ITEMS_TO_SHIP,itemsToShip);
    lPipelineParams.put(AllocationConstants.PIPELINE_PARAMETER_TRACKING_NO, itemToTrackingNumberMap);
    return lPipelineParams;
  }

}

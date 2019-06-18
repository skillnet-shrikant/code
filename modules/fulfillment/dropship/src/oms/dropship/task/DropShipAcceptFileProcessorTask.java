package oms.dropship.task;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import mff.dropship.loadAccept.xsd.SalesOrderSuccess;

public class DropShipAcceptFileProcessorTask extends DropShipFileProcessorTask{

  @Override
  protected Object getMessageToProcess(File pFile) throws Exception {
    JAXBContext jaxbContext = JAXBContext.newInstance(mff.dropship.loadAccept.xsd.Message.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    mff.dropship.loadAccept.xsd.Message message = (mff.dropship.loadAccept.xsd.Message) jaxbUnmarshaller.unmarshal(pFile);

    return message;
  }

  @Override
  protected String getOrderNumberFromMessage(Object pMessage) {
    String orderNumber = "";
    if(pMessage instanceof mff.dropship.loadAccept.xsd.Message){
      mff.dropship.loadAccept.xsd.Message message = (mff.dropship.loadAccept.xsd.Message)pMessage;
      SalesOrderSuccess success = message.getSalesOrderSuccess();
      if(success != null && success.getHeader() != null && success.getHeader().getOrderHeader() != null){
        orderNumber = success.getHeader().getOrderHeader().getCustomerOrderNumber();
      }
    }
    
    return orderNumber;
  }

}

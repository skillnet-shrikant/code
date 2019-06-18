package oms.dropship.processor;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.mff.commerce.order.MFFCommerceItemImpl;
import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.states.MFFCommerceItemStates;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.states.StateDefinitions;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import mff.dropship.salesOrder.xsd.Message;
import oms.allocation.item.AllocationConstants;
import oms.commerce.order.OMSOrderManager;

public class ProcGenerateSalesOrderFile extends GenericService implements PipelineProcessor {

  private final static int  SUCCESS = 1;

  public int[] getRetCodes() {
    int[] ret = { SUCCESS };
    return ret;
  }
  
  @SuppressWarnings("rawtypes")
  public int runProcess(Object pPipelineParams, PipelineResult pPipelineResults) throws Exception {
    
    vlogDebug("Entering ProcGenerateSalesOrderFile - runProcess");
    Map lParams = (Map) pPipelineParams;
    Order lOrder = (Order) lParams.get(AllocationConstants.PIPELINE_PARAMETER_ORDER);
    Object message = lParams.get(AllocationConstants.PIPELINE_PARAMETER_DROP_SHIP_MESSAGE);
    
    if(lOrder!= null && message != null && message instanceof mff.dropship.salesOrder.xsd.Message ){
      mff.dropship.salesOrder.xsd.Message lMessage = (mff.dropship.salesOrder.xsd.Message)message;
      writeToFile(((MFFOrderImpl)lOrder).getOrderNumber(),lMessage);
      updateItemState(lOrder);
    }
    
    return SUCCESS;
  }
  
 private void writeToFile(String pOrderNumber,Object pJaxbObject) throws JAXBException{
    
    vlogDebug("Entering ProcGenerateSalesOrderFile writeToFile for orderId {0}",pOrderNumber);
    
    File file = new File(generateFileName(pOrderNumber));
    JAXBContext jaxbContext = JAXBContext.newInstance(Message.class);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

    // output pretty printed
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    jaxbMarshaller.marshal(pJaxbObject, file);
    //jaxbMarshaller.marshal(pJaxbObject, System.out);
  }
  
  @SuppressWarnings("unchecked")
  private void updateItemState(Order pOrder) throws CommerceException{
    
    List<CommerceItem> ciList = pOrder.getCommerceItems();
    for(CommerceItem ciItem : ciList){
      MFFCommerceItemImpl lItem = (MFFCommerceItemImpl) ciItem;
      if(lItem.getStateAsString().equalsIgnoreCase(
          MFFCommerceItemStates.PENDING_DROP_SHIP_FULFILLMENT)){
        lItem.setState(StateDefinitions.COMMERCEITEMSTATES.getStateValue(MFFCommerceItemStates.PENDING_DROP_SHIP_CONFIRM));
      }
    }
    getOmsOrderManager().updateOrder(pOrder);
  }
  
  private String generateFileName(String pOrderNumber){
    StringBuffer sb = new StringBuffer();
    sb.append(getFolderPath());
    sb.append("/");
    sb.append(getFilePrefix());
    sb.append(pOrderNumber);
    sb.append("_");
    sb.append(System.currentTimeMillis());
    sb.append(getFileSuffix());
    
    vlogDebug("Inside generateFileName : Generating file {0}",sb.toString());
    return sb.toString();
  }
  
  private String mFolderPath;
  
  public String getFolderPath() {
    return mFolderPath;
  }

  public void setFolderPath(String pFolderPath) {
    this.mFolderPath = pFolderPath;
  }
  
  private String mFilePrefix;
  
  public String getFilePrefix() {
    return mFilePrefix;
  }

  public void setFilePrefix(String pFilePrefix) {
    mFilePrefix = pFilePrefix;
  }

  private String mFileSuffix;
  
  public String getFileSuffix() {
    return mFileSuffix;
  }

  public void setFileSuffix(String pFileSuffix) {
    this.mFileSuffix = pFileSuffix;
  }

  private OMSOrderManager mOrderManager;

  public void setOmsOrderManager(OMSOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  public OMSOrderManager getOmsOrderManager() {
    return mOrderManager;
  }

}

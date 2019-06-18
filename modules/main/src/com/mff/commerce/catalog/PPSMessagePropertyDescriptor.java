package com.mff.commerce.catalog;

import com.google.common.base.Strings;
import com.mff.constants.MFFConstants;

import atg.adapter.gsa.GSARepository;
import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor;

public class PPSMessagePropertyDescriptor extends RepositoryPropertyDescriptor{

  private static final long serialVersionUID = 1L;
  protected static final String CONTENT_REP_PATH = "/com/mff/content/repository/MFFContentRepository";
  
  /**
   * Properties of this type should always be read-only. This is a no-op method.
   */
  public void setPropertyValue(RepositoryItemImpl item, Object value) {
    // this is not a writable property, do nothing.
    return;
  }

   public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue) {
     
    String ppsMessageId= (String) pItem.getPropertyValue(MFFConstants.SKU_PPS_MSG_IDS);
    //System.out.println("PPSMessagePropertyDescriptor getPropertyValue : ppsMessageId - " + ppsMessageId);
    StringBuffer ppsMessage = new StringBuffer();
    if(!Strings.isNullOrEmpty(ppsMessageId)){
      GSARepository contentRep = (GSARepository)Nucleus.getGlobalNucleus().resolveName(CONTENT_REP_PATH);
      try {
        String tokens[] = StringUtils.splitStringAtCharacter(ppsMessageId, '|');
        if(tokens != null && contentRep != null){
            for(String messageId : tokens){
              RepositoryItem ppsMessageItem = contentRep.getItem(messageId, MFFConstants.ITEM_DESC_PPS_MESSAGE);
              if(ppsMessageItem != null){
                String message = (String)ppsMessageItem.getPropertyValue(MFFConstants.PROPERTY_PPS_MESSAGE_TEXT);
                ppsMessage.append(message);
              }
            }
        }
      } catch (RepositoryException e) {
        e.printStackTrace();
      }
    }
    
    //System.out.println("PPSMessagePropertyDescriptor getPropertyValue : ppsMessage.toString() - " + ppsMessage.toString());
    return ppsMessage.toString();
  }

}

package com.mff.commerce.csr.order.invoice;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.email.MFFEmailManager;

import atg.commerce.CommerceException;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class MFFInvoiceFormHandler extends GenericFormHandler {

  private String mOrderNumber;
  private String mOrderId;
  private String mEmailAddress;
  private String mSendInvoiceSuccessURL;
  private String mSendInvoiceErrorURL;
  private Repository mInvoiceRepository;
  private Repository mOrderRepository;
  private Repository mProductCatalog;
  private MFFEmailManager mEmailManager;
  private RepeatingRequestMonitor mRepeatingRequestMonitor;
  private String mFileCreationPath;
  private String mPdfTemplateFileNameWithPath;
  private String mBopisInvoicePdfTemplatePath;
  private DecimalFormat mDecimalFormat;
  private Map<String,String> mPaymentTypeMap;
  
  private MFFOrderManager mOrderManager;
  private static final String DOLLAR = "$";
  
  private DecimalFormat getDecimalFormat() {
    if(mDecimalFormat==null)
        mDecimalFormat = new DecimalFormat("####0.00");
    return mDecimalFormat;
  }
  public String getOrderNumber() {
    return mOrderNumber;
  }
  public void setOrderNumber(String pOrderNumber) {
    mOrderNumber = pOrderNumber;
  }
  public void setOrderId(String pOrderId) {
    mOrderId = pOrderId;
  }
  public String getOrderId() {
    return mOrderId;
  }
  public String getEmailAddress() {
    return mEmailAddress;
  }
  public void setEmailAddress(String pEmailAddress) {
    mEmailAddress = pEmailAddress;
  }
  public String getSendInvoiceSuccessURL() {
    return mSendInvoiceSuccessURL;
  }
  public void setSendInvoiceSuccessURL(String pSendInvoiceSuccessURL) {
    mSendInvoiceSuccessURL = pSendInvoiceSuccessURL;
  }
  public String getSendInvoiceErrorURL() {
    return mSendInvoiceErrorURL;
  }
  public void setSendInvoiceErrorURL(String pSendInvoiceErrorURL) {
    mSendInvoiceErrorURL = pSendInvoiceErrorURL;
  }
  public Repository getInvoiceRepository() {
    return mInvoiceRepository;
  }
  public void setInvoiceRepository(Repository pInvoiceRepository) {
    this.mInvoiceRepository = pInvoiceRepository;
  }
  public Repository getOrderRepository() {
    return mOrderRepository;
  }
  public void setOrderRepository(Repository pOrderRepository) {
    this.mOrderRepository = pOrderRepository;
  }
  public Repository getProductCatalog() {
    return mProductCatalog;
  }
  public void setProductCatalog(Repository pRepository) {
    this.mProductCatalog = pRepository;
  }
  
  public MFFEmailManager getEmailManager() {
    return mEmailManager;
  }

  public void setEmailManager(MFFEmailManager pEmailManager) {
    mEmailManager = pEmailManager;
  }
  
  public void setRepeatingRequestMonitor(RepeatingRequestMonitor pRepeatingRequestMonitor) {
    mRepeatingRequestMonitor = pRepeatingRequestMonitor;
  }

  public RepeatingRequestMonitor getRepeatingRequestMonitor() {
    return mRepeatingRequestMonitor;
  }
  
  public String getFileCreationPath() {
    return mFileCreationPath;
  }
  public void setFileCreationPath(String pFileCreationPath) {
    mFileCreationPath = pFileCreationPath;
  }
  
  public String getPdfTemplateFileNameWithPath() {
    return mPdfTemplateFileNameWithPath;
  }
  public void setPdfTemplateFileNameWithPath(String pPdfTemplateFileNameWithPath) {
    mPdfTemplateFileNameWithPath = pPdfTemplateFileNameWithPath;
  }
  
  public boolean handleSendInvoice(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "InvoiceFormHandler.handleSendInvoice";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {

        if (getFormError()) {
          return checkFormRedirect(null, getSendInvoiceErrorURL(), pRequest, pResponse);
        }
        preSendInvoice(pRequest, pResponse);

        if (getFormError()) {
          return checkFormRedirect(null, getSendInvoiceErrorURL(), pRequest, pResponse);
        }
        sendInvoice(pRequest, pResponse);

        if (getFormError()) {
          return checkFormRedirect(null, getSendInvoiceErrorURL(), pRequest, pResponse);
        }
        postSendInvoice(pRequest, pResponse);
        return checkFormRedirect(getSendInvoiceSuccessURL(),
            getSendInvoiceErrorURL(), pRequest, pResponse);
    }
    return true;
  }
  
  private void preSendInvoice(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    vlogInfo("email address [{0}], orderNumber[{1}]", getEmailAddress(),getOrderNumber());
    if(StringUtils.isEmpty(getEmailAddress())) {
      addFormException(new DropletException("Email Address is empty"));
      return;
    }
    if(StringUtils.isEmpty(getOrderNumber())) {
      addFormException(new DropletException("Order Number is empty"));
      return;
    }
  }
  
  private void postSendInvoice(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    
  }
  
  private void sendInvoice(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    
    try {
      RepositoryItem[] lInvoiceRepoItems = fetchInvoceRepoItems(getOrderNumber());
      if(lInvoiceRepoItems==null || lInvoiceRepoItems.length==0) {
        addFormException(new DropletException("No invoices found for "+ getOrderNumber()));
        return;
      }
      boolean isBopisOrder=((MFFOrderImpl)((MFFOrderManager)getOrderManager()).loadOrder(getOrderId())).isBopisOrder();
      List<File> lInvoiceFilesList = createInvoicePDF(lInvoiceRepoItems,isBopisOrder);
      if(!getFormError()) {
        if(!lInvoiceFilesList.isEmpty()) {
        sendInvoiceEmail(getOrderNumber(), lInvoiceFilesList, getEmailAddress());
        cleanUpInvoice(lInvoiceFilesList);
        }else {
          addFormException(new DropletException("No Invoice files created for Order Number "+  getOrderNumber() ));
          return;
        }
      }else {
        //cleaning up files even when in error & file created list is not empty
        if(!lInvoiceFilesList.isEmpty()) {
          cleanUpInvoice(lInvoiceFilesList);
        }
      }
      
    }catch (RepositoryException lRepositoryException) {
      vlogError("Repository Exception while fetching Invoice: " + lRepositoryException, lRepositoryException);
      addFormException(new DropletException("Error fetching Invoice repositry items for Order Number "+  getOrderNumber() ));
      return;
    } catch (CommerceException e) {
    	 vlogError("Repository Exception while fetching Invoice: " + e, e);
         addFormException(new DropletException("Error fetching Invoice repositry items for Order Number "+  getOrderNumber() ));
         return;
	}
    
  }
 
  private List<File> createInvoicePDF(RepositoryItem[] pInvoiceRepoItems, boolean isBopisOrder){
    List<File> lFilesList = new ArrayList<File>();
    try {
      vlogDebug("Current system dir FOR Invoice Templates-->", System. getProperty("user.dir"));
      String pdfTemplatePath=isBopisOrder?getBopisInvoicePdfTemplatePath():getPdfTemplateFileNameWithPath();
      for(RepositoryItem lInvoiceRepoItem : pInvoiceRepoItems) {
        if(hasValidLineItems(lInvoiceRepoItem)) {
          List<String>lTrackingNumbers = getTrackingNumbers(lInvoiceRepoItem);
          if(!isBopisOrder && !lTrackingNumbers.isEmpty()) {
            for(String lTrackingNumber : lTrackingNumbers) {
              PDDocument pdfDocument = PDDocument.load(new File(pdfTemplatePath));
              PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
              if (acroForm != null) {
                /*PDResources res = new PDResources();
                res.put(COSName.getPDFName("Helv"), PDType1Font.HELVETICA);
                acroForm.setDefaultResources(res);*/
                setPdfFieldValuesFromInvoice(acroForm, lInvoiceRepoItem, lTrackingNumber);
                acroForm.flatten();
              }else {
                addFormException(new DropletException("No Form found in Invoice Template"));
                return lFilesList;
              }
              pdfDocument.save(getFileCreationPath()+"/"+lInvoiceRepoItem.getRepositoryId()+"_"+lTrackingNumber+".pdf");
              pdfDocument.close();
              lFilesList.add(new File(getFileCreationPath()+"/"+lInvoiceRepoItem.getRepositoryId()+"_"+lTrackingNumber+".pdf"));
            }
          }else {
            PDDocument pdfDocument = PDDocument.load(new File(pdfTemplatePath));
            PDAcroForm acroForm = pdfDocument.getDocumentCatalog().getAcroForm();
            if (acroForm != null) {
             /* PDResources res = new PDResources();
              res.put(COSName.getPDFName("Helvetica"), PDType1Font.HELVETICA);
              acroForm.setDefaultResources(res);*/
              setPdfFieldValuesFromInvoice(acroForm, lInvoiceRepoItem,null);
              acroForm.flatten();
            }else {
              addFormException(new DropletException("No Form found in Invoice Template"));
              return lFilesList;
            }
            pdfDocument.save(getFileCreationPath()+"/"+lInvoiceRepoItem.getRepositoryId()+".pdf");
            pdfDocument.close();
            lFilesList.add(new File(getFileCreationPath()+"/"+lInvoiceRepoItem.getRepositoryId()+".pdf"));
          }
        }
      }
    }catch (InvalidPasswordException lInvalidPasswordException) {
      vlogError("InvalidPasswordException while fetching Invoice template: " + lInvalidPasswordException, lInvalidPasswordException);
      addFormException(new DropletException("Error loading Invoice Template - invalid Password "));
      return lFilesList;
    }catch (IOException lIOException) {
      vlogError("IOException while fetching Invoice template & creating invioce PDFs: " + lIOException, lIOException);
      addFormException(new DropletException("No invoices found for "));
      return lFilesList;
    }catch (Exception lException) {
	  vlogError("Exception while fetching Invoice template & creating invioce PDFs: " + lException, lException);
      addFormException(new DropletException("Technical error occurred. Please recheck after a while."));
      return lFilesList;
    }
    return lFilesList;
  }
  
  
  private RepositoryItem[] fetchInvoceRepoItems(String pOrderNumber) throws RepositoryException{
    Object[] lParams                  = new Object[1];
    lParams[0]                        = pOrderNumber;
    RepositoryView lView              = getInvoiceRepository().getView("invoice");
    RqlStatement lStatement           = RqlStatement.parseRqlStatement("orderNumber EQUALS ?0");
    RepositoryItem[] lRepositoryItems = lStatement.executeQuery(lView, lParams);
    return lRepositoryItems;
  }
  
  private void sendInvoiceEmail(String pOrderNumber,List<File>pInvoiceFileList, String pEmailAddress) {
    vlogInfo("in send invoice");
    File[] lInvoiceFiles = new File[pInvoiceFileList.size()]; 
    lInvoiceFiles =  pInvoiceFileList.toArray(lInvoiceFiles);
    getEmailManager().sendInvoiceEmail(pOrderNumber, lInvoiceFiles, pEmailAddress);
  }
  
  private void cleanUpInvoice(List<File>pInvoiceFileList) {
    vlogInfo("in cleanup");
    for(File lInvoiceFile :pInvoiceFileList) {
      lInvoiceFile.delete();
    }
  }
  
  
  private void setPdfFieldValuesFromInvoice(PDAcroForm pPDAcroForm, RepositoryItem pInvoiceRepoItem, String pTrackingNumber) throws IOException, Exception{
    RepositoryItem lOrderRepoItem=null;
    try {
      lOrderRepoItem = getOrderRepository().getItem(getOrderId(), "order");
    } catch (RepositoryException e) {
      vlogError("RepositoryException while fetching Order["+getOrderId()+"]: " + e, e);
      addFormException(new DropletException("No Orders found for order id : "+getOrderId()));
      return;
    }
    
    setTextArea(pPDAcroForm,"SOLDTo",getAddressString((RepositoryItem)pInvoiceRepoItem.getPropertyValue("billingAddress"),false));
    if(!getOrderNumber().startsWith("MB")) {
      setTextArea(pPDAcroForm,"SHIPTo",getAddressString((RepositoryItem)pInvoiceRepoItem.getPropertyValue("shippingAddress"),false));
      pPDAcroForm.getField("TRACKINGNumber").setValue(pTrackingNumber);
      pPDAcroForm.getField("PO#").setValue((String)pInvoiceRepoItem.getPropertyValue("customerPurchaseOrder"));
      pPDAcroForm.getField("Order/PCTL").setValue(getStringPropertyValue(pInvoiceRepoItem,"orderNumber"));
    }else if(getOrderNumber().startsWith("MB")){
    	setTextArea(pPDAcroForm,"PICKUPLocation",getAddressString((RepositoryItem)pInvoiceRepoItem.getPropertyValue("shippingAddress"),true));
    	if(pPDAcroForm.getField("Order#")!=null){
    		pPDAcroForm.getField("Order#").setValue(getStringPropertyValue(pInvoiceRepoItem,"orderNumber"));
    	}
    }
    pPDAcroForm.getField("Invocie#").setValue(getStringPropertyValue(pInvoiceRepoItem,"reference"));
    //need to check where pack date is available
    String lFormattedDate = new SimpleDateFormat("MM-dd-yyyy").format(pInvoiceRepoItem.getPropertyValue("orderDate"));
    pPDAcroForm.getField("PackDate").setValue(lFormattedDate);
//    pPDAcroForm.getField("Ship Via").setValue(getStringPropertyValue(pInvoiceRepoItem,"shipVia"));
    /////////not sure what to fill in terms have to look back
    setTextArea(pPDAcroForm, "Terms", getTerms((List<RepositoryItem>) pInvoiceRepoItem.getPropertyValue("payments")));
    /////////
    fillLineItems(pPDAcroForm, pInvoiceRepoItem,lOrderRepoItem,pTrackingNumber);
//    fillOrderFieldItems(pPDAcroForm, lOrderRepoItem, pInvoiceRepoItem);
    
  }
  
  private void setTextArea(PDAcroForm pPDAcroForm, String pFieldName, String pValue) throws IOException {
	  PDTextField pdTextArea=(PDTextField) pPDAcroForm.getField(pFieldName);
      pdTextArea.getCOSObject().setString(COSName.DA, "/Helv 9 Tf 0 0 0 rg");
      pdTextArea.setValue(pValue);
  }
  private String getTerms(List<RepositoryItem> pPayments) {
	  StringBuilder lTerms = new StringBuilder();
	  for(RepositoryItem lPayment : pPayments) {
		  lTerms.append(getPaymentTypeMap().get(lPayment.getPropertyValue("paymentType"))).append(" ")
		  .append(DOLLAR+lPayment.getPropertyValue("amount")).append("\n");
	  }
	return lTerms.toString();
  }
  
  /* private Double getOrderChargeTotal(List<RepositoryItem> pPayments) {
	 Double orderChargeAmount=0.0;
	  for(RepositoryItem lPayment : pPayments) {
		  orderChargeAmount+=(Double)lPayment.getPropertyValue("amount");
	  }
	  return orderChargeAmount;
  }
  private void fillOrderFieldItems(PDAcroForm pPDAcroForm, RepositoryItem pOrderRepoItem, RepositoryItem pInvoiceRepoItem) throws IOException {
    RepositoryItem lOrderPriceInfoRepoItem = (RepositoryItem) pOrderRepoItem.getPropertyValue("priceInfo");
    pPDAcroForm.getField("SHIPPINGOrdered").setValue(DOLLAR+getDecimalFormat().format((Double)lOrderPriceInfoRepoItem.getPropertyValue("shipping")));
    Double lMerchandiseTotal = getMerchandiseOrderedTotal(pOrderRepoItem);
    Double lOrderChargeTotal = getOrderChargeTotal((List<RepositoryItem>) pInvoiceRepoItem.getPropertyValue("payments"));
    pPDAcroForm.getField("MERCHOrdered").setValue(DOLLAR+getDecimalFormat().format((Double)lOrderPriceInfoRepoItem.getPropertyValue("amount")));
    pPDAcroForm.getField("SALESTAXOrdered").setValue(DOLLAR+getDecimalFormat().format((Double)lOrderPriceInfoRepoItem.getPropertyValue("tax")));
    pPDAcroForm.getField("DISCOUNTOrdered").setValue(DOLLAR+getDecimalFormat().format(lMerchandiseTotal
        +(Double)lOrderPriceInfoRepoItem.getPropertyValue("shipping")
        +(Double)lOrderPriceInfoRepoItem.getPropertyValue("tax")
        -(Double)lOrderChargeTotal)
        );
    //pPDAcroForm.getField("ORDEREDADDITIONAL CREDITS").setValue(String.valueOf((Double)lLineSummaryRepoitem.getPropertyValue("discountTotal")));
    //pPDAcroForm.getField("ORDEREDADDITIONAL DEBITS").setValue(String.valueOf((Double)lLineSummaryRepoitem.getPropertyValue("discountTotal")));
    pPDAcroForm.getField("TOTALOrdered").setValue(DOLLAR+getDecimalFormat().format(lOrderChargeTotal));
  }
  
  
  private Double getMerchandiseOrderedTotal(RepositoryItem pOrderRepoItem) {
    Double lMerchandiseTotal = 0.00D;
    List<RepositoryItem>  lCommerceItemRepoItemList= (List<RepositoryItem>) pOrderRepoItem.getPropertyValue("commerceItems");
    for(RepositoryItem lCommerceItemRepoItem : lCommerceItemRepoItemList) {
      RepositoryItem lItemPriceInfoRepoItem = (RepositoryItem)lCommerceItemRepoItem.getPropertyValue("priceInfo");
      lMerchandiseTotal += (Double)lItemPriceInfoRepoItem.getPropertyValue("rawTotalPrice");
    }
    return lMerchandiseTotal;


  }*/
  
  private boolean hasValidLineItems(RepositoryItem pInvoiceRepoItem) {
    if(null!=pInvoiceRepoItem.getPropertyValue("shippedItems")) {
      List <RepositoryItem> lShippedItemRepoList=(List<RepositoryItem>) pInvoiceRepoItem.getPropertyValue("shippedItems");
      for(RepositoryItem lShippedItemRepoItem : lShippedItemRepoList) {
        if(!getStringPropertyValue(lShippedItemRepoItem, "clientLineId").startsWith("sg")) {
          return true;
        }
      }
    }
    return false;
  }
  
  private void fillLineItems(PDAcroForm pPDAcroForm, RepositoryItem pInvoiceRepoItem, RepositoryItem pOrderRepoItem, String pTrackingNumber) throws IOException {
    if(null!=pInvoiceRepoItem.getPropertyValue("shippedItems")) {
      List <RepositoryItem> lShippedItemRepoList=(List<RepositoryItem>) pInvoiceRepoItem.getPropertyValue("shippedItems");
      int count=1;
      long totalShippedQty=(long) 0;
      long totalOrderedQty=(long) 0;
      Double lTotalExtPrice=0.0D,lTotalShippingAmount=0.0D,lTotalTaxTotal=0.0D,lTotalUnitPrice=0.0D, lShippingTaxAmount =0.0D;
      for(RepositoryItem lShippedItemRepoItem : lShippedItemRepoList) {
        String lTrackingNumber = getStringPropertyValue(((List<RepositoryItem>)lShippedItemRepoItem.getPropertyValue("lineCartons")).get(0), "trackingNumber");
        if(!getStringPropertyValue(lShippedItemRepoItem, "clientLineId").startsWith("sg") && (null==pTrackingNumber || pTrackingNumber.equals(lTrackingNumber)) ) {
          pPDAcroForm.getField("ITEMRow"+count).setValue(getStringPropertyValue(lShippedItemRepoItem, "itemNumber"));
          pPDAcroForm.getField("COLORRow"+count).setValue(getStringPropertyValue(lShippedItemRepoItem, "colorCode").equals("N1") ? "" : getStringPropertyValue(lShippedItemRepoItem, "colorCode"));
          pPDAcroForm.getField("SIZERow"+count).setValue(getStringPropertyValue(lShippedItemRepoItem, "sizeCode").equals("N1") ? "" : getStringPropertyValue(lShippedItemRepoItem, "sizeCode"));
          String skucode = getStringPropertyValue(lShippedItemRepoItem, "skucode");
          try {
            RepositoryItem lSkuRepoItem = getProductCatalog().getItem(skucode, "sku");
            if(null!=lSkuRepoItem) {
//              pPDAcroForm.getField("UPCRow"+count).setValue(getStringPropertyValue(lSkuRepoItem, "upcs"));
              pPDAcroForm.getField("DESCRIPTIONRow"+count).setValue(getStringPropertyValue(lSkuRepoItem, "description"));
            }else {
              vlogError("Sku item not found in catalog {0}", skucode);
            }
          } catch (RepositoryException e) {
            vlogError("RepositoryException while fetching sku item : " + skucode, e);
          }
         
          Long lQuantity = (Long)lShippedItemRepoItem.getPropertyValue("quantity");
          Double lUnitPrice = (Double)lShippedItemRepoItem.getPropertyValue("unitPrice");
          Double lExtPrice = (Double)lShippedItemRepoItem.getPropertyValue("extendedPrice");
          Double lTaxTotal = (Double)lShippedItemRepoItem.getPropertyValue("lineTaxTotal");
          Double lShippingAmount = (Double)lShippedItemRepoItem.getPropertyValue("shippingAmount");
          lTotalExtPrice+=lExtPrice;
          lTotalUnitPrice+=lUnitPrice*lQuantity;
          lTotalShippingAmount+=lShippingAmount;
          lTotalTaxTotal+=lTaxTotal;
          totalShippedQty+=lQuantity;
          long quantityOrdered=getQuantityOrdered(skucode, pOrderRepoItem);
          totalOrderedQty+=quantityOrdered;
          pPDAcroForm.getField("ORDEREDRow"+count).setValue(String.valueOf(quantityOrdered));
          pPDAcroForm.getField("SHIPPEDRow"+count).setValue(String.valueOf(lQuantity));
          pPDAcroForm.getField("UNIT COSTRow"+count).setValue(DOLLAR+getDecimalFormat().format(lUnitPrice));
          pPDAcroForm.getField("EXTRow"+count).setValue(DOLLAR+getDecimalFormat().format(lExtPrice));
          count++;
        }else if(getStringPropertyValue(lShippedItemRepoItem, "clientLineId").startsWith("sg")) {
        	lShippingTaxAmount+=(Double)lShippedItemRepoItem.getPropertyValue("lineTaxTotal");
        }
        
      }
      pPDAcroForm.getField("SHIPPINGExt").setValue(DOLLAR+getDecimalFormat().format(lTotalShippingAmount));
//      pPDAcroForm.getField("SHIPPINGShipped").setValue(DOLLAR+getDecimalFormat().format(lTotalShippingAmount));


      pPDAcroForm.getField("MERCHExt").setValue(DOLLAR+getDecimalFormat().format(lTotalExtPrice));
      pPDAcroForm.getField("MERCHShipped").setValue(String.valueOf(totalShippedQty));
      pPDAcroForm.getField("MERCHOrdered").setValue(String.valueOf(totalOrderedQty));
      
      pPDAcroForm.getField("SALESTAXExt").setValue(DOLLAR+getDecimalFormat().format(lTotalTaxTotal+lShippingTaxAmount));
//      pPDAcroForm.getField("SALESTAXShipped").setValue(DOLLAR+getDecimalFormat().format(lTotalTaxTotal+lShippingTaxAmount));

      pPDAcroForm.getField("DISCOUNTExt").setValue(DOLLAR+getDecimalFormat().format(lTotalUnitPrice - lTotalExtPrice));
//      pPDAcroForm.getField("DISCOUNTShipped").setValue(DOLLAR+getDecimalFormat().format(lTotalUnitPrice - lTotalExtPrice));

//      pPDAcroForm.getField("TOTALShipped").setValue(DOLLAR+getDecimalFormat().format(lTotalExtPrice + lTotalShippingAmount + lTotalTaxTotal +lShippingTaxAmount));
      pPDAcroForm.getField("TOTALExt").setValue(DOLLAR+getDecimalFormat().format(lTotalExtPrice + lTotalShippingAmount + lTotalTaxTotal + lShippingTaxAmount));
      
    }
  }
  
  private long getQuantityOrdered(String pSkucode, RepositoryItem pOrderRepoItem) {
    List<RepositoryItem>  lCommerceItemRepoItemList = (List<RepositoryItem>) pOrderRepoItem.getPropertyValue("commerceItems");
    for(RepositoryItem lCommerceItemRepoItem : lCommerceItemRepoItemList) {
      if(pSkucode.equals(lCommerceItemRepoItem.getPropertyValue("catalogRefId"))){
        return (Long)lCommerceItemRepoItem.getPropertyValue("quantity");
      }
    }
    return (long) 0;
  }

  private List<String> getTrackingNumbers(RepositoryItem pInvoiceRepoItem) {
    List<String> lTrackingNumberList = new ArrayList<String>();
    if( null != pInvoiceRepoItem.getPropertyValue("shippedItems") ) {
      List <RepositoryItem> lShippedItemRepoList=(List<RepositoryItem>) pInvoiceRepoItem.getPropertyValue("shippedItems");
      for(RepositoryItem lShippedItemRepoItem : lShippedItemRepoList) {
        String lTrackingNumber = getStringPropertyValue(((List<RepositoryItem>)lShippedItemRepoItem.getPropertyValue("lineCartons")).get(0), "trackingNumber");
        if(StringUtils.isNotBlank(lTrackingNumber) && !lTrackingNumber.equals("PENDING")) {
          if(lTrackingNumberList.isEmpty()) {
            lTrackingNumberList.add(lTrackingNumber);
          }else if(!lTrackingNumberList.contains(lTrackingNumber)) {
            lTrackingNumberList.add(lTrackingNumber);
          }
        }
      }
      
    }
    return lTrackingNumberList;
  }
  
  private String getAddressString(RepositoryItem pInvoiceAddressItem, boolean isBopisItem) {
    StringBuilder lStringBuilder = new StringBuilder();
    if(isBopisItem) {
    	lStringBuilder.append("Fleet Farm").append("\n");
    }else {
	    lStringBuilder.append(getStringPropertyValue(pInvoiceAddressItem,"firstName")).append(" ")
	    .append(getStringPropertyValue(pInvoiceAddressItem,"lastName")).append("\n");
    }
    lStringBuilder.append(getStringPropertyValue(pInvoiceAddressItem,"address1")).append("\n");
    if(StringUtils.isNotBlank(getStringPropertyValue(pInvoiceAddressItem,"address2"))) {
      lStringBuilder.append(getStringPropertyValue(pInvoiceAddressItem,"address2")).append("\n");
    }
    lStringBuilder.append(getStringPropertyValue(pInvoiceAddressItem,"city")).append(", ")
    .append(getStringPropertyValue(pInvoiceAddressItem,"provinceCode")).append(", ")
    .append(getStringPropertyValue(pInvoiceAddressItem,"countryCode")).append(", ")
    .append(getStringPropertyValue(pInvoiceAddressItem,"postalCode")).append("\n");
    return lStringBuilder.toString();
  }
  
  private String getStringPropertyValue(RepositoryItem pRepositoryItem, String pPropertyName) {
    if(null!=pRepositoryItem.getPropertyValue(pPropertyName)) {
      return (String)pRepositoryItem.getPropertyValue(pPropertyName);
    }else {
      return "";
    }
    
  }
public MFFOrderManager getOrderManager() {
	return mOrderManager;
}
public void setOrderManager(MFFOrderManager pOrderManager) {
	mOrderManager = pOrderManager;
}
public String getBopisInvoicePdfTemplatePath() {
	return mBopisInvoicePdfTemplatePath;
}
public void setBopisInvoicePdfTemplatePath(String pBopisInvoicePdfTemplatePath) {
	mBopisInvoicePdfTemplatePath = pBopisInvoicePdfTemplatePath;
}
public Map<String,String> getPaymentTypeMap() {
	return mPaymentTypeMap;
}
public void setPaymentTypeMap(Map<String,String> pPaymentTypeMap) {
	mPaymentTypeMap = pPaymentTypeMap;
}
  
}

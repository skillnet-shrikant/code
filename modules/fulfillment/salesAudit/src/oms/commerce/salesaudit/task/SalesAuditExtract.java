package oms.commerce.salesaudit.task;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

import atg.nucleus.GenericService;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import oms.commerce.salesaudit.exception.SalesAuditException;
import oms.commerce.salesaudit.record.Extract;
import oms.commerce.salesaudit.record.Invoice;
import oms.commerce.salesaudit.record.SalesAuditConstants;
import oms.commerce.salesaudit.schema.sale.Sales;
import oms.commerce.salesaudit.schema.sale.SalesOrder;
import oms.commerce.salesaudit.schema.sale.Sales.Orders;
import oms.commerce.salesaudit.util.InvoiceManager;
import oms.commerce.salesaudit.util.SalesAuditFile;
import oms.commerce.salesaudit.util.SalesAuditMapper;

/**
 * This class will create the Sales Audit extract file using entries from the 
 * Invoice repository.
 *  
 * @author jvose
 *
 */
public class SalesAuditExtract  
  extends GenericService {

  /**
   * Main Task for the scheduler
   * @throws SalesAuditException 
   */
  public void performTask () 
      throws SalesAuditException {
    // Check if the extract is enabled
    if (!isEnabled()) {
      vlogError ("Sales Audit task is not enabled ... Sales Audit process is terminating");
      return;
    }
    
    // Create the Sales Audit file name so it is available for use in the extract process
    getSalesAuditFile().createExtractFile ();
    
    // Get the Invoice Records to extract and convert to XML records
    Sales lSales       = createSalesAuditRecords ();
    if (lSales == null) 
      return;
    
    // Open the Sales Audit File
    if (! openExtractFile ()) {
      vlogError ("Unable to open the Sales Audit file ... Sales Audit process is terminating");
      return;
    }
    
    // Convert XML to string and write to the file
    String lXMLRecord  = getSalesAuditMapper().convertXMLToString (lSales);    
    if (! writeExtractFile(lXMLRecord)) {
      vlogError ("Unable to write the XML to the Sales Audit file ... Sales Audit process is terminating");
      return;
    }
   
    // Close the Extract File
    if (! closeExtractFile ()) {
      vlogError ("Unable to close the Sales Audit file ... Sales Audit process is terminating");
      return;
    }

    // Update record status
    if (isExtractOnly()) {
      vlogInfo ("Records will not be marked as updated");
    }   
   
    // Move the file to the pickup directory
    if (! moveToDoneDirectory ()) {
      vlogError ("Unable to move the Sales Audit file to the pickup directory ... Sales Audit process is terminating");
      return;
    }   
  }
  
  
  private Sales createSalesAuditRecords () 
      throws SalesAuditException {
    // Create the Sales Audit record
    Sales lSales = getSalesAuditMapper().createSalesRecord ();
    
    // Create the header fields
    getSalesAuditMapper().copyHeaderFields (lSales);
    
    // Get the invoice records to process
    String lFileName   = null;
    String lRunType    = null; 
    if (getReprocesFileName() != null) {
      vlogDebug ("Re-processing entries for file " + getReprocesFileName());
      lFileName     = getReprocesFileName();
      lRunType      = SalesAuditConstants.RUN_TYPE_RERUN;      
    }
    else {
      vlogDebug ("Invoke standard processing for file " + getReprocesFileName());
      lFileName     = this.getSalesAuditFile().getFileName();
      lRunType      = SalesAuditConstants.RUN_TYPE_STANDARD;      
    }
    RepositoryItem [] lInvoiceItems = getInvoiceManager().getInvoicesToProcess (lRunType, lFileName);
    if (lInvoiceItems == null) {
      vlogInfo ("No invoices found ... process terminating");
      return null;
    }
    
    // Create Sales Orders in the JAXB record
    Orders lOrders = new Orders();
    lSales.setOrders(lOrders);
    
    // Add the items to the sales record
    for (RepositoryItem lInvoiceItem : lInvoiceItems) {
      Invoice lInvoice = new Invoice ((MutableRepositoryItem) lInvoiceItem);  
      
      try {
        // Convert Invoice to XML and add to Sales
        SalesOrder lSalesOrder = getSalesAuditMapper().copyOrderRecord(lInvoice);
        lSales.getOrders().getOrder().add(lSalesOrder);
      }
      catch (Exception ex) {
        vlogError ("Unable to extract invoice : " + lInvoice.getId() + " skipping Invoice");
        getInvoiceManager().updateInvoiceStatusToError (lInvoice, new Timestamp (new Date().getTime()));
      }
    }  
    
    // Create Extract Summary
    Extract lExtract = new Extract();
    if (lRunType.equals(SalesAuditConstants.RUN_TYPE_STANDARD))
      lExtract = getSalesAuditMapper().generateFileSummary (lSales, getSalesAuditFile().getFileName());
    else
      lExtract = getSalesAuditMapper().generateFileSummaryForReRun (lSales, getReprocesFileName());
    
    // Update status of invoice records
    Timestamp lNow = new Timestamp(new Date().getTime());
    getInvoiceManager().updateExtractStatus (lInvoiceItems, lNow, lExtract); 
    return lSales;
  }
  
  /**
   * Open the Sales Audit Extract File
   * @return
   *      True  - Open was successful
   *      False - Open Failed
   */
  private boolean openExtractFile () {
    vlogDebug ("Opening file name " + getSalesAuditFile().getFileName());
    try {
      getSalesAuditFile().openExtractFile();
    } catch (IOException e) {
      vlogError ("Unable to open the Sales Audit file: " + getSalesAuditFile().getFileName());
      return false;
    }
    return true;
  }
  
  /**
   * Write content to the Sales Audit Extract File
   * @return
   *      True  - Write was successful
   *      False - Write Failed
   */  
  private boolean writeExtractFile (String pRecord) {
    vlogDebug ("Writing XML to extract file name " + getSalesAuditFile().getFileName());
    try {
      getSalesAuditFile().writeExtractFile(pRecord);
    } catch (IOException e) {
      vlogError ("Unable to write XML to the Sales Audit file: " + getSalesAuditFile().getFileName());
      return false;
    }
    return true;
  }
  
  /**
   * Close the Sales Audit Extract File
   * @return
   *      True  - Close was Successful
   *      False - Close Failed
   */
  private boolean closeExtractFile () {
    vlogDebug ("Closing file name " + getSalesAuditFile().getFileName());
    try {
      getSalesAuditFile().closeExtractFile();
    } catch (IOException e) {
      vlogError ("Unable to close the Sales Audit file: " + getSalesAuditFile().getFileName());
      return false;
    }
    return true;
  }
  
  /**
   * Move the generated file to the pickup directory.
   * 
   * @return
   *      True  - Move was Successful
   *      False - Move Failed
   */
  private boolean moveToDoneDirectory () {
    return getSalesAuditFile().moveToDoneDirectory();
  }

  
  /**
   * Get the list of invoices that are ready for processing.
   * 
   * @return        Array of Invoice Repository items
   * @throws SalesAuditException
   */
  /*
  private RepositoryItem [] getInvoicesToProcess () 
      throws SalesAuditException {   
      vlogDebug ("Begin - Pull Invoices for the Sales Audit feed");
      RepositoryItem [] lInvoiceItems     = null;
      try {
        Object [] lParams         = new String[1];
        lParams[0]                = SalesAuditConstants.EXTRACT_TO_SALES_AUDIT;
        RepositoryView lView      = getInvoiceRepository().getView(SalesAuditConstants.ITEM_INVOICE);
        RqlStatement lStatement   = RqlStatement.parseRqlStatement("status EQUALS ?0");
        //RqlStatement lStatement   = RqlStatement.parseRqlStatement("ALL");
        lInvoiceItems             = lStatement.executeQuery(lView, lParams);
        vlogDebug ("Found " + lInvoiceItems.length + " Invoice items for extraction");
      }
      catch (RepositoryException ex) {
        String lErrorMessage = String.format("Unable to get list of open invoices");
        vlogError (ex, lErrorMessage);
        throw new SalesAuditException (ex, lErrorMessage);
      }
      vlogDebug ("End - Pull Invoices for the Sales Audit feed");
      return lInvoiceItems;
    }
    */
  
  // *********************************************************
  //            Getter/setters
  // *********************************************************
  Repository mInvoiceRepository;
  public Repository getInvoiceRepository() {
    return mInvoiceRepository;
  }
  public void setInvoiceRepository(Repository pInvoiceRepository) {
    this.mInvoiceRepository = pInvoiceRepository;
  }

  InvoiceManager mInvoiceManager;
  public InvoiceManager getInvoiceManager() {
    return mInvoiceManager;
  }
  public void setInvoiceManager(InvoiceManager pInvoiceManager) {
    this.mInvoiceManager = pInvoiceManager;
  }

  boolean mEnabled;
  public boolean isEnabled() {
    return mEnabled;
  }
  public void setEnabled(boolean pEnabled) {
    this.mEnabled = pEnabled;
  }
  
  boolean mExtractOnly;
  public boolean isExtractOnly() {
    return mExtractOnly;
  }
  public void setExtractOnly(boolean pExtractOnly) {
    this.mExtractOnly = pExtractOnly;
  }

  SalesAuditFile mSalesAuditFile;
  public SalesAuditFile getSalesAuditFile() {
     return mSalesAuditFile;
  }
  public void setSalesAuditFile(SalesAuditFile pSalesAuditFile) {
    this.mSalesAuditFile = pSalesAuditFile;
  }
  
  SalesAuditMapper mSalesAuditMapper;
  public SalesAuditMapper getSalesAuditMapper() {
    return mSalesAuditMapper;
  }

  public void setSalesAuditMapper(SalesAuditMapper pSalesAuditMapper) {
    this.mSalesAuditMapper = pSalesAuditMapper;
  }  

  String mReprocesFileName;
  public String getReprocesFileName() {
    return mReprocesFileName;
  }
  public void setReprocesFileName(String pReprocesFileName) {
    this.mReprocesFileName = pReprocesFileName;
  }
  
}

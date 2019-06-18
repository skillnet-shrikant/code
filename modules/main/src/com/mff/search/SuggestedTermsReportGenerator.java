package com.mff.search;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.rql.RqlStatement;

public class SuggestedTermsReportGenerator extends GenericService {

  private static final String ENCODING_UTF8 = "UTF-8";
  private static final String DEFAULT_SEARCH_QUERYSTRING = "N=0&Ntt=";
  
  private Repository mSuggestedTermsRepository;
  private String mItemDescriptorName;
  private String mItemPropertyName;
  private String mEndecaQueryNavMatchMode = "mode matchall";
  private String mEndecaQueryNavSearchInterface = "All";
  private String mEndecaQueryNavSearchRecordFilter = "record.source:ProductCatalog";
  private String mEndecaMdexHost;
  private int mEndecaMdexPort;
  private String mItemsAllQuery;
  private String mItemsOnlyEnabledQuery;
  private boolean mOnlyEnabledItems;
  
  public Repository getSuggestedTermsRepository() {
    return mSuggestedTermsRepository;
  }
  public void setSuggestedTermsRepository(Repository pSuggestedTermsRepository) {
    mSuggestedTermsRepository = pSuggestedTermsRepository;
  }
  
  public String getItemDescriptorName() {
    return mItemDescriptorName;
  }
  public void setItemDescriptorName(String pItemDescriptorName) {
    mItemDescriptorName = pItemDescriptorName;
  }
  public String getItemPropertyName() {
    return mItemPropertyName;
  }
  public void setItemPropertyName(String pItemPropertyName) {
    mItemPropertyName = pItemPropertyName;
  }

  public String getEndecaQueryNavMatchMode() {
    return mEndecaQueryNavMatchMode;
  }

  public void setEndecaQueryNavMatchMode(String pEndecaQueryNavMatchMode) {
    mEndecaQueryNavMatchMode = pEndecaQueryNavMatchMode;
  }

  public String getEndecaQueryNavSearchInterface() {
    return mEndecaQueryNavSearchInterface;
  }

  public void setEndecaQueryNavSearchInterface(String pEndecaQueryNavSearchInterface) {
    mEndecaQueryNavSearchInterface = pEndecaQueryNavSearchInterface;
  }

  public String getEndecaQueryNavSearchRecordFilter() {
    return mEndecaQueryNavSearchRecordFilter;
  }

  public void setEndecaQueryNavSearchRecordFilter(String pEndecaQueryNavSearchRecordFilter) {
    mEndecaQueryNavSearchRecordFilter = pEndecaQueryNavSearchRecordFilter;
  }

  public String getEndecaMdexHost() {
    return mEndecaMdexHost;
  }

  public void setEndecaMdexHost(String pEndecaMdexHost) {
    mEndecaMdexHost = pEndecaMdexHost;
  }

  public int getEndecaMdexPort() {
    return mEndecaMdexPort;
  }

  public void setEndecaMdexPort(int pEndecaMdexPort) {
    mEndecaMdexPort = pEndecaMdexPort;
  }
  
  public String getItemsAllQuery() {
    return mItemsAllQuery;
  }
  public void setItemsAllQuery(String pItemsAllQuery) {
    mItemsAllQuery = pItemsAllQuery;
  }
  public String getItemsOnlyEnabledQuery() {
    return mItemsOnlyEnabledQuery;
  }
  public void setItemsOnlyEnabledQuery(String pItemsOnlyEnabledQuery) {
    mItemsOnlyEnabledQuery = pItemsOnlyEnabledQuery;
  }
  public boolean isOnlyEnabledItems() {
    return mOnlyEnabledItems;
  }
  public void setOnlyEnabledItems(boolean pOnlyEnabledItems) {
    mOnlyEnabledItems = pOnlyEnabledItems;
  }
  public boolean createReportFile(String lFileNameWithPath) {
    vlogInfo("{0} - createReportFile -- started",getName());
    RepositoryItem[] lRepositoryItems = itemsForReport();
    if(lRepositoryItems!=null) {
      vlogInfo("{0} - Num of items for report -- {1}",getName(),lRepositoryItems.length);
      try {
        File lReportFile = new File(lFileNameWithPath);
        FileWriter lFileWriter = new FileWriter(lReportFile);
        lFileWriter.write("Suggested_Term,Enabled,Weight,Product_Count\n");
        lFileWriter.flush();
        ENEConnection lENEConnection = new HttpENEConnection(getEndecaMdexHost(), getEndecaMdexPort());
        for(RepositoryItem lRepositoryItem : lRepositoryItems) {
          lFileWriter.write((String)lRepositoryItem.getPropertyValue("term")+","+(boolean)lRepositoryItem.getPropertyValue("enabled")+","+(int)lRepositoryItem.getPropertyValue("weight")+","+productEndecaResultCount((String)lRepositoryItem.getPropertyValue("term"),lENEConnection)+"\n");
          lFileWriter.flush();
        }
        lFileWriter.close();
        return true;
      } catch (IOException e) {
        vlogError(e.getMessage());
      }
      
    }else {
      vlogInfo("Suggested Terms items are null or empty");
    }
    return false;
  }
  
  private RepositoryItem[] itemsForReport() {
    try {
      RqlStatement lStatement;
      if(isOnlyEnabledItems()) {
        lStatement = RqlStatement.parseRqlStatement(getItemsOnlyEnabledQuery());
      }else {
        lStatement = RqlStatement.parseRqlStatement(getItemsAllQuery());
      }
      
     return lStatement.executeQuery(getSuggestedTermsRepository().getView(getItemDescriptorName()), null);
   } catch (RepositoryException e) {
     vlogError(e.getMessage(),e.getMessageArguments());
   }
  
   return null;
 }
  
  private int productEndecaResultCount(String pSearchString,ENEConnection lENEConnection) {
    StringBuilder lQueryString = new StringBuilder()
        .append(DEFAULT_SEARCH_QUERYSTRING)
        .append(pSearchString);
    
    try {
      UrlENEQuery lQuery = new UrlENEQuery(lQueryString.toString(),ENCODING_UTF8);
      lQuery.setNtx(getEndecaQueryNavMatchMode());
      lQuery.setNr(getEndecaQueryNavSearchRecordFilter());
      lQuery.setNtk(getEndecaQueryNavSearchInterface());
      lQuery.setNavNumERecs(0);
      lQuery.setNe("0");
      ENEQueryResults lResult = lENEConnection.query(lQuery);
      if(lResult.containsERecs()) {
        return lResult.getERecs().size();
      }
      if(lResult.containsNavigation()) {
        return (int) lResult.getNavigation().getTotalNumERecs();
      }
      
    } catch (UrlENEQueryParseException e) {
      vlogError(e.getMessage());
    } catch (ENEQueryException e) {
      vlogError(e.getMessage());
    }
    return 0;
  }
  
  public void testCreateReportFile() {
    DateFormat lDateFormat = new SimpleDateFormat("-yyyy_MM_dd");
    String lFilename = "/tmp/SuggestedTermsReport"+lDateFormat.format(new Date())+".csv";
    createReportFile(lFilename);
  }

}

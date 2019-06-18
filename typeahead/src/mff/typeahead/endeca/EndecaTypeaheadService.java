package mff.typeahead.endeca;

import java.io.FileInputStream;

//import atg.nucleus.GenericService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.endeca.navigation.ENEConnection;
import com.endeca.navigation.ENEQuery;
import com.endeca.navigation.ENEQueryException;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.FieldList;
import com.endeca.navigation.HttpENEConnection;
import com.endeca.navigation.UrlENEQuery;
import com.endeca.navigation.UrlENEQueryParseException;

import mff.typeahead.util.PropertyReaderUtilSingleton;



/**
 * Encapsulates the Endeca-specific code used for
 * retrieving results from the MDEX Engine.
 *
 * @author foldenburg
 *
 */
public final class EndecaTypeaheadService {


	private String mEndecaProductIndexHost = "192.168.70.200";
	private int mEndecaProductIndexPort = 15002;
	private String mEndecaTermIndexHost = "192.168.70.200";
	private int mEndecaTermIndexPort = 15002;
	private String mEndecaQueryBasicMatchMode = "mode matchany";
	private String mEndecaQueryBasicSearchInterface = "All";
	private String mEndecaQueryDimensionMatchMode = "mode matchallany rel nterms,exact,static(rank,descending)";
	private String mEndecaQueryNavMatchMode = "mode matchall";
	private String mEndecaQueryNavSearchInterface = "All";
	private String mEndecaQueryNavSearchRecordFilter = "record.source:ProductCatalog";
	private String mEndecaQueryNpcMatchMode = "mode matchallany spell nospell";
	private String mEndecaQueryNpcSearchInterface = "All";
//	private String mAbsoluteMaxResultCount = "50";
	private int mAbsoluteMaxTermResultCount = 20;
	private int mMaxProductResultCount = 3;
	private int mMaxCategoryResultCount = 5;
	private int mMaxBrandResultCount = 5;
	private String mSearchTermMaxLength = "75";
	private String mEndecaQueryTermMatchMode= "mode matchall";
	private boolean mEndecaQueryTermRelrankEnabled = true;
	private String mEndecaQueryTermSearchInterface = "typeaheadSuggestions";
	private String mEndecaQueryTermRelrankStrategy = "Terms";
	private boolean mTypeaheadNpcTriggerTermsEnabled = false;
	private boolean mTypeaheadNpcDimsearchEnabled = false;
	private boolean mTypeaheadDimsearchEnabled = true;
	private boolean mTypeaheadNavsearchEnabled = true;
	private String mConfigFilePathPropertyName="typeaheadConfigFilePath";
	private String mMdexPropertyFileName="mdex.properties";




	// Constants
	private static final String ENCODING_UTF8 = "UTF-8";
	//private static final String DIMSEARCH_QUERYSTRING = "D=";
	private static final String DEFAULT_SEARCH_QUERYSTRING = "N=0&Ntt=";

	// FieldLists for limiting query response size
	private FieldList npFieldList;
	private FieldList productFieldList;

	/**
	 * Constructor
	 */
	public EndecaTypeaheadService() {
		this.npFieldList = buildNonProductQueryFieldList();
		this.productFieldList = buildProductQueryFieldList();
		try {
		
				PropertyReaderUtilSingleton readerUtil=PropertyReaderUtilSingleton.getInstance();
				setEndecaProductIndexHost(readerUtil.getEndecaProductIndexHost());
				setEndecaProductIndexPort(readerUtil.getEndecaProductIndexPort());
				setEndecaTermIndexHost(readerUtil.getEndecaTermIndexHost());
				setEndecaTermIndexPort(readerUtil.getEndecaTermIndexPort());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Performs a barebones Endeca navigation query with no field selections, etc
	 * @param searchTerm Search terms for the query
	 * @param numResults Max number of results
	 * @return Endeca query results object
	 */
	public ENEQueryResults performBasicQuery(String searchTerm, int numResults)
			throws MalformedQueryException, QueryException {
		String queryString= DEFAULT_SEARCH_QUERYSTRING + searchTerm;
		String ntxValue = getEndecaQueryBasicMatchMode();
				// applicationContext.getProperty("endeca.query.basic.mode", MATCH_MODE_BEST_PARTIAL);
		String ntkValue = getEndecaQueryBasicSearchInterface();
				//applicationContext.getProperty("endeca.query.basic.interface", INTERFACE_PRODUCTS);
		UrlENEQuery query;
		try {
			query = new UrlENEQuery(queryString,ENCODING_UTF8);
			query.setNtx(ntxValue);
			query.setNtk(ntkValue);
		} catch (UrlENEQueryParseException e) {
			// Throw a wrapped exception rather than an API-specific one
			throw new MalformedQueryException(e);
		}

		return performQuery(query,
							getEndecaProductIndexHost(),
							getEndecaProductIndexPort());
	}



	/**
	 * Performs a combined navigation/dimension search query and returns the results
	 * @param searchTerm Search term to apply to the query
	 * @param numResults Max number of records to retrieve
	 * @return An Endeca query results object
	 */
	public ENEQueryResults performCombinedQuery(String searchTerm, int numResults)
			throws MalformedQueryException, QueryException {
//		final String methodName = "performCombinedQuery";

		StringBuilder queryString = new StringBuilder()
			.append(DEFAULT_SEARCH_QUERYSTRING)
			.append(searchTerm)
			.append("&D=")
			.append(searchTerm);

		String ntxValue = getEndecaQueryNavMatchMode();
		String ntkValue = getEndecaQueryNavSearchInterface();
		String dxValue = getEndecaQueryDimensionMatchMode();
		String recFilter = getEndecaQueryNavSearchRecordFilter();


		UrlENEQuery query;
		try {
			query = new UrlENEQuery(queryString.toString(),ENCODING_UTF8);
			// Nav search mode
			query.setNtx(ntxValue);
			// Dim search mode
			query.setDx(dxValue);
			// Limit 10 dim results per dimension
			query.setNr(recFilter);
			query.setDr(recFilter);
			//query.setDp(Integer.toString(numResults));
			// Use rel ranking instead of default ordering
			query.setDk("1");
			// Search interface
			query.setNtk(ntkValue);
			// Max number of records to return
			query.setNavNumERecs(numResults);
			query.setDimSearchCompound(true);

			// Limit query response to these fields
			query.setSelection(productFieldList);

//			if(isLoggingDebug()) {
//				logDebug(CLASSNAME + ": " + methodName  + " " + "Query: "+query.toString());
//			}


		} catch(ENEQueryException e) {
			// Throw a wrapped exception rather than an API-specific one
			throw new MalformedQueryException(e);
		}
		return performQuery(query,
				getEndecaProductIndexHost(),
				getEndecaProductIndexPort());
	}

	/**
	 * Performs a non-product query on the Endeca MDEX
	 *
	 * @param searchTerm Search terms to apply to the query
	 * @param numResults Max number of results to return
	 * @return ENEQueryResults object containing the query response
	 */
	public ENEQueryResults performNonProductQuery(String searchTerm, int numResults)
		throws MalformedQueryException, QueryException {
//		final String methodName = "performNonProductQuery";

		StringBuilder queryString = new StringBuilder()
			.append(DEFAULT_SEARCH_QUERYSTRING)
			.append(searchTerm)
			.append("&D=")
			.append(searchTerm);
		String ntxValue = getEndecaQueryNpcMatchMode();

		String ntkValue = getEndecaQueryNpcSearchInterface();


		UrlENEQuery query;
		try {
			query = new UrlENEQuery(queryString.toString(),ENCODING_UTF8);
			// Nav search mode
			query.setNtx(ntxValue);
			// Search interface
			query.setNtk(ntkValue);
			// Max number of records to return
			query.setNavNumERecs(numResults);

			// Limit query response to these fields
			query.setSelection(npFieldList);

//			if(isLoggingDebug()) {
//				logDebug(CLASSNAME + ": " + methodName  + " " + "Query: "+query.toString());
//			}


		} catch(ENEQueryException e) {
			// Throw a wrapped exception rather than an API-specific one
			throw new MalformedQueryException(e);
		}
		return performQuery(query,
				getEndecaProductIndexHost(),
				getEndecaProductIndexPort());
	}

	/**
	 * Performs an Term query on the Endeca MDEX specified for Terms
	 *
	 * @param searchTerm Search terms to apply to the query
	 * @param numResults Max number of results to return
	 * @return ENEQueryResults object containing the query response
	 */
	public ENEQueryResults performTermQuery(String searchTerm, int numResults)
			throws MalformedQueryException, QueryException, ConfigurationException {
//		final String methodName = "performTermQuery";
		String queryString= "N=0";
		StringBuilder ntxValue =
			new StringBuilder(getEndecaQueryTermMatchMode());
		boolean relRankEnabled = getEndecaQueryTermRelrankEnabled();

		UrlENEQuery query;
		try {
		query = new UrlENEQuery(queryString,ENCODING_UTF8);
			if(relRankEnabled) {
				// If rel rank is enabled, use the configured interface/rel rank strategy
				String relRank = getEndecaQueryTermRelrankStrategy();

				String searchInterface = getEndecaQueryTermSearchInterface();

				if(relRank==null || searchInterface==null || searchInterface.isEmpty()) {
					// If the either setting is missing or search interface is empty,
					// throw an Exception. Rel rank can be left blank to use the default.
					throw new ConfigurationException("Missing autocomplete rel rank or search interface."+
							"Check properties file.");
				}
				// Use Wildcard search for terms
				searchTerm += "*";
				query.setNtt(searchTerm);
				query.setNtk(searchInterface);
				ntxValue.append(' ').append(relRank);
				//query.setNu("correction");
			} else {
				// Otherwise, use a record filter and sort by weight
				query.setNr("term:"+searchTerm);
				query.setNs("weight|0");
				query.setNavNumERecs(numResults);
			}
			query.setNtx(ntxValue.toString());
//			if(isLoggingDebug()) {
//				logDebug(CLASSNAME + ": " + methodName  + " " + "Query: "+query.toString());
//			}

		} catch(ENEQueryException e)  {
			// Throw a wrapped exception rather than an API-specific one
			throw new MalformedQueryException(e);
		}
		return performQuery(query,
				getEndecaTermIndexHost(),
				getEndecaTermIndexPort());
	}

	/**
	 * Establishes connection the MDEX and performs supplied query
	 * @param query Query object to execute
	 * @return Endeca query results
	 */
	private ENEQueryResults performQuery(ENEQuery query, String mdexHost, int mdexPort) throws QueryException {
		try {
			ENEConnection conn = new HttpENEConnection(mdexHost, mdexPort);
			return conn.query(query);
		} catch (ENEQueryException e)
		{
			// Throw a wrapped exception rather than an API-specific one
			throw new QueryException(e);
		}
	}


	/**
	 * Builds FieldList for the product query
	 * @return FieldList with product properties
	 */
	private FieldList buildProductQueryFieldList() {
		FieldList ret = new FieldList();
		ret.addField("product.description");
		//ret.addField("product.displayName");
		ret.addField("product.thumbnailUrl");
		ret.addField("product.repositoryId");
		ret.addField("product.brand");
		ret.addField("DGraph.Bins");
		return ret;
	}

	/**
	 * Builds FieldList for the non-product query
	 * @return FieldList with non-product properties
	 *
	 * For Future Use
	 */
	private FieldList buildNonProductQueryFieldList() {
		FieldList ret = new FieldList();
		ret.addField("article.id");
		ret.addField("article.img");
		ret.addField("article.url");
		return ret;
	}


	public String getEndecaProductIndexHost() {
		return mEndecaProductIndexHost;
	}
	public void setEndecaProductIndexHost(String pEndecaProductIndexHost) {
		this.mEndecaProductIndexHost = pEndecaProductIndexHost;
	}
	public int getEndecaProductIndexPort() {
		return mEndecaProductIndexPort;
	}
	public void setEndecaProductIndexPort(int pEndecaProductIndexPort) {
		this.mEndecaProductIndexPort = pEndecaProductIndexPort;
	}
	public String getEndecaTermIndexHost() {
		return mEndecaTermIndexHost;
	}
	public void setEndecaTermIndexHost(String pEndecaTermIndexHost) {
		this.mEndecaTermIndexHost = pEndecaTermIndexHost;
	}
	public int getEndecaTermIndexPort() {
		return mEndecaTermIndexPort;
	}
	public void setEndecaTermIndexPort(int pEndecaTermIndexPort) {
		this.mEndecaTermIndexPort = pEndecaTermIndexPort;
	}
	public String getEndecaQueryBasicMatchMode() {
		return mEndecaQueryBasicMatchMode;
	}
	public void setEndecaQueryBasicMatchMode(String pEndecaQueryBasicMatchMode) {
		this.mEndecaQueryBasicMatchMode = pEndecaQueryBasicMatchMode;
	}
	public String getEndecaQueryBasicSearchInterface() {
		return mEndecaQueryBasicSearchInterface;
	}
	public void setEndecaQueryBasicSearchInterface(
			String pEndecaQueryBasicSearchInterface) {
		this.mEndecaQueryBasicSearchInterface = pEndecaQueryBasicSearchInterface;
	}
	public String getEndecaQueryDimensionMatchMode() {
		return mEndecaQueryDimensionMatchMode;
	}
	public void setEndecaQueryDimensionMatchMode(
			String pEndecaQueryDimensionMatchMode) {
		this.mEndecaQueryDimensionMatchMode = pEndecaQueryDimensionMatchMode;
	}
	public String getEndecaQueryNavMatchMode() {
		return mEndecaQueryNavMatchMode;
	}
	public void setEndecaQueryNavMatchMode(String pEndecaQueryNavMatchMode) {
		this.mEndecaQueryNavMatchMode = pEndecaQueryNavMatchMode;
	}
	public String getEndecaQueryNavSearchInterface() {
		return mEndecaQueryNavSearchInterface;
	}
	public void setEndecaQueryNavSearchInterface(
			String pEndecaQueryNavSearchInterface) {
		this.mEndecaQueryNavSearchInterface = pEndecaQueryNavSearchInterface;
	}
	public String getEndecaQueryNpcMatchMode() {
		return mEndecaQueryNpcMatchMode;
	}
	public void setEndecaQueryNpcMatchMode(String pEndecaQueryNpcMatchMode) {
		this.mEndecaQueryNpcMatchMode = pEndecaQueryNpcMatchMode;
	}
	public String getEndecaQueryNpcSearchInterface() {
		return mEndecaQueryNpcSearchInterface;
	}
	public void setEndecaQueryNpcSearchInterface(
			String pEndecaQueryNpcSearchInterface) {
		this.mEndecaQueryNpcSearchInterface = pEndecaQueryNpcSearchInterface;
	}
//	public String getAbsoluteMaxResultCount() {
//		return mAbsoluteMaxResultCount;
//	}
//	public void setAbsoluteMaxResultCount(String pAbsoluteMaxResultCount) {
//		this.mAbsoluteMaxResultCount = pAbsoluteMaxResultCount;
//	}
	public String getSearchTermMaxLength() {
		return mSearchTermMaxLength;
	}
	public void setSearchTermMaxLength(String pSearchTermMaxLength) {
		this.mSearchTermMaxLength = pSearchTermMaxLength;
	}
	public String getEndecaQueryTermMatchMode() {
		return mEndecaQueryTermMatchMode;
	}
	public void setEndecaQueryTermMatchMode(
			String pEndecaQueryTermMatchMode) {
		this.mEndecaQueryTermMatchMode = pEndecaQueryTermMatchMode;
	}
	public boolean getEndecaQueryTermRelrankEnabled() {
		return mEndecaQueryTermRelrankEnabled;
	}
	public void setEndecaQueryTermRelrankEnabled(
			boolean pEndecaQueryTermRelrankEnabled) {
		this.mEndecaQueryTermRelrankEnabled = pEndecaQueryTermRelrankEnabled;
	}
	public String getEndecaQueryTermSearchInterface() {
		return mEndecaQueryTermSearchInterface;
	}
	public void setEndecaQueryTermSearchInterface(
			String pEndecaQueryTermSearchInterface) {
		this.mEndecaQueryTermSearchInterface = pEndecaQueryTermSearchInterface;
	}
	public String getEndecaQueryTermRelrankStrategy() {
		return mEndecaQueryTermRelrankStrategy;
	}
	public void setEndecaQueryTermRelrankStrategy(
			String pEndecaQueryTermRelrankStrategy) {
		this.mEndecaQueryTermRelrankStrategy = pEndecaQueryTermRelrankStrategy;
	}
	public boolean getTypeaheadNpcTriggerTermsEnabled() {
		return mTypeaheadNpcTriggerTermsEnabled;
	}
	public void setTypeaheadNpcTriggerTermsEnabled(
			boolean pTypeaheadNpcTriggerTermsEnabled) {
		this.mTypeaheadNpcTriggerTermsEnabled = pTypeaheadNpcTriggerTermsEnabled;
	}
	public boolean getTypeaheadNpcDimsearchEnabled() {
		return mTypeaheadNpcDimsearchEnabled;
	}
	public void setTypeaheadNpcDimsearchEnabled(
			boolean pTypeaheadNpcDimsearchEnabled) {
		this.mTypeaheadNpcDimsearchEnabled = pTypeaheadNpcDimsearchEnabled;
	}
	public boolean getTypeaheadNavsearchEnabled() {
		return mTypeaheadNavsearchEnabled;
	}
	public void setTypeaheadNavsearchEnabled(boolean pTypeaheadNavsearchEnabled) {
		this.mTypeaheadNavsearchEnabled = pTypeaheadNavsearchEnabled;
	}

	public int getAbsoluteMaxTermResultCount() {
		return mAbsoluteMaxTermResultCount;
	}

	public void setAbsoluteMaxTermResultCount(int pAbsoluteMaxTermResultCount) {
		this.mAbsoluteMaxTermResultCount = pAbsoluteMaxTermResultCount;
	}

	public boolean getTypeaheadDimsearchEnabled() {
		return mTypeaheadDimsearchEnabled;
	}

	public void setTypeaheadDimsearchEnabled(boolean pTypeaheadDimsearchEnabled) {
		this.mTypeaheadDimsearchEnabled = pTypeaheadDimsearchEnabled;
	}

	public int getmMaxProductResultCount() {
		return mMaxProductResultCount;
	}

	public void setMaxProductResultCount(int pMaxProductResultCount) {
		this.mMaxProductResultCount = pMaxProductResultCount;
	}

	public int getMaxCategoryResultCount() {
		return mMaxCategoryResultCount;
	}

	public void setMaxCategoryResultCount(int pMaxCategoryResultCount) {
		this.mMaxCategoryResultCount = pMaxCategoryResultCount;
	}

	public int getMaxBrandResultCount() {
		return mMaxBrandResultCount;
	}

	public void setMaxBrandResultCount(int pMaxBrandResultCount) {
		this.mMaxBrandResultCount = pMaxBrandResultCount;
	}

	public String getEndecaQueryNavSearchRecordFilter() {
		return mEndecaQueryNavSearchRecordFilter;
	}

	public void setEndecaQueryNavSearchRecordFilter(
			String pEndecaQueryNavSearchRecordFilter) {
		this.mEndecaQueryNavSearchRecordFilter = pEndecaQueryNavSearchRecordFilter;
	}
}

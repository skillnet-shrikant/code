package com.mff.integration.ws.service.vertex;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.mff.integration.ws.service.exception.IntegrationException;
import com.mff.integration.ws.vertex.calculatetax.client.CalculateTaxWS70;
import com.mff.integration.ws.vertex.calculatetax.client.CalculateTaxWSService70;
import com.mff.integration.ws.vertex.calculatetax.client.CurrencyType;
import com.mff.integration.ws.vertex.calculatetax.client.CustomerCodeType;
import com.mff.integration.ws.vertex.calculatetax.client.CustomerType;
import com.mff.integration.ws.vertex.calculatetax.client.InvoiceRequestType;
import com.mff.integration.ws.vertex.calculatetax.client.InvoiceResponseType;
import com.mff.integration.ws.vertex.calculatetax.client.JurisdictionLevelCodeType;
import com.mff.integration.ws.vertex.calculatetax.client.LineItemISIType;
import com.mff.integration.ws.vertex.calculatetax.client.LineItemISOType;
import com.mff.integration.ws.vertex.calculatetax.client.LineItemQSIType;
import com.mff.integration.ws.vertex.calculatetax.client.LineItemQSOType;
import com.mff.integration.ws.vertex.calculatetax.client.LocationType;
import com.mff.integration.ws.vertex.calculatetax.client.LoginType;
import com.mff.integration.ws.vertex.calculatetax.client.MeasureType;
import com.mff.integration.ws.vertex.calculatetax.client.Product;
import com.mff.integration.ws.vertex.calculatetax.client.QuotationRequestType;
import com.mff.integration.ws.vertex.calculatetax.client.QuotationResponseType;
import com.mff.integration.ws.vertex.calculatetax.client.SaleTransactionType;
import com.mff.integration.ws.vertex.calculatetax.client.SellerType;
import com.mff.integration.ws.vertex.calculatetax.client.TaxResultCodeType;
import com.mff.integration.ws.vertex.calculatetax.client.TaxesType;
import com.mff.integration.ws.vertex.calculatetax.client.TaxesType.Jurisdiction;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.mff.integration.ws.vertex.calculatetax.client.VertexEnvelope;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingAdjustment;
import atg.commerce.states.StateDefinitions;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.payment.tax.TaxRequestInfo;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

public class CalculateTaxService extends GenericService {

	private MutableRepository	catalogRepository;								// atg/commerce/catalog/ProductCatalog
	// Trusted Id , Company Code & Division
	private String				mTrustedId;									// Trusted
																				// Id
	private String				mCompanyCode;									// Company
																				// Code
	private String				mDivisionCode;									// Division
																				// Code
	private String				mDeptCode;									// Division
	// Code
	// Origin Address
	private String				mOriginAddress1;								// Origin
																				// Address
																				// 1
	private String				mOriginAddress2;								// Origin
																				// Address
																				// 2
	private String				mOriginCity;									// Origin
																				// City
	private String				mOriginState;									// Origin
																				// State
	private String				mOriginPostalCode;								// Origin
																				// Postal
																				// Code
	private String				mOriginCountry;								// Origin
																				// Country
	private MutableRepository mOrderRepository;

	private static String		DESTINATION_COUNTRY			= "UNITED STATES";
	private static String		COMMERCEITEMSTATE_RETURNED	= "returned";
	private static String		SHIPPINGSTATE_REMOVED		= "removed";

	public static enum TAX_CALL {
		QUOTATION, INVOICE
	};

	private CalculateTaxWSService70	mCalculateTaxService;
	private String					mWsdlLocation;
	private boolean					mInitialized			= false;
	private boolean					logXML;

	// remove below properties once catalog changes have been done
	private boolean					productCodeAvailable	= false;
	private String					mDeptId;
	private String					mClassId;
	private String	giftCardProductID;
	private List<String>	giftCardProductIds;

	private void initialize() {
		if (isInitialized() == false) {
			URL url;
			try {
				url = new URL(getWsdlLocation());
				mCalculateTaxService = new CalculateTaxWSService70(url);
				setInitialized(true);
			} catch (MalformedURLException e) {
				vlogError(e, "Error occurred while initializing");
				if (isLoggingError()) {
					logError("Unable to form URL from " + getWsdlLocation());
				}
			} catch (RuntimeException rex) {
				vlogError(rex, "Error occurred while initializing");
				if (isLoggingError()) {
					logError(rex.getMessage() + ": Unable to form URL from " + getWsdlLocation());
				}
			} catch (Exception ex) {
				vlogError(ex, "Error occurred while initializing");
				if (isLoggingError()) {
					logError(ex.getMessage() + ": Unable to form URL from " + getWsdlLocation());
				}
			}
		}
	}

	public CalculateTaxWSService70 getCalculateTaxService() throws IntegrationException {
		if (null == mCalculateTaxService) {
			initialize();
			if (isInitialized() == false) {
				throw new IntegrationException("Unable to initialize Vertex service");
			}
		}
		return mCalculateTaxService;
	}

	public String getCompanyCode() {
		return mCompanyCode;
	}

	public void setCompanyCode(String mCompanyCode) {
		this.mCompanyCode = mCompanyCode;
	}

	public String getDivisionCode() {
		return mDivisionCode;
	}

	public void setDivisionCode(String mDivisionCode) {
		this.mDivisionCode = mDivisionCode;
	}

	public String getDeptCode() {
		return mDeptCode;
	}

	public void setDeptCode(String pDeptCode) {
		mDeptCode = pDeptCode;
	}

	public String getOriginAddress1() {
		return mOriginAddress1;
	}

	public void setOriginAddress1(String mOriginAddress1) {
		this.mOriginAddress1 = mOriginAddress1;
	}

	public String getOriginAddress2() {
		return mOriginAddress2;
	}

	public void setOriginAddress2(String mOriginAddress2) {
		this.mOriginAddress2 = mOriginAddress2;
	}

	public String getOriginCity() {
		return mOriginCity;
	}

	public void setOriginCity(String mOriginCity) {
		this.mOriginCity = mOriginCity;
	}

	public String getOriginState() {
		return mOriginState;
	}

	public void setOriginState(String mOriginState) {
		this.mOriginState = mOriginState;
	}

	public String getOriginPostalCode() {
		return mOriginPostalCode;
	}

	public void setOriginPostalCode(String mOriginPostalCode) {
		this.mOriginPostalCode = mOriginPostalCode;
	}

	public String getOriginCountry() {
		return mOriginCountry;
	}

	public void setOriginCountry(String mOriginCountry) {
		this.mOriginCountry = mOriginCountry;
	}

	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public String getTrustedId() {
		return mTrustedId;
	}

	public void setTrustedId(String mTrustedId) {
		this.mTrustedId = mTrustedId;
	}

	/**
	 * @param wsdlLocation
	 *            the wsdlLocation to set
	 */
	public void setWsdlLocation(String wsdlLocation) {
		mWsdlLocation = wsdlLocation;
	}

	/**
	 * @return the wsdlLocation
	 */
	public String getWsdlLocation() {
		return mWsdlLocation;
	}

	/**
	 * @param initialized
	 *            the initialized to set
	 */
	private void setInitialized(boolean initialized) {
		mInitialized = initialized;
	}

	/**
	 * @return the initialized
	 */
	private boolean isInitialized() {
		return mInitialized;
	}

	/**
	 * Calculate tax. Makes Vertex quotation call.
	 * 
	 * @param pTaxRequestInfo
	 *            @see {@link TaxRequestInfo}
	 * @return
	 * @throws MalformedURLException
	 * @throws DatatypeConfigurationException
	 */
	public Tax calculateTax(TaxRequestInfo pTaxRequestInfo) throws MalformedURLException, DatatypeConfigurationException {
		Tax mTax = null;
		try {
			mTax = calculateTaxCall(pTaxRequestInfo.getOrder(), TAX_CALL.QUOTATION, false, null, Boolean.FALSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mTax;
	}

	/**
	 * Calculate tax for the order.
	 * 
	 * @param pOrder
	 *            Order
	 * @param pCallType
	 *            Type of call Quotation|Request.
	 * @param isCredit
	 *            is this an Invoice credit request? Ignored in Quotation
	 *            requests.
	 * @return Tax
	 * @throws MalformedURLException
	 * @throws DatatypeConfigurationException
	 */
	public Tax calculateTax(Order pOrder, TAX_CALL pCallType, boolean isCredit, Set<String> commerceItemIDs, Boolean isFirstShipment) throws MalformedURLException, DatatypeConfigurationException {
		Tax mTax = null;
		mTax = calculateTaxCall(pOrder, pCallType, isCredit, commerceItemIDs, isFirstShipment);
		return mTax;
	}

	/**
	 * Calculate tax for the order.
	 * 
	 * @param pOrder
	 *            Order
	 * @param pCallType
	 *            Type of call Quotation|Request.
	 * @param isCredit
	 *            is this an Invoice credit request? Ignored in Quotation
	 *            requests.
	 * @return Tax
	 * @throws MalformedURLException
	 * @throws DatatypeConfigurationException
	 */
	public Tax calculateTaxCall(Order pOrder, TAX_CALL pCallType, boolean isCredit, Set<String> commerceItemIDs, Boolean isFirstShipment) throws MalformedURLException, DatatypeConfigurationException {
		VertexEnvelope mEnvelope = new VertexEnvelope();
		mEnvelope.setLogin(createLogin());
		if (pCallType == TAX_CALL.QUOTATION) {
			QuotationRequestType mQuotationRequest = createQuotationRequest(pOrder, null);
			mEnvelope.setQuotationRequest(mQuotationRequest);
		} else {
			// Order pOrder = pTaxRequestInfo.getOrder();
			InvoiceRequestType mInvoiceRequest = createInvoiceRequest(pOrder, isCredit, commerceItemIDs, isFirstShipment);
			mEnvelope.setInvoiceRequest(mInvoiceRequest);
		}

		Tax pTax = null;
		try {
			pTax = callWebService(mEnvelope, pCallType);
		} catch (Exception e) {
			logError("Error during webservice call.Error:" + e.getMessage());
		}

		return pTax;

	}

	/**
	 * Call the Vertex Tax WS and Calculate the Tax for the Order
	 * 
	 * @return Tax
	 * @throws IntegrationException
	 */
	protected VertexEnvelope callWebService(VertexEnvelope pRequestEnvelope) throws IntegrationException {

		VertexEnvelope mResponseEnvelope = null;

		printRequest(pRequestEnvelope);
		CalculateTaxWS70 calculateTax = getCalculateTaxService().getCalculateTax70();

		mResponseEnvelope = calculateTax.calculateTax70(pRequestEnvelope);
		
		return mResponseEnvelope;

	}
	
	/**
	 * Call the Vertex Tax WS and Calculate the Tax for the Order
	 * 
	 * @return Tax
	 * @throws IntegrationException
	 */
	protected VertexEnvelope estimateTax(VertexEnvelope pRequestEnvelope) throws IntegrationException {

		VertexEnvelope mResponseEnvelope = null;
		printRequest(pRequestEnvelope);
		CalculateTaxWS70 calculateTax = getCalculateTaxService().getCalculateTax70();
		
		mResponseEnvelope = calculateTax.calculateTax70(pRequestEnvelope);
		
		return mResponseEnvelope;

	}

	/**
	 * Call the Vertex Tax WS and Calculate the Tax for the Order
	 * 
	 * @return Tax
	 */
	protected Tax callWebService(VertexEnvelope pRequestEnvelope, TAX_CALL pCallType) throws IntegrationException {

		VertexEnvelope mResponseEnvelope = new VertexEnvelope();
		QuotationResponseType mQuotationResponse = null;
		InvoiceResponseType mInvoiceResponse = null;

		printRequest(pRequestEnvelope);

		CalculateTaxWS70 calculateTax = getCalculateTaxService().getCalculateTax70();
		try {
			mResponseEnvelope = calculateTax.calculateTax70(pRequestEnvelope);
		} catch (Exception exc) {
			vlogError(exc, "An exception occurred while calculating the tax");
			printRequestOnError(pRequestEnvelope);
			String localizedErrorMessage = exc.getLocalizedMessage();
			boolean loginFailed = localizedErrorMessage.contains("User login failed");
			if (loginFailed) {
				vlogError("VERTEX CONNECTION ERROR: THERE WAS PROBLEM IN CONNECTING TO VERTEX");
				vlogError("TAX IS SET TO 0");
			}
			if (!loginFailed) {
				boolean addressError = localizedErrorMessage.contains("Please provide a valid address and retry");
				if (!addressError) {
					vlogError("VERTEX MISC ERROR: ERROR OCCURRED DURING TAX CALCULATION");
					vlogError("TAX IS SET TO 0");
				}
			}
		}

		printResponse(mResponseEnvelope);

		Tax mTax = null;
		if (pCallType == TAX_CALL.QUOTATION) {
			if (isLoggingDebug()) {
				logDebug("Vertex Quotation Response : " + mResponseEnvelope.getQuotationResponse());
			}
			mQuotationResponse = mResponseEnvelope.getQuotationResponse();
			if (null != mQuotationResponse) {
				printTaxDetails(mQuotationResponse);
				mTax = buildTaxDetails(mQuotationResponse);
			}
		} else {
			mInvoiceResponse = mResponseEnvelope.getInvoiceResponse();
			if (mInvoiceResponse != null)
				printTaxDetails(mInvoiceResponse);
			// Do not update order taxes after invoice call.
			// mEXTNTax = buildTaxDetails(mInvoiceResponse);
		}

		return mTax;

	}

	/**
	 * build the Tax Object from the Vertex QuotationResponseType Object
	 * 
	 * @return Tax
	 */
	protected Tax buildTaxDetails(QuotationResponseType mQuotationResponse) {
		Tax mTax = new Tax();

		mQuotationResponse.getTotalTax().doubleValue();
		BigDecimal mTotalTaxObject = mQuotationResponse.getTotalTax();
		double mTotalTax = mTotalTaxObject.doubleValue();
		mTax.setTotalTax(mTotalTax);

		List<LineItemTax> mLineItemListTax = new ArrayList<LineItemTax>();
		List<LineItemQSOType> mLineItems = mQuotationResponse.getLineItem();
		for (LineItemQSOType lineItem : mLineItems) {
			if (isLoggingDebug()) {
				logDebug("LINEITEM ID : " + lineItem.getLineItemId());
				logDebug("LINEITEM Usuage : " + lineItem.getUsage());
				logDebug("LINEITEM Total Tax : " + lineItem.getTotalTax().doubleValue());
			}

			LineItemTax mLineItemTax = new LineItemTax();
			mLineItemTax.setLineItemId(lineItem.getLineItemId());
			mLineItemTax.setLineItemInfo(lineItem.getUsage());
			mLineItemTax.setTotalTax(lineItem.getTotalTax().doubleValue());
			List<TaxesType> mListTaxes = lineItem.getTaxes();
			for (TaxesType taxesType : mListTaxes) {
				String isTaxable = taxesType.getTaxResult().toString();

				if (TaxResultCodeType.TAXABLE.value().equals(isTaxable)) {
					Jurisdiction mJurisdiction = taxesType.getJurisdiction();
					String mJurisdictionType = mJurisdiction.getJurisdictionLevel().toString();
					if (JurisdictionLevelCodeType.CITY.value().equals(mJurisdictionType)) {
						if (isLoggingDebug()) {
							logDebug("LINEITEM CITY TAX : " + taxesType.getCalculatedTax().doubleValue());
						}
						mLineItemTax.setCityTax(taxesType.getCalculatedTax().doubleValue());
					}
					if (JurisdictionLevelCodeType.STATE.value().equals(mJurisdictionType)) {
						if (isLoggingDebug()) {
							logDebug("LINEITEM STATE TAX : " + taxesType.getCalculatedTax().doubleValue());
						}
						mLineItemTax.setStateTax(taxesType.getCalculatedTax().doubleValue());
					}
					if (JurisdictionLevelCodeType.COUNTY.value().equals(mJurisdictionType)) {
						if (isLoggingDebug()) {
							logDebug("LINEITEM COUNTY TAX : " + taxesType.getCalculatedTax().doubleValue());
						}
						mLineItemTax.setCountyTax(taxesType.getCalculatedTax().doubleValue());
						
						StringBuilder sb = new StringBuilder();
						sb.append(String.valueOf(taxesType.getJurisdiction().getJurisdictionId()));
						sb.append("|");
						sb.append(taxesType.getJurisdiction().getValue());
						
						mLineItemTax.setCountyTaxInfo(sb.toString());
					}
					if (JurisdictionLevelCodeType.DISTRICT.value().equals(mJurisdictionType)) {
						vlogDebug("LINEITEM DISTRICT TAX : " + taxesType.getCalculatedTax().doubleValue());
						mLineItemTax.setDistrictTax((mLineItemTax.getDistrictTax() + taxesType.getCalculatedTax().doubleValue()));
					}
				}

			}

			mLineItemListTax.add(mLineItemTax);
			// updateLineItemTaxInfo(lineItem, mTax, mLineItemTax);
		}
		mTax.setLineItemTax(mLineItemListTax);

		return mTax;
	}

	/**
	 * Prepare the Total County Tax And State from all LineItems and update Tax
	 */
	protected void updateLineItemTaxInfo(LineItemQSOType pLineItem, Tax pTax, List<LineItemTax> pLineItemTax) {
		List<TaxesType> mListTaxes = pLineItem.getTaxes();

		LineItemTax mLineItemTax = new LineItemTax();
		mLineItemTax.setLineItemId(pLineItem.getLineItemId());
		mLineItemTax.setLineItemInfo(pLineItem.getUsage());
		mLineItemTax.setTotalTax(pLineItem.getTotalTax().doubleValue());

		for (TaxesType taxesType : mListTaxes) {
			String isTaxable = taxesType.getTaxResult().toString();
			if (TaxResultCodeType.TAXABLE.value().equals(isTaxable)) {
				Jurisdiction mJurisdiction = taxesType.getJurisdiction();
				String mJurisdictionType = mJurisdiction.getJurisdictionLevel().toString();
				if (JurisdictionLevelCodeType.CITY.value().equals(mJurisdictionType)) {
					vlogDebug("LINEITEM CITY TAX : " + taxesType.getCalculatedTax().doubleValue());
					mLineItemTax.setCityTax(taxesType.getCalculatedTax().doubleValue());
				}
				if (JurisdictionLevelCodeType.STATE.value().equals(mJurisdictionType)) {
					vlogDebug("LINEITEM STATE TAX : " + taxesType.getCalculatedTax().doubleValue());
					mLineItemTax.setStateTax(taxesType.getCalculatedTax().doubleValue());
				}
			}
			pLineItemTax.add(mLineItemTax);
		}

	}

	/**
	 * Prepare the Total County Tax And State from all LineItems and update Tax
	 */
	protected void updateLineItemTaxInfo(LineItemISOType pLineItem, Tax pTax, List<LineItemTax> pLineItemTax) {
		List<TaxesType> mListTaxes = pLineItem.getTaxes();
		LineItemTax mLineItemTax = new LineItemTax();
		mLineItemTax.setLineItemId(pLineItem.getLineItemId());
		mLineItemTax.setLineItemInfo(pLineItem.getUsage());
		mLineItemTax.setTotalTax(pLineItem.getTotalTax().doubleValue());
		for (TaxesType taxesType : mListTaxes) {
			String isTaxable = taxesType.getTaxResult().toString();
			if (TaxResultCodeType.TAXABLE.value().equals(isTaxable)) {
				Jurisdiction mJurisdiction = taxesType.getJurisdiction();
				String mJurisdictionType = mJurisdiction.getJurisdictionLevel().toString();
				if (JurisdictionLevelCodeType.CITY.value().equals(mJurisdictionType)) {
					if (isLoggingDebug()) {
						logDebug("LINEITEM CITY TAX : " + taxesType.getCalculatedTax().doubleValue());
					}
					mLineItemTax.setCityTax(taxesType.getCalculatedTax().doubleValue());
				}
				if (JurisdictionLevelCodeType.STATE.value().equals(mJurisdictionType)) {
					if (isLoggingDebug()) {
						logDebug("LINEITEM STATE TAX : " + taxesType.getCalculatedTax().doubleValue());
					}
					mLineItemTax.setStateTax(taxesType.getCalculatedTax().doubleValue());
				}
			}
			pLineItemTax.add(mLineItemTax);
		}
	}

	/**
	 * set the Login Authentication to call the Vertex WS
	 * 
	 * @return LoginType
	 */
	protected LoginType createLogin() {
		LoginType mLogin = new LoginType();
		mLogin.setTrustedId(getTrustedId());
		return mLogin;
	}

	/**
	 * creates the VertexQuotationRequest
	 * 
	 * @return QuotationRequestType
	 */
	protected QuotationRequestType createQuotationRequest(Order pOrder, String zipCode) throws DatatypeConfigurationException {
		QuotationRequestType mQuotationRequest = new QuotationRequestType();
		mQuotationRequest.setDocumentDate(createDocumentDate());
		mQuotationRequest.setDocumentNumber(createDocumentNumber(pOrder));
		mQuotationRequest.setReturnAssistedParametersIndicator(true);
		mQuotationRequest.setTransactionType(SaleTransactionType.SALE);
		mQuotationRequest.setCurrency(createCurrencyType());
		mQuotationRequest.setSeller(createSeller());
		mQuotationRequest.getLineItem().addAll(addLineItems(pOrder, zipCode));
		return mQuotationRequest;
	}

	protected InvoiceRequestType createInvoiceRequest(Order pOrder, boolean isCredit, Set<String> commerceItemIDs, Boolean isFirstShipment) throws DatatypeConfigurationException {
		InvoiceRequestType mInvoiceRequest = new InvoiceRequestType();
		mInvoiceRequest.setDocumentDate(createDocumentDate(pOrder.getSubmittedDate()));
		mInvoiceRequest.setDocumentNumber(pOrder.getId());
		mInvoiceRequest.setReturnAssistedParametersIndicator(true);
		mInvoiceRequest.setTransactionType(SaleTransactionType.SALE);
		mInvoiceRequest.setCurrency(createCurrencyType());
		mInvoiceRequest.setSeller(createSeller());

		mInvoiceRequest.getLineItem().addAll(createISITypeLineItems(pOrder, isCredit, commerceItemIDs, isFirstShipment));

		return mInvoiceRequest;
	}

	/**
	 * create the DocumentDate for the QuotationRequest
	 * 
	 * @return XMLGregarianCalender
	 */
	protected XMLGregorianCalendar createDocumentDate() throws DatatypeConfigurationException {
		Integer mYear = Calendar.getInstance().get(Calendar.YEAR);
		Integer mMonth = Calendar.getInstance().get(Calendar.MONTH);
		Integer mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

		XMLGregorianCalendar documentDate = null;
		documentDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(mYear, mMonth, mDay));

		return documentDate;
	}

	/**
	 * For invoice requests the date should be the original order submit date
	 * 
	 * @param pDate
	 *            Date to which the document date is to be set to
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	protected XMLGregorianCalendar createDocumentDate(Date pDate) throws DatatypeConfigurationException {
		if (pDate == null)
			return createDocumentDate();

		Calendar cal = new GregorianCalendar();
		cal.setTime(pDate);
		Integer mYear = cal.get(Calendar.YEAR);
		Integer mMonth = cal.get(Calendar.MONTH);
		Integer mDay = cal.get(Calendar.DAY_OF_MONTH);

		XMLGregorianCalendar documentDate = null;
		documentDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar(mYear, mMonth, mDay));

		return documentDate;
	}

	/**
	 * Create LineItems List that the Customer is buying
	 * 
	 * @return List<LineItemQSITType>
	 */
	protected List<LineItemQSIType> addLineItems(Order pOrder, String zipCode) {
		Address lAddress = null;
		Double shippingPriceInfo = 0.0D;

		List<LineItemQSIType> mLineItems = new ArrayList<LineItemQSIType>();

		List<ShippingGroup> mShippingGroups = pOrder.getShippingGroups();
		int lineItemNumber = 0;
		for (ShippingGroup shippingGroup : mShippingGroups) {
			if (shippingGroup instanceof HardgoodShippingGroup) {
				lAddress = ((HardgoodShippingGroup) shippingGroup).getShippingAddress();
				
				if(shippingGroup.getPriceInfo() != null){
					shippingPriceInfo = shippingGroup.getPriceInfo().getAmount();
				}
				
				if (isLoggingDebug()) {
					logDebug("Shipping Price : " + shippingPriceInfo);
				}
				
				if(zipCode != null)
					lAddress.setPostalCode(zipCode);
				/*if(lAddress != null && lAddress.getPostalCode() == null){
					lAddress.setPostalCode(zipCode);
				}*/
				List<CommerceItemRelationship> lCommerceItemRelationships = shippingGroup.getCommerceItemRelationships();

				for (CommerceItemRelationship commerceItemRelationship : lCommerceItemRelationships) {
					// Create commerce item
					lineItemNumber += 1;
					CommerceItem lCommerceItem = commerceItemRelationship.getCommerceItem();

					Product pProduct = new Product();
					String productId = lCommerceItem.getAuxiliaryData().getProductId();
					
					LineItemQSIType pLineItem = new LineItemQSIType();
					pLineItem.setLineItemNumber(new BigInteger(Integer.toString(lineItemNumber)));
					pLineItem = createCommerceItem(lCommerceItem, pLineItem);
					pLineItem.setCustomer(createCustomer(pOrder, lAddress));

					lCommerceItem.getAuxiliaryData().getCatalogRef();

					// set Quantity
					MeasureType measureType = new MeasureType();
					measureType.setValue(BigDecimal.valueOf(lCommerceItem.getQuantity()));
					pLineItem.setQuantity(measureType);

					// set Extended Price
					//AmountType extendedPrice = new AmountType();
					double ciPrice = lCommerceItem.getPriceInfo().getAmount() - lCommerceItem.getPriceInfo().getOrderDiscountShare();
					RepositoryItem ciItem=null;
					try {
						ciItem = getOrderRepository().getItem(lCommerceItem.getId(), "commerceItem");
						
					} catch (RepositoryException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// if ci is a qualifier
					// deduct the gift value from the cost of the item
					RepositoryItem priceInfoItem=(RepositoryItem)ciItem.getPropertyValue("priceInfo");
					if(ciItem != null && ciItem.getPropertyValue("gwpPromoId") != null) {
						if(isLoggingDebug()) {
							logDebug ("Commerce Item " + lCommerceItem.getId() + " is a qualifier");
						}
						
						if(priceInfoItem != null) {
						
							if(isLoggingDebug()) {
								logDebug ("Using effective Price of the item " + (Double)priceInfoItem.getPropertyValue("effectivePrice"));
							}
							
							ciPrice = (Double)priceInfoItem.getPropertyValue("effectivePrice");
						}
					}
					if((Boolean)ciItem.getPropertyValue("gwp")) {
						if(isLoggingDebug()) {
							logDebug ("Commerce Item " + lCommerceItem.getId() + " is a gwp item");
						}
						
						if(priceInfoItem != null) {
							if(isLoggingDebug()) {
								logDebug ("Using effective price of the item " + (Double)priceInfoItem.getPropertyValue("effectivePrice"));
							}
							ciPrice = (Double)priceInfoItem.getPropertyValue("effectivePrice");
						}
					}
					
					if(productId != null && productId.equalsIgnoreCase(getGiftCardProductID())){
						ciPrice = 0;
					}
					if(productId != null && getGiftCardProductIds() != null 
							&& getGiftCardProductIds().contains(productId)){
						ciPrice = 0;
					}

					BigDecimal bd = new BigDecimal(ciPrice);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					//extendedPrice.setValue(bd);
					pLineItem.setExtendedPrice(bd);

					// set PRODUCT ID into LINEITEM
					String commerceItemAndLineItemLink = "PRODUCT:" + lCommerceItem.getId();
					pLineItem.setUsage(commerceItemAndLineItemLink);
					pLineItem.setLineItemId(lCommerceItem.getId());
					mLineItems.add(pLineItem);
				}
			}

		}
		// Add FREIGHT SKU LINEITEM
		LineItemQSIType pLineItem = new LineItemQSIType();

		pLineItem.setLineItemNumber(new BigInteger(Integer.toString((lineItemNumber + 1))));

		pLineItem.setCustomer(createCustomer(pOrder, lAddress));

		Product frieghtProduct = new Product();
		frieghtProduct.setProductClass("906");
		frieghtProduct.setValue("FREIGHT");
		pLineItem.setProduct(frieghtProduct);

//		BigDecimal extendedPrice = new BigDecimal(BigDecimal.valueOf(shippingPriceInfo));
//		extendedPrice.setValue(BigDecimal.valueOf(shippingPriceInfo));
		pLineItem.setExtendedPrice(BigDecimal.valueOf(shippingPriceInfo));

		MeasureType measureType = new MeasureType();
		measureType.setValue(BigDecimal.ONE);
		pLineItem.setQuantity(measureType);

		pLineItem.setLineItemId("FREIGHT");
		pLineItem.setUsage("FREIGHT");

		mLineItems.add(pLineItem);

		return mLineItems;
	}

	/**
	 * create the DocumentNumber for the QuotationRequest
	 * 
	 * @return String
	 */
	protected String createDocumentNumber(Order pOrder) {
		String pOrderId = pOrder.getId();

		return (pOrderId);
	}

	/**
	 * set the CurrencyType for the QuotationRequest
	 * 
	 * @return CurrencyType
	 */
	protected CurrencyType createCurrencyType() {
		CurrencyType mCurrencyType = new CurrencyType();
		mCurrencyType.setIsoCurrencyCodeAlpha("USD");

		return mCurrencyType;
	}

	/**
	 * create Seller Component for the Vertex Quotation Request
	 * 
	 * @return SellerType for the Quotation Request
	 */
	protected SellerType createSeller() {
		SellerType mSeller = new SellerType();
		if (isLoggingDebug()) {
			logDebug("Company Code : " + getCompanyCode());
			logDebug("Division Code : " + getDivisionCode());
			logDebug("Division Code : " + getDeptCode());
		}
		mSeller.setCompany(getCompanyCode());
		mSeller.setDivision(getDivisionCode());
		mSeller.setDepartment(getDeptCode());
		// Set Physical Location
//		if (isLoggingDebug()) {
//			logDebug("OriginCity : " + getOriginCity());
//			logDebug("OriginState : " + getOriginState());
//			logDebug("OriginPostalCode : " + getOriginPostalCode());
//			logDebug("OriginCountry : " + getOriginCountry());
//		}
//		LocationType mPhysicalOrigin = new LocationType();
//		mPhysicalOrigin.setCity(getOriginCity());
//		mPhysicalOrigin.setMainDivision(getOriginState().toUpperCase());
//		mPhysicalOrigin.setCountry(getOriginCountry());
//		mPhysicalOrigin.setPostalCode(getOriginPostalCode());
//		mSeller.setPhysicalOrigin(mPhysicalOrigin);

		return mSeller;
	}

	/**
	 * Create LineItems List that the Customer is buying
	 * 
	 * @return List<LineItemQSITType>
	 */
	protected List<LineItemISIType> createISITypeLineItems(Order pOrder, boolean isCredit, Set<String> commerceItemIDs, Boolean isFirstShipment) {
		Address lAddress = null;
		Double shippingPriceInfo = 0.0D;

		List<LineItemISIType> mLineItems = new ArrayList<LineItemISIType>();

		List<ShippingGroup> mShippingGroups = pOrder.getShippingGroups();
		if (isLoggingDebug()) {
			logDebug("Commerce Items IDs count:" + commerceItemIDs.size());
			for (int i = 0; i < commerceItemIDs.size(); i++) {
				logDebug("Commerce item [" + i + "] :" + commerceItemIDs.toArray()[i]);
			}
		}
		int lineItemNumber = 0;
		for (ShippingGroup shippingGroup : mShippingGroups) {
			if (shippingGroup instanceof HardgoodShippingGroup) {
				// Do not continue if the shipping group is removed.
				if (shippingGroup.getState() == StateDefinitions.SHIPPINGGROUPSTATES.getStateFromString(SHIPPINGSTATE_REMOVED)) {
					if (isLoggingDebug()) {
						logDebug("The shipping group:" + shippingGroup.getId() + " is removed.");
					}
					continue;
				}
				lAddress = ((HardgoodShippingGroup) shippingGroup).getShippingAddress();
				shippingPriceInfo = shippingGroup.getPriceInfo().getAmount();
				if (isLoggingDebug()) {
					logDebug("Shipping Price : " + shippingPriceInfo);
					logDebug("Shipping State:" + shippingGroup.getState());
					logDebug("Shipping State Detail:" + shippingGroup.getStateDetail());
				}

				List<CommerceItemRelationship> lCommerceItemRelationships = shippingGroup.getCommerceItemRelationships();

				for (CommerceItemRelationship commerceItemRelationship : lCommerceItemRelationships) {
					// Create commerce item
					lineItemNumber += 1;
					CommerceItem lCommerceItem = commerceItemRelationship.getCommerceItem();
					if (isLoggingDebug()) {
						logDebug("commerceItem id:" + lCommerceItem.getId());
						logDebug("commerceitem id in list?" + commerceItemIDs.contains(lCommerceItem.getId()));
					}
					if (!commerceItemIDs.contains(lCommerceItem.getId())) {
						continue;
					}
					;

					if (isCredit && lCommerceItem.getState() != StateDefinitions.COMMERCEITEMSTATES.getStateValue(COMMERCEITEMSTATE_RETURNED)) {
						continue;
					}
					if (isLoggingDebug()) {
						logDebug("Addding commerce item :" + lCommerceItem.getId() + " as line item BEGIN");
					}
					LineItemISIType pLineItem = new LineItemISIType();
					pLineItem.setLineItemNumber(new BigInteger(Integer.toString(lineItemNumber)));
					pLineItem = createInvoiceCommerceItem(lCommerceItem, pLineItem);

					pLineItem.setCustomer(createCustomerForInvoiceCall(pOrder, lAddress));

					Product pProduct = new Product();
					String productId = lCommerceItem.getAuxiliaryData().getProductId();
					lCommerceItem.getAuxiliaryData().getCatalogRef();

					String classId = "";
					String deptId = "";

					// for testing purpose, if prodcut code is not available
					// then it will use sample values from properties file
					// otherwise from product catalog
					if (!getProductCodeAvailable()) {
						classId = getClassId();
						deptId = getDeptId();
					} else {
						try {
							RepositoryItem productItem = getCatalogRepository().getItem(productId, "product");
							if (productItem != null) {
								deptId = (String) productItem.getPropertyValue("departmentId");
								classId = (String) productItem.getPropertyValue("classId");
							} else {
								if (isLoggingDebug()) {
									logDebug("Product Item is Null");
								}
							}

						} catch (RepositoryException e) {
							e.printStackTrace();
						}

					}

					pProduct.setProductClass(deptId);
					pProduct.setValue(removeLeadingZeroes(classId));
					pLineItem.setProduct(pProduct);

					// set Quantity
					MeasureType measureType = new MeasureType();
					measureType.setValue(BigDecimal.valueOf(lCommerceItem.getQuantity()));
					pLineItem.setQuantity(measureType);

					// set Extended Price
					//AmountType extendedPrice = new AmountType();
					double ciPrice = lCommerceItem.getPriceInfo().getAmount() - lCommerceItem.getPriceInfo().getOrderDiscountShare();
					BigDecimal bd = new BigDecimal(ciPrice);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					BigDecimal extendedPrice =bd;
					if (isCredit) {
						extendedPrice=extendedPrice.multiply(new BigDecimal(-1D));
					}

					pLineItem.setExtendedPrice(extendedPrice);

					// set PRODUCT ID into LINEITEM
					String commerceItemAndLineItemLink = "PRODUCT:" + lCommerceItem.getId();
					pLineItem.setUsage(commerceItemAndLineItemLink);
					pLineItem.setLineItemId(lCommerceItem.getId());
					mLineItems.add(pLineItem);
					if (isLoggingDebug()) {
						logDebug("Addding commerce item :" + lCommerceItem.getId() + " as line item END");
						logDebug("Total line items:" + mLineItems.size());
					}
				}
			}

		}

		// do not add for credit invoice or when it's not the first shipment.
		if (isLoggingDebug()) {
			logDebug("is first shipment ?" + isFirstShipment.booleanValue());
			logDebug("is Credit?" + isCredit);
		}
		if (!isCredit && isFirstShipment) {
			if (isLoggingDebug()) {
				logDebug("Adding freight sku line item");
			}
			// Add FREIGHT SKU LINEITEM
			LineItemISIType pLineItem = new LineItemISIType();

			pLineItem.setLineItemNumber(new BigInteger(Integer.toString((lineItemNumber + 1))));

			pLineItem.setCustomer(createCustomerForInvoiceCall(pOrder, lAddress));

			Product frieghtProduct = new Product();
			frieghtProduct.setValue("FREIGHT");
			pLineItem.setProduct(frieghtProduct);

//			AmountType extendedPrice = new AmountType();
//			extendedPrice.setValue(BigDecimal.valueOf(shippingPriceInfo));
			pLineItem.setExtendedPrice(BigDecimal.valueOf(shippingPriceInfo));

			MeasureType measureType = new MeasureType();
			measureType.setValue(BigDecimal.ONE);
			pLineItem.setQuantity(measureType);

			pLineItem.setLineItemId("FREIGHT");
			pLineItem.setUsage("FREIGHT");
			mLineItems.add(pLineItem);
		}

		return mLineItems;
	}

	/**
	 * create the LineItem and add it to the List of List<LineItemQSITType>
	 * 
	 * @return LineItemQSITType
	 */
	//
	protected LineItemQSIType createCommerceItem(CommerceItem pCommerceItem, LineItemQSIType pLineItem) {

		// Build a request for each commerce item
		Product pProduct = new Product();
		pProduct.setValue(pCommerceItem.getCatalogRefId());
		pLineItem.setProduct(pProduct);

		pCommerceItem.getCommerceItemClassType();		
		String skuId = pCommerceItem.getCatalogRefId();
		String classId = "";
			try {
			RepositoryItem skuItem = getCatalogRepository().getItem(skuId, "sku");
			if (skuItem != null) {				
				classId = (String) skuItem.getPropertyValue("taxCode");
			} else {
				if (isLoggingDebug()) {
					logDebug("Sku Item is Null");
				}
			}
	
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
			
		pProduct.setProductClass(classId);
		MeasureType pMeasureType = new MeasureType();
		pMeasureType.setValue(new BigDecimal(pCommerceItem.getQuantity()));
		pLineItem.setQuantity(pMeasureType);

		return pLineItem;
	}

	/**
	 * create the LineItem and add it to the List of List<LineItemISITType>
	 * 
	 * @return LineItemISIType
	 */
	protected LineItemISIType createInvoiceCommerceItem(CommerceItem pCommerceItem, LineItemISIType pLineItem) {

		// Build a request for each commerce item
		Product pProduct = new Product();
		pProduct.setValue(pCommerceItem.getCatalogRefId());
		pLineItem.setProduct(pProduct);

		pCommerceItem.getCommerceItemClassType();

		pProduct.setProductClass(pCommerceItem.getCommerceItemClassType());

		MeasureType pMeasureType = new MeasureType();
		pMeasureType.setValue(new BigDecimal(pCommerceItem.getQuantity()));
		pLineItem.setQuantity(pMeasureType);

		return pLineItem;
	}

	/**
	 * create the customer to whom the item is being shipped
	 * 
	 * @return CustomerType
	 */
	@SuppressWarnings("deprecation")
	protected CustomerType createCustomer(Order pOrder, Address pAddress) {
		CustomerType mCustomer = new CustomerType();

		String pProfileId = pOrder.getProfileId();
		CustomerCodeType mCodeType = new CustomerCodeType();
		try {
			
		RepositoryItem lorder = getOrderRepository().getItem(pOrder.getId(),"order");
		Object taxExemptCode = lorder.getPropertyValue("taxExemptionCode");
		
		//set tax exempt code only if it exists on the order
		if(taxExemptCode!=null){
		  if(!((String)taxExemptCode).isEmpty())
		    mCodeType.setClassCode((String)taxExemptCode);
		}
		
		mCodeType.setValue(pProfileId);
		if (isLoggingDebug()) {
			logDebug("Customer StreetAddress1 : " + pAddress.getAddress1());
			logDebug("Customer StreetAddress2 : " + pAddress.getAddress2());
			logDebug("Customer City : " + pAddress.getCity());
			logDebug("Customer State : " + pAddress.getState());
			logDebug("Customer PostalCode : " + pAddress.getPostalCode());
		}
		LocationType mShippingDestination = createLocation(pAddress);
		mCustomer.setDestination(mShippingDestination);
		mCustomer.setCustomerCode(mCodeType);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mCustomer;
	}

	/**
	 * create the customer to whom the item is being shipped
	 * 
	 * @return CustomerType
	 */
	protected CustomerType createCustomerForInvoiceCall(Order pOrder, Address pAddress) {
		CustomerType mCustomer = new CustomerType();

		String pProfileId = pOrder.getProfileId();
		CustomerCodeType mCodeType = new CustomerCodeType();
		mCodeType.setValue(pProfileId);

		LocationType mShippingDestination = createLocation(pAddress);
		mCustomer.setDestination(mShippingDestination);
		mCustomer.setCustomerCode(mCodeType);

		return mCustomer;
	}

	/**
	 * create the DestinationAddress to which the Item is being Shipped
	 * 
	 * @return LocationType
	 */
	protected LocationType createLocation(Address pAddress) {
		LocationType mlLocationType = new LocationType();

		if (!StringUtils.isBlank(pAddress.getAddress1())) {
			mlLocationType.setStreetAddress1(pAddress.getAddress1().trim());
		}

		if (!StringUtils.isBlank(pAddress.getAddress2())) {
			mlLocationType.setStreetAddress2(pAddress.getAddress2().trim());
		}

		if (!StringUtils.isBlank(pAddress.getCity())) {
			mlLocationType.setCity(pAddress.getCity().trim());
		}

		if (!StringUtils.isBlank(pAddress.getState())) {
			mlLocationType.setMainDivision(pAddress.getState().trim().toUpperCase());
		}
		if (!StringUtils.isBlank(pAddress.getCountry())) {
			mlLocationType.setCountry(pAddress.getCountry().trim().toUpperCase());
		}
		if (!StringUtils.isBlank(pAddress.getPostalCode())) {
			mlLocationType.setPostalCode(pAddress.getPostalCode().trim());
		}

		return mlLocationType;
	}

	/**
	 * print All the Details returned in the QuotationResponse
	 */
	protected void printTaxDetails(QuotationResponseType pQuotationResponse) {
		if (isLoggingDebug()) {
			logDebug("Order Total Tax : " + pQuotationResponse.getTotalTax());
			logDebug("Order Total : " + pQuotationResponse.getTotal());
		}

		List<LineItemQSOType> pLineItems = pQuotationResponse.getLineItem();

		if (pLineItems != null) {
			for (LineItemQSOType pLineItem : pLineItems) {
				if (isLoggingDebug()) {
					logDebug("LineItem Total Tax : " + pLineItem.getTotalTax());
					logDebug("LineItem Total Freight : " + pLineItem.getFreight());
				}
				List<TaxesType> pTaxes = pLineItem.getTaxes();
				for (TaxesType pTax : pTaxes) {
					if (isLoggingDebug()) {
						logDebug("Jurisdiction Level : " + pTax.getJurisdiction().getJurisdictionLevel() + "\t Name : " + pTax.getJurisdiction().getValue());
						logDebug("Calcultaed Tax : " + pTax.getCalculatedTax());
					}
				}
			}
		}
	}

	/**
	 * print All the Details returned in the InvoiceResponse
	 */
	protected void printTaxDetails(InvoiceResponseType pInvoiceResponse) {
		if (isLoggingDebug()) {
			logDebug("Order Total Tax : " + pInvoiceResponse.getTotalTax());
			logDebug("Order Total : " + pInvoiceResponse.getTotal());
		}

		List<LineItemISOType> pLineItems = pInvoiceResponse.getLineItem();

		if (pLineItems != null) {
			for (LineItemISOType pLineItem : pLineItems) {
				if (isLoggingDebug()) {
					logDebug("LineItem Total Tax : " + pLineItem.getTotalTax());
					logDebug("LineItem Total Freight : " + pLineItem.getFreight());
				}
				List<TaxesType> pTaxes = pLineItem.getTaxes();
				for (TaxesType pTax : pTaxes) {
					if (isLoggingDebug()) {
						logDebug("Jurisdiction Level : " + pTax.getJurisdiction().getJurisdictionLevel() + "\t Name : " + pTax.getJurisdiction().getValue());
						logDebug("Calcultaed Tax : " + pTax.getCalculatedTax());
					}
				}
			}
		}
	}

	/**
	 * print vertex request in xml format.
	 * 
	 * @param pRequestEnvelope
	 */
	protected void printRequest(VertexEnvelope pRequestEnvelope) {
		if (isLogXML()) {
			try {
				StringWriter sw = new StringWriter();
				JAXBContext jaxbContext = JAXBContext.newInstance("com.mff.integration.ws.vertex.calculatetax.client");
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(false));
				marshaller.marshal(pRequestEnvelope, sw);
				String quotationEnvelopeXML = sw.toString();
				logDebug("Sending Quotation Request XML to Vertex :\n");
				logDebug(quotationEnvelopeXML);

			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void printRequestOnError (VertexEnvelope pRequestEnvelope) {
		try {
			StringWriter sw = new StringWriter();
			JAXBContext jaxbContext = JAXBContext.newInstance("com.mff.integration.ws.vertex.calculatetax.client");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(false));
			marshaller.marshal(pRequestEnvelope, sw);
			String quotationEnvelopeXML = sw.toString();
			vlogError("Sending Quotation Request XML to Vertex :\n");
			vlogError(quotationEnvelopeXML);

		} catch (JAXBException e) {
			vlogError(e, "Error printing the tax envelope");
			
		}
	}

	/**
	 * Print Vertex Response in XML format
	 */
	protected void printResponse(VertexEnvelope pResponseEnvelope) {
		if (isLogXML()) {
			try {
				StringWriter sw = new StringWriter();
				JAXBContext jaxbContext = JAXBContext.newInstance("com.mff.integration.ws.vertex.calculatetax.client");
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(false));
				marshaller.marshal(pResponseEnvelope, sw);
				String quotationEnvelopeXML = sw.toString();
				logDebug("Sending Quotation Response XML from Vertex :\n");
				logDebug(quotationEnvelopeXML);

			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Check if Address entered by User is valid for tax calculation or not
	 */
	public void validateAddress(TaxRequestInfo pTaxRequestInfo) throws Exception {
		VertexEnvelope mRequestEnvelope = new VertexEnvelope();
		try {
			QuotationRequestType mQuotationRequest = createQuotationRequest(pTaxRequestInfo.getOrder(), null);
			mRequestEnvelope.setLogin(createLogin());
			mRequestEnvelope.setQuotationRequest(mQuotationRequest);
			callWebService(mRequestEnvelope);
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * Check if Address entered by User is valid for tax calculation or not
	 */
	public double estimatedTax(TaxRequestInfo pTaxRequestInfo, String zipCode) throws Exception {
		VertexEnvelope mRequestEnvelope = new VertexEnvelope();
		VertexEnvelope mResponseEnvelope = new VertexEnvelope();
		double estimatedTax = 0;
		try {
			QuotationRequestType mQuotationRequest = createQuotationRequest(pTaxRequestInfo.getOrder(), zipCode);
			mRequestEnvelope.setLogin(createLogin());
			mRequestEnvelope.setQuotationRequest(mQuotationRequest);
			mResponseEnvelope = estimateTax(mRequestEnvelope);
			
			if(mResponseEnvelope.getQuotationResponse() != null 
					&& mResponseEnvelope.getQuotationResponse().getTotalTax() != null
					&& mResponseEnvelope.getQuotationResponse().getTotalTax() != null
					&& mResponseEnvelope.getQuotationResponse().getTotalTax().doubleValue() > 0){
				estimatedTax = mResponseEnvelope.getQuotationResponse().getTotalTax().doubleValue();
			}
		} catch (Exception e) {
			throw e;
		}

		return estimatedTax;
	}
	
	/**
	 * Removes leading zeroes in a string if any.
	 * 
	 * @param inStr
	 *            Input string
	 * @return inStr after stripping out any leading zeroes if any
	 */
	private static String removeLeadingZeroes(String inStr) {
		if (inStr == null)
			return inStr;

		return inStr.replaceFirst(LEADING_ZERO_REGX, "");
	}

	/*
	 * The following removes leading zeroes, but leaves one if necessary (i.e.
	 * it wouldn't just turn "0" to a blank string) The ^ anchor will make sure
	 * that the 0+ being matched is at the beginning of the input. The (?!$)
	 * negative lookahead ensures that not the entire string will be matched.
	 */
	private static String	LEADING_ZERO_REGX	= "^0+(?!$)";

	public void doStartService() throws ServiceException {
		initialize();
	}

	public boolean isLogXML() {
		return logXML;
	}

	public void setLogXML(boolean logXML) {
		this.logXML = logXML;
	}

	/**
	 * @return the deptId
	 */
	public String getDeptId() {
		return mDeptId;
	}

	/**
	 * @param pDeptId
	 *            the deptId to set
	 */
	public void setDeptId(String pDeptId) {
		mDeptId = pDeptId;
	}

	/**
	 * @return the classId
	 */
	public String getClassId() {
		return mClassId;
	}

	/**
	 * @param pClassId
	 *            the classId to set
	 */
	public void setClassId(String pClassId) {
		mClassId = pClassId;
	}

	/**
	 * @return the productCodeAvailable
	 */
	public boolean getProductCodeAvailable() {
		return productCodeAvailable;
	}

	/**
	 * @param pProductCodeAvailable
	 *            the productCodeAvailable to set
	 */
	public void setProductCodeAvailable(boolean pProductCodeAvailable) {
		productCodeAvailable = pProductCodeAvailable;
	}

	public String getGiftCardProductID() {
		return giftCardProductID;
	}

	public void setGiftCardProductID(String giftCardProductID) {
		this.giftCardProductID = giftCardProductID;
	}
	
	public MutableRepository getOrderRepository() {
	    return mOrderRepository;
	}

	public void setOrderRepository(MutableRepository pOrderRepository) {
	    mOrderRepository = pOrderRepository;
	}

	public List<String> getGiftCardProductIds() {
		return giftCardProductIds;
	}

	public void setGiftCardProductIds(List<String> pGiftCardProductIds) {
		giftCardProductIds = pGiftCardProductIds;
	}
	
}

package com.aci.pipeline.params;

import java.util.List;
import java.util.Map;

import com.aci.configuration.AciConfiguration;
import com.aci.payment.creditcard.AciCreditCardInfo;
import com.liveprocessor.LPClient.LPTransaction;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.core.util.ContactInfo;
import atg.repository.Repository;
import atg.repository.RepositoryItem;

public class AciPipelineProcessParam {
	
	/**
	 * isBillingAddressAdded
	 */
	
	private boolean mBillingAddressAdded;
	
	/**
	 * Email Address
	 */
	
	private String mEmailAddress;
	
	/**
	 * The switch to enable cvv verification
	 */
	private boolean mEnableCvvVerification;
	
	private String mCardVerificationNumber;
	/**
	 * The req type needed for transaction
	 */
	private String mReqType;
	
	/**
	 * The subclient Id needed for transaction
	 */
	private String mSubclientId;
	
	/**
	 * The currency Code needed for transaction
	 */
	private String mCurrencyCode;
	/**
	 * The division number information related to the transaction
	 */
	private String mDivNum;
	
	/**
	 * The transaction action value to process the request 
	 */
	private String mTransactionAction;
	
	/**
	 * The order that needs to be screened for fraud.
	 */
	private Order mOrder;
	
	/**
	 * Request object to be sent to RED
	 */
	private LPTransaction mRequest;
		
	/**
	 * The Credit card information from order that needs to be passed to red transaction object
	 */
	private List<PaymentGroup> mCreditCards;
	
	/**
	 * The Gift Certificate information from order that needs to be passed to red transaction object
	 */
	private List<PaymentGroup> mGiftCards;
	
	/**
	 * Credit card billing info objects
	 */
	private List<ContactInfo> mCreditCardBillingInfos;
	
	/**
	 * Flag that indicates order has credit cards in payments
	 */
	private boolean mHasCreditCards;
	
	/**
	 * Flag that indicates order has gift cards in payments
	 */
	private boolean mHasGifftCards;
	
	/**
	 * 
	 * Gift Certificate billing info objects
	 */
	private List<ContactInfo> mGiftCardBillingInfos;
	
	/**
	 * Hard good shipping group infos
	 */
	private List<HardgoodShippingGroup> mHardgoodShippingGroupInfos;
	
	/**
	 * Electronic shipping group infos
	 */
	private List<ElectronicShippingGroup> mElectronicShippingGroupInfos;
	
	/**
	 * Captures if order has hard good and electronic good
	 */
	private boolean mHasBothHardgoodAndElectronicgood;
	
	/**
	 * Captures if order has All electronic goods
	 */
	private boolean mHasAllElectronicgoods;
	
	/**
	 * Commerce Items present in order
	 */
	private List<CommerceItem> mCommerceItems;
	
	/**
	 * Order Repository Param
	 */
	private Repository mOrderRepository;
	
	/**
	 * ReDConfgiuration object
	 */
	private AciConfiguration mAciConfiguration;
	
	/**
	 * CommerceItem ShippingGroup Relationships
	 */
	private List<RepositoryItem> mCommerceItemShippingGroupRelationShips;
	
	/**
	 * CommerceItem paymentGroup Relationships
	 */
	private List<RepositoryItem> mCommerceItemPaymentGroupRelationShips;
	
	/**
	 * Order paymentGroup Relationships
	 */
	private List<RepositoryItem> mOrderPaymentGroupRelationShips;
	
	/**
	 * CommerceItemNumber commerceItem Map
	 */
	private Map<String,String> mCommerceItemNumberCommerceItemMap;
	
	/**
	 * CommerceItem ShippingRelationship Map
	 */
	private Map<String,RepositoryItem> mCommerceItemShippingGroupRelationShipMap;
	
	
	/**
	 * Order Additional shipping prices
	 */
	
	private double mAdditionalShippingPrice;
	
	/**
	 * Payment Group Total
	 */
	
	private double mTotalPaymentGroupPrice;
	
	/**
	 * Order tax amount
	 */
	
	private double mTaxTotal;
	
	/**
	 * Order SubTotal includes order amount,shipping,giftservice and additional prices
	 */
	
	private double mOrderSubTotal;
	
	/**
	 * Original Request Id
	 */
	private String mOriginalReqId;
	
	/*
	 * Shipping Price
	 * */
	
	private AciCreditCardInfo mAciCreditCardInfo;
	
	private double mShippingPrice;
	
	private boolean mCreditCardBillingAddressPresent;
	
	private boolean mGiftCardBillingAddressPresent;
	
	private boolean mCscOrder;
	
	private String mMopType;
	
	private String mGiftCardPaymentGroupClassName;
	
	private String mOriginalJournalKey;
	
	private double mAmountToReverse;
	
	private String mOriginalAuthDate;
	private String mOriginalAuthTime;
	private boolean mAuthReversalRequest;
	
	
	
	public boolean isAuthReversalRequest() {
		return mAuthReversalRequest;
	}

	public void setAuthReversalRequest(boolean pAuthReversalRequest) {
		mAuthReversalRequest = pAuthReversalRequest;
	}

	public String getOriginalAuthDate() {
		return mOriginalAuthDate;
	}

	public void setOriginalAuthDate(String pOriginalAuthDate) {
		mOriginalAuthDate = pOriginalAuthDate;
	}

	public String getOriginalAuthTime() {
		return mOriginalAuthTime;
	}

	public void setOriginalAuthTime(String pOriginalAuthTime) {
		mOriginalAuthTime = pOriginalAuthTime;
	}
	
	public String getOriginalJournalKey() {
    return mOriginalJournalKey;
  }

  public void setOriginalJournalKey(String pOriginalJournalKey) {
    mOriginalJournalKey = pOriginalJournalKey;
  }

  public AciCreditCardInfo getAciCreditCardInfo(){
		return mAciCreditCardInfo;
	}
	
	public void setAciCreditCardInfo(AciCreditCardInfo pAciCreditCardInfo){
		mAciCreditCardInfo=pAciCreditCardInfo;
	}
	
	public String getGiftCardPaymentGroupClassName(){
		return mGiftCardPaymentGroupClassName;
	}
	
	public void setGiftCardPaymentGroupClassName(String pGiftCardPaymentGroupClassName){
		mGiftCardPaymentGroupClassName=pGiftCardPaymentGroupClassName;
	}
	
	public String getMopType(){
		return this.mMopType;
	}
	
	public void setMopType(String pMopType){
		this.mMopType=pMopType;
	}
	
	public boolean getGiftCardBillingAddressPresent(){
		return this.mGiftCardBillingAddressPresent;
	}
	
	public void setGiftCardBillingAddressPresent(boolean pGiftCardBillingAddressPresent){
		this.mGiftCardBillingAddressPresent=pGiftCardBillingAddressPresent;
	}
	
	public boolean getCreditCardBillingAddressPresent(){
		return this.mCreditCardBillingAddressPresent;
	}
	
	public void setCreditCardBillingAddressPresent(boolean pCreditCardBillingAddressPresent){
		this.mCreditCardBillingAddressPresent=pCreditCardBillingAddressPresent;
	}
	
	public double getShippingPrice(){
		return this.mShippingPrice;
	}
	
	public void setShippingPrice(double pShippingPrice){
		this.mShippingPrice=pShippingPrice;
	}
	
	public double getTotalPaymentGroupPrice(){
		return this.mTotalPaymentGroupPrice;
	}
	
	public void setTotalPaymentGroupPrice(double pTotalPaymentGroupPrice){
		this.mTotalPaymentGroupPrice=pTotalPaymentGroupPrice;
	}
	
	public double getAdditionalShippingPrice(){
		return this.mAdditionalShippingPrice;
	}
	
	public void setAdditionalShippingPrice(double pAdditionalShippingPrice){
		this.mAdditionalShippingPrice=pAdditionalShippingPrice;
	}
	
	public double getTaxTotal(){
		return this.mTaxTotal;
	}
	
	public void setTaxTotal(double pTaxTotal){
		this.mTaxTotal=pTaxTotal;
	}
	
	
	public double getOrderSubTotal(){
		return this.mOrderSubTotal;
	}
	
	public void setOrderSubTotal(double pOrderSubTotal){
		this.mOrderSubTotal=pOrderSubTotal;
	}
	
	public boolean isCscOrder(){
		return this.mCscOrder;
	}
	
	public void setCscOrder(boolean pCscOrder){
		this.mCscOrder=pCscOrder;
	}
	

	public Map<String, RepositoryItem> getCommerceItemShippingGroupRelationShipMap() {
		return mCommerceItemShippingGroupRelationShipMap;
	}

	public void setCommerceItemShippingGroupRelationShipMap(
			Map<String, RepositoryItem> pCommerceItemShippingGroupRelationShipMap) {
		this.mCommerceItemShippingGroupRelationShipMap = pCommerceItemShippingGroupRelationShipMap;
	}

	
	public Map<String, String> getCommerceItemNumberCommerceItemMap() {
		return mCommerceItemNumberCommerceItemMap;
	}

	public void setCommerceItemNumberCommerceItemMap(
			Map<String, String> pCommerceItemNumberCommerceItemMap) {
		this.mCommerceItemNumberCommerceItemMap = pCommerceItemNumberCommerceItemMap;
	}

	public List<RepositoryItem> getCommerceItemShippingGroupRelationShips() {
		return mCommerceItemShippingGroupRelationShips;
	}

	public void setCommerceItemShippingGroupRelationShips(
			List<RepositoryItem> pCommerceItemShippingGroupRelationShips) {
		this.mCommerceItemShippingGroupRelationShips = pCommerceItemShippingGroupRelationShips;
	}

	public List<RepositoryItem> getCommerceItemPaymentGroupRelationShips() {
		return mCommerceItemPaymentGroupRelationShips;
	}

	public void setCommerceItemPaymentGroupRelationShips(
			List<RepositoryItem> pCommerceItemPaymentGroupRelationShips) {
		this.mCommerceItemPaymentGroupRelationShips = pCommerceItemPaymentGroupRelationShips;
	}

	public List<RepositoryItem> getOrderPaymentGroupRelationShips() {
		return mOrderPaymentGroupRelationShips;
	}

	public void setOrderPaymentGroupRelationShips(
			List<RepositoryItem> pOrderPaymentGroupRelationShips) {
		this.mOrderPaymentGroupRelationShips = pOrderPaymentGroupRelationShips;
	}
	
	public AciConfiguration getAciConfiguration(){
		return this.mAciConfiguration;
	}
	
	public void setAciConfiguration(AciConfiguration pAciconfiguration){
		this.mAciConfiguration=pAciconfiguration;
	}
	
	public Repository getOrderRepository(){
		return this.mOrderRepository;
	}
	
	public void setOrderRepository(Repository pOrderRepository){
		this.mOrderRepository=pOrderRepository;
	}
	
	public List<CommerceItem> getCommerceItems(){
		return this.mCommerceItems;
	}
	
	public void setCommerceItems(List<CommerceItem> pCommerceItems){
		this.mCommerceItems=pCommerceItems;
	}
	
	public boolean getHasBothHardgoodAndElectronicgood(){
		return this.mHasBothHardgoodAndElectronicgood;
	}
	
	public void setHasBothHardgoodAndElectronicgood(boolean pHasBothHardgoodAndElectronicgood){
		this.mHasBothHardgoodAndElectronicgood=pHasBothHardgoodAndElectronicgood;
	}
	
	public boolean getHasAllElectronicgoods(){
		return this.mHasAllElectronicgoods;
	}
	
	public void setHasAllElectronicgoods(boolean pHasAllElectronicgoods){
		this.mHasAllElectronicgoods=pHasAllElectronicgoods;
	}
	
	
	
	public List<HardgoodShippingGroup> getHardgoodShippingGroupInfos(){
		return this.mHardgoodShippingGroupInfos;
	}
	
	public void setHardgoodShippingGroupInfos(List<HardgoodShippingGroup> pHardgoodShippingGroupInfos){
		this.mHardgoodShippingGroupInfos=pHardgoodShippingGroupInfos;
	}
	
	public List<ElectronicShippingGroup> getElectronicShippingGroupInfos(){
		return this.mElectronicShippingGroupInfos;
	}
	
	public void setElectronicShippingGroupInfos(List<ElectronicShippingGroup> pElectronicShippingGroupInfos){
		this.mElectronicShippingGroupInfos=pElectronicShippingGroupInfos;
	}
	
	public List<ContactInfo> getGiftCardBillingInfos(){
		return this.mGiftCardBillingInfos;
	}
	
	public void setGiftCardBillingInfos(List<ContactInfo> pGiftCardBillingInfos){
		this.mGiftCardBillingInfos=pGiftCardBillingInfos;
	}
	
	
	
	
	public List<ContactInfo> getCreditCardBillingInfos(){
		return this.mCreditCardBillingInfos;
	}
	
	public void setCreditCardBillingInfos(List<ContactInfo> pCreditCardBillingInfos){
		this.mCreditCardBillingInfos=pCreditCardBillingInfos;
	}
	
	public List<PaymentGroup> getGiftCards(){
		return this.mGiftCards;
	}
	
	public void setGiftCards(List<PaymentGroup> pGiftCards){
		this.mGiftCards=pGiftCards;
	}
	
	public List<PaymentGroup> getCreditCards(){
		return this.mCreditCards;
	}
	
	public void setCreditCards(List<PaymentGroup> pCreditCards){
		this.mCreditCards=pCreditCards;
	}

	public Order getOrder() {
		return mOrder;
	}

	public void setOrder(Order order) {
		this.mOrder = order;
	}
	
	public LPTransaction getRequest() {
		return mRequest;
	}

	public void setRequest(LPTransaction pRequest) {
		this.mRequest = pRequest;
	}
	
	public void setTransactionAction(String pTransactionAction){
		this.mTransactionAction=pTransactionAction;
	}
	
	public String getTransactionAction(){
		return mTransactionAction;
	}
	
	public String getDivNum(){
		return mDivNum;
	}
	
	public void setDivNum(String pDivNum){
		mDivNum=pDivNum;
	}
	
	public String getCurrencyCode(){
		return mCurrencyCode;
	}
	
	public void setCurrencyCode(String pCurrencyCode){
		mCurrencyCode=pCurrencyCode;
	}
	
	public String getSubclientId(){
		return mSubclientId;
	}
	
	public void setSubclientId(String pSubclientId){
		mSubclientId=pSubclientId;
	}
	
	public void setReqType(String pReqType){
		mReqType=pReqType;
	}
	
	public String getReqType(){
		return mReqType;
	}
	
	public String getCardVerificationNumber(){
		return mCardVerificationNumber;
	}
	
	public void setCardVerificationNumber(String pCardVerificationNumber){
		mCardVerificationNumber=pCardVerificationNumber;
	}

	public boolean isHasCreditCards() {
		return mHasCreditCards;
	}

	public void setHasCreditCards(boolean pHasCreditCards) {
		mHasCreditCards = pHasCreditCards;
	}

	public boolean isHasGifftCards() {
		return mHasGifftCards;
	}

	public void setHasGifftCards(boolean pHasGifftCards) {
		mHasGifftCards = pHasGifftCards;
	}
	
	public boolean isEnableCvvVerification(){
		return mEnableCvvVerification;
	}
	
	public void setEnableCvvVerification(boolean pEnableCvvVerification){
		mEnableCvvVerification=pEnableCvvVerification;
	}
	
	public String getEmailAddress(){
		return mEmailAddress;
	}
	
	public void setEmailAddress(String pEmailAddress){
		mEmailAddress=pEmailAddress;
	}

	public boolean isBillingAddressAdded() {
		return mBillingAddressAdded;
	}

	public void setBillingAddressAdded(boolean pBillingAddressAdded) {
		mBillingAddressAdded = pBillingAddressAdded;
	}
	
	public String getOriginalReqId(){
		return mOriginalReqId;
	}
	
	public void setOriginalReqId(String pOriginalReqId){
		mOriginalReqId=pOriginalReqId;
	}
	
	public double getAmountToReverse(){
		return mAmountToReverse;
	}
	
	public void setAmountToReverse(double pAmountToReverse){
		mAmountToReverse=pAmountToReverse;
	}
}

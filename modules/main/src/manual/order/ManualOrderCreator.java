package manual.order;

import java.util.Date;

import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.RepositoryContactInfo;
import atg.commerce.order.processor.ProcSavePriceInfoObjects;
import atg.nucleus.GenericService;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

import com.mff.commerce.order.MFFOrderImpl;
import com.mff.commerce.order.MFFOrderManager;
import com.mff.userprofiling.MFFProfileTools;

public class ManualOrderCreator extends GenericService {

	private String mOrderType;
	
	public static final String COMP_PROFILE_REPOSITORY = "/atg/userprofiling/ProfileAdapterRepository";
	public static final String COMP_PROFILE_TOOLS = "atg/userprofiling/ProfileTools";
	public static final String COMP_ORDER_MANAGER = "/atg/commerce/order/OrderManager";
	public static final String COMP_TRANSACTION_MGR = "/atg/dynamo/transaction/TransactionManager";

	Nucleus nucleus = Nucleus.getGlobalNucleus();

	private MFFOrderManager orderManager;
	private TransactionManager transactionManager;
	private MFFProfileTools profileTools;
	MutableRepository profileRepository;
	ManualCommerceItemManager mManualCommerceItemManager;
	ManualBillingInfoProcessor mManualBillingInfoProcessor;
	ManualOrderCommitProcessor mManualOrderCommitProcessor;
	boolean mAddOrderToRepository;
	ProcSavePriceInfoObjects mSavePriceInfoObj;
		
	private void resolveRequiredNucleusComponents() {

		transactionManager = (TransactionManager) nucleus
				.resolveName(COMP_TRANSACTION_MGR);
		orderManager = (MFFOrderManager) nucleus
				.resolveName(COMP_ORDER_MANAGER);
		profileRepository = (MutableRepository) nucleus
				.resolveName(COMP_PROFILE_REPOSITORY);
		profileTools = (MFFProfileTools) nucleus
				.resolveName(COMP_PROFILE_TOOLS);
		logDebug("Resolved profileRepository:" + profileRepository);
		profileTools.setProfileRepository(profileRepository);
	}
	
	@SuppressWarnings("deprecation")
	public String createManualOrder() {
		
		logDebug("createManualOrder(): Called.");
		
		resolveRequiredNucleusComponents();
		
		String orderId = null;
		String profileId = null;
		RepositoryItem profielItem = null;
		try {
				profielItem = createUser();

				if (profielItem != null) {
					profileId = profielItem.getRepositoryId();

					logDebug("processSingleOrderElement(): Created ATG profile: " + profileId);
				}

			MFFOrderImpl order = createOrder(profileId);
			
			getManualCommerceItemManager().addItemToOrder(order, profielItem);
			getManualBillingInfoProcessor().applyPaymentGroupToOrder(order, profielItem, orderManager, transactionManager);
			getManualBillingInfoProcessor().processBillingWithNewAddressAndNewCard(order,
								profielItem, orderManager);

			getManualOrderCommitProcessor().processOrderCommit(order, profielItem, orderManager, transactionManager);
			order.setState(1);
			order.updateVersion();
			orderManager.updateOrder(order);
			
			orderId = order.getId();
			logDebug("processSingleOrderElement(): Created ATG Order with ID: " + orderId);

		} catch (Exception e) {
			if (isLoggingError()) {
				logError("processSingleOrderElement(): Exception while processing order: " + e, e);
			}
		}
		
		return orderId;
	}

	/**
	 * This method create various order objects.
	 * 
	 * @param pOrderModsMsg
	 * @throws CommerceException
	 */

	public MFFOrderImpl createOrder(String pProfileId) throws Exception {
		
		MFFOrderImpl order = null;
		
		// this will create order with order id as ATG generated.
		order = (MFFOrderImpl) orderManager.createOrder(pProfileId, getOrderType());

		logDebug(" createOrder(): Order Created: " + order);

		populateHardgoodShippingGroup(order);
		order.setOriginOfOrder("scheduledOrder");
		order.setCreationDate(new Date());
		order.setSubmittedDate(new Date());

		if (isAddOrderToRepository() && order != null) {
			orderManager.addOrder(order);
			logDebug(" createOrder(): Order added to repository..");
		}

		return order;
	}

	private void populateHardgoodShippingGroup(MFFOrderImpl order)
			throws Exception {

		HardgoodShippingGroup hgsg = (HardgoodShippingGroup) order
				.getShippingGroups().get(0);
		logDebug(" populateHardgoodShippingGroup():Repair Shipping Group: "
				+ hgsg);

		if (hgsg != null) {

			RepositoryContactInfo shippingAddress = (RepositoryContactInfo) hgsg
					.getShippingAddress();

			shippingAddress.setFirstName("Shipping first name");
			shippingAddress.setLastName("Shipping last name");

			shippingAddress.setAddress1("3110 Eastern Ave");
			shippingAddress.setAddress2("");
			shippingAddress.setAddress3("");
			shippingAddress.setCity("Plymouth");
			shippingAddress.setState("WI");
			shippingAddress.setPostalCode("53073");

			shippingAddress.setCountry("US");
			shippingAddress.setPhoneNumber("920-893-5115");
		}

		hgsg.setShippingMethod("Overnight");

	}

	protected RepositoryItem createUser()
			throws Exception {

		logDebug(" createUser(): Called..");

		MutableRepositoryItem mutableItem = createProfileItem();

		if (mutableItem != null) {

			String uniqueLoginName = getUniqueLoginId("test_mff_order");

			mutableItem.setPropertyValue("login", uniqueLoginName);
			mutableItem.setPropertyValue("password", "password1");
			mutableItem.setPropertyValue("firstName", "test mff first");
			mutableItem.setPropertyValue("lastName", "test mff last");
			mutableItem.setPropertyValue("email", "first_last@gmail.com");
			
			logDebug(" createUser(): mutableItem: " + mutableItem);
		}

		RepositoryItem dbUser = addUser(mutableItem, profileRepository);

		logDebug((new StringBuilder())
				.append(" createUser: user created; item=")
				.append(dbUser).toString());
		return dbUser;
	}

	private String getUniqueLoginId(String stringValueProperty) {

		String uniqueLoginId = stringValueProperty
				+ (int) Math.floor(Math.random() * 100000 + 1);
		return uniqueLoginId;
	}

	protected MutableRepositoryItem createProfileItem() throws Exception {

		logDebug(" createProfileItem(): Called..");

		MutableRepositoryItem mutableItem = null;

		RepositoryItem currentUser = profileRepository.createItem("user");
		if (!(currentUser instanceof MutableRepositoryItem)) {
			String currentUserId = currentUser.getRepositoryId();
			mutableItem = profileRepository.getItemForUpdate(currentUserId,
					"user");
		} else {
			mutableItem = (MutableRepositoryItem) currentUser;
		}

		return mutableItem;
	}

	protected RepositoryItem addUser(MutableRepositoryItem pUser,
			MutableRepository pProfileRepository) throws Exception {

		logDebug(" addUser(): Called..");

		if (pUser.isTransient()) {

			logDebug(" addUser(): Came to transient user block...");
			return pProfileRepository.addItem(pUser);

		} else {
			logDebug(" addUser(): Came to NON-transient user block...");

			pProfileRepository.updateItem(pUser);
			return pProfileRepository.getItem(pUser.getRepositoryId(), "user");
		}
	}

	public String getOrderType() {
		return mOrderType;
	}

	public void setOrderType(String pOrderType) {
		this.mOrderType = pOrderType;
	}

	public ManualCommerceItemManager getManualCommerceItemManager() {
		return mManualCommerceItemManager;
	}

	public void setManualCommerceItemManager(
			ManualCommerceItemManager pManualCommerceItemManager) {
		this.mManualCommerceItemManager = pManualCommerceItemManager;
	}
	
	public ManualBillingInfoProcessor getManualBillingInfoProcessor() {
		return mManualBillingInfoProcessor;
	}

	public void setManualBillingInfoProcessor(
			ManualBillingInfoProcessor pManualBillingInfoProcessor) {
		this.mManualBillingInfoProcessor = pManualBillingInfoProcessor;
	}

	public ManualOrderCommitProcessor getManualOrderCommitProcessor() {
		return mManualOrderCommitProcessor;
	}

	public void setManualOrderCommitProcessor(
			ManualOrderCommitProcessor pManualOrderCommitProcessor) {
		this.mManualOrderCommitProcessor = pManualOrderCommitProcessor;
	}

	public boolean isAddOrderToRepository() {
		return mAddOrderToRepository;
	}

	public void setAddOrderToRepository(boolean pAddOrderToRepository) {
		this.mAddOrderToRepository = pAddOrderToRepository;
	}

	public ProcSavePriceInfoObjects getSavePriceInfoObj() {
		return mSavePriceInfoObj;
	}

	public void setSavePriceInfoObj(ProcSavePriceInfoObjects pSavePriceInfoObj) {
		this.mSavePriceInfoObj = pSavePriceInfoObj;
	}
}
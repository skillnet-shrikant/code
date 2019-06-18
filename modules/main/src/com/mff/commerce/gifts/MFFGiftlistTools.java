package com.mff.commerce.gifts;

import java.util.Date;
import java.util.ResourceBundle;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftObjectCreationException;
import atg.commerce.gifts.GiftlistTools;
import atg.commerce.gifts.InvalidDateException;
import atg.commerce.gifts.InvalidGiftParameterException;
import atg.commerce.gifts.InvalidGiftTypeException;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.dynamo.LangLicense;

/**
 * This class provide extensions to atg OOTB GiftlistTools.
 *
 */
public class MFFGiftlistTools extends GiftlistTools {
	
	static final String MY_RESOURCE_NAME = "atg.commerce.gifts.GiftlistResources";
	private static ResourceBundle sResourceBundle = LayeredResourceBundle
			.getBundle("atg.commerce.gifts.GiftlistResources",
					LangLicense.getLicensedDefault());
	
	
	public RepositoryItem createDefaultGiftlist(String pProfileId, boolean pPublished,
			String pEventName, Date pEventDate, String pEventType,
			String pDescription, String pComments, String pShippingAddressId,
			String pInstructions, String pSiteId) throws CommerceException {
		
		if (pProfileId == null) {
			throw new InvalidGiftParameterException(
					ResourceUtils.getMsgResource("InvalidProfileIdParameter",
							"atg.commerce.gifts.GiftlistResources",
							sResourceBundle));
		} else {
			MutableRepositoryItem giftlist = null;

			try {
				giftlist = createGiftlist();
				if (this.isLoggingDebug()) {
					this.logDebug("Created giftlist: " + giftlist);
				}

				this.updateGiftlist(pProfileId, giftlist, pPublished,
						pEventName, pEventDate, pEventType, pShippingAddressId,
						pDescription, pComments, pInstructions, pSiteId);
				addItem(giftlist);
				
				
			} catch (InvalidGiftTypeException arg13) {
				throw new GiftObjectCreationException(
						ResourceUtils.getMsgResource("InvalidGiftlistName",
								"atg.commerce.gifts.GiftlistResources",
								sResourceBundle), arg13);
			} catch (InvalidDateException arg14) {
				throw new GiftObjectCreationException(
						ResourceUtils.getMsgResource("InvalidDate",
								"atg.commerce.gifts.GiftlistResources",
								sResourceBundle), arg14);
			} catch (RepositoryException arg15) {
				throw new GiftObjectCreationException(
						ResourceUtils.getMsgResource("GiftlistItemNotCreated",
								"atg.commerce.gifts.GiftlistResources",
								sResourceBundle), arg15);
			}

			return giftlist;
		}
	}
	
	public boolean updateGiftlist(String pProfileId,
			MutableRepositoryItem pGiftlist, boolean pPublished,
			String pEventName, Date pEventDate, String pEventType,
			String pShippingAddressId, String pDescription, String pComments,
			String pInstructions, String pSiteId) throws CommerceException {
		if (pProfileId == null) {
			throw new InvalidGiftParameterException(
					ResourceUtils.getMsgResource("InvalidProfileIdParameter",
							"atg.commerce.gifts.GiftlistResources",
							sResourceBundle));
		} else if (pGiftlist == null) {
			throw new InvalidGiftParameterException(
					ResourceUtils.getMsgResource("InvalidGiftlistParameter",
							"atg.commerce.gifts.GiftlistResources",
							sResourceBundle));
		} else {
			MutableRepositoryItem profile = this.getProfile(pProfileId);
			pGiftlist.setPropertyValue(this.getOwnerProperty(), profile);
			pGiftlist.setPropertyValue(this.getPublicProperty(), Boolean.FALSE);
			pGiftlist.setPropertyValue(this.getPublishedProperty(),
					Boolean.valueOf(pPublished));
			if (!pPublished) {
				try {
					this.removeGiftlistFromOtherProfiles(pGiftlist);
				} catch (RepositoryException arg13) {
					throw new CommerceException(arg13);
				}
			}

			pGiftlist.setPropertyValue(this.getEventNameProperty(), pEventName);
			pGiftlist.setPropertyValue(this.getEventDateProperty(), pEventDate);
			pGiftlist.setPropertyValue(this.getEventTypeProperty(), pEventType);
			RepositoryItem address = this.getAddress(pShippingAddressId);
			pGiftlist.setPropertyValue(this.getShippingAddressProperty(),
					address);
			if (pDescription != null) {
				pGiftlist.setPropertyValue(this.getDescriptionProperty(),
						pDescription);
			}

			if (pComments != null) {
				pGiftlist.setPropertyValue(this.getCommentsProperty(),
						pComments);
			}

			if (pInstructions != null) {
				pGiftlist.setPropertyValue(this.getInstructionsProperty(),
						pInstructions);
			}
			
			if (pSiteId == null) {
				pSiteId = SiteContextManager.getCurrentSiteId();
			}

			pGiftlist.setPropertyValue(this.getSiteProperty(), pSiteId);
			return true;
		}
	}
	
}